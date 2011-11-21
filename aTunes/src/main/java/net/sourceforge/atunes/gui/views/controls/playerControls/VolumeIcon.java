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

package net.sourceforge.atunes.gui.views.controls.playerControls;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.model.CachedIconFactory;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.IState;

/**
 * Returns volume icon according to volume level and look and feel
 * @author alex
 *
 */
public class VolumeIcon {
	
	private IState state;
	
	private ILookAndFeel lookAndFeel;
	
	/**
	 * @param state
	 */
	public void setState(IState state) {
		this.state = state;
	}
	
	/**
	 * @param lookAndFeel
	 */
	public void setLookAndFeel(ILookAndFeel lookAndFeel) {
		this.lookAndFeel = lookAndFeel;
	}
	
	/**
	 * @param state
	 * @param lookAndFeel
	 */
	public VolumeIcon(IState state, ILookAndFeel lookAndFeel) {
		this.state = state;
		this.lookAndFeel = lookAndFeel;
	}

	/**
     * Returns icon to use depending on volume and mute state
     * @return
     */
    public ImageIcon getVolumeIcon() {
        if (state.isMuteEnabled()) {
            return Context.getBean("volumeMuteIcon", CachedIconFactory.class).getIcon(lookAndFeel.getPaintForSpecialControls());
        } else {
            int volume = state.getVolume();
            if (volume > 80) {
                return Context.getBean("volumeMaxIcon", CachedIconFactory.class).getIcon(lookAndFeel.getPaintForSpecialControls());
            } else if (volume > 40) {
            	return Context.getBean("volumeMedIcon", CachedIconFactory.class).getIcon(lookAndFeel.getPaintForSpecialControls());
            } else if (volume > 5) {
            	return Context.getBean("volumeMinIcon", CachedIconFactory.class).getIcon(lookAndFeel.getPaintForSpecialControls());
            } else {
            	return Context.getBean("volumeZeroIcon", CachedIconFactory.class).getIcon(lookAndFeel.getPaintForSpecialControls());
            }
        }
    }
}
