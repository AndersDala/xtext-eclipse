/*******************************************************************************
 * Copyright (c) 2008 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.xtext.ui.editor.preferences;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.xtext.GrammarUtil;
import org.eclipse.xtext.IGrammarAccess;
import org.eclipse.xtext.service.IServiceScope;
import org.eclipse.xtext.service.Inject;
import org.eclipse.xtext.service.ServiceRegistry;
import org.eclipse.xtext.ui.internal.Activator;

/**
 * @author Dennis H�bner - Initial contribution and API
 * 
 */
public abstract class AbstractPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage,
		IWorkbenchPropertyPage {
	
	private static final Logger log = Logger.getLogger(AbstractPreferencePage.class);

	private static final String USE_PROJECT_SETTINGS = "useProjectSettings";

	@Inject
	private IServiceScope languageDescriptor;
	private IWorkbench workbench;
	private IProject project;
	private String languageName;

	private Button useProjectSettingsButton;
	private List<FieldEditor> editors = new ArrayList<FieldEditor>();

	private Link link;

	public void init(IWorkbench workbench) {
		this.workbench = workbench;
	}

	protected IWorkbench getWorkbench() {
		return workbench;
	}

	public void setElement(IAdaptable element) {
		this.project = (IProject) element.getAdapter(IProject.class);
	}

	public IAdaptable getElement() {
		return project;
	}

	public boolean isPropertyPage() {
		return project != null;
	}

	/**
	 * @return prefix for preference keys
	 */
	abstract protected String qualifiedName();

	public AbstractPreferencePage() {

		super(GRID);
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		String qualifier = Activator.getDefault().getBundle().getSymbolicName();
		if (isPropertyPage()) {
			ProjectScope projectScope = new ProjectScope(currentProject());
			ScopedPreferenceStore scopedPreferenceStore = new ScopedPreferenceStore(projectScope, qualifier);
			scopedPreferenceStore.setSearchContexts(new IScopeContext[] { projectScope, new InstanceScope(),
					new ConfigurationScope() });
			return scopedPreferenceStore;
		}
		ScopedPreferenceStore scopedPreferenceStore = new ScopedPreferenceStore(new InstanceScope(), qualifier);
		scopedPreferenceStore.setSearchContexts(new IScopeContext[] { new InstanceScope(), new ConfigurationScope() });
		return scopedPreferenceStore;
	}

	/**
	 * @return
	 */
	protected IServiceScope getServiceScope() {
		return languageDescriptor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.FieldEditorPreferencePage#createContents
	 * (org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		if (isPropertyPage())
			createUseProjectSettingsControls(parent);
		return super.createContents(parent);
	}

	private void createUseProjectSettingsControls(Composite parent) {
		Composite projectSettingsParent = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		projectSettingsParent.setLayout(layout);
		projectSettingsParent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// use project settings button
		useProjectSettingsButton = new Button(projectSettingsParent, SWT.CHECK);
		useProjectSettingsButton.setText("Enable pr&oject specific settings");
		useProjectSettingsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleUseProjectSettings();
				log.debug("AbstractPreferencePage.widgetSelected()");
			}
		});

		// configure ws settings link
		link = new Link(projectSettingsParent, SWT.NONE);
		link.setFont(projectSettingsParent.getFont());
		link.setText("<A>" + "Configure Workspace Settings..." + "</A>"); //$NON-NLS-1$//$NON-NLS-3$
		link.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String id = qualifiedName();
				PreferencesUtil.createPreferenceDialogOn(getShell(), id, new String[] { id }, null).open();
			}
		});
		link.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false));

		// separator line
		Label horizontalLine = new Label(projectSettingsParent, SWT.SEPARATOR | SWT.HORIZONTAL);
		horizontalLine.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
		horizontalLine.setFont(projectSettingsParent.getFont());

		try {
			useProjectSettingsButton.setSelection(Boolean.valueOf(currentProject().getPersistentProperty(
					new QualifiedName(qualifiedName(), USE_PROJECT_SETTINGS))));
		}
		catch (CoreException e) {
			log.error("Error", e);
		}

	}

	private IProject currentProject() {
		if (project == null)
			throw new IllegalStateException("Not a property page case, but current project was requested.");
		return project;
	}

	protected String getLanguageName() {
		if (this.languageName == null) {
			this.languageName = GrammarUtil.getName(ServiceRegistry.getService(getServiceScope(), IGrammarAccess.class)
					.getGrammar());
		}
		return this.languageName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.PreferencePage#createControl(org.eclipse
	 * .swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		if (isPropertyPage())
			handleUseProjectSettings();
	}

	protected void addField(FieldEditor editor) {
		editor.setPreferenceName(qualifiedName() + "." + editor.getPreferenceName());
		editors.add(editor);
		super.addField(editor);
	}

	private void handleUseProjectSettings() {
		boolean isUseProjectSettings = useProjectSettingsButton.getSelection();
		link.setEnabled(!isUseProjectSettings);
		if (!isUseProjectSettings) {
			((ScopedPreferenceStore) getPreferenceStore()).setSearchContexts(new IScopeContext[] { new InstanceScope(),
					new ConfigurationScope() });
		}
		else {
			((ScopedPreferenceStore) getPreferenceStore()).setSearchContexts(new IScopeContext[] {
					new ProjectScope(currentProject()), new InstanceScope(), new ConfigurationScope() });
		}
		updateFieldEditors(isUseProjectSettings);
	}

	protected void updateFieldEditors(boolean enabled) {
		// TODO handle do not use project settings sets project settings to
		// default
		Composite parent = getFieldEditorParent();
		Iterator<FieldEditor> it = editors.iterator();
		while (it.hasNext()) {
			FieldEditor editor = (FieldEditor) it.next();
			if (enabled)
				editor.load();
			else
				editor.loadDefault();
			editor.setEnabled(enabled, parent);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		boolean retVal = super.performOk();
		if (retVal && isPropertyPage()) {
			try {
				currentProject().setPersistentProperty(
						new QualifiedName(qualifiedName(), USE_PROJECT_SETTINGS),
						String.valueOf(useProjectSettingsButton.getSelection()));
				((IPersistentPreferenceStore) getPreferenceStore()).save();
			}
			catch (Exception e) {
				log.error("Error", e);
				retVal = false;
			}
		}
		return retVal;
	}
}
