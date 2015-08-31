/*******************************************************************************
 * Copyright (c) 2015 Gerd Wütherich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Gerd Wütherich (gerd@gerd-wuetherich.de) - initial API and implementation
 ******************************************************************************/
package org.eclipse.pde.ds.annotations.test.generation;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

import java.util.List;

import org.eclipse.pde.ds.annotations.test.util.AbstractGenerateComponentDescriptionTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

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
		return testCases("src/org/eclipse/pde/ds/annotations/test/generation");
	}
}
