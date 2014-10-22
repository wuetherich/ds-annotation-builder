package com.wuetherich.osgi.ds.annotations.internal.preferences.fwk;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class ContentProviderPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

  /**
   * <p>
   * Creates a new instance of type {@link ContentProviderPreferencePage}.
   * </p>
   */
  public ContentProviderPreferencePage() {
    //
  }

  /**
   * <p>
   * Creates a new instance of type {@link ContentProviderPreferencePage}.
   * </p>
   * 
   * @param title
   */
  public ContentProviderPreferencePage(String title) {
    super(title);
  }

  /**
   * <p>
   * Creates a new instance of type {@link ContentProviderPreferencePage}.
   * </p>
   * 
   * @param title
   * @param image
   */
  public ContentProviderPreferencePage(String title, ImageDescriptor image) {
    super(title, image);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void init(IWorkbench workbench) {
    //
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(Composite parent) {
    return new Composite(parent, SWT.NONE);
  }
}
