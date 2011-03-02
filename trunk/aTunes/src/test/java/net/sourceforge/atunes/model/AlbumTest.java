package net.sourceforge.atunes.model;

import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class AlbumTest {

	private static final String ARTIST_NAME = "zas heg ha";	
	private static final String ALBUM_NAME = "hswwdhgfdjgf";	
	private static final String AUDIO_FILE_1 = "dhjada";
	private static final String AUDIO_FILE_2 = "dhjadasd";
	
	private static Album a1;
	
	private static Artist artist;
	
	@Before
	public void init() {
		artist = new Artist(ARTIST_NAME);		
		a1 = new Album(artist, ALBUM_NAME);
	}
	
	@Test
	public void createAlbum() {
		Assert.assertEquals(artist, a1.getArtist());
		Assert.assertTrue(a1.getAudioObjects().isEmpty());
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
