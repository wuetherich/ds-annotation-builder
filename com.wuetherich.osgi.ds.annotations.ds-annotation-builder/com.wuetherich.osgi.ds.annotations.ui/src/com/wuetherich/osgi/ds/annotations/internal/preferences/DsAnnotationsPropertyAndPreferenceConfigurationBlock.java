package com.wuetherich.osgi.ds.annotations.internal.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.wuetherich.osgi.ds.annotations.internal.preferences.fwk.AbstractPropertyAndPreferencesPage;
import com.wuetherich.osgi.ds.annotations.internal.preferences.fwk.ConfigurationBlock;

public class DsAnnotationsPropertyAndPreferenceConfigurationBlock extends ConfigurationBlock {

  /** - */
  private static final String         LOCAL_REPO_TITLE    = "Local Repository:";

  /** - */
  private static final String         REMOTE_REPO_TITLE   = "Remote Repository:";

  /** - */
  private static final String         SETTINGS_PATH_TITLE = "Path:";

  /** - */
  private Text                        _text_localRepositoryPath;

  /** - */
  private Text                        _text_remoteRepositoryPath;

  /** - */
  private Text                        _text_settingsXml;

  /** - */
  private Button                      _check_m2eSettings;

  /** - */
  private Button                      _check_settingsXml;

  /** - */
  private Button                      _check_configureRepositories;

  /** - */
  private EnabledStateHelper          _stateHelper_settingsXml;

  /** - */
  private EnabledStateHelper          _stateHelper_configureRepositories;

  /** - */
  private MvnConfigurationSettingEnum _configurationSetting;

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
    GridLayout layout = new GridLayout();
    layout.numColumns = 3;
    setLayout(layout);

