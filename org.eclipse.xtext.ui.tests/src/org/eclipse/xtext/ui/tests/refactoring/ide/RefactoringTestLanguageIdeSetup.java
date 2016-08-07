/*
 * generated by Xtext
 */
package org.eclipse.xtext.ui.tests.refactoring.ide;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.eclipse.xtext.ui.tests.refactoring.RefactoringTestLanguageRuntimeModule;
import org.eclipse.xtext.ui.tests.refactoring.RefactoringTestLanguageStandaloneSetup;

/**
 * Initialization support for running Xtext languages without Equinox extension registry.
 */
public class RefactoringTestLanguageIdeSetup extends RefactoringTestLanguageStandaloneSetup {

	@Override
	public Injector createInjector() {
		return Guice.createInjector(new RefactoringTestLanguageRuntimeModule(), new RefactoringTestLanguageIdeModule());
	}
}