/*******************************************************************************
 * Copyright (c) 2010 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.ui.tests.editor.autoedit;

import static org.eclipse.xtext.junit.util.IResourcesSetupUtil.*;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.tests.editor.AbstractEditorTest;

import com.google.common.collect.Lists;

/**
 * @author Sven Efftinge - Initial contribution and API
 */
public class AutoEditTest extends AbstractEditorTest {

	@Override
	protected String getEditorId() {
		return "org.eclipse.xtext.ui.tests.editor.bracketmatching.BmTestLanguage";
	}

	private List<IFile> files = Lists.newArrayList();


	@Override
	protected void tearDown() throws Exception {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(false);
		files.clear();
		super.tearDown();
	}

	public void testParenthesis_1() throws Exception {
		XtextEditor editor = openEditor("|");
		pressKey(editor, '(');
		assertState("(|)", editor);
		pressKey(editor, '(');
		assertState("((|))", editor);
		pressKey(editor, SWT.BS);
		assertState("(|)", editor);
		pressKey(editor, SWT.BS);
		assertState("|", editor);
	}

	public void testParenthesis_2() throws Exception {
		XtextEditor editor = openEditor("|");
		pressKey(editor, '(');
		assertState("(|)", editor);
		pressKey(editor, ')');
		assertState("()|", editor);
		pressKey(editor, SWT.BS);
		assertState("(|", editor);
		pressKey(editor, SWT.BS);
		assertState("|", editor);
	}

	public void testParenthesis_3() throws Exception {
		XtextEditor editor = openEditor("|)");
		pressKey(editor, '(');
		assertState("(|)", editor);
		pressKey(editor, SWT.BS);
		assertState("|", editor);
	}
	
	public void testParenthesis_4() throws Exception {
		XtextEditor editor = openEditor("|foobar");
		pressKey(editor, '(');
		assertState("(|foobar", editor);
	}
	
	public void testParenthesis_5() throws Exception {
		XtextEditor editor = openEditor("|");
		pressKey(editor, ')');
		assertState(")|", editor);
		pressKey(editor, ')');
		assertState("))|", editor);
		pressKey(editor, SWT.BS);
		assertState(")|", editor);
		pressKey(editor, SWT.BS);
		assertState("|", editor); 
	}
	
	
	public void testParenthesis_6() throws Exception {
		XtextEditor editor = openEditor("(|\n)");
		pressKey(editor, ')');
		assertState("()|\n)", editor);
		pressKey(editor, SWT.BS);
		assertState("(|\n)", editor);
	}
	
	public void testParenthesis_7() throws Exception {
		XtextEditor editor = openEditor("(((|)");
		pressKey(editor, ')');
		assertState("((()|)", editor);
		pressKey(editor, ')');
		assertState("((())|)", editor);
		pressKey(editor, ')');
		assertState("((()))|", editor);
	}

	public void testStringLiteral_1() throws Exception {
		XtextEditor editor = openEditor("|");
		pressKey(editor, '"');
		assertState("\"|\"", editor);
		pressKey(editor, SWT.BS);
		assertState("|", editor);
	}

	public void testStringLiteral_2() throws Exception {
		XtextEditor editor = openEditor("|");
		pressKey(editor, '"');
		assertState("\"|\"", editor);
		pressKey(editor, '"');
		assertState("\"\"|", editor);
		pressKey(editor, SWT.BS);
		assertState("\"|", editor);
	}

	public void testStringLiteral_3() throws Exception {
		XtextEditor editor = openEditor("|'");
		pressKey(editor, '\'');
		assertState("'|'", editor);
		pressKey(editor, '\'');
		assertState("''|", editor);
		pressKey(editor, '\'');
		assertState("'''|'", editor);
		
		// This one doesn't work unless you register the PartitionDeletionEditStrategy also for STING partitions. 
		// As it is very unsual to allow two string literals without any separating tokens, we don't need to support this
//		pressKey(editor, SWT.BS);
//		assertState("''|", editor);
	}
	
	public void testStringLiteral_4() throws Exception {
		XtextEditor editor = openEditor("|foo");
		pressKey(editor, '\'');
		assertState("'|foo", editor);
		pressKey(editor, '\'');
		assertState("''|foo", editor);
	}
	
	public void testStringLiteral_5() throws Exception {
		XtextEditor editor = openEditor("|foo");
		pressKey(editor, '\'');
		assertState("'|foo", editor);
		pressKey(editor, '\'');
		assertState("''|foo", editor);
		pressKey(editor, SWT.BS);
		assertState("'|foo", editor);
		pressKey(editor, SWT.BS);
		assertState("|foo", editor);
	}
	
