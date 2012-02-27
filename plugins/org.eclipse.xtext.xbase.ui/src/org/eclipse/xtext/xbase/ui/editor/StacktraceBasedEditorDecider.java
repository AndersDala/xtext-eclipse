/*******************************************************************************
 * Copyright (c) 2012 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.xbase.ui.editor;

/**
 * Inspects the caller stack to decide whether it is ok to use an Xbase editor to 
 * edit a java file or whether the user really expects the Java editor, e.g. by selecting
 * a generated Java file explicitly in the package explorer.
 * 
 * @author Sebastian Zarnekow - Initial contribution and API
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 * @since 2.3
 */
public class StacktraceBasedEditorDecider {

	public enum Decision {
		FORCE_JAVA, /* FORCE_XBASE, */ FAVOR_XBASE
	}
	
	/**
	 * @noreference This method is not intended to be referenced by clients.
	 * @nooverride This method is not intended to be re-implemented or extended by clients.
	 */
	public Decision decideAccordingToCaller() {
		StackTraceElement[] trace = new Exception().getStackTrace();
		for(StackTraceElement element: trace) {
//			if (isOpenTypeAction(element))
//				return Decision.FORCE_XBASE;
			if (isSourceLookup(element))
				return Decision.FORCE_JAVA;
			if (isPackageExplorerOrNavigator(element))
				return Decision.FORCE_JAVA;
		}
		return Decision.FAVOR_XBASE;
	}
	
	protected boolean isSourceLookup(StackTraceElement element) {
		return "org.eclipse.debug.internal.ui.sourcelookup.SourceLookupFacility".equals(element.getClassName());
	}

	/**
	 * @noreference This method is not intended to be referenced by clients.
	 * @nooverride This method is not intended to be re-implemented or extended by clients.
	 */
	public boolean isCalledFromFindReferences() {
		StackTraceElement[] trace = new Exception().getStackTrace();
		for(StackTraceElement element: trace) {
//			if (isOpenTypeAction(element))
//				return Decision.FORCE_XBASE;
			if (isFindReferences(element))
				return true;
		}
		return false;
	}
	
	// It is currently not possible to supersede an open editor
	// thus if a Java editor for Foo.java is open and the user wants to
	// open Foo.xtend, the Open Type dialog will always reveal Foo.java since
	// the Editor input for Foo.java matches a currently open editor. In other words:
	// No new editor will be opened anyway so such a guard is pointless.
//	protected boolean isOpenTypeAction(StackTraceElement element) {
//		return "org.eclipse.jdt.internal.ui.actions.OpenTypeAction".equals(element.getClassName());
//	}
	
	/**
	 * @noreference This method is not intended to be referenced by clients.
	 * @nooverride This method is not intended to be re-implemented or extended by clients.
	 */
	protected boolean isFindReferences(StackTraceElement element) {
		return "org.eclipse.jdt.internal.ui.search.JavaSearchEditorOpener".equals(element.getClassName()) && "openElement".equals(element.getMethodName());
	}
	
	/**
	 * @noreference This method is not intended to be referenced by clients.
	 * @nooverride This method is not intended to be re-implemented or extended by clients.
	 */
	protected boolean isPackageExplorerOrNavigator(StackTraceElement element) {
		return "org.eclipse.jdt.internal.ui.packageview.PackageExplorerActionGroup".equals(element.getClassName()) && "handleOpen".equals(element.getMethodName()) 
				||	"org.eclipse.ui.navigator.CommonViewer".equals(element.getClassName()) && "handleOpen".equals(element.getMethodName());
	}
	
}
