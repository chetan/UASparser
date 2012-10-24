package cz.mallat.uasparser;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.Test;


public class TestOnlineUpdater {

    @Test
    public void testUpdate() throws InterruptedException, IOException {

        File tmpDir = File.createTempFile("uas", ".test");
        tmpDir.delete();
        tmpDir.mkdirs();
//        tmpDir.deleteOnExit();

        try {

            UASparser parser = new UASparser();
            assertNull(parser.browserMap);

            OnlineUpdater updater = new OnlineUpdater(parser, tmpDir.toString(), 1, TimeUnit.DAYS);
            assertNotNull(parser.browserMap);
            assertTrue(updater.isAlive());

            TestParsers testParsers = new TestParsers();
            testParsers.runUAParser();

            assert(new File(tmpDir, OnlineUpdater.CACHE_FILENAME).exists());
            assert(new File(tmpDir, OnlineUpdater.PROPERTIES_FILENAME).exists());

            parser = new UASparser();
            assertNull(parser.browserMap);
            parser.loadDataFromFile(new File(tmpDir, OnlineUpdater.CACHE_FILENAME));
            assertNotNull(parser.browserMap);

        } finally {
//            new File(tmpDir, OnlineUpdater.CACHE_FILENAME).delete();
//            new File(tmpDir, OnlineUpdater.PROPERTIES_FILENAME).delete();
        }

    }

}
