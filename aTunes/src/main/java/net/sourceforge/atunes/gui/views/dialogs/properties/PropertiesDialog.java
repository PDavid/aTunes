/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
import javax.swing.WindowConstants;

import net.sourceforge.atunes.gui.views.controls.CustomDialog;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.StringUtils;

public class PropertiesDialog extends CustomDialog {

    private static final long serialVersionUID = 6097305595858691246L;

    private static Map<AudioObject, PropertiesDialog> dialogsOpened;
    
    private AudioObject audioObject;
    
    /**
     * Instantiates a new properties dialog.
     * 
     * @param title
     *            the title
     */
    PropertiesDialog(String title, JFrame owner) {
        super(owner, 560, 480);
        setMinimumSize(new Dimension(560, 480));
        setTitle(title);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(true);
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
     *            the a
     * 
     * @return the properties dialog
     */
    public static PropertiesDialog newInstance(AudioObject a, JFrame owner) {
    	if (getDialogsOpened().containsKey(a)) {
    		return getDialogsOpened().get(a);
    	} else {
    		PropertiesDialog dialog = null;
    		if (a instanceof PodcastFeedEntry) {
    			dialog = new PodcastFeedEntryPropertiesDialog((PodcastFeedEntry) a, owner);
    		} else if (a instanceof Radio) {
    			dialog = new RadioPropertiesDialog((Radio) a, owner);
    		} else {
    			dialog = new AudioFilePropertiesDialog((AudioFile) a, owner);
    		}
    		getDialogsOpened().put(a, dialog);
    		return dialog;
    	}
    }
    
    private static Map<AudioObject, PropertiesDialog> getDialogsOpened() {
    	if (dialogsOpened == null) {
    		dialogsOpened = new HashMap<AudioObject, PropertiesDialog>();
    	}
    	return dialogsOpened;
    }
    
    protected AudioObject getAudioObject() {
    	return this.audioObject;
    }

	/**
	 * @param audioObject the audioObject to set
	 */
	protected void setAudioObject(AudioObject audioObject) {
		this.audioObject = audioObject;
	}

}
