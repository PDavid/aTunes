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

package net.sourceforge.atunes.kernel.modules.context.album;

import java.awt.Paint;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.images.AlbumImageIcon;
import net.sourceforge.atunes.gui.images.ColorMutableImageIcon;
import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanel;
import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanelContent;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.LocalAudioObject;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Context panel to show album information
 * 
 * @author alex
 * 
 */
public class AlbumContextPanel extends AbstractContextPanel {

    private static final long serialVersionUID = -7910261492394049289L;

    private List<AbstractContextPanelContent> contents;

    @Override
    protected ColorMutableImageIcon getContextPanelIcon(AudioObject audioObject) {
        return new ColorMutableImageIcon() {
        	@Override
        	public ImageIcon getIcon(Paint paint) {
        		return AlbumImageIcon.getIcon(paint);
        	}
        };
    }

    @Override
    public String getContextPanelName() {
        return "ALBUM";
    }

    @Override
    protected String getContextPanelTitle(AudioObject audioObject) {
        return I18nUtils.getString("ALBUM");
    }

    @Override
    protected List<AbstractContextPanelContent> getContents() {
        if (contents == null) {
            contents = new ArrayList<AbstractContextPanelContent>();
            contents.add(new AlbumBasicInfoContent(getState()));
            contents.add(new AlbumTracksContent(getState()));
        }
        return contents;
    }

    @Override
    protected boolean isPanelVisibleForAudioObject(AudioObject audioObject) {
        // Avoid unknown artist or album
        if (Artist.isUnknownArtist(audioObject.getArtist()) || Album.isUnknownAlbum(audioObject.getAlbum())) {
            return false;
        }

        // Enable panel for LocalAudioObject objects or Radios with song information available
        return audioObject instanceof LocalAudioObject || audioObject instanceof Radio && ((Radio) audioObject).isSongInfoAvailable();
    }

}
