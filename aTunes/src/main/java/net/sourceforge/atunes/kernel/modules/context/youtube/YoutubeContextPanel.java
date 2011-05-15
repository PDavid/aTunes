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

package net.sourceforge.atunes.kernel.modules.context.youtube;

import java.awt.Paint;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.images.ArtistImageIcon;
import net.sourceforge.atunes.gui.images.ColorMutableImageIcon;
import net.sourceforge.atunes.gui.images.VideoImageIcon;
import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanel;
import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanelContent;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.model.AudioObject;

/**
 * Panel to show youtube videos
 * 
 * @author alex
 * 
 */
public class YoutubeContextPanel extends AbstractContextPanel {

    private List<AbstractContextPanelContent> contents;

    @Override
    protected ColorMutableImageIcon getContextPanelIcon(AudioObject audioObject) {
        return new ColorMutableImageIcon() {
        	@Override
        	public ImageIcon getIcon(Paint paint) {
                return VideoImageIcon.getIcon(paint);
        	}
        };
    }

    @Override
    public String getContextPanelName() {
        return "YOUTUBE";
    }

    @Override
    protected String getContextPanelTitle(AudioObject audioObject) {
        return "YouTube";
    }

    @Override
    protected List<AbstractContextPanelContent> getContents() {
        if (contents == null) {
            contents = new ArrayList<AbstractContextPanelContent>();
            contents.add(new YoutubeContent());
        }
        return contents;
    }

    @Override
    protected boolean isPanelVisibleForAudioObject(AudioObject audioObject) {
        return (audioObject instanceof AudioFile) || (audioObject instanceof Radio && ((Radio) audioObject).isSongInfoAvailable());
    }

}
