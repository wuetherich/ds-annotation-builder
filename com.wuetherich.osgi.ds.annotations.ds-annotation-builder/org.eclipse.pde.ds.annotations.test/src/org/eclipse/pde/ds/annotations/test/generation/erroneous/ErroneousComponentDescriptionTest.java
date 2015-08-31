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
package org.eclipse.pde.ds.annotations.test.generation.erroneous;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.pde.ds.annotations.test.util.AbstractGenerateComponentDescriptionTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

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

		// check for errors
		List<IMarker> errors = new LinkedList<IMarker>();
		for (IMarker marker : getProject().findMarkers(null, true,
				IResource.DEPTH_INFINITE)) {
			if (marker.getAttribute(IMarker.SEVERITY).equals(
					IMarker.SEVERITY_ERROR)) {
				errors.add(marker);
			}
		}

		Assert.assertTrue(getTestCase(), !errors.isEmpty());
		System.out.println("~ ~ ~ ~ ~ ~ ~ ~");
		for (IMarker err : errors) {
			System.out.println(err.getAttribute("message"));
		}
		Assert.assertEquals(getTestCase(), 1, errors.size());

		//
		String expectedMessage = fromStream(getClass().getResourceAsStream(
				getTestCase() + ".result"));

		Assert.assertEquals(getTestCase(), expectedMessage.trim(), errors
				.get(0).getAttribute("message"));
	}

	@Parameters
	public static List<String[]> testCases() {

		return testCases("src/org/eclipse/pde/ds/annotations/test/generation/erroneous");
	}

	/**
	 * @see org.eclipse.pde.ds.annotations.test.util.AbstractDsAnnotationsTest#failOnErrors()
	 */
	protected boolean failOnErrors() {
		return false;
	}
}
