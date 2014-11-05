package com.wuetherich.osgi.ds.annotations.internal.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.wuetherich.osgi.ds.annotations.Constants;
import com.wuetherich.osgi.ds.annotations.DsAnnotationVersion;
import com.wuetherich.osgi.ds.annotations.internal.preferences.fwk.AbstractPropertyAndPreferencesPage;
import com.wuetherich.osgi.ds.annotations.internal.preferences.fwk.ConfigurationBlock;

public class DsAnnotationsPropertyAndPreferenceConfigurationBlock extends ConfigurationBlock {

  /** - */
  private Button _button_1_0;

  /** - */
  private Button _button_1_1;

  /** - */
  private Button _button_1_2;

  /** - */
  private Button _button_markAsDerived;

  /**
   * <p>
   * Creates a new instance of type {@link DsAnnotationsPropertyAndPreferenceConfigurationBlock}.
   * </p>
   * 
   * @param parent
   * @param page
   */
  public DsAnnotationsPropertyAndPreferenceConfigurationBlock(Composite parent, AbstractPropertyAndPreferencesPage page) {
    super(parent, SWT.NONE, page);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createContent() {

    //
    GridLayout layout = new GridLayout(1, false);
    setLayout(layout);

    // Create the first group
    Group group1 = new Group(this, SWT.SHADOW_IN);
    group1.setText("Service Component Version");
    group1.setLayout(createRowLayout());

    GridData data = new GridData(SWT.FILL, SWT.TOP, true, false);
    group1.setLayoutData(data);

    _button_1_0 = new Button(group1, SWT.RADIO);
    _button_1_0.setText("1.1 (OSGi Release 4.0/4.1)");

    _button_1_1 = new Button(group1, SWT.RADIO);
    _button_1_1.setText("1.1 (OSGi Release 4.2)");

    _button_1_2 = new Button(group1, SWT.RADIO);
    _button_1_2.setText("1.2 (OSGi Release 4.3+)");

    _button_markAsDerived = new Button(this, SWT.CHECK);
    _button_markAsDerived.setText("Mark generated component descriptors as derived");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void initialize() {

    //
    switch (getDsAnnotationVersion(false)) {
    case V_1_0: {
      _button_1_0.setSelection(true);
      break;
    }
    case V_1_1: {
      _button_1_1.setSelection(true);
      break;
    }
    case V_1_2: {
      _button_1_2.setSelection(true);
      break;
    }
    default:
      break;
    }

    _button_markAsDerived.setSelection(getMarkGeneratedDescriptorsAsDerived(false));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void performDefaults() {

    //
    switch (getDsAnnotationVersion(true)) {
    case V_1_0: {
      _button_1_0.setSelection(true);
      break;
    }
    case V_1_1: {
      _button_1_1.setSelection(true);
      break;
    }
    case V_1_2: {
      _button_1_2.setSelection(true);
      break;
    }
    default:
      break;
    }

    //
    _button_markAsDerived.setSelection(getMarkGeneratedDescriptorsAsDerived(true));
  }

  @Override
  protected String[] getPreferenceKeys() {
    return new String[] { Constants.PREF_DS_VERSION, Constants.PREF_MARK_COMPONENT_DESCRIPTOR_AS_DERIVED };
  }

  @Override
  public boolean performOk() {

    //
    DsAnnotationVersion dsAnnotationVersion = DsAnnotationVersion.V_1_2;

    if (_button_1_0.getSelection()) {
      dsAnnotationVersion = DsAnnotationVersion.V_1_0;
    } else if (_button_1_1.getSelection()) {
      dsAnnotationVersion = DsAnnotationVersion.V_1_1;
    } else if (_button_1_2.getSelection()) {
      dsAnnotationVersion = DsAnnotationVersion.V_1_2;
    }

    getPage().getPreferenceStore().putValue(Constants.PREF_DS_VERSION, dsAnnotationVersion.name());

    //
    getPage().getPreferenceStore().putValue(Constants.PREF_MARK_COMPONENT_DESCRIPTOR_AS_DERIVED,
        Boolean.toString(_button_markAsDerived.getSelection()));

    //
    return true;
  }

  private DsAnnotationVersion getDsAnnotationVersion(boolean getDefault) {

    String version = getDefault ? getPage().getPreferenceStore().getDefaultString(Constants.PREF_DS_VERSION)
        : getPage().getPreferenceStore().getString(Constants.PREF_DS_VERSION);

    DsAnnotationVersion annotationVersion = DsAnnotationVersion.valueOf(version);

    return annotationVersion;
  }

  private boolean getMarkGeneratedDescriptorsAsDerived(boolean getDefault) {

    return getDefault ? getPage().getPreferenceStore().getDefaultBoolean(
        Constants.PREF_MARK_COMPONENT_DESCRIPTOR_AS_DERIVED) : getPage().getPreferenceStore().getBoolean(
        Constants.PREF_MARK_COMPONENT_DESCRIPTOR_AS_DERIVED);
  }

  private static RowLayout createRowLayout() {
    RowLayout layout = new RowLayout(SWT.VERTICAL);
    layout.spacing = 10;
    layout.marginTop = 10;
    layout.marginBottom = 10;
    layout.fill = true;
    return layout;
  }
}
