package com.wuetherich.osgi.ds.annotations.internal.preferences;

import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class BundleMakerPreferencesConfigurationBlock extends ConfigurationBlock {

  /** the radio group */
  private RadioGroupDialogField _radioGroup;

  /**
   * <p>
   * Creates a new instance of type {@link BundleMakerPreferencesConfigurationBlock}.
   * </p>
   * 
   * @param parent
   */
  public BundleMakerPreferencesConfigurationBlock(Composite parent, AbstractPropertyAndPreferencesPage page) {
    super(parent, SWT.NONE, page);
  }

  @Override
  protected void createContent() {

    // set the layout
    this.setLayout(new FillLayout(SWT.VERTICAL));

    // create the radio group
    _radioGroup = createRadioGroup(this, "Open BM Perspective after opening a BM project:",
        new String[] { MessageDialogWithToggle.ALWAYS,
            MessageDialogWithToggle.NEVER, MessageDialogWithToggle.PROMPT },
        new Object[] { MessageDialogWithToggle.ALWAYS,
            MessageDialogWithToggle.NEVER, MessageDialogWithToggle.PROMPT }, SWT.HORIZONTAL);
  }

  @Override
  public void initialize() {

    //
    String pref = getPage().getPreferenceStore().getString(
        BundleMakerPreferenceInitializer.PREF_SWITCH_TO_PERSPECTIVE_ON_PROJECT_OPEN);

    //
    _radioGroup.setSelection(pref != null ? pref
        : MessageDialogWithToggle.PROMPT);
  }

  /**
   * <p>
   * </p>
   */
  @Override
  public void performDefaults() {

    //
    String pref = getPage().getPreferenceStore().getString(
        BundleMakerPreferenceInitializer.PREF_SWITCH_TO_PERSPECTIVE_ON_PROJECT_OPEN);

    //
    _radioGroup.setSelection(pref != null ? pref
        : MessageDialogWithToggle.PROMPT);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String[] getPreferenceKeys() {
    return new String[] { BundleMakerPreferenceInitializer.PREF_SWITCH_TO_PERSPECTIVE_ON_PROJECT_OPEN };
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  @Override
  public boolean performOk() {

    //
    getPage().getPreferenceStore().setValue(
        BundleMakerPreferenceInitializer.PREF_SWITCH_TO_PERSPECTIVE_ON_PROJECT_OPEN,
        (String) _radioGroup.getSelection());

    //
    return true;
  }
}
