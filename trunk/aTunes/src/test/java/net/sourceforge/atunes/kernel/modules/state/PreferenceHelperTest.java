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

package net.sourceforge.atunes.kernel.modules.state;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

public class PreferenceHelperTest {

	@Test
	public void testNull() {
		PreferenceHelper sut = new PreferenceHelper();
		IStateStore stateStore = mock(IStateStore.class);
		sut.setStateStore(stateStore);
		when(stateStore.retrievePreference(Preferences.ALBUM_COLUMNS, null))
				.thenReturn(null);

		assertNull(sut.getPreference(Preferences.ALBUM_COLUMNS, String.class,
				null));
	}

	@Test
	public void testNotNull() {
		PreferenceHelper sut = new PreferenceHelper();
		IStateStore stateStore = mock(IStateStore.class);
		sut.setStateStore(stateStore);
		when(stateStore.retrievePreference(Preferences.ALBUM_COLUMNS, null))
				.thenReturn("TEST");

		assertEquals("TEST", sut.getPreference(Preferences.ALBUM_COLUMNS,
				String.class, null));
	}

	@Test
	public void testWrongObjectType() {
		PreferenceHelper sut = new PreferenceHelper();
		IStateStore stateStore = mock(IStateStore.class);
		sut.setStateStore(stateStore);
		when(stateStore.retrievePreference(Preferences.ALBUM_COLUMNS, null))
				.thenReturn("TEST");

		assertEquals(null, sut.getPreference(Preferences.ALBUM_COLUMNS,
				Integer.class, null));
	}

	@Test
	public void testWrongObjectType2() {
		PreferenceHelper sut = new PreferenceHelper();
		IStateStore stateStore = mock(IStateStore.class);
		sut.setStateStore(stateStore);
		when(stateStore.retrievePreference(Preferences.ALBUM_COLUMNS, 0))
				.thenReturn("TEST");

		assertTrue(sut.getPreference(Preferences.ALBUM_COLUMNS, Integer.class,
				0).intValue() == 0);

	}

}
