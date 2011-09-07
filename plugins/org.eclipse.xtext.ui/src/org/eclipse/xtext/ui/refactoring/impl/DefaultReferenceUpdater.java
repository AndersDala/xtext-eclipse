/*******************************************************************************
 * Copyright (c) 2010 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.ui.refactoring.impl;

import static org.eclipse.ltk.core.refactoring.RefactoringStatus.*;

import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.parsetree.reconstr.ITransientValueService;
import org.eclipse.xtext.resource.ILocationInFileProvider;
import org.eclipse.xtext.resource.IReferenceDescription;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.refactoring.ElementRenameArguments;
import org.eclipse.xtext.ui.refactoring.IRefactoringUpdateAcceptor;
import org.eclipse.xtext.util.ITextRegion;

import com.google.common.collect.Multimap;
import com.google.inject.Inject;

/**
 * Creates updates for all references from Xtext based resources to a renamed element using Xtext's serialization API.
 * 
 * @author Jan Koehnlein - Initial contribution and API
 */
public class DefaultReferenceUpdater extends AbstractReferenceUpdater {

	@Inject
	private ILocationInFileProvider locationInFileProvider;

	@Inject
	private ITransientValueService transientValueService;

	@Inject
	private CrossReferenceSerializerFacade crossReferenceSerializerFacade;

	@Override
	protected void createReferenceUpdates(ElementRenameArguments elementRenameArguments,
			Multimap<URI, IReferenceDescription> resource2references, ResourceSet resourceSet,
			IRefactoringUpdateAcceptor updateAcceptor, IProgressMonitor monitor) {
		SubMonitor progress = SubMonitor.convert(monitor, "Creating reference updates", resource2references.keySet()
				.size());
		for (URI referringResourceURI : resource2references.keySet()) {
			if (progress.isCanceled())
				return;
			Resource referringResource = resourceSet.getResource(referringResourceURI, false);
			if (!(referringResource instanceof XtextResource)) {
				updateAcceptor.getRefactoringStatus().add(ERROR, "Resource {0} is not an XtextResource.",
						referringResource.getURI(), resourceSet);
			} else {
				((XtextResource) referringResource).getCache().clear(referringResource);
				for (IReferenceDescription referenceDescription : resource2references.get(referringResourceURI)) {
					createReferenceUpdate(referenceDescription, referringResourceURI, elementRenameArguments,
							resourceSet, updateAcceptor);
				}
			}
			progress.worked(1);
		}
	}

	protected void createReferenceUpdate(IReferenceDescription referenceDescription, URI referringResourceURI,
			ElementRenameArguments elementRenameArguments, ResourceSet resourceSet,
			IRefactoringUpdateAcceptor updateAcceptor) {
		URI referringElementNewURI = elementRenameArguments
				.getNewElementURI(referenceDescription.getSourceEObjectUri());
		EObject referringElement = resourceSet.getEObject(referringElementNewURI, false);
		URI targetElementNewURI = elementRenameArguments.getNewElementURI(referenceDescription.getTargetEObjectUri());
		EObject newTargetElement = resourceSet.getEObject(targetElementNewURI, false);
		createReferenceUpdate(referringElement, referringResourceURI, referenceDescription.getEReference(),
				referenceDescription.getIndexInList(), newTargetElement, updateAcceptor);
	}

	protected void createReferenceUpdate(EObject referringElement, URI referringResourceURI, EReference reference,
			int indexInList, EObject newTargetElement, IRefactoringUpdateAcceptor updateAcceptor) {
		if (!transientValueService.isTransient(referringElement, reference, indexInList)) {
			ITextRegion referenceTextRegion = locationInFileProvider.getFullTextRegion(referringElement, reference,
					indexInList);
			CrossReference crossReference = getCrossReference(referringElement, referenceTextRegion.getOffset());
			if (crossReference != null) {
				String newReferenceText = crossReferenceSerializerFacade.serializeCrossRef(referringElement,
						crossReference, newTargetElement, referenceTextRegion, updateAcceptor.getRefactoringStatus());
				if (newReferenceText != null) {
					// TODO: add import hook
					TextEdit referenceEdit = new ReplaceEdit(referenceTextRegion.getOffset(),
							referenceTextRegion.getLength(), newReferenceText);
					updateAcceptor.accept(referringResourceURI, referenceEdit);
				}
			}
		}
	}

	protected CrossReference getCrossReference(EObject referringElement, int offset) {
		ICompositeNode node = NodeModelUtils.getNode(referringElement);
		if (node != null) {
			Iterator<INode> iter = node.getAsTreeIterable().iterator();
			while (iter.hasNext()) {
				INode childNode = iter.next();
				if (childNode.getOffset() >= offset && childNode.getGrammarElement() instanceof CrossReference)
					return (CrossReference) childNode.getGrammarElement();
			}
		}
		return null;
	}

}
