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

package net.sourceforge.atunes.model;

import java.util.Collection;
import java.util.List;

/**
 * Defines a field to make queries
 * 
 * @author alex
 * 
 * @param <O>
 * @param <F>
 */
public interface ISearchField<O, F> {

	/**
	 * @return name of field
	 */
	String getName();

	/**
	 * @return operators that can be used with this field
	 */
	List<ISearchOperator> getOperators();

	/**
	 * Transforms original string value entered by user to internal data type
	 * needed for search
	 * 
	 * @param originalValue
	 * @return
	 */
	F transform(String originalValue);

	/**
	 * @param operator
	 * @param originalValue
	 * @return list of objects matching value with given operator
	 */
	List<IAudioObject> evaluate(ISearchOperator operator, String originalValue);

	/**
	 * @param object
	 * @return audio objects associated to this object
	 */
	List<IAudioObject> getAudioObjects(O object);

	/**
	 * @return objects used for evaluation of field
	 */
	Collection<O> getObjectsForEvaluation();

	/**
	 * @param object
	 * @return value for evaluation
	 */
	F getValueForEvaluation(O object);
}
