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

package net.sourceforge.atunes.kernel.modules.context.album;

import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanel;
import net.sourceforge.atunes.model.CachedIconFactory;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.UnknownObjectCheck;

/**
 * Context panel to show album information
 * 
 * @author alex
 * 
 */
public class AlbumContextPanel extends AbstractContextPanel {

    private static final long serialVersionUID = -7910261492394049289L;
    
    private CachedIconFactory albumSmallIcon;
    
    /**
     * @param albumSmallIcon
     */
    public void setAlbumSmallIcon(CachedIconFactory albumSmallIcon) {
		this.albumSmallIcon = albumSmallIcon;
	}

    @Override
    public IColorMutableImageIcon getContextPanelIcon(IAudioObject audioObject) {
    	return albumSmallIcon.getColorMutableIcon();
    }

    @Override
    public String getContextPanelName() {
        return "ALBUM";
    }

    @Override
    public String getContextPanelTitle(IAudioObject audioObject) {
        return I18nUtils.getString("ALBUM");
    }

    @Override
    public boolean isPanelVisibleForAudioObject(IAudioObject audioObject) {
        // Avoid unknown artist or album
        if (UnknownObjectCheck.isUnknownArtist(audioObject.getArtist()) || UnknownObjectCheck.isUnknownAlbum(audioObject.getAlbum())) {
            return false;
        }

        // Enable panel for LocalAudioObject objects or Radios with song information available
        return audioObject instanceof ILocalAudioObject || audioObject instanceof IRadio && ((IRadio) audioObject).isSongInfoAvailable();
    }

}
