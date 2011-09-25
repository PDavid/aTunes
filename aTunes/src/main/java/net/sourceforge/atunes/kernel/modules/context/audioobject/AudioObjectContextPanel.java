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

import java.awt.Paint;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.images.AudioFileImageIcon;
import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanel;
import net.sourceforge.atunes.model.GenericImageSize;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Context panel to show song information
 * 
 * @author alex
 * 
 */
public class AudioObjectContextPanel extends AbstractContextPanel {

    private static final long serialVersionUID = -7910261492394049289L;

    @Override
    protected IColorMutableImageIcon getContextPanelIcon(final IAudioObject audioObject) {
        if (audioObject != null) {
            return audioObject.getGenericImage(GenericImageSize.SMALL, getLookAndFeel());
        } else {
        	return new IColorMutableImageIcon() {
        		@Override
        		public ImageIcon getIcon(Paint paint) {
        			return AudioFileImageIcon.getSmallImageIcon(paint, getLookAndFeel());
        		}
        	};
        }
    }

    @Override
    public String getContextPanelName() {
        return "AUDIOOBJECT";
    }

    @Override
    protected String getContextPanelTitle(IAudioObject audioObject) {
        if (audioObject instanceof ILocalAudioObject || (audioObject instanceof IRadio && ((IRadio) audioObject).isSongInfoAvailable())) {
            return I18nUtils.getString("SONG");
        } else if (audioObject instanceof IRadio) {
            return I18nUtils.getString("RADIO");
        } else if (audioObject instanceof IPodcastFeedEntry) {
            return I18nUtils.getString("PODCAST_FEED");
        }

        return I18nUtils.getString("SONG");
    }

    @Override
    protected boolean isPanelVisibleForAudioObject(IAudioObject audioObject) {
        return audioObject != null;
    }

}
