/*
 * aTunes 2.1.0-SNAPSHOT
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

import java.awt.Paint;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.images.ArtistSimilarImageIcon;
import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanel;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Panel to show similar artists
 * 
 * @author alex
 * 
 */
public class SimilarArtistsContextPanel extends AbstractContextPanel {

    @Override
    public IColorMutableImageIcon getContextPanelIcon(IAudioObject audioObject) {
        return new IColorMutableImageIcon() {
        	@Override
        	public ImageIcon getIcon(Paint paint) {
                return ArtistSimilarImageIcon.getIcon(paint, getLookAndFeel());
        	}
        };
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
        if (Artist.isUnknownArtist(audioObject.getArtist())) {
            return false;
        }

        // Enable panel for LocalAudioObject objects or Radios with song information available
        return audioObject instanceof ILocalAudioObject || audioObject instanceof IRadio && ((IRadio) audioObject).isSongInfoAvailable();
    }

}
