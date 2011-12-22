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

package net.sourceforge.atunes.kernel;

import java.util.Collection;

import net.sourceforge.atunes.model.IFavoritesListener;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Calls to FavoritesListener instances
 * @author fleax
 *
 */
public class FavoritesListeners implements ApplicationContextAware {

	private Collection<IFavoritesListener> listeners;
	
	@Override
	public void setApplicationContext(ApplicationContext ctx) {
		listeners = ctx.getBeansOfType(IFavoritesListener.class).values();
	}
	
	protected void setListeners(Collection<IFavoritesListener> listeners) {
		this.listeners = listeners;
	}
	
    /**
     * Called when favorites changed
     */
    public void favoritesChanged() {
    	for (IFavoritesListener listener : listeners) {
    		listener.favoritesChanged();
    	}
    }
}
