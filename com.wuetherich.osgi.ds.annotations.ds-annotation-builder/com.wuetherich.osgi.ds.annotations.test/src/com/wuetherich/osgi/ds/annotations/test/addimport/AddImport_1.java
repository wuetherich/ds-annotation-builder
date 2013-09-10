package com.wuetherich.osgi.ds.annotations.test.addimport;

import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

import com.wuetherich.osgi.ds.annotations.internal.Constants;
import com.wuetherich.osgi.ds.annotations.test.util.AbstractDsAnnotationsTest;

public class AddImport_1 extends AbstractDsAnnotationsTest {

	@Test
	public void test() throws CoreException {

		//
		checkFileExists(Constants.COMPONENT_DESCRIPTION_FOLDER
				+ "/de.test.Test.xml");
	}

	@Override
	protected SourceFile createSourceFile() {
		return new SourceFile.Default(
				"de/test/Test.java",
				"package de.test; import org.osgi.service.component.annotations.Component; @Component public class Test {}");
	}
}
