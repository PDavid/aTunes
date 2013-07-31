/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
 *
 * See http://www.atunes.org/wiki/index.php?title=Contributing for information about contributors
 *
 * http://www.atunes.org
 * http://sourceforge.net/projects/atunes
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package net.sourceforge.atunes.kernel.modules.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILogicalSearchOperator;
import net.sourceforge.atunes.model.ISearchNode;
import net.sourceforge.atunes.model.ISearchNodeRepresentation;

/**
 * A logical operator to apply to operands
 * 
 * @author alex
 * 
 */
public final class LogicalSearchNode implements ISearchNode {

	private ILogicalSearchOperator operator;

	private final List<ISearchNode> children = new ArrayList<ISearchNode>();

	/**
	 * @param operator
	 */
	public void setOperator(final ILogicalSearchOperator operator) {
		this.operator = operator;
	}

	@Override
	public final Collection<IAudioObject> evaluate() {
		return this.operator.evaluate(this.children);
	}

	/**
	 * @param node
	 */
	public void addChild(final ISearchNode node) {
		this.children.add(node);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(this.operator.getDescription());
		sb.append(" ");
		for (ISearchNode child : this.children) {
			sb.append(child);
		}
		sb.append(")");
		return sb.toString();
	}

	@Override
	public ISearchNodeRepresentation getRepresentation() {
		return new LogicalSearchNodeRepresentation(this);
	}

	/**
	 * @return children
	 */
	protected List<ISearchNode> getChildren() {
		return this.children;
	}

	/**
	 * @return operator
	 */
	protected ILogicalSearchOperator getOperator() {
		return this.operator;
	}
}
