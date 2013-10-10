package cz.mallat.uasparser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jregex.Matcher;

/**
 * This parser creates a Matcher set per-Thread using a ThreadLocal<Map>. It is faster than
 * the standard {@link UASparser} at the expense of greater memory usage.
 *
 * Copyright: Copyright (c) 09.10.2012 <br>
 * Company: Braintags GmbH <br>
 *
 * @author mremme
 */
public class MultithreadedUASparser extends SingleThreadedUASparser {

    private ThreadLocal<Map<Matcher, Long>> compiledBrowserMatcherMapT;
    private ThreadLocal<Map<Matcher, Long>> compiledOsMatcherMapT;
    private ThreadLocal<Map<Matcher, Long>> compiledDeviceMatcherMapT;

    public MultithreadedUASparser(InputStream inputStreamToDefinitionFile) throws IOException {
        super(inputStreamToDefinitionFile);
    }

    public MultithreadedUASparser(String localDefinitionFilename) throws IOException {
        super(localDefinitionFilename);
    }

    @Override
    protected void preCompileBrowserRegMap() {
        compiledBrowserMatcherMapT = new ThreadLocal<Map<Matcher, Long>>() {
            @Override
            protected Map<Matcher, Long> initialValue() {
                return preCompileBrowserMatcherMap();
            }
        };
    }

    @Override
    protected void preCompileOsRegMap() {
        compiledOsMatcherMapT = new ThreadLocal<Map<Matcher, Long>>() {
            @Override
            protected Map<Matcher, Long> initialValue() {
                return preCompileOsMatcherMap();
            }
        };
    }

    @Override
    protected void preCompileDeviceRegMap() {
        compiledDeviceMatcherMapT = new ThreadLocal<Map<Matcher,Long>>() {
            @Override
            protected Map<Matcher, Long> initialValue() {
                return preCompileDeviceMatcherMap();
            }
        };
    }

    @Override
    protected Set<Entry<Matcher, Long>> getOsMatcherSet() {
        return compiledOsMatcherMapT.get().entrySet();
    }

    @Override
    protected Set<Entry<Matcher, Long>> getBrowserMatcherSet() {
        return compiledBrowserMatcherMapT.get().entrySet();
    }

    @Override
    protected Set<Entry<Matcher, Long>> getDeviceMatcherSet() {
        Map<Matcher, Long> map = compiledDeviceMatcherMapT.get();
        if (map == null) {
            return null;
        }
        return map.entrySet();
    }

}

