/*******************************************************************************
 * Copyright (c) 2008 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.xtext.ui.service;

import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.xtext.service.ILanguageService;
import org.eclipse.xtext.ui.editor.model.IEditorModel;

/**
 * @author Dennis H�bner - Initial contribution and API
 * 
 */
public interface IFoldingStructureProvider extends ILanguageService {
	void updateFoldingStructure(IEditorModel model, ProjectionViewer projectionViewer);
}
