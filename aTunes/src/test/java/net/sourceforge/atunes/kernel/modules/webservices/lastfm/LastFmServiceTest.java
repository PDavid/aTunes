/*
 * aTunes 2.2.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.kernel.modules.webservices.lastfm;

import java.util.List;

import junit.framework.Assert;
import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.model.ILovedTrack;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IState;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class LastFmServiceTest {

	@Before
	public void init() {
		Context.initialize("/spring/");
	}
	
    @Test
    public void testLovedTracks() {
    	IState state = Mockito.mock(IState.class);
    	Mockito.when(state.getLastFmUser()).thenReturn("alexaranda");
    	
    	LastFmService service = new LastFmService(state, Mockito.mock(IOSManager.class), null);
        List<ILovedTrack> lovedTracks = service.getLovedTracks();
        
        Assert.assertFalse(lovedTracks.isEmpty());
    }
}
