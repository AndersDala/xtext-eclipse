package org.eclipse.xtext.ui.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.xtext.resource.IClasspathUriResolver;
import org.eclipse.xtext.ui.internal.Activator;

public class WorkspaceClasspathUriResolverTest extends AbstractClasspathUriResolverTest {

	private IJavaProject _javaProject;
	private IClasspathUriResolver _resolver;

	@Override
	protected void setUp() throws Exception {
		_resolver = new WorkspaceClasspathUriResolver();
	}


	public void testClasspathUriForFileInWorkspace() throws Exception {
		_javaProject = JavaProjectSetupUtil.createJavaProject(TEST_PROJECT_NAME);
		_project = _javaProject.getProject();
		PluginUtil.copyFileToWorkspace(Activator.getDefault(), "/testfiles/" + MODEL_FILE, _project, "src/"
				+ MODEL_FILE);
		URI classpathUri = URI.createURI("classpath:/" + MODEL_FILE);
		String expectedUri = "platform:/resource/" + TEST_PROJECT_NAME + "/src/" + MODEL_FILE;
		URI normalizedUri = _resolver.resolve(_project, classpathUri);
		assertResourceLoadable(classpathUri, normalizedUri, expectedUri);
	}

	public void testClasspathUriForFileInJarInWorkspace() throws Exception {
		_javaProject = JavaProjectSetupUtil.createJavaProject(TEST_PROJECT_NAME);
		_project = _javaProject.getProject();
		IFile jarFile = PluginUtil.copyFileToWorkspace(Activator.getDefault(), "/testfiles/" + JAR_FILE, _project, "/"
				+ JAR_FILE);
		JavaProjectSetupUtil.addJarToClasspath(_javaProject, jarFile);
		URI classpathUri = URI.createURI("classpath:/model/" + MODEL_FILE);
		String expectedUri = "jar:platform:/resource/" + TEST_PROJECT_NAME + "/" + JAR_FILE + "!/model/" + MODEL_FILE;
		URI normalizedUri = _resolver.resolve(_project, classpathUri);
		assertResourceLoadable(classpathUri, normalizedUri, expectedUri);
	}

}
