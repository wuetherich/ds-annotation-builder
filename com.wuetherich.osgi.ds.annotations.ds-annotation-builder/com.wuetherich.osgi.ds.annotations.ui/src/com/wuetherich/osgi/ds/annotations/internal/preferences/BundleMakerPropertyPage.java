package com.wuetherich.osgi.ds.annotations.internal.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;

public class BundleMakerPropertyPage extends PropertyPage {

  @Override
  protected Control createContents(Composite parent) {
    return new Composite(parent, SWT.NONE);
  }
}
