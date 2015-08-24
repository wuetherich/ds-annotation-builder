package com.wuetherich.osgi.ds.annotations.test.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.pde.core.target.ITargetDefinition;
import org.eclipse.pde.core.target.ITargetHandle;
import org.eclipse.pde.core.target.ITargetLocation;
import org.eclipse.pde.core.target.ITargetPlatformService;
import org.eclipse.pde.core.target.LoadTargetDefinitionJob;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.util.tracker.ServiceTracker;

import com.wuetherich.osgi.ds.annotations.internal.Activator;

public class TargetPlatformUtil {

	public static void setupTargetPlatform() throws Exception {

		// create new target definition
		File directory = createTargetDirectory();

		if (directory.exists()) {
			return;
		} else {
			directory.mkdirs();
		}

		// fetch the ITargetPlatformService
		ServiceTracker<ITargetPlatformService, ITargetPlatformService> serviceTracker = new ServiceTracker<ITargetPlatformService, ITargetPlatformService>(
				Activator.getBundleContext(), ITargetPlatformService.class,
				null);
		serviceTracker.open();
		ITargetPlatformService tpService = serviceTracker.getService();

		// delete all existing target definitions
		ITargetHandle[] targetHandles = tpService.getTargets(null);
		for (ITargetHandle handle : targetHandles) {
			tpService.deleteTarget(handle);
		}

		//
		copyCodeSource(BundleContext.class, directory);
		copyCodeSource(Component.class, directory);

		ITargetDefinition targetDefinition = tpService.newTarget();
		targetDefinition.setName("DS_ANNOTATIONS_TEST_TARGET");
		ITargetLocation targetLocation = tpService
				.newDirectoryLocation(directory.getAbsolutePath());
		targetDefinition
				.setTargetLocations(new ITargetLocation[] { targetLocation });

		tpService.saveTargetDefinition(targetDefinition);

		targetDefinition.resolve(null);
		LoadTargetDefinitionJob job = new LoadTargetDefinitionJob(
				targetDefinition);
		IStatus status = job.run(null);
		if (status.getSeverity() == IStatus.ERROR)
			throw new CoreException(status);

		serviceTracker.close();
	}

	/**
	 * <p>
	 * 
	 * @param source
	 * @param dest
	 * @throws IOException
	 */
	public static void copyFileUsingStream(InputStream source, OutputStream dest)
			throws IOException {

		//
		try {
			byte[] buffer = new byte[1024];
			int length;
			while ((length = source.read(buffer)) > 0) {
				dest.write(buffer, 0, length);
			}
		} finally {
			source.close();
			dest.close();
		}
	}

	/**
	 * @param clazz
	 * @param directory
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void copyCodeSource(Class<?> clazz, File directory)
			throws FileNotFoundException, IOException {

		//
		URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
		String path = url.getFile().replace("\\", "/");
		String name = path.substring(path.lastIndexOf('/') + 1);
		copyFileUsingStream(url.openStream(), new FileOutputStream(new File(
				directory, name)));
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @return
	 */
	public static File createTargetDirectory() {

		//
		return new File(System.getProperty("user.dir"), "target/test_tp");
	}
}
