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

package net.sourceforge.atunes.kernel.actions;

import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.KeyStroke;

import net.sourceforge.atunes.model.EditTagSources;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ITagHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;

/**
 * Opens edit tag dialog
 * 
 * @author fleax
 * 
 */
public class EditTagPlaylistAction extends
	AbstractActionOverSelectedObjects<ILocalAudioObject> {

    private static final long serialVersionUID = -4310895355731333072L;

    private ITagHandler tagHandler;

    /**
     * @param tagHandler
     */
    public void setTagHandler(final ITagHandler tagHandler) {
	this.tagHandler = tagHandler;
    }

    /**
     * Default constructor
     */
    public EditTagPlaylistAction() {
	super(I18nUtils.getString("EDIT_TAG"));
    }

    @Override
    protected void initialize() {
	super.initialize();
	putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0));
    }

    @Override
    protected void executeAction(final List<ILocalAudioObject> objects) {
	// Start edit by opening edit dialog
	try {
	    tagHandler.editFiles(EditTagSources.PLAYLIST, objects);
	} catch (IllegalArgumentException iae) {
	    Logger.error(iae);
	}
    }
}
