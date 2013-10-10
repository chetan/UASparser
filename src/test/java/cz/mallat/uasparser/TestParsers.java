package cz.mallat.uasparser;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

/**
 * No real JUnit tests
 *
 * @author oli
 *
 */
public class TestParsers {

    @Test
    public void runUAParser() throws IOException {
        UASparser p = new UASparser(OnlineUpdater.getVendoredInputStream());
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
        UASparser p = new SingleThreadedUASparser(OnlineUpdater.getVendoredInputStream());
        testUserAgents(p);
    }

    @Test
    public void testMultithreadedParser() throws IOException {
        UASparser p = new MultithreadedUASparser(OnlineUpdater.getVendoredInputStream());
        testUserAgents(p);
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
        assertEquals("Computational Crawling, LP", uai.getUaCompany());

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
    	UserAgentInfo uai = parser.parse("Mozilla/5.0 (iPad; CPU OS 7_0 like Mac OS X) AppleWebKit/537.51.1 (KHTML, like Gecko) Version/7.0 Mobile/11A465 Safari/9537.53");
        assertNotNull(uai);
        assertEquals("Mobile Browser", uai.getType());
        assertEquals("Mobile Safari 7.0", uai.getUaName());
        assertEquals("Mobile Safari", uai.getUaFamily());
        assertEquals("Apple Inc.", uai.getUaCompany());
        assertEquals("iOS 7", uai.getOsName());
        assertEquals("iOS", uai.getOsFamily());
        assertEquals("Apple Inc.", uai.getOsCompany());
        
        assertTrue(uai.hasDeviceInfo());
        assertEquals("Tablet", uai.getDeviceType());
        assertEquals("tablet.png", uai.getDeviceIcon());
        assertEquals("http://user-agent-string.info/list-of-ua/device-detail?device=Tablet", uai.getDeviceInfoUrl());
    }
    
    private void testSmartphoneAgent(UASparser parser) throws IOException {
    	UserAgentInfo uai = parser.parse("Mozilla/5.0 (iPhone; CPU iPhone OS 7_0 like Mac OS X) AppleWebKit/546.10 (KHTML, like Gecko) Version/6.0 Mobile/7E18WD Safari/8536.25");
        assertNotNull(uai);
        assertEquals("Mobile Browser", uai.getType());
        assertEquals("Mobile Safari 6.0", uai.getUaName());
        assertEquals("Mobile Safari", uai.getUaFamily());
        assertEquals("Apple Inc.", uai.getUaCompany());
        assertEquals("iOS 7", uai.getOsName());
        assertEquals("iOS", uai.getOsFamily());
        assertEquals("Apple Inc.", uai.getOsCompany());
        
//        assertTrue(uai.hasDeviceInfo());
//        assertEquals("Smartphone", uai.getDeviceType());
//        assertEquals("phone.png", uai.getDeviceIcon());
//        assertEquals("http://user-agent-string.info/list-of-ua/device-detail?device=Smartphone", uai.getDeviceInfoUrl());
    }

    @Test
    public void testArrayIndexBug() throws IOException {
        // should not throw exception
        UASparser p = new UASparser(OnlineUpdater.getVendoredInputStream());
        UserAgentInfo uai = p.parse("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; MyIE2; MRA 4.7 (build 01670); .NET CLR 1.1.4322)");
    }

}
