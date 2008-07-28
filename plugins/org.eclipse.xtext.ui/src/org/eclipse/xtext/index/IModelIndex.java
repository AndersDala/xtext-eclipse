/*******************************************************************************
 * Copyright (c) 2008 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.xtext.index;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;

/**
 * @author Jan K�hnlein - Initial contribution and API
 */
public interface IModelIndex {

	boolean exists(URI fragmentUri);
	
	List<URI> findReferencesTo(URI referencedUri, IProject scope);
	
	List<URI> findInstances(EClass type, IProject scope);
}
