package com.wuetherich.osgi.ds.annotations.test.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;
import org.eclipse.jdt.launching.environments.IExecutionEnvironmentsManager;
import org.junit.Assert;

import com.wuetherich.osgi.ds.annotations.Constants;

/**
 */
public class EclipseProjectUtils {

	/**
	 * <p>
	 * </p>
	 * 
	 * @return
	 * @throws CoreException
	 * @throws JavaModelException
	 */
	public static IJavaProject createEclipsePluginProject(IProject project,
			String... importedPackages) throws CoreException,
			JavaModelException {

		//
		EclipseProjectUtils.deleteProjectIfExists(project);

		//
		IJavaProject javaProject = JavaCore.create(project);
		Hashtable<String, String> options = JavaCore.getDefaultOptions();
		options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_7);
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_7);
		options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_7);
		javaProject.setOptions(options);
		
		final IProjectDescription projectDescription = ResourcesPlugin
				.getWorkspace().newProjectDescription(project.getName());
		projectDescription.setLocation(null);
		project.create(projectDescription, null);

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

		project.open(null);
		project.setDescription(projectDescription, null);

		// set the bin folder
		IFolder binFolder = project.getFolder("bin");
		binFolder.create(false, true, null);
		javaProject.setOutputLocation(binFolder.getFullPath(), null);

		// set the class path (src and JDK)
		List<IClasspathEntry> entries = new LinkedList<IClasspathEntry>();
		IFolder srcFolder = project.getFolder("src");
		srcFolder.create(false, true, null);
		entries.add(JavaCore.newSourceEntry(srcFolder.getFullPath()));
		IExecutionEnvironmentsManager executionEnvironmentsManager = JavaRuntime
				.getExecutionEnvironmentsManager();
		IExecutionEnvironment[] executionEnvironments = executionEnvironmentsManager
				.getExecutionEnvironments();
		for (IExecutionEnvironment iExecutionEnvironment : executionEnvironments) {
			// We will look for JavaSE-1.6 as the JRE container to add to our
			// classpath
			System.out.println(iExecutionEnvironment.getId());
			if ("JavaSE-1.6".equals(iExecutionEnvironment.getId())) {
				entries.add(JavaCore.newContainerEntry(JavaRuntime
						.newJREContainerPath(iExecutionEnvironment)));
				break;
			}
		}

		//
		entries.add(JavaCore.newContainerEntry(new Path(
				"org.eclipse.pde.core.requiredPlugins")));

		//
		javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[0]),
				null);

		//
		EclipseProjectUtils.createManifest(project, importedPackages);
		EclipseProjectUtils.createBuildProperties(project);

		javaProject.open(null);

		//
		return javaProject;
	}

	/**
	 * @param project
	 * @throws CoreException
	 */
	public static void failOnErrors(IProject project) throws CoreException {

		// check for errors
		List<IMarker> errors = new LinkedList<IMarker>();
		for (IMarker marker : project.findMarkers(null, true,
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
	 * @param project
	 * @throws CoreException
	 */
	public static void enableDsAnnotationNature(IProject project)
			throws CoreException {

		IProjectDescription description = project.getDescription();
		String[] natures = description.getNatureIds();
		String[] newNatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length] = Constants.NATURE_ID;
		description.setNatureIds(newNatures);
		project.setDescription(description, null);
	}

	/**
	 * <p>
	 * Deletes the project.
	 * </p>
	 * 
	 * 
	 * @param project
	 *            the project to delete
	 * @throws CoreException
	 */
	public static void deleteProjectIfExists(IProject project)
			throws CoreException {

		//
		if (project.isOpen()) {
			project.close(null);
		}

		// create the project if not exists
		while (project.exists()) {
			try {
				project.delete(true, true, null);
				Thread.sleep(100);
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}

		Assert.assertFalse(project.exists());
	}

	/**
	 * @param project
	 */
	public static void createBuildProperties(final IProject project) {
		final StringBuilder bpContent = new StringBuilder("source.. = src");
		bpContent.append("\n");
		bpContent.append("bin.includes = META-INF/,.\n");
		createFile("build.properties", project, bpContent.toString());
	}

	/**
	 * @param project
	 * @param importedPackages
	 * @throws CoreException
	 */
	public static void createManifest(final IProject project,
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
	 * @param projectRelativePath
	 */
	public static void checkFileExists(IProject project,
			String projectRelativePath) {

		//
		IFile file = project.getFile(new Path(projectRelativePath));

		//
		Assert.assertTrue(
				String.format("File '%s' has to exist.",
						file.getProjectRelativePath()), file.exists());
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
}
