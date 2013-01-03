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

package net.sourceforge.atunes.kernel.modules.context.youtube;

import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanel;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IRadio;

/**
 * Panel to show youtube videos
 * 
 * @author alex
 * 
 */
public class YoutubeContextPanel extends AbstractContextPanel {

	private IIconFactory videoIcon;
	
	/**
	 * @param videoIcon
	 */
	public void setVideoIcon(IIconFactory videoIcon) {
		this.videoIcon = videoIcon;
	}
	
    @Override
    public IColorMutableImageIcon getContextPanelIcon(IAudioObject audioObject) {
    	return videoIcon.getColorMutableIcon();
    }

    @Override
    public String getContextPanelName() {
        return "YOUTUBE";
    }

    @Override
    public String getContextPanelTitle(IAudioObject audioObject) {
        return "YouTube";
    }

    @Override
    public boolean isPanelVisibleForAudioObject(IAudioObject audioObject) {
        return (audioObject instanceof ILocalAudioObject) || (audioObject instanceof IRadio && ((IRadio) audioObject).isSongInfoAvailable());
    }

    @Override
    public boolean panelNeedsToBeUpdated(IAudioObject previousAudioObject, IAudioObject newAudioObject) {
   		return true;
    }

}
