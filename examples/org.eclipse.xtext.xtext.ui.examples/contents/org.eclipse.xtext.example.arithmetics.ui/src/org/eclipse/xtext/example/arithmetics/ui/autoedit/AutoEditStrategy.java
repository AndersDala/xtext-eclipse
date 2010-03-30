package org.eclipse.xtext.example.arithmetics.ui.autoedit;

import org.eclipse.xtext.ui.editor.autoedit.DefaultAutoEditStrategy;

public class AutoEditStrategy extends DefaultAutoEditStrategy {
	@Override
	protected void configure(IEditStrategyAcceptor acceptor) {
		super.configure(acceptor);
		acceptor.accept(newShortCuts("PI", "�"));
		acceptor.accept(new InterpreterAutoEdit());
	}
}
