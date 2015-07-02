/*******************************************************************************
 * Copyright (c) 2015 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.xtext;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.Grammar;
import org.eclipse.xtext.ParserRule;
import org.eclipse.xtext.XtextStandaloneSetup;
import org.eclipse.xtext.validation.AbstractValidationMessageAcceptingTestCase;
import org.junit.Test;

/**
 * @author Sebastian Zarnekow - Initial contribution and API
 */
public class Bug287082Test extends AbstractValidationMessageAcceptingTestCase {

	@Override
	public void setUp() throws Exception {
		super.setUp();
		with(XtextStandaloneSetup.class);
	}
	
	@Test public void testBug285605() throws Exception {
		String grammarAsString = "grammar org.xtext.example.MyDsl with org.eclipse.xtext.common.Terminals\n" + 
				"generate myDsl \"http://www.xtext.org/example/MyDsl\"\n" + 
				"\n" + 
				"A: {A} (feature=ID+)?;\n";
		Grammar grammar = (Grammar) getModel(grammarAsString);
		OverriddenValueInspector inspector = new OverriddenValueInspector(this);
		inspector.inspect((ParserRule) grammar.getRules().get(0));
	}

	@Override
	public void acceptWarning(String message, EObject object, EStructuralFeature feature, int index, String code,
			String... issueData) {
		if (code.equals(OverriddenValueInspector.ISSUE_CODE)) {
			String expectation = "";
			assertEquals(expectation, message);
		} else {
			super.acceptWarning(message, object, feature, index, code, issueData);
		}
	}

}
