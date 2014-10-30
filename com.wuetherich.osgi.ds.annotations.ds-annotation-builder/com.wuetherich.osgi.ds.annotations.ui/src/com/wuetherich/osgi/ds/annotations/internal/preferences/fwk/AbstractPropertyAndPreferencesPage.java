package com.wuetherich.osgi.ds.annotations.internal.preferences.fwk;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.ControlEnableState;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.service.prefs.BackingStoreException;

import com.wuetherich.osgi.ds.annotations.DsAnnotationsCore;

/**
 * <p>
 * </p>
 * 
 * <p>
 * This source was copied (and than modified) from the internal class
 * {@link org.eclipse.jdt.internal.ui.preferences.PropertyAndPreferencePage}.
 * </p>
 */
public abstract class AbstractPropertyAndPreferencesPage extends PreferencePage
		implements IWorkbenchPreferencePage, IWorkbenchPropertyPage {

	/** the parent composite */
	private Composite _parentComposite;

	/** the 'change ... settings' link */
	private Link _changeSettings;

	/** the use project settings field */
	private SelectionButtonDialogField _useProjectSettings;

	/** the configuration block */
	private ConfigurationBlock _configurationBlock;

	/** the enable state helper */
	private ControlEnableState _blockEnableState;

	/** project or null */
	private IProject _project;

	/** the page data */
	private Map<String, Object> _pageData;

	/** the DATA_NO_LINK constant */
	public static final String DATA_NO_LINK = "AbstractPropertyAndPreferencesPage.nolink";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performOk() {

		// check if the _configurationBlock is ok
		if (_configurationBlock != null && !_configurationBlock.performOk()) {
			return false;
		}

		savePreferences();

		//
		return true;
	}

	@Override
	protected void performApply() {

		// check if the _configurationBlock is ok
		if (_configurationBlock != null && !_configurationBlock.performApply()) {
			return;
		}

		savePreferences();
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();

		//
		if (_configurationBlock != null) {

			//
			if (isProjectPreferencePage()) {

				// set the defaults
				try {
					IEclipsePreferences eclipsePreferences = InstanceScope.INSTANCE
							.getNode(getStoreIdentifier());
					for (String key : eclipsePreferences.keys()) {
						getPreferenceStore().setDefault(key,
								eclipsePreferences.get(key, null));
					}
				} catch (BackingStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			_configurationBlock.performDefaults();
		}
	}

	@Override
	public boolean performCancel() {
		if (_configurationBlock != null && !_configurationBlock.performCancel()) {
			return false;
		}
		return super.performCancel();
	}

	@Override
	public void performHelp() {
		_configurationBlock.performHelp();
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @param project
	 * @return
	 */
	protected boolean hasProjectSpecificOptions(IProject project) {
		return project != null && _configurationBlock != null
				&& _configurationBlock.hasProjectSpecificOptions(project);
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @return
	 */
	public abstract String getStoreIdentifier();

	/**
	 * <p>
	 * </p>
	 * 
	 * @return
	 */
	protected abstract String getPreferencePageID();

	/**
	 * <p>
	 * </p>
	 * 
	 * @return
	 */
	protected abstract String getPropertyPageID();

	/**
	 * <p>
	 * </p>
	 * 
	 * @param composite
	 * @return
	 */
	protected abstract ConfigurationBlock createPreferenceContent(
			Composite composite);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IAdaptable getElement() {
		return _project;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setElement(IAdaptable element) {
		_project = (IProject) element.getAdapter(IResource.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.PreferencePage#applyData(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void applyData(Object data) {

		//
		if (data instanceof Map) {
			_pageData = (Map<String, Object>) data;
		}

		//
		if (_changeSettings != null) {
			if (!offerLink()) {
				_changeSettings.dispose();
				_parentComposite.layout(true, true);
			}
		}
	}

	protected Map<String, Object> getData() {
		return _pageData;
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @return
	 */
	public boolean isProjectPreferencePage() {
		return _project != null;
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @return
	 */
	protected boolean supportsProjectSpecificOptions() {
		return getPropertyPageID() != null;
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @return
	 */
	public IProject getProject() {
		return _project;
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @param data
	 */
	protected final void openWorkspacePreferences(Object data) {
		String id = getPreferencePageID();
		PreferencesUtil.createPreferenceDialogOn(getShell(), id,
				new String[] { id }, data).open();
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @param project
	 * @param data
	 */
	protected final void openProjectProperties(IProject project, Object data) {
		String id = getPropertyPageID();
		if (id != null) {
			PreferencesUtil.createPropertyDialogOn(getShell(), project, id,
					new String[] { id }, data).open();
		}
	}

	protected boolean offerLink() {
		return _pageData == null
				|| !Boolean.TRUE.equals(_pageData.get(DATA_NO_LINK));
	}

	@Override
	protected Label createDescriptionLabel(Composite parent) {

		_parentComposite = parent;

		if (isProjectPreferencePage()) {
			Composite composite = new Composite(parent, SWT.NONE);
			composite.setFont(parent.getFont());
			GridLayout layout = new GridLayout();
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			layout.numColumns = 2;
			composite.setLayout(layout);
			composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
					false));

			IDialogFieldListener listener = new IDialogFieldListener() {
				@Override
				public void dialogFieldChanged(DialogField field) {
					boolean enabled = ((SelectionButtonDialogField) field)
							.isSelected();
					enableProjectSpecificSettings(enabled);

					if (enabled && getData() != null) {
						applyData(getData());
					}
				}
			};

			_useProjectSettings = new SelectionButtonDialogField(SWT.CHECK);
			_useProjectSettings.setDialogFieldListener(listener);
			_useProjectSettings
					.setLabelText("Enable project specific settings");
			_useProjectSettings.doFillIntoGrid(composite, 1);
			// LayoutUtil.setHorizontalGrabbing(_useProjectSettings.getSelectionButton(null));

			if (offerLink()) {
				_changeSettings = createLink(composite,
						"Configure Workspace Settings...");
				_changeSettings.setLayoutData(new GridData(SWT.END, SWT.CENTER,
						false, false));
			} else {
				// LayoutUtil.setHorizontalSpan(fUseProjectSettings.getSelectionButton(null),
				// 2);
			}

			Label horizontalLine = new Label(composite, SWT.SEPARATOR
					| SWT.HORIZONTAL);
			horizontalLine.setLayoutData(new GridData(GridData.FILL,
					GridData.FILL, true, false, 2, 1));
			horizontalLine.setFont(composite.getFont());
		} else if (supportsProjectSpecificOptions() && offerLink()) {
			_changeSettings = createLink(parent,
					"Configure Project Specific Settings...");
			_changeSettings.setLayoutData(new GridData(SWT.END, SWT.CENTER,
					true, false));
		}

		return super.createDescriptionLabel(parent);
	}

	protected void enableProjectSpecificSettings(
			boolean useProjectSpecificSettings) {
		_useProjectSettings.setSelection(useProjectSpecificSettings);
		enablePreferenceContent(useProjectSpecificSettings);
		updateLinkVisibility();
		// doStatusChanged();
	}

	protected void enablePreferenceContent(boolean enable) {
		if (enable) {
			if (_blockEnableState != null) {
				_blockEnableState.restore();
				_blockEnableState = null;
			}
		} else {
			if (_blockEnableState == null) {
				_blockEnableState = ControlEnableState
						.disable(_configurationBlock);
			}
		}
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @return
	 */
	public ConfigurationBlock getConfigurationBlock() {
		return _configurationBlock;
	}

	/**
	 * <p>
	 * </p>
	 * 
	 */
	private void updateLinkVisibility() {
		if (_changeSettings == null || _changeSettings.isDisposed()) {
			return;
		}

		if (isProjectPreferencePage()) {
			_changeSettings.setEnabled(!useProjectSettings());
		}
	}

	public boolean useProjectSettings() {
		return isProjectPreferencePage() && _useProjectSettings != null
				&& _useProjectSettings.isSelected();
	}

	private Link createLink(Composite composite, String text) {
		Link link = new Link(composite, SWT.NONE);
		link.setFont(composite.getFont());
		link.setText("<A>" + text + "</A>"); //$NON-NLS-1$//$NON-NLS-2$
		link.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doLinkActivated((Link) e.widget);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				doLinkActivated((Link) e.widget);
			}
		});
		return link;
	}

	final void doLinkActivated(Link link) {

		// mark it as "NO_LINK"
		Map<String, Object> data = getData();
		if (data == null)
			data = new HashMap<String, Object>();
		data.put(DATA_NO_LINK, Boolean.TRUE);

		//
		if (isProjectPreferencePage()) {
			openWorkspacePreferences(data);
		}

		//
		else {

			//
			DsAnnotationsCore.getDsAnnotationAwareProjects();

			HashSet<IProject> projectsWithSpecifics = new HashSet<IProject>();
			for (IProject project : DsAnnotationsCore
					.getDsAnnotationAwareProjects()) {
				if (hasProjectSpecificOptions(project)) {
					projectsWithSpecifics.add(project);
				}
			}

			//
			ProjectSelectionDialog dialog = new ProjectSelectionDialog(
					getShell(), projectsWithSpecifics);

			//
			if (dialog.open() == Window.OK) {
				IProject result = (IProject) dialog.getFirstResult();
				openProjectProperties(result, data);
			}
		}
	}

	public IEclipsePreferences getPreferenceStoreAsEclipsePreferences() {

		//
		IEclipsePreferences[] eclipsePreferences = ((ScopedPreferenceStore) getPreferenceStore())
				.getPreferenceNodes(false);

		return eclipsePreferences[0];
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
	protected IPreferenceStore doGetPreferenceStore() {

		//
		if (isProjectPreferencePage()) {

			// speichern von Werten unter
			// '${workspace}\${projectname}\.settings\com.wuetherich.eclipse.propsprefs.prefs'
			return new ScopedPreferenceStore(new ProjectScope(getProject()),
					getStoreIdentifier());

		} else {

			// speichern von Werten unter
			// '${workspace}\.metadata\.plugins\org.eclipse.core.runtime\.settings\com.wuetherich.eclipse.propsprefs.prefs'
			return new ScopedPreferenceStore(InstanceScope.INSTANCE,
					getStoreIdentifier());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		composite.setFont(parent.getFont());

		GridData data = new GridData(GridData.FILL, GridData.BEGINNING, true,
				true);
		_configurationBlock = createPreferenceContent(composite);
		_configurationBlock.setLayoutData(data);

		//
		_configurationBlock.initialize();

		//
		if (isProjectPreferencePage()) {
			boolean useProjectSettings = hasProjectSpecificOptions(getProject());
			enableProjectSpecificSettings(useProjectSettings);
		}

		Dialog.applyDialogFont(composite);
		return composite;
	}

	private void savePreferences() {
		//
		try {

			// delete local setting...
			if (isProjectPreferencePage() && !useProjectSettings()) {
				for (String key : _configurationBlock.getPreferenceKeys()) {
					getPreferenceStoreAsEclipsePreferences().remove(key);
				}
			}

			ScopedPreferenceStore scopedPreferenceStore = (ScopedPreferenceStore) getPreferenceStore();
			scopedPreferenceStore.save();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int convertHeightInCharsToPixels(int chars) {
		return super.convertHeightInCharsToPixels(chars);
	}

	@Override
	public int convertHorizontalDLUsToPixels(int dlus) {
		return super.convertHorizontalDLUsToPixels(dlus);
	}

	@Override
	public int convertVerticalDLUsToPixels(int dlus) {
		return super.convertVerticalDLUsToPixels(dlus);
	}

	@Override
	public int convertWidthInCharsToPixels(int chars) {
		return super.convertWidthInCharsToPixels(chars);
	}
}