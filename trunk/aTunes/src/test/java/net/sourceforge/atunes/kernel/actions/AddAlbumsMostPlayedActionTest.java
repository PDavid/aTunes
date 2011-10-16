package net.sourceforge.atunes.kernel.actions;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import net.sourceforge.atunes.model.ISmartPlayListHandler;

import org.junit.Test;

public class AddAlbumsMostPlayedActionTest {

	@Test
	public void test() {
		AddAlbumsMostPlayedAction sut = new AddAlbumsMostPlayedAction();
		ISmartPlayListHandler smartPlayListHandler = mock(ISmartPlayListHandler.class);
		sut.setAlbums(3);
		sut.setSmartPlayListHandler(smartPlayListHandler);
		
		sut.executeAction();
		
		verify(smartPlayListHandler).addAlbumsMostPlayed(3);
	}
}
