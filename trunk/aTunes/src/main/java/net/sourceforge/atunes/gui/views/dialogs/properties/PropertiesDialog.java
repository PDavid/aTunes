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

package net.sourceforge.atunes.gui.views.dialogs.properties;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.StringUtils;

public class PropertiesDialog extends AbstractCustomDialog {

    private static final long serialVersionUID = 6097305595858691246L;

    private static Map<IAudioObject, PropertiesDialog> dialogsOpened;

    private IAudioObject audioObject;

    /**
     * Instantiates a new properties dialog.
     * 
     * @param title
     *            the title
     */
    PropertiesDialog(String title, JFrame owner) {
        super(owner, 560, 480, true, CloseAction.DISPOSE);
        setMinimumSize(new Dimension(560, 480));
        setTitle(title);
        setResizable(true);
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
     * Gets the html formatted (only a description)
     * @param desc
     * @return
     */
    static String getHtmlFormatted(String desc) {
        return StringUtils.getString("<html><b>", desc, ": </b></html>");
    }

    @Override
    public void dispose() {
        getDialogsOpened().remove(getAudioObject());
        if (!getDialogsOpened().isEmpty()) {
            getDialogsOpened().values().iterator().next().toFront();
        }
        super.dispose();
    }

    /**
     * New instance.
     * 
     * @param a
     * @param owner
     * @param state
     * @return
     */
    public static PropertiesDialog newInstance(IAudioObject a, JFrame owner, IState state) {
        if (getDialogsOpened().containsKey(a)) {
            return getDialogsOpened().get(a);
        } else {
            PropertiesDialog dialog = null;
            if (a instanceof PodcastFeedEntry) {
                dialog = new PodcastFeedEntryPropertiesDialog((PodcastFeedEntry) a, owner, state);
            } else if (a instanceof Radio) {
                dialog = new RadioPropertiesDialog((Radio) a, owner);
            } else if (a instanceof AudioFile) {
                dialog = new AudioFilePropertiesDialog((AudioFile) a, owner, state);
            }
            getDialogsOpened().put(a, dialog);
            return dialog;
        }
    }

    private static Map<IAudioObject, PropertiesDialog> getDialogsOpened() {
        if (dialogsOpened == null) {
            dialogsOpened = new HashMap<IAudioObject, PropertiesDialog>();
        }
        return dialogsOpened;
    }

    protected IAudioObject getAudioObject() {
        return this.audioObject;
    }

    /**
     * @param audioObject
     *            the audioObject to set
     */
    protected void setAudioObject(IAudioObject audioObject) {
        this.audioObject = audioObject;
    }

}
