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


public class AlbumTest {

	private static final String ARTIST_NAME = "zas heg ha";	
	private static final String ALBUM_NAME = "hswwdhgfdjgf";	
	private static final String AUDIO_FILE_1 = "dhjada";
	private static final String AUDIO_FILE_2 = "dhjadasd";
	
	private static Artist artist = new Artist(ARTIST_NAME);
	
	private static Album a1 = new Album(artist, ALBUM_NAME);
	
	@Test
	public void createAlbum() {
		Assert.assertEquals(artist, a1.getArtist());
		Assert.assertTrue(a1.isEmpty());
		Assert.assertTrue(a1.getName().equals(ALBUM_NAME));		
	}
	
	@Test
	public void testAudioObjects() {
		AudioFile af1 = new AudioFile(AUDIO_FILE_1);
		AudioFile af2 = new AudioFile(AUDIO_FILE_2);
		
		a1.addAudioFile(af1);
		a1.addAudioFile(af2);
		
		Assert.assertTrue(a1.getAudioObjects().contains(af1));
		Assert.assertTrue(a1.getAudioObjects().contains(af2));
		
		a1.removeAudioFile(af1);
		Assert.assertFalse(a1.getAudioObjects().contains(af1));
		Assert.assertTrue(a1.getAudioObjects().contains(af2));
	}
}
