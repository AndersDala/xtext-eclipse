/*******************************************************************************
 * Copyright (c) 2017 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.ui.editor.contentassist

import com.google.inject.Inject
import com.google.inject.Provider
import java.util.ArrayList
import org.eclipse.emf.ecore.EObject
import org.eclipse.jface.viewers.StyledString
import org.eclipse.swt.graphics.Image
import org.eclipse.xtext.Assignment
import org.eclipse.xtext.Keyword
import org.eclipse.xtext.RuleCall
import org.eclipse.xtext.ide.editor.contentassist.ContentAssistContext.Builder
import org.eclipse.xtext.ide.editor.contentassist.ContentAssistEntry
import org.eclipse.xtext.ide.editor.contentassist.IIdeContentProposalAcceptor
import org.eclipse.xtext.ide.editor.contentassist.IdeContentProposalProvider
import org.eclipse.xtext.resource.IEObjectDescription
import org.eclipse.xtext.ui.editor.contentassist.AbstractContentProposalProvider.NullSafeCompletionProposalAcceptor
import org.eclipse.xtext.util.TextRegion

/**
 * Delegates to the generic IDE content proposal provider. Use this Implementation to share the same content assist
 * code between Eclipse and other editors for your DSL.
 * 
 * @author Titouan Vervack - Initial contribution and API
 * 
 * @since 2.13
 */
class UiToIdeContentProposalProvider extends AbstractContentProposalProvider {
    
	@Inject IdeContentProposalProvider ideProvider
	@Inject Provider<Builder> builderProvider
    
	override createProposals(ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		val entries = new ArrayList<Pair<ContentAssistEntry, Integer>>
        val ideAcceptor = new IIdeContentProposalAcceptor {
            override accept(ContentAssistEntry entry, int priority) {
                entries += entry -> priority
            }
            override canAcceptMoreProposals() {
                entries.size < maxProposals
            }
        }
		ideProvider.createProposals(#[context.getIdeContext], ideAcceptor)
		val uiAcceptor = new NullSafeCompletionProposalAcceptor(acceptor)

		entries.forEach [ p, idx |
            val entry = p.key
			val proposal = doCreateProposal(entry.proposal, entry.displayString, entry.image, p.value, context)
			uiAcceptor.accept(proposal)
		]
	}
	
	protected def int getMaxProposals() {
		1000
	}

	private def org.eclipse.xtext.ide.editor.contentassist.ContentAssistContext getIdeContext(ContentAssistContext c) {
		val builder = builderProvider.get()
		val replaceRegion = c.replaceRegion
		builder
			.setPrefix(c.prefix)
			.setSelectedText(c.selectedText)
			.setRootModel(c.rootModel)
			.setRootNode(c.rootNode)
			.setCurrentModel(c.currentModel)
			.setPreviousModel(c.previousModel)
			.setCurrentNode(c.currentNode)
			.setLastCompleteNode(c.lastCompleteNode)
			.setOffset(c.offset)
			.setReplaceRegion(new TextRegion(replaceRegion.offset, replaceRegion.length))
			.setResource(c.resource)
		for (grammarElement : c.firstSetGrammarElements) {
			builder.accept(grammarElement)
		}
		return builder.toContext()
	}
    
    protected def StyledString getDisplayString(ContentAssistEntry entry) {
        val result = new StyledString(entry.label ?: entry.proposal)
        if (!entry.description.nullOrEmpty)
            result.append(new StyledString(' \u2013 ' + entry.description, StyledString.QUALIFIER_STYLER))
        return result
    }

    protected def Image getImage(ContentAssistEntry entry) {
        switch source: entry.source {
            IEObjectDescription:
                getImage(source)
            EObject:
                getImage(source)
        }
    }
    
    /**
     * This method does nothing and should not be used.
     */
    override final completeAssignment(Assignment object, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    }
    
    /**
     * This method does nothing and should not be used.
     */
    override final completeKeyword(Keyword object, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    }
    
    /**
     * This method does nothing and should not be used.
     */
    override final completeRuleCall(RuleCall object, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
    }
    
}
