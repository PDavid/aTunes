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

package net.sourceforge.atunes.kernel.modules.repository;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IDeviceHandler;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IUnknownObjectChecker;

import org.junit.Before;
import org.junit.Test;

public class RepositoryRemoverTest {

    private RepositoryRemover sut;

    private IUnknownObjectChecker unknownObjectChecker;

    private IRepositoryHandler repositoryHandler;

    @Before
    public void init() {
	sut = new RepositoryRemover();
	unknownObjectChecker = mock(IUnknownObjectChecker.class);
	sut.setUnknownObjectChecker(unknownObjectChecker);
	sut.setOsManager(mock(IOSManager.class));
	repositoryHandler = mock(IRepositoryHandler.class);
	sut.setRepositoryHandler(repositoryHandler);
	sut.setDeviceHandler(mock(IDeviceHandler.class));
    }

    @Test
    public void testRemoveLastAudioObjectOfAlbumFromTreeStructure() {
	final ILocalAudioObject ao1 = RepositoryTestMockUtils
		.createMockLocalAudioObject(null, "Artist 1", "Album 1", null);

	IAlbum album1 = RepositoryTestMockUtils.createMockAlbum("Album 1", ao1);
	IAlbum album2 = RepositoryTestMockUtils.createMockAlbum("Album 2",
		RepositoryTestMockUtils.createMockLocalAudioObject(null,
			"Artist 1", "Album 2", null));
	IArtist artist1 = RepositoryTestMockUtils.createMockArtist("Artist 1",
		album1, album2);
	when(repositoryHandler.getArtist("Artist 1")).thenReturn(artist1);

	sut.removeFromTreeStructure(ao1);

	verify(artist1).removeAlbum(album1);
	verify(artist1, never()).removeAlbum(album2);
	verify(repositoryHandler, never()).removeArtist(artist1);
    }

    @Test
    public void testRemoveNotLastAudioObjectOfAlbumFromTreeStructure() {
	final ILocalAudioObject ao1 = RepositoryTestMockUtils
		.createMockLocalAudioObject(null, "Artist 1", "Album 1", null);

	final ILocalAudioObject ao2 = RepositoryTestMockUtils
		.createMockLocalAudioObject(null, "Artist 1", "Album 1", null);

	IAlbum album1 = RepositoryTestMockUtils.createMockAlbum("Album 1", ao1,
		ao2);
	IArtist artist1 = RepositoryTestMockUtils.createMockArtist("Artist 1",
		album1);
	when(repositoryHandler.getArtist("Artist 1")).thenReturn(artist1);

	sut.removeFromTreeStructure(ao1);

	verify(artist1, never()).removeAlbum(album1);
	verify(repositoryHandler, never()).removeArtist(artist1);
    }

    @Test
    public void testRemoveFromFolderStructure() {
	final ILocalAudioObject ao1 = RepositoryTestMockUtils
		.createMockLocalAudioObject(null, "Artist 1", "Album 1", null);

	sut.removeFromFolderStructure(ao1);
    }

}
