package cz.mallat.uasparser;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.junit.Before;
import org.junit.Test;

/**
 * Test against a copy of the database without device info
 *
 * @author chetan
 *
 */
public class TestOldDatabase extends TestParsers {

    @Before
    public void disableDeviceTests() {
        this.testDeviceInfo = false;
    }

    @Override
    protected InputStream getDataInputStream() {
        try {
            return new GZIPInputStream(this.getClass().getClassLoader().getResourceAsStream("uas-nodevice.txt.gz"));
        } catch (IOException e) {
        }
        return null;
    }

    @Override
    @Test
    public void runOnlineUAParser() throws IOException {
        // disable
    }

}
