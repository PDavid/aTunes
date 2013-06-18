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
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.ISearchNode;
import net.sourceforge.atunes.utils.I18nUtils;

import org.apache.commons.collections.CollectionUtils;

/**
 * Calculates "NOT" operation
 * 
 * @author alex
 * 
 */
public class NotLogicalSearchOperator implements ILogicalSearchOperator {

	private IRepositoryHandler repositoryHandler;

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	@Override
	public String getDescription() {
		return I18nUtils.getString("THIS_RULE_IS_FALSE");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<IAudioObject> evaluate(List<ISearchNode> operands) {
		// This operand has only one operand
		if (operands.size() > 1) {
			throw new IllegalStateException("NOT has more than one operand");
		}
		Collection<IAudioObject> intersection = null;
		Collection<IAudioObject> operandResult = operands.get(0).evaluate();
		if (operandResult != null) {
			return CollectionUtils.disjunction(
					repositoryHandler.getAudioFilesList(), operandResult);
		} else {
			repositoryHandler.getAudioFilesList();
		}
		return intersection;
	}
}
