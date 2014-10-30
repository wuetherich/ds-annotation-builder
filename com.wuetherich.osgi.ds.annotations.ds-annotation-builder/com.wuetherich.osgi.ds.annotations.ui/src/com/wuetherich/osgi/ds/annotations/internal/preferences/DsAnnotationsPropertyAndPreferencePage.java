package com.wuetherich.osgi.ds.annotations.internal.preferences;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Composite;

import com.wuetherich.osgi.ds.annotations.Constants;
import com.wuetherich.osgi.ds.annotations.internal.preferences.fwk.AbstractPropertyAndPreferencesPage;
import com.wuetherich.osgi.ds.annotations.internal.preferences.fwk.ConfigurationBlock;

public class DsAnnotationsPropertyAndPreferencePage extends
		AbstractPropertyAndPreferencesPage {

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
		return Constants.BUNDLE_ID;
	}

	@Override
	public boolean performOk() {
		
		//
		if (super.performOk()) {
			rebuildProjects();
			return true;
		}

		//
		return false;
	}

	@Override
	protected void performApply() {
		super.performApply();

		rebuildProjects();
	}

	private void rebuildProjects() {
		//
		if (hasProjectSpecificOptions(getProject())) {

			Job job = new Job(String.format("Rebuild '%s'...", getProject()
					.getName())) {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						getProject().build(
								IncrementalProjectBuilder.FULL_BUILD,
								Constants.BUILDER_ID, null, monitor);
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return Status.OK_STATUS;
				}

			};
			job.schedule();
		}
	}
}
