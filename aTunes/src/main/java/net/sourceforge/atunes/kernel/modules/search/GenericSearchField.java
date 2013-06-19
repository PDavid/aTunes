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
import net.sourceforge.atunes.model.ISearchBinaryOperator;
import net.sourceforge.atunes.model.ISearchField;
import net.sourceforge.atunes.model.ISearchOperator;
import net.sourceforge.atunes.model.ISearchUnaryOperator;

/**
 * Common search field logic
 * 
 * @author alex
 * 
 * @param <O>
 * @param <F>
 */
public abstract class GenericSearchField<O, F> implements ISearchField<O, F> {

	@SuppressWarnings("unchecked")
	@Override
	public final List<IAudioObject> evaluate(final ISearchOperator operator,
			final String originalValue) {
		F value = transform(originalValue);
		List<IAudioObject> audioObjects = new ArrayList<IAudioObject>();
		Collection<O> objectsToEvaluate = getObjectsForEvaluation();
		if (operator instanceof ISearchBinaryOperator) {
			evaluateBinaryOperator((ISearchBinaryOperator<F>) operator, value,
					audioObjects, objectsToEvaluate);
		} else {
			evaluateUnaryOperator((ISearchUnaryOperator<O>) operator,
					audioObjects, objectsToEvaluate);
		}
		return audioObjects;
	}

	private void evaluateBinaryOperator(
			final ISearchBinaryOperator<F> operator, F value,
			List<IAudioObject> audioObjects, Collection<O> objectsToEvaluate) {
		for (O objectToEvaluate : objectsToEvaluate) {
			if (operator.evaluate(getValueForEvaluation(objectToEvaluate),
					value)) {
				audioObjects.addAll(getAudioObjects(objectToEvaluate));
			}
		}
	}

	private void evaluateUnaryOperator(final ISearchUnaryOperator<O> operator,
			List<IAudioObject> audioObjects, Collection<O> objectsToEvaluate) {
		for (O objectToEvaluate : objectsToEvaluate) {
			if (operator.evaluate(objectToEvaluate)) {
				audioObjects.addAll(getAudioObjects(objectToEvaluate));
			}
		}
	}
}
