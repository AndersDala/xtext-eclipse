/*******************************************************************************
 * Copyright (c) 2010 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.builder.impl;

import static org.eclipse.xtext.ui.junit.util.IResourcesSetupUtil.*;
import static org.eclipse.xtext.ui.junit.util.JavaProjectSetupUtil.*;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.xtext.builder.IXtextBuilderParticipant;
import org.eclipse.xtext.builder.IXtextBuilderParticipant.BuildType;
import org.eclipse.xtext.builder.tests.Activator;
import org.eclipse.xtext.builder.tests.LoggingBuilderParticipant;
import org.eclipse.xtext.ui.XtextProjectHelper;
import org.eclipse.xtext.util.StringInputStream;

/**
 * @author Sebastian Zarnekow - Initial contribution and API
 */
public class BuilderParticipantTest extends AbstractBuilderTest {

	private LoggingBuilderParticipant participant;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		IXtextBuilderParticipant instance = Activator.getInstance().getInjector(
				"org.eclipse.xtext.builder.tests.BuilderTestLanguage").getInstance(IXtextBuilderParticipant.class);
		participant = (LoggingBuilderParticipant) instance;
		participant.startLogging();
	}
	
	@Override
	protected void tearDown() throws Exception {
		participant.stopLogging();
		participant = null;
		super.tearDown();
	}
	
	public void testParticipantInvoked() throws Exception {
		IJavaProject project = createJavaProject("foo");
		addNature(project.getProject(), XtextProjectHelper.NATURE_ID);
		IFolder folder = addSourceFolder(project, "src");
		IFile file = folder.getFile("Foo" + F_EXT);
		file.create(new StringInputStream("object Foo"), true, monitor());
		waitForAutoBuild();
		assertTrue(0 < participant.getInvocationCount());
		participant.reset();
		
		file.delete(true, monitor());
		waitForAutoBuild();
		assertEquals(1, participant.getInvocationCount());
		assertSame(BuildType.INCREMENTAL, participant.getContext().getBuildType());
		participant.reset();
		
		project.getProject().build(IncrementalProjectBuilder.CLEAN_BUILD, monitor());
		assertSame(BuildType.CLEAN, participant.getContext().getBuildType());
		waitForAutoBuild();
		assertEquals(2, participant.getInvocationCount());
		assertSame(BuildType.FULL, participant.getContext().getBuildType());
		participant.reset();
		
		project.getProject().build(IncrementalProjectBuilder.FULL_BUILD, monitor());
		assertSame(BuildType.FULL, participant.getContext().getBuildType());
		participant.reset();
	}
	
}
