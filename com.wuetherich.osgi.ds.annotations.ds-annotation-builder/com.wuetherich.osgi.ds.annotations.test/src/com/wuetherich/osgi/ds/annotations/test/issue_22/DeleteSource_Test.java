package com.wuetherich.osgi.ds.annotations.test.issue_22;

import java.util.concurrent.Callable;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

import com.jayway.awaitility.Awaitility;
import com.wuetherich.osgi.ds.annotations.Constants;
import com.wuetherich.osgi.ds.annotations.test.util.AbstractDsAnnotationsTest;

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
