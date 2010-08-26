/*******************************************************************************
 * Copyright (c) 2009 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.builder.builderState;

import org.eclipse.xtext.builder.builderState.impl.EObjectDescriptionImpl;
import org.eclipse.xtext.builder.builderState.impl.ReferenceDescriptionImpl;
import org.eclipse.xtext.builder.builderState.impl.ResourceDescriptionImpl;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IReferenceDescription;
import org.eclipse.xtext.resource.IResourceDescription;

import com.google.common.collect.Lists;

/**
 * @author Sven Efftinge - Initial contribution and API
 */
public class BuilderStateUtil {
	
	public static ResourceDescriptionImpl create(IResourceDescription desc) {
		if (desc instanceof ResourceDescriptionImpl)
			return (ResourceDescriptionImpl) desc;
		ResourceDescriptionImpl description = (ResourceDescriptionImpl) BuilderStateFactory.eINSTANCE.createResourceDescription();
		description.getImportedNames().addAll(Lists.newArrayList(desc.getImportedNames()));
		description.setURI(desc.getURI());
		for (IEObjectDescription objDesc : desc.getExportedObjects()) {
			description.getExportedObjects().add(create(objDesc));
		}
		Iterable<IReferenceDescription> referenceDescriptions = desc.getReferenceDescriptions();
		for (IReferenceDescription iReferenceDescription : referenceDescriptions) {
			description.getReferenceDescriptions().add(create(iReferenceDescription));
		}
		Iterable<String> names = desc.getImportedNames();
		for (String string : names) {
			description.getImportedNames().add(string);
		}
		return description;
	}
	
	public static ReferenceDescriptionImpl create(IReferenceDescription desc) {
		if (desc instanceof ReferenceDescriptionImpl)
			return (ReferenceDescriptionImpl) desc;
		ReferenceDescriptionImpl description = (ReferenceDescriptionImpl) BuilderStateFactory.eINSTANCE.createReferenceDescription();
		description.setIndexInList(desc.getIndexInList());
		description.setSourceEObjectUri(desc.getSourceEObjectUri());
		description.setTargetEObjectUri(desc.getTargetEObjectUri());
		description.setReference(desc.getEReference());
		description.setContainerEObjectURI(desc.getContainerEObjectURI());
		return description;
	}

	public static EObjectDescriptionImpl create(IEObjectDescription desc) {
		if (desc instanceof EObjectDescriptionImpl)
			return (EObjectDescriptionImpl) desc;
		EObjectDescriptionImpl objectDescription = (EObjectDescriptionImpl) BuilderStateFactory.eINSTANCE.createEObjectDescription();
		objectDescription.setEClass(desc.getEClass());
		objectDescription.setFragment(desc.getEObjectURI().fragment());
		objectDescription.setName(desc.getName());
		for (String key : desc.getUserDataKeys()) {
			objectDescription.getUserData().put(key, desc.getUserData(key));
		}
		return objectDescription;
	}
}
