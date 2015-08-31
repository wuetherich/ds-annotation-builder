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
package org.eclipse.pde.ds.annotations.test.resources;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.pde.ds.annotations.test.util.AbstractDsAnnotationsTest;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class RenameSource_Test extends AbstractDsAnnotationsTest {

	@Test
	@Ignore
	public void test() throws CoreException {

		Assert.fail("TODO");
	}

	@Override
	protected SourceFile createSourceFile() {
		return new SourceFile.Default(
				"de/test/Test.java",
				"package de.test; import org.osgi.service.component.annotations.Component; @Component public class Test {}");
	}
}
