package cz.mallat.uasparser;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jregex.Matcher;
import jregex.Pattern;

/**
 * This parser implementation is not thread-safe as it re-uses Matcher objects instead of creating
 * them on each call to parse() for a modest speedup.
 *
 * Recommended for single-threaded scenarios, such as Hadoop map/reduce jobs.
 *
 * @author chetan
 *
 */
public class SingleThreadedUASparser extends UASparser {

    protected Map<Matcher, Long> compiledBrowserMatcherMap;
    protected Map<Matcher, Long> compiledOsMatcherMap;

    public SingleThreadedUASparser(InputStream inputStreamToDefinitionFile) throws IOException {
        super(inputStreamToDefinitionFile);
    }

    public SingleThreadedUASparser(String localDefinitionFilename) throws IOException {
        super(localDefinitionFilename);
    }

    /**
     * Precompile browser regexes
     */
    @Override
    protected void preCompileBrowserRegMap() {
        compiledBrowserMatcherMap = new LinkedHashMap<Matcher, Long>(browserRegMap.size());

        for (Map.Entry<String, Long> entry : browserRegMap.entrySet()) {
            Pattern pattern = new Pattern(entry.getKey(), Pattern.IGNORE_CASE | Pattern.DOTALL);
            compiledBrowserMatcherMap.put(pattern.matcher(), entry.getValue());
        }
    }

    /**
     * Precompile OS regexes
     */
    @Override
    protected void preCompileOsRegMap() {
        compiledOsMatcherMap = new LinkedHashMap<Matcher, Long>(osRegMap.size());
        for (Map.Entry<String, Long> entry : osRegMap.entrySet()) {
            Pattern pattern = new Pattern(entry.getKey(), Pattern.IGNORE_CASE | Pattern.DOTALL);
            compiledOsMatcherMap.put(pattern.matcher(), entry.getValue());
        }
    }

    /**
     * Searches in the os regex table. if found a match copies the os data
     *
     * @param useragent
     * @param retObj
     */
    @Override
    protected void processOsRegex(String useragent, UserAgentInfo retObj) {
        Set<Entry<Matcher, Long>> osMatcherSet = getOsMatcherSet();
        for (Map.Entry<Matcher, Long> entry : osMatcherSet) {
            Matcher matcher = entry.getKey();
            matcher.setTarget(useragent);
            if (matcher.find()) {
                retObj.setOsEntry(osMap.get(entry.getValue()));
                break;
            }
        }
    }

    /**
     * Searchs in the browser regex table. if found a match copies the browser data and if possible os data
     *
     * @param useragent
     * @param retObj
     * @return
     */
    @Override
    protected boolean processBrowserRegex(String useragent, UserAgentInfo retObj) {
        Set<Entry<Matcher, Long>> browserMatcherSet = getBrowserMatcherSet();
        boolean osFound = false;
        for (Map.Entry<Matcher, Long> entry : browserMatcherSet) {
            Matcher matcher = entry.getKey();
            matcher.setTarget(useragent);
            if (matcher.find()) {
                Long idBrowser = entry.getValue();
                BrowserEntry be = browserMap.get(idBrowser);
                if (be != null) {
                    retObj.setType(browserTypeMap.get(be.getType()));;
                    if (matcher.groupCount() > 0) {
                        retObj.setBrowserVersionInfo(matcher.group(1));
                    }
                    retObj.setBrowserEntry(be);
                }
                // check if this browser has exactly one OS mapped
                Long idOs = browserOsMap.get(idBrowser);
                if (idOs != null) {
                    osFound = true;
                    retObj.setOsEntry(osMap.get(idOs));
                }
                break;
            }
        }
        return osFound;
    }

    protected Set<Entry<Matcher, Long>> getOsMatcherSet() {
        return compiledOsMatcherMap.entrySet();
    }

    protected Set<Entry<Matcher, Long>> getBrowserMatcherSet() {
        return compiledBrowserMatcherMap.entrySet();
    }

}
