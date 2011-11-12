/*
 * aTunes 2.2.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.utils;

import net.sourceforge.atunes.model.IState;

/**
 * Returns java.text.Collator to use according to state locale
 * @author alex
 *
 */
public class Collator {

	private IState state;
	
	/**
	 * @param state
	 */
	public void setState(IState state) {
		this.state = state;
	}
	
	/**
	 * The Collator to use according to state locale
	 * @return
	 */
	public java.text.Collator getCollator() {
		if (state == null || state.getLocale() == null) {
			return java.text.Collator.getInstance();
		}
        return java.text.Collator.getInstance(state.getLocale().getLocale());
	}
}