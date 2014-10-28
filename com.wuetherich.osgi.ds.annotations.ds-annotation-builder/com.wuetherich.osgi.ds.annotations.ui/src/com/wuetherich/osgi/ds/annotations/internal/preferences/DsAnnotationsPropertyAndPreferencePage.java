package com.wuetherich.osgi.ds.annotations.internal.preferences;

import org.eclipse.swt.widgets.Composite;

import com.wuetherich.osgi.ds.annotations.internal.Activator;
import com.wuetherich.osgi.ds.annotations.internal.preferences.fwk.AbstractPropertyAndPreferencesPage;
import com.wuetherich.osgi.ds.annotations.internal.preferences.fwk.ConfigurationBlock;

public class DsAnnotationsPropertyAndPreferencePage extends
		AbstractPropertyAndPreferencesPage {

	/** PREF_MVN_LOCAL_REPO */
	public static final String PREF_MVN_LOCAL_REPO = Activator.PLUGIN_ID
			+ ".local_repository";

	/** PREF_MVN_REMOTE_REPO */
	public static final String PREF_MVN_REMOTE_REPO = Activator.PLUGIN_ID
			+ ".remote_repository";

	/** PREF_MVN_REMOTE_REPO */
	public static final String PREF_MVN_CONFIGURATION_SETTING = Activator.PLUGIN_ID
			+ ".configurationsetting";

	@Override
	protected String getPreferencePageID() {
		return "com.wuetherich.osgi.ds.annotations.ui.preferences";
	}

	@Override
	protected String getPropertyPageID() {
		return "com.wuetherich.osgi.ds.annotations.ui.properties";
	}

	@Override
	protected ConfigurationBlock createPreferenceContent(Composite composite) {
		return new DsAnnotationsPropertyAndPreferenceConfigurationBlock(
				composite, this);
	}

	@Override
	public String getStoreIdentifier() {
		return Activator.PLUGIN_ID;
	}
}
