package com.wuetherich.osgi.ds.annotations.internal.builder.store;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.pde.core.project.IBundleProjectDescription;

import com.wuetherich.osgi.ds.annotations.Constants;
import com.wuetherich.osgi.ds.annotations.internal.Activator;

public class UpdateManifestAndBuildPropertiesSaveParticipant {

  public void propertiesSaved(IProject project) throws CoreException {

    boolean shouldApply = false;

    IBundleProjectDescription bundleProjectDescription = Activator.getBundleProjectDescription(project);
    if (bundleProjectDescription != null) {

      //
      List<IPath> genFiles = GeneratedComponentDescriptionsStore.getGeneratedFiles(project);

      if (genFiles.size() > 0) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Iterator<IPath> iterator = genFiles.iterator(); iterator.hasNext();) {
          IPath iPath = (IPath) iterator.next();
          stringBuilder.append(iPath.makeRelativeTo(project.getFullPath()).toPortableString());
          if (iterator.hasNext()) {
            stringBuilder.append(",\n ");
          }
        }

        //
        String newHeader = stringBuilder.toString();
        newHeader = newHeader.replace(",\n ", ",");
        shouldApply = !newHeader.equals(bundleProjectDescription.getHeader(Constants.BUNDLE_HEADER_SERVICE_COMPONENT));

        //
        bundleProjectDescription.setHeader(Constants.BUNDLE_HEADER_SERVICE_COMPONENT, stringBuilder.toString());
      } else {

        //
        shouldApply = bundleProjectDescription.getHeader(Constants.BUNDLE_HEADER_SERVICE_COMPONENT) == null;
        bundleProjectDescription.setHeader(Constants.BUNDLE_HEADER_SERVICE_COMPONENT, null);
      }

      //
      IPath componentDescriptionFolder = new Path(Constants.COMPONENT_DESCRIPTION_FOLDER + "/");

      //
      IPath[] binIncludePaths = bundleProjectDescription.getBinIncludes();
      if (!contains(binIncludePaths, componentDescriptionFolder) && genFiles.size() > 0) {
        if (binIncludePaths != null) {
          IPath[] newPaths = new IPath[binIncludePaths.length + 1];
          System.arraycopy(binIncludePaths, 0, newPaths, 0, binIncludePaths.length);
          newPaths[binIncludePaths.length] = componentDescriptionFolder;
          binIncludePaths = newPaths;
        } else {
          binIncludePaths = new IPath[] { componentDescriptionFolder };
        }
      }

      //
      else if (contains(binIncludePaths, componentDescriptionFolder) && genFiles.size() == 0) {
        List<IPath> newPathList = new LinkedList<IPath>();
        for (IPath binIncludePath : binIncludePaths) {
          if (!binIncludePath.equals(componentDescriptionFolder)) {
            newPathList.add(binIncludePath);
          }
        }
        binIncludePaths = newPathList.toArray(new IPath[0]);
      }

      shouldApply = shouldApply || !Arrays.deepEquals(bundleProjectDescription.getBinIncludes(), binIncludePaths);
      bundleProjectDescription.setBinIncludes(binIncludePaths);

      if (shouldApply) {
        // bundleProjectDescription.setHeader("Meta-Persistence", "");
        bundleProjectDescription.apply(null);
      }
    }
  }

  /**
   * <p>
   * </p>
   * 
   * @param paths
   * @param path
   * @return
   */
  private static boolean contains(IPath[] paths, IPath path) {

    //
    if (paths == null) {
      return false;
    }

    //
    for (IPath iPath : paths) {
      if (iPath.equals(path)) {
        return true;
      }
    }

    //
    return false;
  }
}
