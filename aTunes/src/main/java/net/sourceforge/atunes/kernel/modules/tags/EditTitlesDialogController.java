/*
 * aTunes 3.1.0
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.gui.views.dialogs.EditTitlesDialog;
import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.kernel.modules.process.EditTitlesProcess;
import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IProcessFactory;
import net.sourceforge.atunes.model.IWebServicesHandler;

final class EditTitlesDialogController extends
		AbstractSimpleController<EditTitlesDialog> {

	private IAlbum album;
	private EditTitlesTableModel model;
	private IProcessFactory processFactory;

	private IWebServicesHandler webServicesHandler;

	/**
	 * @param webServicesHandler
	 */
	public void setWebServicesHandler(IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}

	/**
	 * @param processFactory
	 */
	public void setProcessFactory(IProcessFactory processFactory) {
		this.processFactory = processFactory;
	}

	/**
	 * Instantiates a new edits the titles dialog controller.
	 * 
	 * @param dialog
	 * @param processFactory
	 * @param webServicesHandler
	 */
	EditTitlesDialogController(EditTitlesDialog dialog,
			IProcessFactory processFactory,
			IWebServicesHandler webServicesHandler) {
		super(dialog);
		this.processFactory = processFactory;
		this.webServicesHandler = webServicesHandler;
		addBindings();
	}

	@Override
	public void addBindings() {
		EditTitlesDialogActionListener actionListener = new EditTitlesDialogActionListener(
				getComponentControlled(), this, webServicesHandler);
		getComponentControlled().getRetrieveTitles().addActionListener(
				actionListener);
		getComponentControlled().getOkButton()
				.addActionListener(actionListener);
		getComponentControlled().getCancelButton().addActionListener(
				actionListener);
	}

	/**
	 * Edits the files.
	 */
	protected void editFiles() {
		Map<ILocalAudioObject, String> filesAndTitles = ((EditTitlesTableModel) getComponentControlled()
				.getTable().getModel()).getNewValues();
		EditTitlesProcess process = (EditTitlesProcess) processFactory
				.getProcessByName("editTitlesProcess");
		process.setFilesToChange(new ArrayList<ILocalAudioObject>(
				filesAndTitles.keySet()));
		process.setFilesAndTitles(filesAndTitles);
		process.execute();
	}

	/**
	 * Edits the files.
	 * 
	 * @param alb
	 *            the alb
	 */
	public void editFiles(IAlbum alb) {
		this.album = alb;
		List<ILocalAudioObject> filesToEdit = alb.getAudioObjects();
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
	protected IAlbum getAlbum() {
		return album;
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
