package net.sourceforge.atunes.model;

import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class ArtistTest {

	private static final String ARTIST_NAME = "zas heg ha";	
	private static final String ALBUM_NAME = "hswwdhgfdjgf";	
	private static final String AUDIO_FILE_1 = "dhjada";
	private static final String AUDIO_FILE_2 = "dhjadasd";
	
	private static Artist artist;
	
	@Before
	public void init() {
		artist = new Artist(ARTIST_NAME);		
	}
	
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
