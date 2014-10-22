/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.wuetherich.osgi.ds.annotations.internal.preferences.fwk;

/**
 * Change listener used by <code>DialogField</code>
 * 
 * <p>
 * This source was copied (and than modified) from the internal class
 * {@link org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener}.
 * </p>
 */
public interface IDialogFieldListener {

  /**
   * The dialog field has changed.
   * 
   * @param field
   *          the dialog field that changed
   */
  void dialogFieldChanged(DialogField field);

}
