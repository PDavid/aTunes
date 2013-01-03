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

package net.sourceforge.atunes.kernel.modules.favorites;

import net.sourceforge.atunes.model.IFavorites;
import net.sourceforge.atunes.model.IHandlerBackgroundInitializationTask;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IStateHandler;

/**
 * Reads favorites
 * @author alex
 *
 */
public class FavoritesInitializationTask implements IHandlerBackgroundInitializationTask {
	
	private FavoritesHandler favoritesHandler;
	
	private IStateHandler stateHandler;
	
	private INavigationHandler navigationHandler;
	
	private IPlayListHandler playListHandler;
	
	private INavigationView favoritesNavigationView;
	
	/**
	 * @param favoritesNavigationView
	 */
	public void setFavoritesNavigationView(INavigationView favoritesNavigationView) {
		this.favoritesNavigationView = favoritesNavigationView;
	}
	
	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}
	
	/**
	 * @param navigationHandler
	 */
	public void setNavigationHandler(INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}
	
	/**
	 * @param favoritesHandler
	 */
	public void setFavoritesHandler(FavoritesHandler favoritesHandler) {
		this.favoritesHandler = favoritesHandler;
	}
	
	/**
	 * @param stateHandler
	 */
	public void setStateHandler(IStateHandler stateHandler) {
		this.stateHandler = stateHandler;
	}
	
	@Override
	public Runnable getInitializationTask() {
		return new Runnable() {

			@Override
			public void run() {
				IFavorites favorites = stateHandler.retrieveFavoritesCache();
				if (favorites != null) {
					favoritesHandler.setFavorites(favorites);
				}
			}
		};
	}
	
	@Override
	public Runnable getInitializationCompletedTask() {
		return new Runnable() {
			@Override
			public void run() {
				// Update navigator
				navigationHandler.refreshView(favoritesNavigationView);
				// Update playlist
				playListHandler.refreshPlayList();
			}
		};
	}
}
