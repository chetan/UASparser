package cz.mallat.uasparser;

import static org.junit.Assert.*;

import org.junit.Test;


public class TestOnlineUpdater {

    @Test
    public void testUpdate() throws InterruptedException {

        UASparser parser = new UASparser();
        assertNull(parser.browserMap);

        OnlineUpdater updater = new OnlineUpdater(parser);
        assertNotNull(parser.browserMap);
        assertTrue(updater.isAlive());
    }

}
