package cz.mallat.uasparser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import cz.mallat.uasparser.fileparser.PHPFileParser;
import cz.mallat.uasparser.fileparser.Section;

/**
 * An updater which runs in a separate background thread and will update once per day.
 * If the initial update fails, it will fallback to an included copy of uas.ini.
 *
 * @author chetan
 *
 */
public class OnlineUpdater extends Thread implements Updater {

    protected static final String DATA_RETRIVE_URL = "http://user-agent-string.info/rpc/get_data.php?key=free&format=ini";
    protected static final String VERSION_CHECK_URL = "http://user-agent-string.info/rpc/get_data.php?key=free&format=ini&ver=y";

    protected final long updateInterval;
    protected String currentVersion;

    protected UASparser parser;

    /**
     * Create a new updater with the default interval of 1 day
     *
     * @param parser        Parser instance to update
     */
    public OnlineUpdater(UASparser parser) {
        this(parser, 1, TimeUnit.DAYS);
    }

    /**
     * Create a new updater
     *
     * @param parser        Parser instance to update
     * @param interval      number of intervals for the given units
     * @param units         unit type
     */
    public OnlineUpdater(UASparser parser, long interval, TimeUnit units) {
        this.parser = parser;
        // add up to 60sec of jitter to interval
        updateInterval = units.toMillis(interval) + (new Random().nextInt(60) * 1000);
        if (!update()) {
            try {
                parser.loadDataFromFile(getClass().getClassLoader().getResourceAsStream("uas.ini"));
            } catch (IOException e) {
            }
        }
        start();
    }

    /**
     * Fetch latest file from the internet if update interval has passed
     *
     * @return boolean True if parser was updated.
     */
    @Override
    public boolean update() {
        try {
            String versionOnServer = getVersionFromServer();
            if (currentVersion == null || versionOnServer.compareTo(currentVersion) > 0) {
                currentVersion = versionOnServer;
                parser.createInternalDataStructre(loadDataFromInternet());
                return true;
            }
        } catch (IOException e) {
        }
        return false;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(updateInterval);
            } catch (InterruptedException e) {
                return;
            }
            update();
        }
    }

    /**
     * Loads the data file from user-agent-string.info
     * @return
     *
     * @throws IOException
     */
    protected List<Section> loadDataFromInternet() throws IOException {
        URL url = new URL(DATA_RETRIVE_URL);
        InputStream is = url.openStream();
        try {
            PHPFileParser fp = new PHPFileParser(is);
            return fp.getSections();
        } catch (Throwable t) {
            if (t instanceof IOException) {
                throw (IOException) t;
            }
            throw new IOException(t);
        } finally {
            is.close();
        }
    }

    /**
     * Gets the current version from user-agent-string.info
     *
     * @return
     * @throws IOException
     */
    protected String getVersionFromServer() throws IOException {
        URL url = new URL(VERSION_CHECK_URL);
        InputStream is = url.openStream();
        try {
            byte[] buff = new byte[4048];
            int len = is.read(buff);
            return new String(buff, 0, len);
        } finally {
            is.close();
        }
    }


}
