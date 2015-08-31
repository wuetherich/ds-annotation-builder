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
package org.eclipse.pde.ds.annotations.test.issues_26;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

import java.io.InputStream;
import java.util.concurrent.Callable;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.pde.ds.annotations.Constants;
import org.eclipse.pde.ds.annotations.test.util.AbstractDsAnnotationsTest;
import org.eclipse.pde.ds.annotations.test.util.EclipseProjectUtils;
import org.junit.Test;

import com.jayway.awaitility.Awaitility;

public class Issue26_UpdateResourceTest extends AbstractDsAnnotationsTest {

	@Test
	public void test() throws Exception {

		//
		EclipseProjectUtils.checkFileExists(getProject(),
				Constants.COMPONENT_DESCRIPTION_FOLDER + "/de.test.Test.xml");

		//
		String expected_before = fromStream(getClass().getResourceAsStream(
				this.getClass().getSimpleName() + "_Before.xml"));
		//
		String actual_before = fromStream(getProject().getFile(
				Constants.COMPONENT_DESCRIPTION_FOLDER + "/de.test.Test.xml")
				.getContents());

		//
		// System.out.println("Actual  : " + actual_before);
		// System.out.println("Expected: " + expected_before);
		assertXMLEqual(expected_before, actual_before);

		IFile javaSourcefile = getProject().getFile("src/de/test/Test.java");
		InputStream sourceInput = getClass().getResourceAsStream(
				this.getClass().getSimpleName() + ".input");
		javaSourcefile.setContents(sourceInput, true, false, null);

		//
		final String expected_after = fromStream(getClass()
				.getResourceAsStream(
						this.getClass().getSimpleName() + "_After.xml"));

		//
		Awaitility.await().until(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				//
				IFile file = getProject().getFile(
						Constants.COMPONENT_DESCRIPTION_FOLDER
								+ "/de.test.Test.xml");
				
				if (!file.exists()) {
				  return false;
				}

				file.refreshLocal(IResource.DEPTH_INFINITE, null);
				String actual_after = fromStream(file.getContents());

				//
				return expected_after.equals(actual_after);
			}
		});
	}

	@Override
	protected SourceFile createSourceFile() {
		return new SourceFile.Default(
				"de/test/Test.java",
				"package de.test; import org.osgi.service.component.annotations.Component; @Component public class Test {}");
	}

}
