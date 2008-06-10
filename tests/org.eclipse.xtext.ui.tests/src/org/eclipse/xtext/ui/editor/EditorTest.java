/*******************************************************************************
 * Copyright (c) 2008 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.xtext.ui.editor;

import junit.framework.TestCase;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.texteditor.ContentAssistAction;
import org.eclipse.xtext.ui.core.editor.BaseTextEditor;

/**
 * @author Dennis H�bner - Initial contribution and API
 * 
 */
public class EditorTest extends TestCase {

	private static final long STEP_DELAY = 0;
	private static final String EDITOR_ID = "org.eclipse.xtext.reference.ui_gen.ui.editor.ReferenceGrammarXtextEditor";
	private BaseTextEditor openedEditor;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();

	}

	public void testOpenBlankFile() throws Exception {
		openEditor(createBlankEditorInput());
		assertNotNull(openedEditor);
		assertNotNull(openedEditor.getLanguageDescriptor());
	}

	private void openEditor(IEditorInput editorInput) throws Exception {
		openedEditor = (BaseTextEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.openEditor(editorInput, EDITOR_ID);
		waitForJobCompletion();
		sleep(STEP_DELAY);
	}

	public void testOpenFile() throws Exception {
		openEditor(createTestEditorInput());
		assertNotNull(openedEditor);
		assertNotNull(openedEditor.getLanguageDescriptor());
		assertNotNull(openedEditor.getModel());
	}

	public void testActions() throws Exception {
		openEditor(createTestEditorInput());
		ContentAssistAction caAction = (ContentAssistAction) openedEditor.getAction("ContentAssistProposal");
		assertNotNull(caAction);
		caAction.update();
		openedEditor.selectAndReveal(5, 0);
		sleep(STEP_DELAY);
		waitForJobCompletion();
		caAction.run();
		sleep(STEP_DELAY);
		// TODO test implementation
	}

	private IEditorInput createTestEditorInput() {
		return createEditorInput("./resources/xtest.tst");
	}

	private IEditorInput createBlankEditorInput() {
		return createEditorInput("./resources/xtestblank.test");
	}

	private IEditorInput createEditorInput(String fullPath) {
		IFileStore fileStore = EFS.getLocalFileSystem().getStore(new Path(fullPath));
		IEditorInput editorInput = new FileStoreEditorInput(fileStore);
		return editorInput;
	}

	private void waitForJobCompletion() throws InterruptedException {
		while (Job.getJobManager().currentJob() != null)
			sleep(500);
	}

	private void sleep(long i) throws InterruptedException {
		Display displ = Display.getCurrent();
		if (displ != null) {
			long timeToGo = System.currentTimeMillis() + i;
			while (System.currentTimeMillis() < timeToGo) {
				if (!displ.readAndDispatch())
					displ.sleep();
			}
			displ.update();
		}
		else {
			Thread.sleep(i);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		if (openedEditor != null)
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeEditor(openedEditor, false);
	}

}
