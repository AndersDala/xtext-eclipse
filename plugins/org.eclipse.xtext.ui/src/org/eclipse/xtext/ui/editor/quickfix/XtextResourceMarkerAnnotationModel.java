/*******************************************************************************
 * Copyright (c) 2010 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.ui.editor.quickfix;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.eclipse.ui.texteditor.ResourceMarkerAnnotationModel;
import org.eclipse.xtext.ui.editor.model.edit.IssueUtil;

/**
 * @author Heiko Behrens - Initial contribution and API
 * @author Sebastian Zarnekow
 */
public class XtextResourceMarkerAnnotationModel extends ResourceMarkerAnnotationModel {

	private final IssueResolutionProvider issueResolutionProvider;
	private final IssueUtil issueUtil;

	public XtextResourceMarkerAnnotationModel(IFile file, IssueResolutionProvider issueResolutionProvider, IssueUtil markerUtil) {
		super(file);
		this.issueResolutionProvider = issueResolutionProvider;
		this.issueUtil = markerUtil;
	}

	@Override
	protected MarkerAnnotation createMarkerAnnotation(IMarker marker) {
		MarkerAnnotation annotation = super.createMarkerAnnotation(marker);
		String issueCode = issueUtil.getCode(annotation);
		annotation.setQuickFixable(issueResolutionProvider.hasResolutionFor(issueCode));
		return annotation;
	}
	
	public void fireAnnotationChangedEvent(Annotation annotation) {
		queueAnnotationChanged(annotation);
		fireQueuedEvents();
	}
	
	public void queueAnnotationChanged(Annotation annotation) {
		synchronized (getLockObject()) {
			getAnnotationModelEvent().annotationChanged(annotation);
		}
	}
	
	public void fireQueuedEvents() {
		fireModelChanged();
	}

}
