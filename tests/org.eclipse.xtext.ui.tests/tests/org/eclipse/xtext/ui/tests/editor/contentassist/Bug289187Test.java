/*******************************************************************************
 * Copyright (c) 2009 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.ui.tests.editor.contentassist;

import org.eclipse.xtext.ISetup;
import org.eclipse.xtext.ui.UIPluginModule;
import org.eclipse.xtext.ui.junit.editor.contentassist.AbstractContentAssistProcessorTest;
import org.eclipse.xtext.ui.tests.Activator;
import org.eclipse.xtext.ui.tests.editor.contentassist.Bug289187TestLanguageStandaloneSetup;
import org.eclipse.xtext.ui.tests.editor.contentassist.ui.Bug289187TestLanguageUiModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Sebastian Zarnekow - Initial contribution and API
 */
public class Bug289187Test extends AbstractContentAssistProcessorTest {

	public ISetup getBug289187TestLanguageSetup() {
		return new Bug289187TestLanguageStandaloneSetup() {
			@Override
			public Injector createInjector() {
				return Guice.createInjector(new Bug289187TestLanguageUiModule(), new UIPluginModule(Activator.getInstance()));
			}
		};
	}
	
	public void testBug289187_01() throws Exception {
    	newBuilder(getBug289187TestLanguageSetup()).append(
    			"class Foo {\n" + 
    			"   PRIVATE ").assertText("attribute", "operation");
    }
    
}
