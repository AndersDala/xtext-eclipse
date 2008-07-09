/*******************************************************************************
 * Copyright (c) 2008 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.xtext.ui.service;

import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xtext.service.ILanguageService;
import org.eclipse.xtext.ui.editor.model.IEditorModelProvider;

/**
 * @author Dennis H�bner - Initial contribution and API
 * 
 */
public interface IHoverInfo extends ILanguageService {
	Object getHoverInfo(ITextViewer textViewer, IRegion hoverRegion, IEditorModelProvider editorModelProvider);

	void createContents(Object input, IContentContainer contentContainer);

	List<IAction> getHoverActions(Object input);

	public interface IContentContainer {
		void appendText(StyledString text);

		void appendImage(Image image);
	}
}
