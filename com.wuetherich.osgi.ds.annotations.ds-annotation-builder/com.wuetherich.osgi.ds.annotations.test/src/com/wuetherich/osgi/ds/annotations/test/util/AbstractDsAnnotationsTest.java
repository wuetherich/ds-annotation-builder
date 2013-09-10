package com.wuetherich.osgi.ds.annotations.test.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;
import org.eclipse.jdt.launching.environments.IExecutionEnvironmentsManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import com.wuetherich.osgi.ds.annotations.internal.actions.EnableNatureAction;

/**
 * <p>
 * </p>
 */
public abstract class AbstractDsAnnotationsTest {

	/** - */
	private static final String DEFAULT_JDT_TEST_PROJECT_NAME = "DEFAULT-JDT-TEST-PROJECT";

	/** - */
	private IJavaProject _javaProject;

	/** - */
	private IProject _project;

	/**
	 * <p>
	 * </p>
	 * 
	 * @throws JavaModelException
	 * @throws CoreException
	 */
	@Before
	public void setup() throws JavaModelException, CoreException {

		//
		createTestJavaProject();

		//
		EnableNatureAction.enableNature(getProject(), true);

		// refresh and build
		_project.build(IncrementalProjectBuilder.CLEAN_BUILD, null);
		_project.build(IncrementalProjectBuilder.FULL_BUILD, null);

		// check for errors
		List<IMarker> errors = new LinkedList<IMarker>();
		for (IMarker marker : _project.findMarkers(null, true,
				IResource.DEPTH_INFINITE)) {
			if (marker.getAttribute(IMarker.SEVERITY).equals(
					IMarker.SEVERITY_ERROR)) {
				errors.add(marker);
			}
		}

		// fails if any errors
		if (!errors.isEmpty()) {

			//
			StringBuilder builder = new StringBuilder(
					"The test project has compile errors:\n");

			//
			for (IMarker iMarker : errors) {
				builder.append(" - " + iMarker.getAttribute(IMarker.MESSAGE));
				builder.append("\n");
			}

			//
			Assert.fail(builder.toString());
		}
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @throws CoreException
	 */
	@After
	public void teardown() throws CoreException {

		// //
		// _javaProject.getProject().close(null);
		// EclipseProjectUtils.deleteProjectIfExists(_javaProject.getProject()
		// .getName());
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @return
	 */
	public IJavaProject getJavaProject() {
		return _javaProject;
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @return
	 */
	public IProject getProject() {
		return _javaProject.getProject();
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
	 * 
	 * @return
	 * @throws CoreException
	 * @throws JavaModelException
	 */
	protected void createTestJavaProject() throws CoreException,
			JavaModelException {

		//
		String projectName = DEFAULT_JDT_TEST_PROJECT_NAME;

		//
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		_project = root.getProject(projectName);

		//
		if (_project.isOpen()) {
			_project.close(null);
		}
		deleteProjectIfExists(_project.getName());

		//
		_javaProject = JavaCore.create(_project);

		final IProjectDescription projectDescription = ResourcesPlugin
				.getWorkspace().newProjectDescription(projectName);
		projectDescription.setLocation(null);
		_project.create(projectDescription, null);

		projectDescription.setNatureIds(new String[] { JavaCore.NATURE_ID,
				"org.eclipse.pde.PluginNature" });

		final ICommand java = projectDescription.newCommand();
		java.setBuilderName(JavaCore.BUILDER_ID);

		final ICommand manifest = projectDescription.newCommand();
		manifest.setBuilderName("org.eclipse.pde.ManifestBuilder");

		final ICommand schema = projectDescription.newCommand();
		schema.setBuilderName("org.eclipse.pde.SchemaBuilder");

		projectDescription
				.setBuildSpec(new ICommand[] { java, manifest, schema });

		_project.open(null);
		_project.setDescription(projectDescription, null);

		// set the bin folder
		IFolder binFolder = _project.getFolder("bin");
		binFolder.create(false, true, null);
		_javaProject.setOutputLocation(binFolder.getFullPath(), null);

		// set the class path (src and JDK)
		List<IClasspathEntry> entries = new LinkedList<IClasspathEntry>();
		IFolder srcFolder = _project.getFolder("src");
		srcFolder.create(false, true, null);
		entries.add(JavaCore.newSourceEntry(srcFolder.getFullPath()));
		IExecutionEnvironmentsManager executionEnvironmentsManager = JavaRuntime
				.getExecutionEnvironmentsManager();
		IExecutionEnvironment[] executionEnvironments = executionEnvironmentsManager
				.getExecutionEnvironments();
		for (IExecutionEnvironment iExecutionEnvironment : executionEnvironments) {
			// We will look for JavaSE-1.6 as the JRE container to add to our
			// classpath
			if ("JavaSE-1.6".equals(iExecutionEnvironment.getId())) {
				entries.add(JavaCore.newContainerEntry(JavaRuntime
						.newJREContainerPath(iExecutionEnvironment)));
				break;
			}
		}

		entries.add(JavaCore
				.newContainerEntry(new Path(
						"org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/J2SE-1.5")));

		entries.add(JavaCore.newContainerEntry(new Path(
				"org.eclipse.pde.core.requiredPlugins")));

		//
		_javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[0]),
				null);

		//
		createTestSourceFiles(srcFolder);
		_project.getFolder("src").refreshLocal(IResource.DEPTH_INFINITE, null);

		createManifest(_project, getImportedPackages());
		createBuildProps(_project);
	}

	protected String[] getImportedPackages() {
		return null;
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @param projectRelativePath
	 */
	protected void checkFileExists(String projectRelativePath) {

		//
		IFile file = getProject().getFile(new Path(projectRelativePath));

		//
		Assert.assertTrue(
				String.format("File '%s' has to exist.",
						file.getProjectRelativePath()), file.exists());
	}

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

	private static void createBuildProps(final IProject project) {
		final StringBuilder bpContent = new StringBuilder("source.. = src");
		bpContent.append("\n");
		bpContent.append("bin.includes = META-INF/,.\n");
		createFile("build.properties", project, bpContent.toString());
	}

	private static void createManifest(final IProject project,
			final String... importedPackages) throws CoreException {

		final StringBuilder maniContent = new StringBuilder(
				"Manifest-Version: 1.0\n");

		maniContent.append("Bundle-ManifestVersion: 2\n");
		maniContent.append("Bundle-Name: " + project.getName() + "\n");
		maniContent.append("Bundle-SymbolicName: " + project.getName()
				+ "; singleton:=true\n");
		maniContent.append("Bundle-Version: 1.0.0\n");

		if (importedPackages != null && importedPackages.length != 0) {
			maniContent.append("Import-Package: " + importedPackages[0]);
			for (int i = 1, x = importedPackages.length; i < x; i++) {
				maniContent.append(",\n " + importedPackages[i]);
			}
			maniContent.append("\n");
		}

		maniContent.append("Bundle-RequiredExecutionEnvironment: J2SE-1.5\r\n");

		final IFolder metaInf = project.getFolder("META-INF");
		metaInf.create(false, true, null);
		createFile("MANIFEST.MF", metaInf, maniContent.toString());
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @param name
	 * @param container
	 * @param content
	 * @return
	 */
	private static IFile createFile(final String name,
			final IContainer container, final String content) {
		
		final IFile file = container.getFile(new Path(name));
		assertExist(file.getParent());
		try {
			final InputStream stream = new ByteArrayInputStream(
					content.getBytes(file.getCharset()));
			if (file.exists()) {
				file.setContents(stream, true, true, null);
			} else {
				file.create(stream, true, null);
			}
			stream.close();
		} catch (final Exception e) {
			// TODO
			e.printStackTrace();
		}

		return file;
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @param c
	 */
	private static void assertExist(final IContainer c) {

		if (!c.exists()) {
			if (!c.getParent().exists()) {
				assertExist(c.getParent());
			}
			if (c instanceof IFolder) {
				try {
					((IFolder) c)
							.create(false, true, new NullProgressMonitor());
				} catch (final CoreException e) {
					// TODO
					e.printStackTrace();
				}
			}

		}

	}

	/**
	 * <p>
	 * Deletes the project with the given name.
	 * </p>
	 * 
	 * 
	 * @param projectName
	 *            the name of the project to delete
	 * @throws CoreException
	 */
	private static void deleteProjectIfExists(String projectName)
			throws CoreException {

		// get the project
		IProject project = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(projectName);

		// create the project if not exists
		if (project.exists()) {
			try {
				project.delete(true, true, null);
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
	}
}
