/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectPropertiesDialog;
import net.sourceforge.atunes.model.IAudioObjectPropertiesDialogFactory;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IRadio;

/**
 * Creates dialog for given type of audio object
 * 
 * @author alex
 * 
 */
public class AudioObjectPropertiesDialogFactory implements
	IAudioObjectPropertiesDialogFactory {

    private IDialogFactory dialogFactory;

    @Override
    public IAudioObjectPropertiesDialog newInstance(final IAudioObject a) {
	AudioObjectPropertiesDialog dialog = null;
	if (a instanceof IPodcastFeedEntry) {
	    dialog = dialogFactory.newDialog(
		    "podcastFeedEntryPropertiesDialog",
		    AudioObjectPropertiesDialog.class);
	} else if (a instanceof IRadio) {
	    dialog = dialogFactory.newDialog("radioPropertiesDialog",
		    AudioObjectPropertiesDialog.class);
	} else if (a instanceof ILocalAudioObject) {
	    dialog = dialogFactory.newDialog(
		    "localAudioObjectPropertiesDialog",
		    AudioObjectPropertiesDialog.class);
	}
	if (dialog != null) {
	    dialog.setAudioObject(a);
	}
	return dialog;
    }

    /**
     * @param dialogFactory
     */
    public void setDialogFactory(final IDialogFactory dialogFactory) {
	this.dialogFactory = dialogFactory;
    }
}
