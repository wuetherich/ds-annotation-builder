package com.wuetherich.osgi.ds.annotations.test.generation.erroneous;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.wuetherich.osgi.ds.annotations.Constants;
import com.wuetherich.osgi.ds.annotations.test.util.AbstractGenerateComponentDescriptionTest;

@RunWith(value = Parameterized.class)
public class ErroneousComponentDescriptionTest extends
		AbstractGenerateComponentDescriptionTest {

	/**
	 * @param testCase
	 */
	public ErroneousComponentDescriptionTest(String testCase) {
		super(testCase);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void test() throws Exception {

		//
		Assert.assertNull(getProject().findMember(
				Constants.COMPONENT_DESCRIPTION_FOLDER + "/"
						+ COMPONENT_DESCRIPTION_FILE));
	}

	@Parameters
	public static List<String[]> testCases() {
		return testCases("src/com/wuetherich/osgi/ds/annotations/test/generation/erroneous");
	}
}
