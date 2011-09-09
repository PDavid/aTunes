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

package net.sourceforge.atunes.kernel.modules.tags;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.gui.views.dialogs.EditTagDialog;
import net.sourceforge.atunes.gui.views.dialogs.EditTitlesDialog;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.actions.EditTagAction.EditTagSources;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.LocalAudioObject;

public class TagHandler extends AbstractHandler {

	private static TagHandler instance;
	
    /** The edit tag dialog controller. */
    private Map<EditTagSources, EditTagDialogController> editTagDialogControllerMap;

	public static TagHandler getInstance() {
		if (instance == null) {
			instance = new TagHandler();
		}
		return instance;
	}
	
	@Override
	public void applicationStarted(List<AudioObject> playList) {
	}

	@Override
	public void applicationFinish() {
	}

	@Override
	public void applicationStateChanged(IState newState) {
	}

	@Override
	protected void initHandler() {
	}

	public void editFiles(Album a) {
		new EditTitlesDialogController(new EditTitlesDialog(GuiHandler.getInstance().getFrame().getFrame()), getState()).editFiles(a);
	}
	
    /**
     * Gets the edits the tag dialog controller.
     * 
     * @return the edits the tag dialog controller
     */
    public EditTagDialogController getEditTagDialogController(EditTagSources sourceOfEditTagDialog) {
        if (editTagDialogControllerMap == null) {
            editTagDialogControllerMap = new HashMap<EditTagSources, EditTagDialogController>();
        }

        if (!editTagDialogControllerMap.containsKey(sourceOfEditTagDialog)) {
            boolean arePrevNextButtonsShown = sourceOfEditTagDialog != EditTagSources.NAVIGATOR;
            editTagDialogControllerMap.put(sourceOfEditTagDialog, new EditTagDialogController(new EditTagDialog(GuiHandler.getInstance().getFrame().getFrame(), arePrevNextButtonsShown), getState()));
        }
        return editTagDialogControllerMap.get(sourceOfEditTagDialog);
    }

	public void editFiles(EditTagSources navigator, List<LocalAudioObject> asList) {
		getEditTagDialogController(navigator).editFiles(asList);
	}
	
	@Override
	public void playListCleared() {}

	@Override
	public void selectedAudioObjectChanged(AudioObject audioObject) {}

}
