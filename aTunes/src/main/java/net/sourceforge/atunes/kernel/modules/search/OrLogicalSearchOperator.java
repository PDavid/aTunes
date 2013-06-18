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

import java.util.Collection;
import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILogicalSearchOperator;
import net.sourceforge.atunes.model.ISearchNode;
import net.sourceforge.atunes.utils.I18nUtils;

import org.apache.commons.collections.CollectionUtils;

/**
 * Calculates "OR" operation
 * 
 * @author alex
 * 
 */
public class OrLogicalSearchOperator implements ILogicalSearchOperator {

	@Override
	public String getDescription() {
		return I18nUtils.getString("AT_LEAST_ONE_RULE_IS_TRUE");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<IAudioObject> evaluate(List<ISearchNode> operands) {
		Collection<IAudioObject> union = null;
		for (ISearchNode operand : operands) {
			Collection<IAudioObject> operandResult = operand.evaluate();
			if (operandResult != null) {
				if (union == null) {
					union = operandResult;
				} else {
					union = CollectionUtils.union(union, operandResult);
				}
			}
		}
		return union;
	}
}
