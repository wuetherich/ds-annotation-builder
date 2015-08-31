/*******************************************************************************
 * Copyright (c) 2015 Gerd Wütherich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Gerd Wütherich (gerd@gerd-wuetherich.de) - initial API and implementation
 ******************************************************************************/
package org.eclipse.pde.ds.annotations.internal.util;

import org.eclipse.core.runtime.IPath;

/**
 * <p>
 * Helper class.
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class PathUtils {

  /**
   * <p>
   * Returns <code>true</code> if the specified array of paths contains the specified path.
   * </p>
   * 
   * @param paths
   *          the array of paths
   * @param path
   *          the path that should be contained
   * @return <code>true</code> if the specified array of paths contains the specified path, <code>false</code>
   *         otherwise.
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
