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

package net.sourceforge.atunes.kernel.modules.context.similar;

import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanel;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.UnknownObjectCheck;

/**
 * Panel to show similar artists
 * 
 * @author alex
 * 
 */
public class SimilarArtistsContextPanel extends AbstractContextPanel {
	
	private IIconFactory artistSimilarIcon;
	
	/**
	 * @param artistSimilarIcon
	 */
	public void setArtistSimilarIcon(IIconFactory artistSimilarIcon) {
		this.artistSimilarIcon = artistSimilarIcon;
	}

    @Override
    public IColorMutableImageIcon getContextPanelIcon(IAudioObject audioObject) {
    	return artistSimilarIcon.getColorMutableIcon();
    }

    @Override
    public String getContextPanelName() {
        return "SIMILAR";
    }

    @Override
    public String getContextPanelTitle(IAudioObject audioObject) {
        return I18nUtils.getString("SIMILAR");
    }

    @Override
    public boolean isPanelVisibleForAudioObject(IAudioObject audioObject) {
        // Avoid unknown artist or album
        if (UnknownObjectCheck.isUnknownArtist(audioObject.getArtist())) {
            return false;
        }

        // Enable panel for LocalAudioObject objects or Radios with song information available
        return audioObject instanceof ILocalAudioObject || audioObject instanceof IRadio && ((IRadio) audioObject).isSongInfoAvailable();
    }
    
    @Override
    public boolean panelNeedsToBeUpdated(IAudioObject previousAudioObject, IAudioObject newAudioObject) {
    	if (previousAudioObject == null || newAudioObject == null) {
    		return true;
    	}
    	
    	return !previousAudioObject.getArtist().equalsIgnoreCase(newAudioObject.getArtist());
    }
}
