package com.wuetherich.osgi.ds.annotations.test.issue_22;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.wuetherich.osgi.ds.annotations.Constants;
import com.wuetherich.osgi.ds.annotations.test.util.AbstractDsAnnotationsTest;
import com.wuetherich.osgi.ds.annotations.test.util.EclipseProjectUtils;

public class DeleteSource_Test extends AbstractDsAnnotationsTest {

	@Test
	@Ignore
	public void test() throws CoreException {

		// Step 1: assert that file exists
		EclipseProjectUtils.checkFileExists(getProject(),
				Constants.COMPONENT_DESCRIPTION_FOLDER + "/de.test.Test.xml");

		final IResource resourceToDelete = getProject().findMember(
				"src/de/test/Test.java");
		Assert.assertTrue(resourceToDelete.exists());

		ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {

			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				resourceToDelete.delete(true, null);
			}
		}, null);

		Assert.assertNull(getProject().findMember("src/de/test/Test.java"));
		Assert.assertNull(getProject().findMember(
				Constants.COMPONENT_DESCRIPTION_FOLDER + "/de.test.Test.xml"));
	}

	@Override
	protected SourceFile createSourceFile() {
		return new SourceFile.Default(
				"de/test/Test.java",
				"package de.test; import org.osgi.service.component.annotations.Component; @Component public class Test {}");
	}
}
