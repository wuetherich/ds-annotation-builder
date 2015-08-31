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
package org.eclipse.pde.ds.annotations.test.issue_8;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.pde.ds.annotations.Constants;
import org.eclipse.pde.ds.annotations.test.util.AbstractDsAnnotationsTest;
import org.eclipse.pde.ds.annotations.test.util.EclipseProjectUtils;
import org.junit.Test;

public class AddImport_2_Test extends AbstractDsAnnotationsTest {

	@Test
	public void test() throws CoreException {

		//
		EclipseProjectUtils.checkFileExists(getProject(), Constants.COMPONENT_DESCRIPTION_FOLDER
				+ "/de.test.Test.xml");
	}

	@Override
	protected SourceFile createSourceFile() {
		return new SourceFile.Default(
				"de/test/Test.java",
				"package de.test; import org.osgi.service.component.annotations.Component; @Component public class Test {}");
	}

	@Override
	protected String[] getImportedPackages() {
		return new String[] { "org.osgi.framework" };
	}
}
