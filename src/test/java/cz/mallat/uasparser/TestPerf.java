package cz.mallat.uasparser;

import org.apache.commons.lang.time.StopWatch;

public class TestPerf {

    public static void main(String[] args) throws Exception {

        new TestPerf().run();

    }

    public void run() throws Exception {

        StopWatch sw = new StopWatch();
        sw.start();

        String test = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.0.12) Gecko/2009070611 Firefox/3.0.12";

        UASparser p = new UASparser(getClass().getClassLoader().getResourceAsStream("uas.ini"));

        for (int i = 0; i < 10000; i++) {
            UserAgentInfo uai = p.parse(test);
        }


        sw.stop();
        System.err.println(sw.toString());

    }

}
