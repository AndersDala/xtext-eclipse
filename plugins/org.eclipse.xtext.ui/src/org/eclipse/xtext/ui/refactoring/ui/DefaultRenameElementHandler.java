/*******************************************************************************
 * Copyright (c) 2010 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.ui.refactoring.ui;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.resource.IGlobalServiceProvider;
import org.eclipse.xtext.resource.ILocationInFileProvider;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.eclipse.xtext.ui.refactoring.IRenameStrategy;
import org.eclipse.xtext.util.ITextRegion;
import org.eclipse.xtext.util.TextRegion;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import com.google.inject.Inject;

/**
 * @author Jan Koehnlein - Initial contribution and API
 * @author Holger Schill
 */
public class DefaultRenameElementHandler extends AbstractHandler implements IRenameElementHandler {

	@Inject
	private EObjectAtOffsetHelper eObjectAtOffsetHelper;

	@Inject
	private ILocationInFileProvider locationInFileProvider;
	
	@Inject
	protected RenameRefactoringController renameRefactoringController;

	@Inject
	protected IGlobalServiceProvider globalServiceProvider;
	
	@Inject
	protected RefactoringPreferences preferences;
	
	protected static final Logger LOG = Logger.getLogger(DefaultRenameElementHandler.class);

	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			final XtextEditor editor = EditorUtils.getActiveXtextEditor(event);
			if (editor != null) {
				final ITextSelection selection = (ITextSelection) editor.getSelectionProvider().getSelection();
				IRenameElementContext renameElementContext = editor.getDocument().readOnly(
						new IUnitOfWork<IRenameElementContext, XtextResource>() {
							public IRenameElementContext exec(XtextResource resource) throws Exception {
								EObject selectedElement = eObjectAtOffsetHelper.resolveElementAt(resource,
										selection.getOffset());
								if (selectedElement != null) {
									IRenameElementContext renameElementContext = createRenameElementContext(
											selectedElement, editor, selection, resource);
									if (isRefactoringEnabled(renameElementContext, resource))
										return renameElementContext;
								}
								return null;
							}
						});
				if (renameElementContext != null) {
					startRenameElement(renameElementContext);
				}
			}
		} catch (Exception exc) {
			LOG.error("Error initializing refactoring", exc);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error initializing refactoring",
					exc.getMessage() + "\nSee log for details");
		}
		return null;
	}

	protected boolean isRefactoringEnabled(IRenameElementContext renameElementContext, Resource resource) {
		ResourceSet resourceSet = resource.getResourceSet();
		if (renameElementContext != null && resourceSet != null) {
			EObject targetElement = resourceSet.getEObject(renameElementContext.getTargetElementURI(), true);
			if (targetElement != null && !targetElement.eIsProxy()) {
				if(targetElement.eResource() == resource && renameElementContext.getTriggeringEditorSelection() instanceof ITextSelection) {
					ITextRegion significantRegion = locationInFileProvider.getSignificantTextRegion(targetElement);
					ITextSelection textSelection = (ITextSelection) renameElementContext.getTriggeringEditorSelection();
					ITextRegion selectedRegion = new TextRegion(textSelection.getOffset(), textSelection.getLength());
					if(!significantRegion.contains(selectedRegion)) {
						return false;
					}
				}
				IRenameStrategy.Provider renameStrategyProvider = globalServiceProvider.findService(targetElement,
						IRenameStrategy.Provider.class);
				return renameStrategyProvider.get(targetElement, renameElementContext) != null;
			}
		}
		return false;
	}

	public IRenameElementContext createRenameElementContext(EObject targetElement, final XtextEditor editor,
			final ITextSelection selection, XtextResource resource) {
		final URI targetElementURI = EcoreUtil2.getNormalizedURI(targetElement);
		IRenameElementContext.Impl renameElementContext = new IRenameElementContext.Impl(targetElementURI,
				targetElement.eClass(), editor, selection, resource.getURI());
		return renameElementContext;
	}

	protected void startRenameElement(IRenameElementContext renameElementContext) throws InterruptedException {
		renameRefactoringController.initialize(renameElementContext);
		if(preferences.useInlineRefactoring())
			renameRefactoringController.startRefactoring(RefactoringType.LINKED_EDITING);
		else 
			renameRefactoringController.startRefactoring(RefactoringType.REFACTORING_DIALOG);
	}

}
