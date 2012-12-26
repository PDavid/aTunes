package net.sourceforge.atunes.kernel.modules.playlist;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.kernel.modules.repository.RepositoryTestMockUtils;
import net.sourceforge.atunes.model.IAudioObjectComparator;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IRepositoryHandler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author alex
 * 
 */
public class SmartPlayListHandlerTest {

	private SmartPlayListHandler sut;

	private IPlayListHandler playListHandler;

	private IRepositoryHandler repositoryHandler;

	@Before
	public void init() {
		sut = new SmartPlayListHandler();
		repositoryHandler = Mockito.mock(IRepositoryHandler.class);

		sut.setRepositoryHandler(repositoryHandler);

		sut.setAudioObjectComparator(Mockito.mock(IAudioObjectComparator.class));
		playListHandler = Mockito.mock(IPlayListHandler.class);
		sut.setPlayListHandler(playListHandler);
	}

	@Test
	public void testAddRandomSongs() {
		List<ILocalAudioObject> list = new ArrayList<ILocalAudioObject>();
		for (int i = 0; i < 10; i++) {
			list.add(RepositoryTestMockUtils.createRandomMockLocalAudioObject());
		}

		Mockito.when(repositoryHandler.getAudioFilesList()).thenReturn(list);

		sut.addRandomSongs(10);
		ArgumentCaptor<ArrayList> argument = ArgumentCaptor
				.forClass(ArrayList.class);
		Mockito.verify(playListHandler)
				.addToVisiblePlayList(argument.capture());
		Assert.assertEquals(10, argument.getValue().size());
	}

	@Test
	public void testAddRandomSongsEmpty() {
		List<ILocalAudioObject> list = new ArrayList<ILocalAudioObject>();
		Mockito.when(repositoryHandler.getAudioFilesList()).thenReturn(list);

		sut.addRandomSongs(10);
		ArgumentCaptor<ArrayList> argument = ArgumentCaptor
				.forClass(ArrayList.class);
		Mockito.verify(playListHandler)
				.addToVisiblePlayList(argument.capture());
		Assert.assertEquals(0, argument.getValue().size());
	}

	@Test
	public void testAddRandomSongsNull() {
		Mockito.when(repositoryHandler.getAudioFilesList()).thenReturn(null);

		sut.addRandomSongs(10);
		ArgumentCaptor<ArrayList> argument = ArgumentCaptor
				.forClass(ArrayList.class);
		Mockito.verify(playListHandler)
				.addToVisiblePlayList(argument.capture());
		Assert.assertEquals(0, argument.getValue().size());
	}

}
