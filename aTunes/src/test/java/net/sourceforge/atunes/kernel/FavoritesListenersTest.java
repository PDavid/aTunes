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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.IFavoritesListener;

import org.junit.Before;
import org.junit.Test;


public class FavoritesListenersTest {

	private FavoritesListeners sut;	
	private IFavoritesListener mock1;
	private IFavoritesListener mock2;
	
	@Before
	public void init() {
		sut = new FavoritesListeners();
		List<IFavoritesListener> listeners = new ArrayList<IFavoritesListener>();
		mock1 = mock(IFavoritesListener.class);
		mock2 = mock(IFavoritesListener.class);
		listeners.add(mock1);
		listeners.add(mock2);
		sut.setListeners(listeners);
	}
	
	@Test
	public void favoritesChanged() {
		sut.favoritesChanged();
		
		verify(mock1).favoritesChanged();
		verify(mock2).favoritesChanged();
	}
}
