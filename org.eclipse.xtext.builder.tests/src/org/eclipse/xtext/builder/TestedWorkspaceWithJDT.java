/*******************************************************************************
 * Copyright (c) 2019 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.xtext.builder.impl.ProjectOpenedOrClosedListener;
import org.eclipse.xtext.ui.shared.contribution.ISharedStateContributionRegistry;
import org.eclipse.xtext.ui.testing.util.IResourcesSetupUtil;
import org.eclipse.xtext.ui.testing.util.JavaProjectSetupUtil;
import org.eclipse.xtext.util.Exceptions;
import org.junit.runner.Description;

import com.google.common.annotations.Beta;
import com.google.inject.Inject;

/**
 * @author Sebastian Zarnekow - Initial contribution and API
 */
@Beta
public class TestedWorkspaceWithJDT extends TestedWorkspace {

	private volatile Job updateClasspathJob;
	private final UpdateClasspathJobListener listener = new UpdateClasspathJobListener();

	class UpdateClasspathJobListener implements IJobChangeListener {

		@Override
		public void aboutToRun(IJobChangeEvent event) {
			// ignore
		}

		@Override
		public void awake(IJobChangeEvent event) {
			// ignore
		}

		@Override
		public void done(IJobChangeEvent event) {
			// ignore
		}

		@Override
		public void running(IJobChangeEvent event) {
			// ignore
		}

		@Override
		public void scheduled(IJobChangeEvent event) {
			if (updateClasspathJob == null) {
				Class<? extends Job> jobType = event.getJob().getClass();
				if (org.eclipse.pde.internal.core.PluginModelManager.class.equals(jobType.getEnclosingClass())) {
					updateClasspathJob = event.getJob();
				}
			}
		}

		@Override
		public void sleeping(IJobChangeEvent event) {
			// ignore
		}
	}

	@Inject
	public TestedWorkspaceWithJDT(ISharedStateContributionRegistry contributions) {
		this(contributions.getSingleContributedInstance(ProjectOpenedOrClosedListener.class));
	}

	public TestedWorkspaceWithJDT(ProjectOpenedOrClosedListener closedProjectTaskProcessor) {
		super(closedProjectTaskProcessor);
	}

	@Override
	protected void starting(Description d) {
		super.starting(d);
		addJobListener();
	}
	
	private void addJobListener() {
		IJobManager jobManager = Job.getJobManager();
		jobManager.addJobChangeListener(listener);
	}

	private void removeJobListener() {
		IJobManager jobManager = Job.getJobManager();
		jobManager.removeJobChangeListener(listener);
		this.updateClasspathJob = null;
	}

	@Override
	protected void finished(Description description) {
		super.finished(description);
		removeJobListener();
	}

	public void joinUpdateClasspathJob() {
		try {
			// Pseudo fence to make sure that we see what we want to see.
			synchronized (this) {
				wait(1);
			}
			Job joinMe = updateClasspathJob;
			if (updateClasspathJob != null) {
				joinMe.join();
			}
		} catch (Exception e) {
			Exceptions.throwUncheckedException(e);
		}
	}

	@Override
	public void joinJobsBeforeBuild() {
		joinUpdateClasspathJob();
		super.joinJobsBeforeBuild();
	}

	public void addToClasspath(IJavaProject project, IClasspathEntry newClassPathEntry) {
		try {
			JavaProjectSetupUtil.addToClasspath(project, newClassPathEntry, true);
			build();
		} catch(Exception e) {
			Exceptions.throwUncheckedException(e);
		}
	}

	public IJavaProject createJavaProject(String name) {
		try {
			IJavaProject result = JavaProjectSetupUtil.createJavaProject(name, false);
			build();
			return result;
		} catch(Exception e) {
			return Exceptions.throwUncheckedException(e);
		}
	}

	public IClasspathEntry addJarToClasspath(IJavaProject project, IFile jarFile) {
		try {
			IClasspathEntry result = JavaProjectSetupUtil.addJarToClasspath(project, jarFile);
			build();
			return result;
		} catch(Exception e) {
			return Exceptions.throwUncheckedException(e);
		}
	}

	public void addProjectReference(IJavaProject from, IJavaProject to) {
		try {
			JavaProjectSetupUtil.addProjectReference(from, to);
			build();
		} catch(Exception e) {
			Exceptions.throwUncheckedException(e);
		}
	}
	
	public void removeProjectReference(IJavaProject from, IJavaProject to) {
		try {
			JavaProjectSetupUtil.removeProjectReference(from, to);
			build();
		} catch(Exception e) {
			Exceptions.throwUncheckedException(e);
		}
	}

	public void addSourceFolder(IJavaProject project, String folder) {
		try {
			JavaProjectSetupUtil.addSourceFolder(project, folder, false);
			build();
		} catch(Exception e) {
			Exceptions.throwUncheckedException(e);
		}
	}

	public IFolder createSubFolder(IProject project, String name) {
		try {
			return IResourcesSetupUtil.createFolder(project.getFolder(name).getFullPath());
		} catch(Exception e) {
			return Exceptions.throwUncheckedException(e);
		}
	}

	public void deleteClasspathEntry(IJavaProject project, IPath entry) {
		try {
			JavaProjectSetupUtil.deleteClasspathEntry(project, entry);
		} catch(Exception e) {
			Exceptions.throwUncheckedException(e);
		}
	}

}
