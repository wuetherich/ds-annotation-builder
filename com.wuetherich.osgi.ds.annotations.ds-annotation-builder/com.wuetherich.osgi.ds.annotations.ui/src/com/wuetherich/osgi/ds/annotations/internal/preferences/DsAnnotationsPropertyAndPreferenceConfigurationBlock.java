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

public class DsAnnotationsPropertyAndPreferenceConfigurationBlock extends
		ConfigurationBlock {

	/** - */
	private static final String DS_VERSION_TITLE = "DS Version:";

	/** - */
	private Button _button_1_1;

	/** - */
	private Button _button_1_2;

	/**
	 * <p>
	 * Creates a new instance of type
	 * {@link DsAnnotationsPropertyAndPreferenceConfigurationBlock}.
	 * </p>
	 * 
	 * @param parent
	 * @param page
	 */
	public DsAnnotationsPropertyAndPreferenceConfigurationBlock(
			Composite parent, AbstractPropertyAndPreferencesPage page) {
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

		// Create the first group
		Group group1 = new Group(this, SWT.SHADOW_IN);
		group1.setText("Declarative Services Version");
		group1.setLayout(new RowLayout(SWT.VERTICAL));
		group1.setLayoutData(new GridData(GridData.FILL_BOTH));

		_button_1_1 = new Button(group1, SWT.RADIO);
		_button_1_1.setText("1.1 (OSGi Release 4.2)");

		_button_1_2 = new Button(group1, SWT.RADIO);
		_button_1_2.setText("1.2 (OSGi Release 4.3+)");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		switch (getDsAnnotationVersion(false)) {
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

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performDefaults() {

		switch (getDsAnnotationVersion(true)) {
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
	}

	@Override
	protected String[] getPreferenceKeys() {
		return new String[] { Constants.PREF_DS_VERSION };
	}

	@Override
	public boolean performOk() {

		DsAnnotationVersion dsAnnotationVersion = _button_1_1.getSelection() ? DsAnnotationVersion.V_1_1
				: DsAnnotationVersion.V_1_2;

		//
		getPage().getPreferenceStore().putValue(Constants.PREF_DS_VERSION,
				dsAnnotationVersion.name());

		//
		return true;
	}

	private DsAnnotationVersion getDsAnnotationVersion(boolean getDefault) {

		String version = getDefault ? getPage().getPreferenceStore()
				.getDefaultString(Constants.PREF_DS_VERSION) : getPage()
				.getPreferenceStore().getString(Constants.PREF_DS_VERSION);

		DsAnnotationVersion annotationVersion = DsAnnotationVersion
				.valueOf(version);

		return annotationVersion;
	}

}
