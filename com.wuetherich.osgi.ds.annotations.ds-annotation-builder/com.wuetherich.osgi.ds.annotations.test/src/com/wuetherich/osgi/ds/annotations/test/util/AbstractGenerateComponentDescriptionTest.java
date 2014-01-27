package com.wuetherich.osgi.ds.annotations.test.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.LinkedList;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(value = Parameterized.class)
public abstract class AbstractGenerateComponentDescriptionTest extends
		AbstractDsAnnotationsTest {

	/** - */
	public static final String COMPONENT_DESCRIPTION_FILE = "de.test.Test.xml";

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
	public AbstractGenerateComponentDescriptionTest(String testCase) {
		this._testCase = testCase;
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @return
	 */
	public String getTestCase() {
		return _testCase;
	}

	/**
	 * @see com.wuetherich.osgi.ds.annotations.test.util.AbstractDsAnnotationsTest#createSourceFile()
	 */
	@Override
	protected SourceFile createSourceFile() {
		return new SourceFile.Default(JAVA_TEST_FILE, fromStream(getClass()
				.getResourceAsStream(_testCase + ".input")));
	}

	public static List<String[]> testCases(String path) {

		List<String[]> result = new LinkedList<String[]>();

		File file = new File(System.getProperty("user.dir"), path);
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
