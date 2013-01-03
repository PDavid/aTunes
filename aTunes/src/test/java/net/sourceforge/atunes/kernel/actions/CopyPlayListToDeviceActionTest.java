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
import java.util.List;

import net.sourceforge.atunes.model.IDeviceHandler;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayListObjectFilter;

import org.junit.Test;
import org.mockito.Mockito;

public class CopyPlayListToDeviceActionTest {

    @SuppressWarnings("unchecked")
    @Test
    public void test() {
	CopyPlayListToDeviceAction sut = new CopyPlayListToDeviceAction();
	IDeviceHandler deviceHandler = mock(IDeviceHandler.class);
	IPlayListHandler playListHandler = mock(IPlayListHandler.class);
	sut.setDeviceHandler(deviceHandler);
	sut.setPlayListHandler(playListHandler);
	IPlayListObjectFilter<ILocalAudioObject> filter = mock(IPlayListObjectFilter.class);
	sut.setPlayListObjectFilter(filter);
	ILocalAudioObject ao = mock(ILocalAudioObject.class);
	List<ILocalAudioObject> aos = new ArrayList<ILocalAudioObject>();
	aos.add(ao);
	when(filter.getObjects(Mockito.any(IPlayList.class))).thenReturn(aos);

	sut.executeAction();

	verify(playListHandler).getVisiblePlayList();
	verify(deviceHandler).copyFilesToDevice(aos);
    }
}
