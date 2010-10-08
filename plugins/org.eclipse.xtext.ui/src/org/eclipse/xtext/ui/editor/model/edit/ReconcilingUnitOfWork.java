/*******************************************************************************
 * Copyright (c) 2010 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.ui.editor.model.edit;

import org.eclipse.text.edits.TextEdit;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

/**
 * @author Knut Wannheden - Initial contribution and API
 * @author Jan Koehnlein 
 */
public class ReconcilingUnitOfWork<T> implements IUnitOfWork<T, XtextResource> {

	private final IUnitOfWork<T, XtextResource> work;
	private final IXtextDocument document;
	private final ITextEditComposer composer;

	public ReconcilingUnitOfWork(IUnitOfWork<T, XtextResource> work, IXtextDocument document, ITextEditComposer composer) {
		this.work = work;
		this.document = document;
		this.composer = composer;
	}

	public T exec(XtextResource state) throws Exception {
		// lazy linking URIs might change, so resolve everything before applying any changes
		EcoreUtil2.resolveAll(state, new CancelIndicator.NullImpl());
		composer.beginRecording(state);
		T result = work.exec(state);
		final TextEdit edit = composer.endRecording();
		if (edit != null) {
			String original = document.get();
			try {
				edit.apply(document);
			}
			catch (Exception e) {
				document.set(original);
				throw new RuntimeException(e);
			}
		}
		return result;
	}
}