package com.wuetherich.osgi.ds.annotations.builder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

import com.wuetherich.osgi.ds.annotations.Constants;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class GeneratedComponentDescriptionsStore {

	/**
	 * <p>
	 * </p>
	 * 
	 * @param project
	 * @return
	 * @throws CoreException
	 */
	public static final void storeGeneratedFile(IProject project,
			IPath componentDescription, IPath resourcePath)
			throws CoreException {

		try {

			//
			Properties properties = loadProperties(project);

			//
			properties.setProperty(componentDescription.toOSString(),
					resourcePath.toOSString());

			//
			saveProperties(project, properties);

		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR,
					Constants.BUNDLE_ID, e.getMessage(), e));
		}
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @return
	 * @throws CoreException
	 */
	public static List<IPath> getGeneratedFiles(IProject project, IPath resource)
			throws CoreException {

		//
		List<IPath> result = new LinkedList<IPath>();

		//
		Properties properties = loadProperties(project);

		//
		for (Entry<Object, Object> entry : properties.entrySet()) {

			//
			if (resource.toOSString().equals(entry.getValue())) {

				//
				result.add(new Path((String) entry.getKey()));

			}
		}

		//
		return result;
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @param project
	 * @return
	 * @throws CoreException
	 */
	public static List<IPath> getGeneratedFiles(IProject project)
			throws CoreException {

		//
		List<IPath> result = new LinkedList<IPath>();

		//
		Properties properties = loadProperties(project);

		//
		for (Entry<Object, Object> entry : properties.entrySet()) {

			//
			result.add(new Path((String) entry.getKey()));
		}

		//
		return result;
	}

	/**
	 * @param project
	 * @return
	 * @throws CoreException
	 */
	private static Properties loadProperties(IProject project)
			throws CoreException {
		try {
			File file = getFile(project);
			Properties properties = new Properties();
			properties.load(new FileInputStream(file));
			return properties;
		} catch (FileNotFoundException e) {
			throw new CoreException(new Status(IStatus.ERROR,
					Constants.BUNDLE_ID, e.getMessage(), e));
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR,
					Constants.BUNDLE_ID, e.getMessage(), e));
		}
	}

	/**
	 * @param project
	 * @param properties
	 * @throws CoreException
	 */
	private static void saveProperties(IProject project, Properties properties)
			throws CoreException {

		try {
			File file = getFile(project);
			properties.store(new FileOutputStream(file), null);
		} catch (FileNotFoundException e) {
			throw new CoreException(new Status(IStatus.ERROR,
					Constants.BUNDLE_ID, e.getMessage(), e));
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR,
					Constants.BUNDLE_ID, e.getMessage(), e));
		}
	}

	/**
	 * @param project
	 * @return
	 * @throws IOException
	 */
	private static File getFile(IProject project) throws IOException {

		IPath path = project.getWorkingLocation(Constants.BUNDLE_ID);
		IPath iPath = path.append("generatedFiles");
		File file = iPath.toFile();
		if (!file.exists()) {
			file.createNewFile();
		}

		return file;
	}
}
