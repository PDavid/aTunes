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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.io.Serializable;

import net.sourceforge.atunes.model.ISearchNodeRepresentation;

/**
 * Date to write / read to a play list file
 * 
 * @author alex
 * 
 */
public class DynamicPlayListData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3506979542915411533L;

	ISearchNodeRepresentation query;

	/**
	 * @param query
	 */
	public void setQuery(final ISearchNodeRepresentation query) {
		this.query = query;
	}

	/**
	 * @return
	 */
	public ISearchNodeRepresentation getQuery() {
		return this.query;
	}

}
