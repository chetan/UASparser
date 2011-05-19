package cz.mallat.uasparser;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import jregex.Matcher;
import jregex.Pattern;

/**
 * A {@link UASparser} which is only concerned with returning the browser 
 * family string as quickly as possible. Uses the JRegex library for further
 * speedups.
 * 
 * You can optionally ignore unwanted browsers by passing in a list of 
 * browsers which you are interested in.
 * 
 * @author chetan
 *
 */
public class BrowserFamilyParser extends UASparser {

	protected Map<Pattern, Long> compiledBrowserRegMap;
	protected Map<Pattern, Long> compiledOsRegMap;

	public static final String UNKNOWN = "unknown";

	protected Map<String, Integer> browsers;	

	public BrowserFamilyParser(InputStream inputStreamToDefinitionFile) throws IOException {
		super(inputStreamToDefinitionFile);
	}
	
	public BrowserFamilyParser(InputStream inputStreamToDefinitionFile, String[] browsers) throws IOException {
		super(inputStreamToDefinitionFile);
		setBrowsers(browsers);
	}

	public BrowserFamilyParser(String localDefinitionFilename) throws IOException {
		super(localDefinitionFilename);
	}

	/**
	 * Creates a parser which can directly return the Browser Family string
	 * 
	 * @param localDefinitionFilename
	 * @param browsers
	 *            Only the browsers included in this list will be tested for
	 * @throws IOException
	 */
	public BrowserFamilyParser(String localDefinitionFilename, String[] browsers)
			throws IOException {
		super(localDefinitionFilename);
		setBrowsers(browsers);
	}

	private void setBrowsers(String[] browsers) {
		this.browsers = new HashMap<String, Integer>();
		for (String b : browsers) {
			this.browsers.put(b, 1);
		}
		preCompileRegExes(); // recompile
	}

	public String parseBrowserFamily(String userAgent) {
		for (Map.Entry<Pattern, Long> entry : compiledBrowserRegMap.entrySet()) {
			Matcher matcher = entry.getKey().matcher(userAgent);
			if (matcher.find()) {
				Long idBrowser = entry.getValue();
				BrowserEntry be = browserMap.get(idBrowser);
				if (be != null) {
					return be.getFamily();
				}
				return UNKNOWN;
			}
		}
		return UNKNOWN;
	}

	@Override
	protected void preCompileRegExes() {
		preCompileBrowserRegMap();
		preCompileOsRegMap();
	}

	/**
	 * Precompile browser regexes
	 */
	protected void preCompileBrowserRegMap() {
		compiledBrowserRegMap = new LinkedHashMap<Pattern, Long>(browserRegMap.size());
		for (Map.Entry<String, Long> entry : browserRegMap.entrySet()) {
			if (browsers != null
					&& !browsers.containsKey(browserMap.get(entry.getValue()).getFamily())) {
				continue;
			}
			Pattern pattern = new Pattern(entry.getKey(), Pattern.IGNORE_CASE | Pattern.DOTALL);
			compiledBrowserRegMap.put(pattern, entry.getValue());
		}
	}

}
