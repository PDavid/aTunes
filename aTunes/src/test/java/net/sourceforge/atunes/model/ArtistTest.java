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

import net.sourceforge.atunes.kernel.modules.repository.Album;
import net.sourceforge.atunes.kernel.modules.repository.Artist;
import net.sourceforge.atunes.kernel.modules.repository.AudioFile;

import org.junit.Assert;
import org.junit.Test;


public class ArtistTest {

	private static final String ARTIST_NAME = "zas heg ha";	
	private static final String ALBUM_NAME = "hswwdhgfdjgf";	
	private static final String AUDIO_FILE_1 = "dhjada";
	private static final String AUDIO_FILE_2 = "dhjadasd";
	
	private static Artist artist = new Artist(ARTIST_NAME);
	
	@Test
	public void createArtist() {		
		Assert.assertTrue(artist.getName().equals(ARTIST_NAME));
		Assert.assertTrue(artist.getAlbums().isEmpty());
		Assert.assertNull(artist.getAlbum(ALBUM_NAME));
	}
	
	@Test
	public void testAlbums() {
		Album a1 = new Album(artist, ALBUM_NAME);		
		AudioFile af1 = new AudioFile(AUDIO_FILE_1);
		AudioFile af2 = new AudioFile(AUDIO_FILE_2);		
		a1.addAudioFile(af1);
		artist.addAlbum(a1);
		
		Assert.assertEquals(a1, artist.getAlbum(ALBUM_NAME));
		Assert.assertTrue(artist.getAlbums().containsKey(ALBUM_NAME));
		Assert.assertTrue(artist.getAlbums().containsValue(a1));
		
		Assert.assertTrue(artist.getAudioObjects().contains(af1));
		Assert.assertFalse(artist.getAudioObjects().contains(af2));

		artist.removeAlbum(a1);
		Assert.assertNull(artist.getAlbum(ALBUM_NAME));
	}
}
