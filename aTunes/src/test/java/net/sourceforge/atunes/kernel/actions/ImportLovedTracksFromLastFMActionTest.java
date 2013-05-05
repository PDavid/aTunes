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

package net.sourceforge.atunes.kernel.actions;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.atunes.kernel.modules.repository.Album;
import net.sourceforge.atunes.kernel.modules.repository.Artist;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.data.LastFmLovedTrack;
import net.sourceforge.atunes.model.BackgroundWorkerFactoryMock;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IFavoritesHandler;
import net.sourceforge.atunes.model.IIndeterminateProgressDialog;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILovedTrack;
import net.sourceforge.atunes.model.IMessageDialog;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IWebServicesHandler;

import org.junit.Test;

public class ImportLovedTracksFromLastFMActionTest {

	@Test
	public void test() {
		ImportLovedTracksFromLastFMAction sut = new ImportLovedTracksFromLastFMAction();
		sut.setBackgroundWorkerFactory(new BackgroundWorkerFactoryMock());

		IWebServicesHandler webServicesHandler = mock(IWebServicesHandler.class);
		List<ILovedTrack> list = new ArrayList<ILovedTrack>();
		ILovedTrack l1 = new LastFmLovedTrack("artist1", "title1");
		ILovedTrack l2 = new LastFmLovedTrack("artist2", "title2");
		ILovedTrack l3 = new LastFmLovedTrack("artist1", "title3");
		list.add(l1);
		list.add(l2);
		list.add(l3);
		when(webServicesHandler.getLovedTracks()).thenReturn(list);

		IRepositoryHandler repositoryHandler = mock(IRepositoryHandler.class);
		Artist artist1 = new Artist("artist1");
		Artist artist2 = new Artist("artist2");
		Artist artist3 = new Artist("artist3");
		Album album1 = new Album(artist1, "album1");
		Album album2 = new Album(artist2, "album2");
		Album album3 = new Album(artist3, "album3");
		ILocalAudioObject lao1 = mock(ILocalAudioObject.class);
		when(lao1.getTitleOrFileName()).thenReturn("TITLe1");
		ILocalAudioObject lao2 = mock(ILocalAudioObject.class);
		when(lao2.getTitleOrFileName()).thenReturn("title24344343");
		ILocalAudioObject lao3 = mock(ILocalAudioObject.class);
		when(lao3.getTitleOrFileName()).thenReturn("TITLe1");
		album1.addAudioFile(lao1);
		album2.addAudioFile(lao2);
		album3.addAudioFile(lao3);
		artist1.addAlbum(album1);
		artist2.addAlbum(album2);
		artist3.addAlbum(album3);

		when(repositoryHandler.getArtist("artist1")).thenReturn(artist1);
		when(repositoryHandler.getArtist("artist2")).thenReturn(artist2);
		when(repositoryHandler.getArtist("artist3")).thenReturn(artist3);

		IFavoritesHandler favoritesHandler = mock(IFavoritesHandler.class);

		IDialogFactory dialogFactory = mock(IDialogFactory.class);
		when(dialogFactory.newDialog(IMessageDialog.class)).thenReturn(
				mock(IMessageDialog.class));
		when(dialogFactory.newDialog(IIndeterminateProgressDialog.class))
				.thenReturn(mock(IIndeterminateProgressDialog.class));

		sut.setWebServicesHandler(webServicesHandler);
		sut.setRepositoryHandler(repositoryHandler);
		sut.setFavoritesHandler(favoritesHandler);
		sut.setDialogFactory(dialogFactory);

		sut.executeAction();

		verify(favoritesHandler).importFavoriteSongsFromLastFm(
				Collections.singletonList(lao1));

	}
}
