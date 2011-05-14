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

package net.sourceforge.atunes.kernel.modules.context.audioobject;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.images.AudioFileImageIcon;
import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanel;
import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanelContent;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.GenericImageSize;
import net.sourceforge.atunes.model.LocalAudioObject;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Context panel to show song information
 * 
 * @author alex
 * 
 */
public class AudioObjectContextPanel extends AbstractContextPanel {

    private static final long serialVersionUID = -7910261492394049289L;

    private List<AbstractContextPanelContent> contents;

    @Override
    protected ImageIcon getContextPanelIcon(AudioObject audioObject) {
        if (audioObject != null) {
            return audioObject.getGenericImage(GenericImageSize.SMALL);
        } else {
            return AudioFileImageIcon.getSmallImageIcon();
        }
    }

    @Override
    public String getContextPanelName() {
        return "AUDIOOBJECT";
    }

    @Override
    protected String getContextPanelTitle(AudioObject audioObject) {
        if (audioObject instanceof LocalAudioObject || (audioObject instanceof Radio && ((Radio) audioObject).isSongInfoAvailable())) {
            return I18nUtils.getString("SONG");
        } else if (audioObject instanceof Radio) {
            return I18nUtils.getString("RADIO");
        } else if (audioObject instanceof PodcastFeedEntry) {
            return I18nUtils.getString("PODCAST_FEED");
        }

        return I18nUtils.getString("SONG");
    }

    @Override
    protected List<AbstractContextPanelContent> getContents() {
        if (contents == null) {
            contents = new ArrayList<AbstractContextPanelContent>();
            contents.add(new AudioObjectBasicInfoContent());
            contents.add(new LyricsContent());
        }
        return contents;
    }

    @Override
    protected boolean isPanelVisibleForAudioObject(AudioObject audioObject) {
        return audioObject != null;
    }

}
