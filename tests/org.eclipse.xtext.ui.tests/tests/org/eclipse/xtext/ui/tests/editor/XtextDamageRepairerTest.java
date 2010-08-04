/*******************************************************************************
 * Copyright (c) 2008 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.xtext.ui.tests.editor;

import org.eclipse.jface.text.presentation.IPresentationDamager;
import org.eclipse.xtext.parser.antlr.Lexer;
import org.eclipse.xtext.ui.editor.XtextDamagerRepairer;

import com.google.inject.Provider;

/**
 * @author Sven Efftinge - Initial contribution and API
 */
@SuppressWarnings("deprecation")
public class XtextDamageRepairerTest extends AbstractDamagerRepairerTest {

	@Override
	protected IPresentationDamager createRegionDamager() {
		XtextDamagerRepairer repairer = new XtextDamagerRepairer(this, new Provider<Lexer>() {
			public Lexer get() {
				return new org.eclipse.xtext.parser.antlr.internal.InternalXtextLexer();
			}
		});
		return repairer;
	}
	
	@Override
	public void testAddElement() throws Exception {
		assertEquals(4,12,check("foo bar",7,0," honolulu"));
	}
	
	@Override
	public void testChangeInTheMiddleElement() throws Exception {
		assertEquals(4,4,check("foo bar import",6,1,"z"));
	}
	
}
