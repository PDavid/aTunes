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

package net.sourceforge.atunes.kernel.actions;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Color;

import javax.swing.AbstractAction;

import net.sourceforge.atunes.gui.images.VolumeMaxImageIcon;
import net.sourceforge.atunes.gui.images.VolumeMedImageIcon;
import net.sourceforge.atunes.gui.images.VolumeMinImageIcon;
import net.sourceforge.atunes.gui.images.VolumeMuteImageIcon;
import net.sourceforge.atunes.gui.images.VolumeZeroImageIcon;
import net.sourceforge.atunes.gui.views.controls.VolumeIconCalculator;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.IStatePlayer;

import org.junit.Before;
import org.junit.Test;

public class MuteActionTest {
	
	private MuteAction sut;
	
	private IPlayerHandler playerHandler;
	
	private IStatePlayer state;
	
	@Before
	public void init() {
		sut = new MuteAction();
		playerHandler = mock(IPlayerHandler.class);
		ILookAndFeelManager lookAndFeelManager = mock(ILookAndFeelManager.class);
		state = mock(IStatePlayer.class);
		ILookAndFeel lookAndFeel = mock(ILookAndFeel.class);
		when(lookAndFeel.getPaintForSpecialControls()).thenReturn(Color.red);
		when(lookAndFeelManager.getCurrentLookAndFeel()).thenReturn(lookAndFeel);
		sut.setPlayerHandler(playerHandler);
		sut.setStatePlayer(state);
		
		VolumeIconCalculator iconCalculator = new VolumeIconCalculator();
		iconCalculator.setStatePlayer(state);
		iconCalculator.setLookAndFeelManager(lookAndFeelManager);
		iconCalculator.setVolumeMaxIcon(new VolumeMaxImageIcon());
		iconCalculator.setVolumeMedIcon(new VolumeMedImageIcon());
		iconCalculator.setVolumeMinIcon(new VolumeMinImageIcon());
		iconCalculator.setVolumeMuteIcon(new VolumeMuteImageIcon());
		iconCalculator.setVolumeZeroIcon(new VolumeZeroImageIcon());
		
		sut.setVolumeIconCalculator(iconCalculator);
	}

	@Test
	public void testMute() {
		when(state.isMuteEnabled()).thenReturn(true);
		sut.putValue(AbstractAction.SELECTED_KEY, true);
		
		sut.executeAction();
		
		verify(playerHandler).applyMuteState(true);
	}

	@Test
	public void testNoMute() {
		when(state.isMuteEnabled()).thenReturn(false);
		sut.putValue(AbstractAction.SELECTED_KEY, false);
		
		sut.executeAction();
		
		verify(playerHandler).applyMuteState(false);
	}

}
