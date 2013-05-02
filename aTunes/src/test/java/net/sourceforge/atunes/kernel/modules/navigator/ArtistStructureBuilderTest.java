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

package net.sourceforge.atunes.kernel.modules.navigator;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IUnknownObjectChecker;

import org.junit.Test;

public class ArtistStructureBuilderTest {

	@Test
	public void testGetArtistsList() {
		ArtistStructureBuilder sut = new ArtistStructureBuilder();
		IUnknownObjectChecker unknownObjectChecker = mock(IUnknownObjectChecker.class);
		sut.setUnknownObjectChecker(unknownObjectChecker);

		List<ILocalAudioObject> aos = new ArrayList<ILocalAudioObject>();
		ILocalAudioObject ao1 = mock(ILocalAudioObject.class);
		when(ao1.getAlbumArtistOrArtist(unknownObjectChecker)).thenReturn(
				"Children of Bodom");
		when(ao1.getArtist(unknownObjectChecker)).thenReturn(
				"Children of Bodom");
		when(ao1.getAlbumArtist(unknownObjectChecker)).thenReturn(
				"Children of Bodom");
		when(ao1.getAlbum(unknownObjectChecker))
				.thenReturn("Children of Bodom");
		ILocalAudioObject ao2 = mock(ILocalAudioObject.class);
		when(ao2.getAlbumArtistOrArtist(unknownObjectChecker)).thenReturn(
				"Children of Bodom");
		when(ao2.getArtist(unknownObjectChecker)).thenReturn(
				"Children of Bodom");
		when(ao2.getAlbumArtist(unknownObjectChecker)).thenReturn(
				"Children of Bodom");
		when(ao2.getAlbum(unknownObjectChecker))
				.thenReturn("Children of Bodom");

		aos.add(ao1);
		aos.add(ao2);

		Map<String, IArtist> artists = sut.getArtistObjects(aos);

		List<String> names = new ArrayList<String>(artists.keySet());
		assertEquals(1, names.size());
		assertEquals("Children of Bodom", names.get(0));
	}
}