    //
    SelectionListener selectionListener = new SelectionListener() {
      @Override
      public void widgetSelected(SelectionEvent e) {

        if (e.getSource().equals(_check_configureRepositories)) {
          selectConfigureRepositories();
        }
        //
        else if (e.getSource().equals(_check_m2eSettings)) {
          selectCheckM2eSettings();
        }
        //
        else if (e.getSource().equals(_check_settingsXml)) {
          selectSettingsXml();
        }
      }

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
        //
      }
    };

    //
    _check_m2eSettings = new Button(this, SWT.CHECK);
    _check_m2eSettings.addSelectionListener(selectionListener);
    _check_m2eSettings.setData(MvnConfigurationSettingEnum.USE_M2E_SETTINGS);
    Label label_m2eSettings = new Label(this, SWT.NONE);
    label_m2eSettings.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
    label_m2eSettings.setText("Use M2E settings");

    // the settingsXml block
    _check_settingsXml = new Button(this, SWT.CHECK);
    _check_settingsXml.addSelectionListener(selectionListener);
    _check_settingsXml.setData(MvnConfigurationSettingEnum.USE_SETTINGS_XML);
    Label label_settingsXml = new Label(this, SWT.NONE);
    label_settingsXml.setText("Specify 'settings.xml' file");
    label_settingsXml.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

    _stateHelper_settingsXml = new EnabledStateHelper();
    _stateHelper_settingsXml.add(new Label(this, SWT.NONE)); // dummy label
    Label label_settingsXmlFile = new Label(this, SWT.NONE);
    label_settingsXmlFile.setText(SETTINGS_PATH_TITLE);
    _stateHelper_settingsXml.add(label_settingsXmlFile);
    _text_settingsXml = new Text(this, SWT.SINGLE | SWT.BORDER);
    _text_settingsXml.setLayoutData(new GridData(GridData.FILL_BOTH));
    _stateHelper_settingsXml.add(_text_settingsXml);

    // the configure repositories block
    _check_configureRepositories = new Button(this, SWT.CHECK);
    _check_configureRepositories.addSelectionListener(selectionListener);
    _check_configureRepositories.setData(MvnConfigurationSettingEnum.USE_CONFIGURED_RESPOSITORIES);
    Label label_configureRepositories = new Label(this, SWT.NONE);
    label_configureRepositories.setText("Directly configure repositories");
    label_configureRepositories.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
    _stateHelper_configureRepositories = new EnabledStateHelper();
    _stateHelper_configureRepositories.add(new Label(this, SWT.NONE)); // dummy label
    Label label_localRepoPath = new Label(this, SWT.NONE);
    label_localRepoPath.setText(LOCAL_REPO_TITLE);
    _stateHelper_configureRepositories.add(label_localRepoPath);
    _text_localRepositoryPath = new Text(this, SWT.SINGLE | SWT.BORDER);
    _text_localRepositoryPath.setLayoutData(new GridData(GridData.FILL_BOTH));
    _stateHelper_configureRepositories.add(_text_localRepositoryPath);
    _stateHelper_configureRepositories.add(new Label(this, SWT.NONE)); // dummy label
    Label label_remoteRepoPath = new Label(this, SWT.NONE);
    label_remoteRepoPath.setText(REMOTE_REPO_TITLE);
    _stateHelper_configureRepositories.add(label_remoteRepoPath);
    _text_remoteRepositoryPath = new Text(this, SWT.SINGLE | SWT.BORDER);
    _text_remoteRepositoryPath.setLayoutData(new GridData(GridData.FILL_BOTH));
    _stateHelper_configureRepositories.add(_text_remoteRepositoryPath);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void initialize() {

//    //
//    _text_localRepositoryPath.setText(getPage().getPreferenceStore().getString(
//        Constants.PREF_MVN_LOCAL_REPO));
//
//    //
//    _text_remoteRepositoryPath.setText(getPage().getPreferenceStore().getString(
//        Constants.PREF_MVN_REMOTE_REPO));
//
//    //
//    _text_settingsXml.setText(getPage().getPreferenceStore().getString(
//        Constants.PREF_MVN_SETTINGSXML));
//
//    //
//    String currentSettingString = getPage().getPreferenceStore()
//        .getString(Constants.PREF_MVN_CURRENT_SETTING);
//
//    try {
//      MvnConfigurationSettingEnum setting = MvnConfigurationSettingEnum.valueOf(currentSettingString);
//      switch (setting) {
//      case USE_CONFIGURED_RESPOSITORIES:
//        selectConfigureRepositories();
//        break;
//      case USE_M2E_SETTINGS:
//        selectCheckM2eSettings();
//        break;
//      case USE_SETTINGS_XML:
//        selectSettingsXml();
//        break;
//      }
//    } catch (Exception e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void performDefaults() {

//    //
//    _text_localRepositoryPath.setText(getPage().getPreferenceStore().getDefaultString(
//        Constants.PREF_MVN_LOCAL_REPO));
//
//    //
//    _text_remoteRepositoryPath.setText(getPage().getPreferenceStore().getDefaultString(
//        Constants.PREF_MVN_REMOTE_REPO));
//
//    //
//    _text_settingsXml.setText(getPage().getPreferenceStore().getDefaultString(
//        Constants.PREF_MVN_SETTINGSXML));
//
//    //
//    String currentSettingString = getPage().getPreferenceStore()
//        .getDefaultString(Constants.PREF_MVN_CURRENT_SETTING);
//
//    try {
//      MvnConfigurationSettingEnum setting = MvnConfigurationSettingEnum.valueOf(currentSettingString);
//      switch (setting) {
//      case USE_CONFIGURED_RESPOSITORIES:
//        selectConfigureRepositories();
//        break;
//      case USE_M2E_SETTINGS:
//        selectCheckM2eSettings();
//        break;
//      case USE_SETTINGS_XML:
//        selectSettingsXml();
//        break;
//      }
//    } catch (Exception e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    }
  }

  @Override
  protected String[] getPreferenceKeys() {
    return new String[] { /* Constants.PREF_MVN_LOCAL_REPO, Constants.PREF_MVN_REMOTE_REPO */ };
  }

  @Override
  public boolean performOk() {

//    //
//    getPage().getPreferenceStore().putValue(Constants.PREF_MVN_CURRENT_SETTING,
//        _configurationSetting.name());
//
//    getPage().getPreferenceStore().putValue(Constants.PREF_MVN_LOCAL_REPO,
//        _text_localRepositoryPath.getText());
//    getPage().getPreferenceStore().putValue(Constants.PREF_MVN_REMOTE_REPO,
//        _text_remoteRepositoryPath.getText());
//    
//    getPage().getPreferenceStore().putValue(Constants.PREF_MVN_SETTINGSXML,
//        _text_settingsXml.getText());

    //
    return true;
  }

  private void selectSettingsXml() {
    _stateHelper_configureRepositories.disable();
    _stateHelper_settingsXml.enable();
    _check_configureRepositories.setSelection(false);
    _check_m2eSettings.setSelection(false);
    _check_settingsXml.setSelection(true);
    _configurationSetting = (MvnConfigurationSettingEnum) _check_settingsXml.getData();
  }

  private void selectCheckM2eSettings() {
    _stateHelper_configureRepositories.disable();
    _stateHelper_settingsXml.disable();
    _check_configureRepositories.setSelection(false);
    _check_m2eSettings.setSelection(true);
    _check_settingsXml.setSelection(false);
    _configurationSetting = (MvnConfigurationSettingEnum) _check_m2eSettings.getData();
  }

  private void selectConfigureRepositories() {
    _stateHelper_configureRepositories.enable();
    _stateHelper_settingsXml.disable();
    _check_configureRepositories.setSelection(true);
    _check_m2eSettings.setSelection(false);
    _check_settingsXml.setSelection(false);
    _configurationSetting = (MvnConfigurationSettingEnum) _check_configureRepositories.getData();
  }
}
