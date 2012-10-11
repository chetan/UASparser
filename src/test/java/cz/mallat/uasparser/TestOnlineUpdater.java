package cz.mallat.uasparser;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;


public class TestOnlineUpdater {

    @Test
    public void testUpdate() throws InterruptedException, IOException {

        UASparser parser = new UASparser();
        assertNull(parser.browserMap);

        OnlineUpdater updater = new OnlineUpdater(parser);
        assertNotNull(parser.browserMap);
        assertTrue(updater.isAlive());

        TestParsers testParsers = new TestParsers();
        testParsers.setup();
        testParsers.runUAParser();
    }

}
