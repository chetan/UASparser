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
        testParser(p);

        // type:Browser
        // ua_name:Firefox 3.0.12
        // ua_family:Firefox
        // ua_producer:Mozilla Foundation
        // os_name:Windows XP
        // os_family:Windows
        // os_producer:Microsoft Corporation.
    }

    @Test
    public void runOnlineUAParser() throws IOException {
        UASparser p = new OnlineUpdateUASparser();
        testParser(p);
    }

    @Test
    public void runCachedOnlineUAParser() throws IOException {
        UASparser p = new CachingOnlineUpdateUASparser();
        testParser(p);
    }

    @Test
    public void testSingleThreadedParser() throws IOException {
        UASparser p = new SingleThreadedUASparser(OnlineUpdater.getVendoredInputStream());
        testParser(p);
    }

    @Test
    public void testMultithreadedParser() throws IOException {
        UASparser p = new MultithreadedUASparser(OnlineUpdater.getVendoredInputStream());
        testParser(p);
    }

    private void testParser(UASparser parser) throws IOException {
        UserAgentInfo uai = parser.parse("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.0.12) Gecko/2009070611 Firefox/3.0.12");
        assertNotNull(uai);
        assertEquals("Browser", uai.getType());
        assertEquals("Firefox 3.0.12", uai.getUaName());
        assertEquals("Firefox", uai.getUaFamily());
        assertEquals("Mozilla Foundation", uai.getUaCompany());
        assertEquals("Windows XP", uai.getOsName());
        assertEquals("Windows", uai.getOsFamily());
        assertEquals("Microsoft Corporation.", uai.getOsCompany());

        uai = parser.parse("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_5_8) AppleWebKit/534.50.2 (KHTML, like Gecko)");
        assertNotNull(uai);
    }

    @Test
    public void testArrayIndexBug() throws IOException {
        // should not throw exception
        UASparser p = new UASparser(OnlineUpdater.getVendoredInputStream());
        UserAgentInfo uai = p.parse("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; MyIE2; MRA 4.7 (build 01670); .NET CLR 1.1.4322)");
    }

    @Test
    public void testRobotParser() throws IOException {
        UASparser p = new UASparser(OnlineUpdater.getVendoredInputStream());
        UserAgentInfo uai = p.parse("Mozilla/5.0 (compatible; 008/0.83; http://www.80legs.com/spider.html;) Gecko/2008032620");
        assertTrue(uai.isRobot());
        assertEquals("Computational Crawling, LP", uai.getUaCompany());

        uai = p.parse("Googlebot/2.1 (+http://www.googlebot.com/bot.html)");
        assertFalse(uai.isRobot()); // not currently detected

        uai = p.parse("Pingdom.com_bot_version_1.4_(http://www.pingdom.com/)");
        assertTrue(uai.isRobot());
    }

}
