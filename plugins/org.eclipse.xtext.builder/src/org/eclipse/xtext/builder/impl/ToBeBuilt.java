/*******************************************************************************
 * Copyright (c) 2009 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.builder.impl;

import java.util.Set;

import org.eclipse.emf.common.util.URI;

import com.google.common.collect.Sets;

public class ToBeBuilt {
	private Set<URI> toBeUpdated = Sets.newHashSet();
	private Set<URI> toBeDeleted = Sets.newHashSet();
	
	public Set<URI> getToBeDeleted() {
		return toBeDeleted;
	}
	
	public Set<URI> getToBeUpdated() {
		return toBeUpdated;
	}

	public Set<URI> getAndRemoveToBeDeleted() {
		Set<URI> result = toBeDeleted;
		toBeDeleted = null;
		result.removeAll(toBeUpdated);
		return result;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ToBeBuilt [\n\ttoBeUpdated=");
		builder.append(toBeUpdated);
		builder.append(",\n\ttoBeDeleted=");
		builder.append(toBeDeleted);
		builder.append("\n]");
		return builder.toString();
	}

}