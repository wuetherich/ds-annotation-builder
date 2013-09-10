package com.wuetherich.osgi.ds.annotations.test.generation;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.wuetherich.osgi.ds.annotations.test.util.AbstractDsAnnotationsTest;

@RunWith(value = Parameterized.class)
public class GenerateComponentDescriptionTest extends AbstractDsAnnotationsTest {

	/** - */
	private static final String COMPONENT_DESCRIPTION_FILE = "de.test.Test.xml";

	/** - */
	private static final String JAVA_TEST_FILE = "de/test/Test.java";

	/** - */
	private String _testCase;

	/**
	 * <p>
	 * </p>
	 * 
	 * @param testCase
	 */
	public GenerateComponentDescriptionTest(String testCase) {
		this._testCase = testCase;
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void test() throws Exception {

		//
		String expected = fromStream(getClass().getResourceAsStream(
				_testCase + ".result"));

		//
		String actual = assertComponentDescription(COMPONENT_DESCRIPTION_FILE);

		//
		assertXMLEqual(expected, actual);
	}

	/**
	 * @see com.wuetherich.osgi.ds.annotations.test.util.AbstractDsAnnotationsTest#createSourceFile()
	 */
	@Override
	protected SourceFile createSourceFile() {
		return new SourceFile.Default(JAVA_TEST_FILE, fromStream(getClass()
				.getResourceAsStream(_testCase + ".input")));
	}

	@Parameters
	public static List<String[]> testCases() {

		//
		URL url = GenerateComponentDescriptionTest.class.getProtectionDomain()
				.getCodeSource().getLocation();

		List<String[]> result = new LinkedList<String[]>();

		File file = new File(System.getProperty("user.dir"), "src/com/wuetherich/osgi/ds/annotations/test/generation");
		File[] files = file.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".input");
			}
		});

		//
		for (int i = 0; i < files.length; i++) {
			result.add(new String[] { files[i].getName().substring(0,
					files[i].getName().length() - ".input".length()) });
		}

		//
		return result;
	}
}