	public void testStringLiteral_6() throws Exception {
		XtextEditor editor = openEditor("'| '");
		pressKey(editor, '\'');
		assertState("''| '", editor);
		pressKey(editor, SWT.BS);
		assertState("'| '", editor);
	}
	
	public void testStringLiteral_7() throws Exception {
		XtextEditor editor = openEditor("'' '| '");
		pressKey(editor, '\'');
		assertState("'' ''| '", editor);
		pressKey(editor, SWT.BS);
		assertState("'' '| '", editor);
	}
	
	public void testStringLiteral_8() throws Exception {
		XtextEditor editor = openEditor("'| ' ' '");
		pressKey(editor, '\'');
		assertState("''| ' ' '", editor);
		pressKey(editor, SWT.BS);
		assertState("'| ' ' '", editor);
	}

	public void testCurlyBracesBlock_1() throws Exception {
		XtextEditor editor = openEditor("|");
		pressKey(editor, '{');
		assertState("{|}", editor);
		pressKey(editor, '\n');
		assertState("{\n\t|\n}", editor);
		pressKey(editor, '\n');
		assertState("{\n\t\n\t|\n}", editor);
	}

	public void testCurlyBracesBlock_2() throws Exception {
		XtextEditor editor = openEditor("{|\n}");
		pressKey(editor, '\n');
		assertState("{\n\t|\n}", editor);
	}

	public void testCurlyBracesBlock_3() throws Exception {
		XtextEditor editor = openEditor("|");
		pressKey(editor, '}');
		assertState("}|", editor);
	}
	
	public void testCurlyBracesBlock_4() throws Exception {
		XtextEditor editor = openEditor("foo {|");
		pressKey(editor, '\n');
		assertState("foo {\n\t|\n}", editor);
	}
	
	public void testCurlyBracesBlock_5() throws Exception {
		XtextEditor editor = openEditor("{|}");
		pressKey(editor, '\n');
		assertState("{\n\t|\n}", editor);
	}
	
	public void testCurlyBracesBlock_6() throws Exception {
		XtextEditor editor = openEditor("{| }");
		pressKey(editor, '\n');
		assertState("{\n\t|\n}", editor);
	}
	
	public void testCurlyBracesBlock_7() throws Exception {
		XtextEditor editor = openEditor("{ |foo }");
		pressKey(editor, '\n');
		assertState("{ \n\t|foo }", editor);
	}
	
	public void testCurlyBracesBlock_8() throws Exception {
		XtextEditor editor = openEditor("{ foo| }");
		pressKey(editor, '\n');
		assertState("{ foo\n\t|\n}", editor);
	}
	
	public void testLongTerminalsBlock_1() throws Exception {
		XtextEditor editor = openEditor("begin|");
		pressKey(editor, '\n');
		assertState("begin\n\t|\nend", editor);
	}

	public void testLongTerminalsBlock_2() throws Exception {
		XtextEditor editor = openEditor("begin|\nend");
		pressKey(editor, '\n');
		assertState("begin\n\t|\nend", editor);
	}
	
	public void testMLComments_01() throws Exception {
		XtextEditor editor = openEditor("|");
		pressKey(editor, '/');
		pressKey(editor, '*');
		assertState("/*|*/", editor);
		
		pressKey(editor, '\n');
		assertState("/*\n * |\n */", editor);
		
		pressKey(editor, '\n');
		assertState("/*\n * \n * |\n */", editor);
		
		pressKeys(editor, "foo bar");
		pressKey(editor, '\n');
		assertState("/*\n * \n * foo bar\n * |\n */", editor);
	}
	
	public void testMLComments_02() throws Exception {
		XtextEditor editor = openEditor("   |");
		pressKey(editor, '/');
		pressKey(editor, '*');
		assertState("   /*|*/", editor);
		
		pressKey(editor, '\n');
		assertState("   /*\n    * |\n    */", editor);
		
		pressKey(editor, '\n');
		assertState("   /*\n    * \n    * |\n    */", editor);
		
		pressKeys(editor, "foo bar");
		pressKey(editor, '\n');
		assertState("   /*\n    * \n    * foo bar\n    * |\n    */", editor);
	}
	
// Unrealistic scenario. Doesn'T need to be supported.
//	public void testMLComments_03() throws Exception {
//		XtextEditor editor = openEditor("/*\n *| */");
//		
//		pressKey(editor, '\n');
//		assertState("/*\n *\n * |\n */", editor);
//	}

