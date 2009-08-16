/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

package net.sourceforge.atunes.gui.views.dialogs.properties;

import java.awt.Component;

import javax.swing.WindowConstants;

import net.sourceforge.atunes.gui.views.controls.CustomFrame;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class PropertiesDialog.
 */
public class PropertiesDialog extends CustomFrame {

    private static final long serialVersionUID = 6097305595858691246L;

    /**
     * Instantiates a new properties dialog.
     * 
     * @param title
     *            the title
     */
    PropertiesDialog(String title, Component owner) {
        super(title, 560, 480, owner);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        enableDisposeActionWithEscapeKey();
    }

    /**
     * Gets the html formatted.
     * 
     * @param desc
     *            the desc
     * @param text
     *            the text
     * 
     * @return the html formatted
     */
    static String getHtmlFormatted(String desc, String text) {
        return StringUtils.getString("<html><b>", desc, ": </b>", text, "</html>");
    }

    /**
     * New instance.
     * 
     * @param a
     *            the a
     * 
     * @return the properties dialog
     */
    public static PropertiesDialog newInstance(AudioObject a, Component owner) {
        if (a instanceof PodcastFeedEntry) {
            return new PodcastFeedEntryPropertiesDialog((PodcastFeedEntry) a, owner);
        } else if (a instanceof Radio) {
            return new RadioPropertiesDialog((Radio) a, owner);
        }
        return new AudioFilePropertiesDialog((AudioFile) a, owner);
    }

}
