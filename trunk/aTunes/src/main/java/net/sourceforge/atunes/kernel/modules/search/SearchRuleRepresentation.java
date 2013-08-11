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

import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.ISearchField;
import net.sourceforge.atunes.model.ISearchNode;
import net.sourceforge.atunes.model.ISearchNodeRepresentation;
import net.sourceforge.atunes.model.ISearchOperator;

/**
 * @author alex
 * 
 */
public class SearchRuleRepresentation implements ISearchNodeRepresentation {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7249890492370285688L;

	String field;

	String operator;

	String value;

	/**
	 * Default constructor for serialization
	 */
	public SearchRuleRepresentation() {
	}

	/**
	 * @param rule
	 */
	protected SearchRuleRepresentation(final SearchRule rule) {
		this.field = rule.getField().getClass().getName();
		this.operator = rule.getOperator().getClass().getName();
		this.value = rule.getValue();
	}

	@Override
	public ISearchNode createSearchQuery(final IBeanFactory beanFactory) {
		return new SearchRule(beanFactory.getBeanByClassName(this.field,
				ISearchField.class), beanFactory.getBeanByClassName(
				this.operator, ISearchOperator.class), this.value);
	}
}