	public void testMLComments_04() throws Exception {
		XtextEditor editor = openEditor("\t/*\n\t *|\n\t */");
		
		pressKey(editor, '\n');
		assertState("\t/*\n\t *\n\t * |\n\t */", editor);
	}
	
	public void testMLComments_05() throws Exception {
		XtextEditor editor = openEditor("foo /*\n     *|\n      */");
		
		pressKey(editor, '\n');
		assertState("foo /*\n     *\n     * |\n      */", editor);
	}
	
	public void testMLComments_07() throws Exception {
		XtextEditor editor = openEditor("/* */|");
		
		pressKey(editor, '\n');
		assertState("/* */\n|", editor);
	}

	public void testMLComments_08() throws Exception {
		XtextEditor editor = openEditor("  /* foo |*/");
		
		pressKey(editor, '\n');
		assertState("  /* foo \n   * |\n   */", editor);
	}
	
	public void testMLComments_09() throws Exception {
		XtextEditor editor = openEditor("/* foo |*/");
		
		pressKey(editor, '\n');
		assertState("/* foo \n * |\n */", editor);
	}
	
	public void testMLComments_10() throws Exception {
		XtextEditor editor = openEditor("   /* foo |*/");
		
		pressKey(editor, '\n');
		assertState("   /* foo \n    * |\n    */", editor);
	}
	
	public void testMLComments_11() throws Exception {
		XtextEditor editor = openEditor("/* */\n * |");
		
		pressKey(editor, '\n');
		assertState("/* */\n * \n |", editor);
	}
	
	public void testMLComments_12() throws Exception {
		XtextEditor editor = openEditor("foo /*|*/");
		
		pressKey(editor, '\n');
		assertState("foo /*\n     * |\n     */", editor);
	}
	
	public void testMLComments_13() throws Exception {
		XtextEditor editor = openEditor("/* foo| */");
		pressKey(editor, '\n');
		assertState("/* foo\n * |\n */", editor);
	}
	
	public void testMLComments_14() throws Exception {
		XtextEditor editor = openEditor("/* foo|*/");
		pressKey(editor, '\n');
		assertState("/* foo\n * |\n */", editor);
	}
	
	public void testMLComments_15() throws Exception {
		XtextEditor editor = openEditor("  /* foo| */");
		pressKey(editor, '\n');
		assertState("  /* foo\n   * |\n   */", editor);
	}
	
	public void testMLComments_16() throws Exception {
		XtextEditor editor = openEditor("  /* foo|*/");
		pressKey(editor, '\n');
		assertState("  /* foo\n   * |\n   */", editor);
	}
	
	public void testShortcut_1() throws Exception {
		XtextEditor editor = openEditor("fb|");
		pressKey(editor, 'b');
		assertState("foobarbaz|", editor);
		// Don't know how to simulate the press ESC scenario. The following does not work.
		pressKey(editor, SWT.ESC);
		assertState("fbb|", editor);
	}

	private XtextEditor openEditor(String string) throws Exception {
		int cursor = string.indexOf('|');
		IFile file = createFile("foo/myfile" + files.size() + ".bmtestlanguage", string.replace("|", ""));
		files.add(file);
		XtextEditor editor = openEditor(file);
		editor.getInternalSourceViewer().setSelectedRange(cursor, 0);
		editor.getInternalSourceViewer().getTextWidget().setFocus();
		return editor;
	}

	protected void assertState(String string, XtextEditor editor) {
		int cursor = string.indexOf('|');
		assertEquals(string.replace("|", ""), editor.getDocument().get());
		ITextSelection selection = (ITextSelection) editor.getSelectionProvider().getSelection();
		assertEquals(cursor, selection.getOffset());
	}

	protected void pressKey(XtextEditor editor, char c) throws Exception {
		StyledText textWidget = editor.getInternalSourceViewer().getTextWidget();
		Event e = new Event();
		e.character = c;
		e.type = SWT.KeyDown;
		e.doit = true;
		//XXX Hack!
		if (c == SWT.ESC) {
			e.keyCode = 27;
		}
		textWidget.notifyListeners(SWT.KeyDown, e);
	}
	
	protected void pressKeys(XtextEditor editor, String string) throws Exception {
		for(int i = 0; i < string.length(); i++) {
			pressKey(editor, string.charAt(i));
		}
	}

}
