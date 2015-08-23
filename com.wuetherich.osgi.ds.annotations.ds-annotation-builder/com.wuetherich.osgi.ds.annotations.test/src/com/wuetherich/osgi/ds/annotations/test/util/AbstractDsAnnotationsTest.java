package com.wuetherich.osgi.ds.annotations.test.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringBufferInputStream;

import org.custommonkey.xmlunit.XMLUnit;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.internal.runners.statements.FailOnTimeout;

import com.wuetherich.osgi.ds.annotations.Constants;

/**
 * <p>
 * </p>
 */
public abstract class AbstractDsAnnotationsTest {

	/** - */
	private static final String DEFAULT_JDT_TEST_PROJECT_NAME = "DEFAULT-JDT-TEST-PROJECT";

	/** - */
	private IProject _project;

	{
		XMLUnit.setIgnoreWhitespace(true);
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void beforeClass() throws Exception {

		System.out.println("Setup target platform...");

		TargetPlatformUtil.setupTargetPlatform();
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @throws JavaModelException
	 * @throws CoreException
	 */
	@Before
	public void setup() throws JavaModelException, CoreException {

	    System.out.println("Setup test project: " + ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString());
		
		//
		_project = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(DEFAULT_JDT_TEST_PROJECT_NAME);

		// create PDE project
		EclipseProjectUtils.createEclipsePluginProject(_project,
				getImportedPackages());

		//
		IFolder srcFolder = _project.getFolder("src");
		createTestSourceFiles(srcFolder);
		srcFolder.refreshLocal(IResource.DEPTH_INFINITE, null);

		//
		EclipseProjectUtils.enableDsAnnotationNature(_project);

		// TODO: Synchronize
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// noop
		}

		// refresh and build
		try {
			_project.build(IncrementalProjectBuilder.CLEAN_BUILD, null);
			_project.build(IncrementalProjectBuilder.FULL_BUILD, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//
		if (failOnErrors()) {
			EclipseProjectUtils.failOnErrors(_project);
		}
	}

	protected boolean failOnErrors() {
		return true;
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @return
	 */
	public IProject getProject() {
		return _project;
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @param srcFolder
	 * @throws CoreException
	 */
	protected void createTestSourceFiles(IFolder srcFolder)
			throws CoreException {

		SourceFile sourceFile = createSourceFile();

		IPath path = new Path(sourceFile.getFileName());

		for (int i = path.segmentCount() - 1; i > 0; i--) {
			IFolder newFolder = srcFolder.getFolder(path.removeLastSegments(i));
			newFolder.create(true, true, null);
		}

		IFile file = srcFolder.getFile(path);
		file.create(new StringBufferInputStream(sourceFile.getContent()), true,
				null);
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @return
	 */
	protected abstract SourceFile createSourceFile();

	/**
	 * <p>
	 * </p>
	 */
	public static interface SourceFile {

		/**
		 * <p>
		 * </p>
		 */
		public static class Default implements SourceFile {

			/** - */
			private String _fileName;

			/** - */
			private String _content;

			/**
			 * @param _fileName
			 * @param _content
			 */
			public Default(String _fileName, String _content) {
				this._fileName = _fileName;
				this._content = _content;
			}

			@Override
			public String getFileName() {
				return _fileName;
			}

			@Override
			public String getContent() {
				return _content;
			}
		}

		/**
		 * <p>
		 * </p>
		 * 
		 * @return
		 */
		String getFileName();

		/**
		 * <p>
		 * </p>
		 * 
		 * @return
		 */
		String getContent();
	}

	protected String[] getImportedPackages() {
		return null;
	}

	protected String assertComponentDescription(String fileName)
			throws Exception {
		//
		EclipseProjectUtils.checkFileExists(getProject(),
				Constants.COMPONENT_DESCRIPTION_FOLDER + "/" + fileName);

		InputStream inputStream = getProject().getFile(
				Constants.COMPONENT_DESCRIPTION_FOLDER + "/" + fileName)
				.getContents();

		//
		return fromStream(inputStream);
	}

	public static String fromStream(InputStream in) {
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			StringBuilder out = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				out.append(line);
			}
			in.close();
			return out.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail(e.getMessage());
			return null;
		}
	}
}
