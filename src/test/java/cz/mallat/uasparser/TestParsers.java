package cz.mallat.uasparser;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

/**
 * Test the various parser implementations
 *
 * @author chetan
 *
 */
public class TestParsers {

    protected boolean testDeviceInfo = true;

    @Test
    public void runUAParser() throws IOException {
        UASparser p = new UASparser(getDataInputStream());
        testUserAgents(p);
    }

    @Test
    public void runOnlineUAParser() throws IOException {
        UASparser p = new OnlineUpdateUASparser();
        testUserAgents(p);
    }

    @Test
    public void runCachedOnlineUAParser() throws IOException {
        UASparser p = new CachingOnlineUpdateUASparser();
        testUserAgents(p);
    }

    @Test
    public void testSingleThreadedParser() throws IOException {
        UASparser p = new SingleThreadedUASparser(getDataInputStream());
        testUserAgents(p);
    }

    @Test
    public void testMultithreadedParser() throws IOException {
        UASparser p = new MultithreadedUASparser(getDataInputStream());
        testUserAgents(p);
    }

    /**
     * Tests for various device types
     *
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void testDeviceUA() throws IOException, InterruptedException {

        if (!this.testDeviceInfo) {
            return;
        }

        UASparser p = new UASparser(getDataInputStream());

        UserAgentInfo info = p.parse("Mozilla/5.0 (Linux; U; Android 4.0.4; en-au; GT-N7000 Build/IMM76D) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30 Maxthon/4.1.1.2000");
        assertEquals("Smartphone", info.getDeviceType());

        info = p.parse("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.116 Safari/537.36 Mozilla/4.0 (compatible; MSIE 5.0; Windows NT;)");
        assertEquals("Personal computer", info.getDeviceType());

        info = p.parse("Mozilla/5.0 (Linux; U; Android 4.0.3; ko-kr; LG-L160L Build/IML74K) AppleWebkit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
        assertEquals("Smartphone", info.getDeviceType());

        info = p.parse("Mozilla/5.0 (compatible; 008/0.83; http://www.80legs.com/spider.html;) Gecko/2008032620");
        assertEquals("Other", info.getDeviceType());
    }

    /**
     * Get database file to test against as an {@link InputStream}
     * @return
     */
    protected InputStream getDataInputStream() {
        return OnlineUpdater.getVendoredInputStream();
    }

    private void testUserAgents(UASparser parser) throws IOException {
        testRobotAgents(parser);
    	testBrowserAgent(parser);
        testEmailAgent(parser);
        testTabletAgent(parser);
        testSmartphoneAgent(parser);
    }

    private void testRobotAgents(UASparser parser) throws IOException {
        UserAgentInfo uai = parser.parse("Mozilla/5.0 (compatible; 008/0.83; http://www.80legs.com/spider.html;) Gecko/2008032620");
        assertTrue(uai.isRobot());
        if (this.testDeviceInfo) {
            assertEquals("Datafiniti, LLC.", uai.getUaCompany());
        } else {
            // when using the 'old db', this may be either of the two strings..
            // on a normal run/test, it will be Computational, but the caching test will result in Datafiniti..
            assertTrue(uai.getUaCompany().equals("Computational Crawling, LP") || uai.getUaCompany().equals("Datafiniti, LLC."));
        }

        uai = parser.parse("Googlebot/2.1 (+http://www.googlebot.com/bot.html)");
        assertFalse(uai.isRobot()); // not currently detected

        uai = parser.parse("Pingdom.com_bot_version_1.4_(http://www.pingdom.com/)");
        assertTrue(uai.isRobot());
    }

    private void testBrowserAgent(UASparser parser) throws IOException {
    	UserAgentInfo uai = parser.parse("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.0.12) Gecko/2009070611 Firefox/3.0.12");
        assertNotNull(uai);
        assertEquals("Browser", uai.getType());
        assertEquals("Firefox 3.0.12", uai.getUaName());
        assertEquals("Firefox", uai.getUaFamily());
        assertEquals("Mozilla Foundation", uai.getUaCompany());
        assertEquals("Windows XP", uai.getOsName());
        assertEquals("Windows", uai.getOsFamily());
        assertEquals("Microsoft Corporation.", uai.getOsCompany());
    }

    private void testEmailAgent(UASparser parser) throws IOException {
    	UserAgentInfo uai = parser.parse("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_5_8) AppleWebKit/534.50.2 (KHTML, like Gecko)");
        assertNotNull(uai);
        assertEquals("Email client", uai.getType());
        assertEquals("Apple Mail", uai.getUaName());
        assertEquals("Apple Mail", uai.getUaFamily());
        assertEquals("Apple Inc.", uai.getUaCompany());
        assertEquals("OS X 10.5 Leopard", uai.getOsName());
        assertEquals("OS X", uai.getOsFamily());
        assertEquals("Apple Computer, Inc.", uai.getOsCompany());
    }

    private void testTabletAgent(UASparser parser) throws IOException {
        UserAgentInfo uai = parser.parse("Mozilla/5.0 (iPad; CPU OS 6_0 like Mac OS X) AppleWebKit/537.51.1 (KHTML, like Gecko) Version/6.0 Mobile/11A465 Safari/9537.53");
        assertNotNull(uai);
        assertEquals("Mobile Browser", uai.getType());
        assertEquals("Mobile Safari 6.0", uai.getUaName());
        assertEquals("Mobile Safari", uai.getUaFamily());
        assertEquals("Apple Inc.", uai.getUaCompany());
        assertEquals("iOS 6", uai.getOsName());
        assertEquals("iOS", uai.getOsFamily());
        assertEquals("Apple Inc.", uai.getOsCompany());

        if (!testDeviceInfo) {
            return;
        }
        assertTrue(uai.hasDeviceInfo());
        assertEquals("Tablet", uai.getDeviceType());
        assertEquals("tablet.png", uai.getDeviceIcon());
        assertEquals("http://user-agent-string.info/list-of-ua/device-detail?device=Tablet", uai.getDeviceInfoUrl());
    }

    private void testSmartphoneAgent(UASparser parser) throws IOException {
        UserAgentInfo uai = parser.parse("Mozilla/5.0 (iPhone; CPU iPhone OS 6_0 like Mac OS X) AppleWebKit/546.10 (KHTML, like Gecko) Version/6.0 Mobile/7E18WD Safari/8536.25");
        assertNotNull(uai);
        assertEquals("Mobile Browser", uai.getType());
        assertEquals("Mobile Safari 6.0", uai.getUaName());
        assertEquals("Mobile Safari", uai.getUaFamily());
        assertEquals("Apple Inc.", uai.getUaCompany());
        assertEquals("iOS 6", uai.getOsName());
        assertEquals("iOS", uai.getOsFamily());
        assertEquals("Apple Inc.", uai.getOsCompany());

//        if (!testDeviceInfo) {
//            return;
//        }
//        assertTrue(uai.hasDeviceInfo());
//        assertEquals("Smartphone", uai.getDeviceType());
//        assertEquals("phone.png", uai.getDeviceIcon());
//        assertEquals("http://user-agent-string.info/list-of-ua/device-detail?device=Smartphone", uai.getDeviceInfoUrl());
    }

    @Test
    public void testArrayIndexBug() throws IOException {
        // should not throw exception
        UASparser p = new UASparser(getDataInputStream());
        UserAgentInfo uai = p.parse("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; MyIE2; MRA 4.7 (build 01670); .NET CLR 1.1.4322)");
    }
}
