/*
 * aTunes 2.0.0-SNAPSHOT
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
import net.sourceforge.atunes.kernel.controllers.model.SimpleController;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.model.Album;
import net.sourceforge.atunes.kernel.modules.repository.tags.writer.EditTitlesProcess;
import net.sourceforge.atunes.misc.log.LogCategories;

public class EditTitlesDialogController extends SimpleController<EditTitlesDialog> {

    private List<AudioFile> filesToEdit;
    private Album album;
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

    @Override
    protected void addBindings() {
        EditTitlesDialogActionListener actionListener = new EditTitlesDialogActionListener(getComponentControlled(), this);
        getComponentControlled().getRetrieveFromAmazon().addActionListener(actionListener);
        getComponentControlled().getOkButton().addActionListener(actionListener);
        getComponentControlled().getCancelButton().addActionListener(actionListener);
    }

    @Override
    protected void addStateBindings() {
        // Nothing to do
    }

    /**
     * Edits the files.
     */
    protected void editFiles() {
        Map<AudioFile, String> filesAndTitles = ((EditTitlesTableModel) getComponentControlled().getTable().getModel()).getNewValues();
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
        getLogger().debugMethodCall(LogCategories.CONTROLLER, new String[] { alb.getName() });

        this.album = alb;
        filesToEdit = alb.getAudioFiles();
        Collections.sort(filesToEdit);
        model = new EditTitlesTableModel(filesToEdit);
        getComponentControlled().getTable().setModel(model);
        getComponentControlled().setVisible(true);
    }

    /**
     * Gets the album.
     * 
     * @return the album
     */
    protected Album getAlbum() {
        return album;
    }

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
        getComponentControlled().getTable().repaint();
    }
}
