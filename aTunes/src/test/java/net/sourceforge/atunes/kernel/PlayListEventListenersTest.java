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

package net.sourceforge.atunes.kernel;

import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPlayListAudioObject;
import net.sourceforge.atunes.model.IPlayListEventListener;

import org.junit.Before;
import org.junit.Test;

public class PlayListEventListenersTest {

    private PlayListEventListeners sut;
    private IPlayListEventListener mock1;
    private IPlayListEventListener mock2;

    @Before
    public void init() {
	sut = new PlayListEventListeners();
	List<IPlayListEventListener> listeners = new ArrayList<IPlayListEventListener>();
	mock1 = mock(IPlayListEventListener.class);
	mock2 = mock(IPlayListEventListener.class);
	listeners.add(mock1);
	listeners.add(mock2);
	sut.setListeners(listeners);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void audioObjectsAdded() {
	List<IPlayListAudioObject> list = anyList();
	sut.audioObjectsAdded(list);

	verify(mock1).audioObjectsAdded(list);
	verify(mock2).audioObjectsAdded(list);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void audioObjectsRemoved() {
	List<IPlayListAudioObject> list = anyList();
	sut.audioObjectsRemoved(list);

	verify(mock1).audioObjectsRemoved(list);
	verify(mock2).audioObjectsRemoved(list);
    }

    @Test
    public void playListCleared() {
	sut.playListCleared();

	verify(mock1).playListCleared();
	verify(mock2).playListCleared();
    }

    @Test
    public void selectedAudioObjectHasChanged() {
	IAudioObject ao = mock(IAudioObject.class);
	sut.selectedAudioObjectHasChanged(ao);

	verify(mock1).selectedAudioObjectChanged(ao);
	verify(mock2).selectedAudioObjectChanged(ao);
    }

}
