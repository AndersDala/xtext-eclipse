/*******************************************************************************
 * Copyright (c) 2009 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.ui.editor.quickfix;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.xtext.validation.IssueContext;
import org.eclipse.xtext.validation.IssueResolution;
import org.eclipse.xtext.validation.IssueResolutionProvider;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * @author Knut Wannheden - Initial contribution and API
 * @author Heiko Behrens
 */
public class AbstractDeclarativeQuickfixProvider implements IssueResolutionProvider {

	@Inject
	private ILanguageResourceHelper languageResourceHelper;
	
	public boolean isLanguageResource(IResource resource) {
		return languageResourceHelper.isLanguageResource(resource);
	}

	protected Predicate<Method> getFixMethodPredicate(final Class<? extends Object> target, final String issueCode) {
		return new Predicate<Method>() {
			public boolean apply(Method input) {
				Fix annotation = input.getAnnotation(Fix.class);
				boolean result = input.getParameterTypes().length == 2 && Void.TYPE == input.getReturnType()
						&& (target == null || input.getParameterTypes()[0].isAssignableFrom(target))
						&& input.getParameterTypes()[1].isAssignableFrom(IssueContext.class)
						&& annotation != null && issueCode != null && issueCode.equals(annotation.code());
				return result;
			}
		};
	}

	public List<IssueResolution> getResolutions(final IssueContextProvider provider, List<Method> fixMethods) {
		return Lists.transform(fixMethods, new Function<Method, IssueResolution>() {
			public IssueResolution apply(final Method from) {
				return new IssueResolution() {
					private final Fix annotation = from.getAnnotation(Fix.class);

					public void run() {
						executeFixMethod(from, provider.getIssueContext());
					}

					public String getLabel() {
						return annotation.label();
					}

					public String getDescription() {
						return annotation.description();
					}
					
					public String getImage() {
						return annotation.image();
					}
				};
			}
		});
	}

	protected void executeFixMethod(final Method method, final IssueContext issueContext) {
		try {
			method.invoke(AbstractDeclarativeQuickfixProvider.this, new Object[] { issueContext.getModel(), issueContext });
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	protected Iterable<Method> collectMethods(Class<? extends AbstractDeclarativeQuickfixProvider> clazz, Class<? extends Object> target, String issueCode) {
		List<Method> methods = Lists.newArrayList(clazz.getMethods());
		return Iterables.filter(methods, getFixMethodPredicate(target, issueCode));
	}

	protected List<Method> getFixMethods(final IssueContext issueContext) {
		if (issueContext.getModel() == null)
			return Collections.emptyList();

		return Lists.newArrayList(collectMethods(getClass(), issueContext.getModel().getClass(), issueContext.getIssue().getCode()));
	}

	public boolean hasResolutionFor(final String issueCode) {
		// TODO : where should this be extracted to?
//		if (!isLanguageResource(issue.getResource()))
//			return false;
		
		if (issueCode == null)
			return false;

		// TODO : cache this if it's a performance hit
		Iterable<Method> methods = collectMethods(getClass(), null, issueCode);
		return methods.iterator().hasNext();
	}

	public List<IssueResolution> getResolutions(final IssueContext issueContext) {
		final IssueContextProvider provider = new IssueContextProvider() {
			public IssueContext getIssueContext() {
				return issueContext;
			}
		};

		List<Method> fixMethods = getFixMethods(issueContext);
		return getResolutions(provider, fixMethods);
	}
	
	public List<IssueResolution> getResolutions(String issueCode, IssueContextProvider provider) {
		List<Method> fixMethods = Lists.newArrayList(collectMethods(getClass(), null, issueCode));
		return getResolutions(provider, fixMethods);
	}
	
	
	public void setLanguageResourceHelper(ILanguageResourceHelper languageResourceHelper) {
		this.languageResourceHelper = languageResourceHelper;
	}

	public ILanguageResourceHelper getLanguageResourceHelper() {
		return languageResourceHelper;
	}

}
