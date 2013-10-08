package cz.mallat.uasparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import cz.mallat.uasparser.fileparser.PHPFileParser;
import cz.mallat.uasparser.fileparser.Section;

/**
 * An updater which runs in a separate background thread and will update once per day.
 *
 * <p>The updated UA strings are cached on disk. If no cached copy is found on start,
 * one will be fetched immediately. If this initial update fails, it will fallback
 * to an included copy.</p>
 *
 * @author chetan
 *
 */
public class OnlineUpdater extends Thread {

    public static final String CACHE_FILENAME = "user_agent_strings.txt";
    public static final String PROPERTIES_FILENAME = "user_agent_strings-version.txt";

    protected static final String DATA_RETRIVE_URL = "http://user-agent-string.info/rpc/get_data.php?key=free&format=ini";
    protected static final String VERSION_CHECK_URL = "http://user-agent-string.info/rpc/get_data.php?key=free&format=ini&ver=y";

    protected final long updateInterval;
    protected int currentVersion;

    protected UASparser parser;

    protected File cacheFile;
    protected File propsFile;

    /**
     * Create a new updater with the default interval of 1 day
     *
     * @param parser        Parser instance to update
     */
    public OnlineUpdater(UASparser parser) {
        this(parser, null, 1, TimeUnit.DAYS);
    }

    /**
     * Create a new updater
     *
     * @param parser        Parser instance to update
     * @param cacheDir      directory where file should be cached. If null, uses system temp dir
     * @param interval      number of intervals for the given units
     * @param units         unit type
     */
    public OnlineUpdater(UASparser parser, String cacheDir, long interval, TimeUnit units) {
        this.parser = parser;

        if (cacheDir == null) {
            cacheDir = System.getProperty("java.io.tmpdir");
        }
        if (!new File(cacheDir).canWrite()) {
            throw new RuntimeException("Can't write to cacheDir: " + cacheDir);
        }
        this.cacheFile = new File(cacheDir, CACHE_FILENAME);
        this.propsFile = new File(cacheDir, PROPERTIES_FILENAME);
        this.currentVersion = 0;

        updateInterval = units.toMillis(interval);

        init();
        start();
    }

    /**
     * Initialize the parser with cached data. Falls back to vendored copy if no cache is available.
     */
    public void init() {

        if (this.cacheFile.exists()) {
            try {
                parser.loadDataFromFile(cacheFile);
                this.currentVersion =
                        Integer.parseInt(new BufferedReader(new FileReader(propsFile)).readLine());
                return;
            } catch (Throwable t) {
                this.currentVersion = 0;
            }
        }

        try {
            // fall back to vendored copy so we don't block on startup
            parser.loadDataFromFile(getVendoredInputStream());
        } catch (IOException e) {
        }
    }

    /**
     * Retrieve an {@link InputStream} to the vendored copy of the UA strings file.
     * @return {@link InputStream}
     */
    public static InputStream getVendoredInputStream() {
        return OnlineUpdater.class.getClassLoader().getResourceAsStream(CACHE_FILENAME);
    }

    /**
     * Fetch latest UA file if a newer one is available
     *
     * @return boolean True if parser was updated.
     */
    public boolean update() {
        try {
            int versionOnServer = getVersionFromServer();
            if (currentVersion == 0 || versionOnServer > currentVersion) {
                parser.createInternalDataStructure(loadDataFromInternet());

                // if reached this far then we loaded it correctly, store new version #
                currentVersion = versionOnServer;
                FileWriter writer = new FileWriter(propsFile);
                writer.write(Integer.toString(currentVersion));
                writer.close();

                return true;
            }
        } catch (Throwable t) {
        }
        return false;
    }

    /**
     * Update loop
     */
    @Override
    public void run() {
        while (true) {
            update();
            try {
                // add up to 300sec of jitter to interval
                Thread.sleep(updateInterval + (new Random().nextInt(300) * 1000));
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    /**
     * Loads the data file from user-agent-string.info and caches it on disk
     * @return
     *
     * @throws IOException
     */
    protected List<Section> loadDataFromInternet() throws IOException {

        File tmpFile = File.createTempFile("uas", ".txt");

        try {

            // Download file to temp location
            BufferedReader reader = null;
            FileWriter writer = null;
            try {
                URL url = new URL(DATA_RETRIVE_URL);
                reader = new BufferedReader(new InputStreamReader(url.openStream()));
                writer = new FileWriter(tmpFile);
                String line = null;
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                    writer.write(System.getProperty("line.separator"));
                }

            } finally {
                if (reader != null) {
                    reader.close();
                }
                if (writer != null) {
                    writer.close();
                }
            }

            // Try to parse it
            try {
                PHPFileParser fp = new PHPFileParser(tmpFile);
                List<Section> sections = fp.getSections();

                // now that we've finished parsing, we can save the temp copy
                if (cacheFile.exists()) {
                    cacheFile.delete();
                }

                if (!tmpFile.renameTo(cacheFile)) {
                    // was across filesystems or target exists, or something else.
                    // Try other another way
                    // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4017593
                    copyFile(tmpFile, cacheFile);
                }

                return sections;

            } catch (Throwable t) {
                if (t instanceof IOException) {
                    throw (IOException) t;
                }
                throw new IOException(t);
            }

        } finally {
            if (tmpFile.compareTo(cacheFile) != 0) {
                tmpFile.delete();
            }
        }
    }

    /**
     * Copy source file to destination
     *
     * @param src
     * @param dest
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    protected void copyFile(File src, File dest) throws FileNotFoundException, IOException {
        InputStream inStream = new FileInputStream(src);
        OutputStream outStream = new FileOutputStream(dest);

        byte[] buffer = new byte[4096];

        int length;
        while ((length = inStream.read(buffer)) > 0) {
            outStream.write(buffer, 0, length);
        }

        inStream.close();
        outStream.close();
    }

    /**
     * Gets the current version from user-agent-string.info
     *
     * @return long version number (e.g., 2013012301)
     * @throws IOException
     * @throws {@link NumberFormatException}
     */
    protected int getVersionFromServer() throws IOException {
        URL url = new URL(VERSION_CHECK_URL);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String ver = reader.readLine();
            if (ver == null || ver.isEmpty()) {
                throw new IOException("Failed to read version number");
            }
            return Integer.parseInt(ver.replace("-", ""));

        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

}
