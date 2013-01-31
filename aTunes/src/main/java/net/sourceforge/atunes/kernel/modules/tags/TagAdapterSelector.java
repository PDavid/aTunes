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

package net.sourceforge.atunes.kernel.modules.tags;

import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ITagAdapter;

/**
 * Selects tag adapter for a specific file type
 * 
 * @author alex
 * 
 */
public class TagAdapterSelector {

	private JAudiotaggerTagAdapter jAudiotaggerTagAdapter;

	/**
	 * @param jAudiotaggerTagAdapter
	 */
	public void setjAudiotaggerTagAdapter(
			JAudiotaggerTagAdapter jAudiotaggerTagAdapter) {
		this.jAudiotaggerTagAdapter = jAudiotaggerTagAdapter;
	}

	/**
	 * Selects adapter for given local audio object
	 * 
	 * @param audioObject
	 * @return
	 */
	ITagAdapter selectAdapter(final ILocalAudioObject audioObject) {
		return jAudiotaggerTagAdapter;
	}
}
