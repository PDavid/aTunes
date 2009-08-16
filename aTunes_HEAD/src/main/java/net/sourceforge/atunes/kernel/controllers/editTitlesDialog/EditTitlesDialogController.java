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

package net.sourceforge.atunes.kernel.controllers.editTitlesDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.gui.views.dialogs.EditTitlesDialog;
import net.sourceforge.atunes.kernel.controllers.model.DialogController;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.model.Album;
import net.sourceforge.atunes.kernel.modules.repository.tags.writer.EditTitlesProcess;
import net.sourceforge.atunes.misc.log.LogCategories;

/**
 * The Class EditTitlesDialogController.
 */
public class EditTitlesDialogController extends DialogController<EditTitlesDialog> {

    /** The files to edit. */
    private List<AudioFile> filesToEdit;

    /** The album. */
    private Album album;

    /** The model. */
    private EditTitlesTableModel model;

    /**
     * Instantiates a new edits the titles dialog controller.
     * 
     * @param dialog
     *            the dialog
     */
    public EditTitlesDialogController(EditTitlesDialog dialog) {
        super(dialog);
        addBindings();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sourceforge.atunes.kernel.controllers.model.Controller#addBindings()
     */
    @Override
    protected void addBindings() {
        EditTitlesDialogActionListener actionListener = new EditTitlesDialogActionListener(getDialogControlled(), this);
        getDialogControlled().getRetrieveFromAmazon().addActionListener(actionListener);
        getDialogControlled().getOkButton().addActionListener(actionListener);
        getDialogControlled().getCancelButton().addActionListener(actionListener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sourceforge.atunes.kernel.controllers.model.Controller#addStateBindings
     * ()
     */
    @Override
    protected void addStateBindings() {
        // Nothing to do
    }

    /**
     * Edits the files.
     */
    protected void editFiles() {
        Map<AudioFile, String> filesAndTitles = ((EditTitlesTableModel) getDialogControlled().getTable().getModel()).getNewValues();
        EditTitlesProcess process = new EditTitlesProcess(new ArrayList<AudioFile>(filesAndTitles.keySet()));
        process.setFilesAndTitles(filesAndTitles);
        process.execute();
    }

    /**
     * Edits the files.
     * 
     * @param alb
     *            the alb
     */
    public void editFiles(Album alb) {
        getLogger().debug(LogCategories.CONTROLLER, new String[] { alb.getName() });

        this.album = alb;
        filesToEdit = alb.getAudioFiles();
        Collections.sort(filesToEdit);
        model = new EditTitlesTableModel(filesToEdit);
        getDialogControlled().getTable().setModel(model);
        getDialogControlled().setVisible(true);
    }

    /**
     * Gets the album.
     * 
     * @return the album
     */
    protected Album getAlbum() {
        return album;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sourceforge.atunes.kernel.controllers.model.Controller#notifyReload()
     */
    @Override
    protected void notifyReload() {
        // Nothing to do
    }

    /**
     * Sets the titles.
     * 
     * @param tracks
     *            the new titles
     */
    protected void setTitles(List<String> tracks) {
        model.setTitles(tracks);
        getDialogControlled().getTable().repaint();
    }
}
