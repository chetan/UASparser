package cz.mallat.uasparser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Performance tests
 *
 * Copyright: Copyright (c) 09.10.2012 <br>
 * Company: Braintags GmbH <br>
 *
 * @author mremme
 */
public class Benchmark {

    public static InputStream getIni() {
        return OnlineUpdater.getVendoredInputStream();
    }

    public static void main(String[] args) {

        try {
            List<UASparser> parserList = new ArrayList<UASparser>();
            parserList.add(new UASparser(getIni()));
            parserList.add(new SingleThreadedUASparser(getIni()));
            parserList.add(new MultithreadedUASparser(getIni()));
            //parserList.add(new OnlineUpdateUASparser());

            List<String> uaList = new ArrayList<String>();
            uaList.add("user-agent: Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.7.12) Gecko/20050922 Fedora/1.0.7-1.1.fc3 Firefox/1.0.7");
            uaList.add("user-agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; .NET CLR 1.1.4322)");
            uaList.add("WhatWeb/0.4.7");
            uaList.add("check_http/v1.4.16 (nagios-plugins 1.4.16)");
            uaList.add("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)");

            List<String> expectedTypes = new ArrayList<String>();
            expectedTypes.add("Browser");
            expectedTypes.add("Browser");
            expectedTypes.add("unknown");
            expectedTypes.add("Other");
            expectedTypes.add("Robot");

            for (UASparser uaParser : parserList) {
                performTest(uaParser, uaList);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static final void performTest(UASparser uaParser, List<String> uaList) throws IOException {
        long startTime = System.currentTimeMillis();

        int i = 0;

        while (i++ < 5000) {
            for (String tmpString : uaList) {
                UserAgentInfo info = uaParser.parse(tmpString);
                //System.out.println("getType: " + info.getType());
            }
        }
        long newTime = System.currentTimeMillis() - startTime;
        System.out.println(uaParser.getClass().getSimpleName() + ": " + newTime);

    }

    private static final void checkThreadSafe(final UASparser uaParser, final List<String> uaList,
            final List<String> expectedType, final int threadCount, final int runs) {

        List<Runnable> threads = new ArrayList<Runnable>();
        for (int i = 0; i < threadCount; i++) {
            Runnable runnable = new Runnable() {

                @Override
                public void run() {
                    int r = 0;
                    while (r++ < runs) {
                        for (int k = 0; k < uaList.size(); k++) {
                            String uaString = uaList.get(k);
                            String expected = expectedType.get(k);
                            try {
                                UserAgentInfo info = uaParser.parse(uaString);
                                if (!info.getType().equals(expected))
                                    throw new IllegalArgumentException("not expected: " + info.getType() + " / " + expected);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    System.out.println("finished Thread " + Thread.currentThread().getName());
                }
            };
            threads.add(runnable);
        }

        int i = 0;
        for (Runnable runnable : threads) {
            new Thread(runnable, "Thread " + i++).start();
        }
    }


}
