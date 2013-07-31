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

import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ISearchField;
import net.sourceforge.atunes.model.ISearchNode;
import net.sourceforge.atunes.model.ISearchNodeRepresentation;
import net.sourceforge.atunes.model.ISearchOperator;
import net.sourceforge.atunes.model.ISearchRule;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This class represents a simple rule.
 */
public final class SearchRule implements ISearchRule, ISearchNode {

	/** The search field */
	private ISearchField<?, ?> field;

	/** The operator. */
	private ISearchOperator operator;

	/** The value. */
	private String value;

	/**
	 * @param field
	 * @param operator
	 * @param value
	 */
	public SearchRule(final ISearchField<?, ?> field,
			final ISearchOperator operator, final String value) {
		this.field = field;
		this.operator = operator;
		this.value = value;
	}

	/**
	 * @param field
	 */
	public void setField(final ISearchField<?, ?> field) {
		this.field = field;
	}

	@Override
	public ISearchField<?, ?> getField() {
		return this.field;
	}

	@Override
	public ISearchOperator getOperator() {
		return this.operator;
	}

	/**
	 * Sets the operator.
	 * 
	 * @param operator
	 *            the operator to set
	 */
	public void setOperator(final ISearchOperator operator) {
		this.operator = operator;
	}

	@Override
	public String getValue() {
		return this.value;
	}

	/**
	 * Sets the value.
	 * 
	 * @param value
	 *            the value to set
	 */
	public void setValue(final String value) {
		this.value = value;
	}

	@Override
	public final List<IAudioObject> evaluate() {
		return this.field.evaluate(this.operator, this.value);
	}

	@Override
	public String toString() {
		return StringUtils.getString("(", this.field.getName(), " ",
				this.operator.getDescription(), " ", this.value, ")");
	}

	@Override
	public ISearchNodeRepresentation getRepresentation() {
		return new SearchRuleRepresentation(this);
	}
}
