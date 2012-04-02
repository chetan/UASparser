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
        UASparser p = new UASparser(getClass().getClassLoader().getResourceAsStream("uas.ini"));
        UserAgentInfo uai = p.parse("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.0.12) Gecko/2009070611 Firefox/3.0.12");

        // type:Browser
        // ua_name:Firefox 3.0.12
        // ua_family:Firefox
        // ua_producer:Mozilla Foundation
        // os_name:Windows XP
        // os_family:Windows
        // os_producer:Microsoft Corporation.

        assertEquals("Browser", uai.getTyp());
        assertEquals("Firefox 3.0.12", uai.getUaName());
        assertEquals("Firefox", uai.getUaFamily());
        assertEquals("Mozilla Foundation", uai.getUaCompany());
        assertEquals("Windows XP", uai.getOsName());
        assertEquals("Windows", uai.getOsFamily());
        assertEquals("Microsoft Corporation.", uai.getOsCompany());
    }

    @Test
    public void runOnlineUAParser() throws IOException {
        UASparser p = new OnlineUpdateUASparser();
        UserAgentInfo uai = p.parse("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.0.12) Gecko/2009070611 Firefox/3.0.12");
        assertEquals("Browser", uai.getTyp());
        assertEquals("Firefox 3.0.12", uai.getUaName());
        assertEquals("Firefox", uai.getUaFamily());
        assertEquals("Mozilla Foundation", uai.getUaCompany());
        assertEquals("Windows XP", uai.getOsName());
        assertEquals("Windows", uai.getOsFamily());
        assertEquals("Microsoft Corporation.", uai.getOsCompany());
    }

    @Test
    public void runCachedOnlineUAParser() throws IOException {
        UASparser p = new CachingOnlineUpdateUASparser();
        UserAgentInfo uai = p.parse("Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.0.12) Gecko/2009070611 Firefox/3.0.12");
        assertEquals("Browser", uai.getTyp());
        assertEquals("Firefox 3.0.12", uai.getUaName());
        assertEquals("Firefox", uai.getUaFamily());
        assertEquals("Mozilla Foundation", uai.getUaCompany());
        assertEquals("Windows XP", uai.getOsName());
        assertEquals("Windows", uai.getOsFamily());
        assertEquals("Microsoft Corporation.", uai.getOsCompany());
    }

}
