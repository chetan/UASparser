package cz.mallat.uasparser;

import java.io.IOException;

import org.junit.Test;

import cz.mallat.uasparser.fileparser.Entry;
import cz.mallat.uasparser.fileparser.PHPFileParser;
import cz.mallat.uasparser.fileparser.Section;

/**
 * No real JUnit tests
 * 
 * @author oli
 * 
 */
public class TestPHPFileParser {

	@Test
	public void runAll() throws IOException {
		long time = System.currentTimeMillis();
		PHPFileParser fp = new PHPFileParser(PHPFileParser.class.getResourceAsStream("/data.txt"));
		System.out.println("parsing time : " + (System.currentTimeMillis() - time));
		for (Section sec : fp.getSections()) {
			System.out.println("[" + sec.getName() + "]");
			for (Entry e : sec.getEntries()) {
				for (String s : e.getData()) {
					System.out.println(e.getKey() + "[] = \"" + s + "\"");
				}
			}
		}
	}
}
