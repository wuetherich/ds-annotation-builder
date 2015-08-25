package com.wuetherich.osgi.ds.annotations.test.generation;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.wuetherich.osgi.ds.annotations.test.util.AbstractGenerateComponentDescriptionTest;

@RunWith(value = Parameterized.class)
public class GenerateComponentDescriptionTest extends
		AbstractGenerateComponentDescriptionTest {

	/**
	 * @param testCase
	 */
	public GenerateComponentDescriptionTest(String testCase) {
		super(testCase);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void test() throws Throwable {

		//
		String expected = fromStream(getClass().getResourceAsStream(
				getTestCase() + ".result"));

		//
		String actual = assertComponentDescription(COMPONENT_DESCRIPTION_FILE);

		//
		try {
			assertXMLEqual(getTestCase(), expected, actual);
		} catch (Throwable e) {
			System.out.println(actual);
			throw e;
		}
	}

	@Parameters(name = "{0}")
	public static List<String[]> testCases() {
		return testCases("src/com/wuetherich/osgi/ds/annotations/test/generation");
	}
}
