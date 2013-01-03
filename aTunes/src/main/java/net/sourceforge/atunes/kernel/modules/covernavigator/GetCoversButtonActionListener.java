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

package net.sourceforge.atunes.kernel.modules.covernavigator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.sourceforge.atunes.gui.views.dialogs.CoverNavigatorDialog;
import net.sourceforge.atunes.kernel.modules.process.GetCoversProcess;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IProcessFactory;

final class GetCoversButtonActionListener implements ActionListener {

	private final CoverNavigatorController controller;
	
    private final IProcessFactory processFactory;

	private final CoverNavigatorDialog frame;

	/**
	 * @param controller
	 * @param processFactory
	 * @param frame
	 */
	GetCoversButtonActionListener(CoverNavigatorController controller, IProcessFactory processFactory, CoverNavigatorDialog frame) {
		this.controller = controller;
		this.processFactory = processFactory;
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	    IArtist selectedArtist = (IArtist) frame.getList().getSelectedValue();
	    if (selectedArtist != null) {
	        GetCoversProcess process = (GetCoversProcess) processFactory.getProcessByName("getCoversProcess");
	        process.setArtist(selectedArtist);
	        process.addProcessListener(new GetCoversProcessListener(controller));
	        process.execute();
	    }
	}
}