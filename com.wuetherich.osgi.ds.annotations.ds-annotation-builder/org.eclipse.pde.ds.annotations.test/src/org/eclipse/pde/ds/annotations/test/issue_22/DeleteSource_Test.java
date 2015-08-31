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
package org.eclipse.pde.ds.annotations.test.issue_22;

import java.util.concurrent.Callable;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.pde.ds.annotations.Constants;
import org.eclipse.pde.ds.annotations.test.util.AbstractDsAnnotationsTest;
import org.junit.Test;

import com.jayway.awaitility.Awaitility;

public class DeleteSource_Test extends AbstractDsAnnotationsTest {

	@Test
	public void test() throws CoreException {

		assertResource("src/de/test/Test.java");
		assertResource(Constants.COMPONENT_DESCRIPTION_FOLDER
				+ "/de.test.Test.xml");

		getProject().findMember("src/de/test/Test.java").delete(true, null);

		assertNoResource("src/de/test/Test.java");
		assertNoResource(Constants.COMPONENT_DESCRIPTION_FOLDER
				+ "/de.test.Test.xml");
	}

	/**
	 * <p>
	 * </p>
	 */
	protected void assertResource(final String resourcePath) {
		Awaitility.await().until(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				IResource resource = getProject().findMember(resourcePath);
				return resource != null && resource.exists();
			}
		});
	}

	/**
	 * <p>
	 * </p>
	 */
	protected void assertNoResource(final String resourcePath) {
		Awaitility.await().until(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return getProject().findMember(resourcePath) == null;
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
