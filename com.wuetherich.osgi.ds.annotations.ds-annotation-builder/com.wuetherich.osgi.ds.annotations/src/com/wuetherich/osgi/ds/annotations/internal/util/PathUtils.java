package com.wuetherich.osgi.ds.annotations.internal.util;

import org.eclipse.core.runtime.IPath;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class PathUtils {

  /**
   * <p>
   * </p>
   * 
   * @param paths
   * @param path
   * @return
   */
  public static boolean contains(IPath[] paths, IPath path) {

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
