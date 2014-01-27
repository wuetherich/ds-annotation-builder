package com.wuetherich.osgi.ds.annotations.test.resources;

import org.eclipse.core.runtime.CoreException;
import org.junit.Assert;
import org.junit.Test;

import com.wuetherich.osgi.ds.annotations.test.util.AbstractDsAnnotationsTest;

public class RenameSource_Test extends AbstractDsAnnotationsTest {

	@Test
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
