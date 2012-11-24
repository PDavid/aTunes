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

package net.sourceforge.atunes.kernel.modules.covernavigator;

import java.awt.Cursor;

import javax.swing.JList;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.views.dialogs.CoverNavigatorDialog;
import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IAudioObjectImageLocator;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IProcessFactory;

/**
 * Controller for cover navigator
 * 
 * @author alex
 * 
 */
public final class CoverNavigatorController extends
		AbstractSimpleController<CoverNavigatorDialog> {

	static final int COVER_PANEL_WIDTH = Constants.COVER_NAVIGATOR_IMAGE_SIZE
			.getSize() + 20;
	static final int COVER_PANEL_HEIGHT = Constants.COVER_NAVIGATOR_IMAGE_SIZE
			.getSize() + 40;

	private final IProcessFactory processFactory;

	private final IAudioObjectImageLocator audioObjectImageLocator;

	private final IControlsBuilder controlsBuilder;

	/**
	 * Instantiates a new cover navigator controller.
	 * 
	 * @param frame
	 * @param audioObjectImageLocator
	 * @param processFactory
	 * @param controlsBuilder
	 */
	public CoverNavigatorController(final CoverNavigatorDialog frame,
			final IAudioObjectImageLocator audioObjectImageLocator,
			final IProcessFactory processFactory,
			final IControlsBuilder controlsBuilder) {
		super(frame);
		this.audioObjectImageLocator = audioObjectImageLocator;
		this.processFactory = processFactory;
		this.controlsBuilder = controlsBuilder;
		addBindings();
	}

	@Override
	public void addBindings() {
		final CoverNavigatorDialog frame = getComponentControlled();
		frame.getList().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(final ListSelectionEvent e) {
				if (!((JList) e.getSource()).getValueIsAdjusting()) {
					updateCovers();
				}
			}

		});
		frame.getCoversButton().addActionListener(
				new GetCoversButtonActionListener(this, this.processFactory,
						frame));
	}

	/**
	 * Update covers.
	 */
	protected void updateCovers() {
		final IArtist artistSelected = (IArtist) getComponentControlled()
				.getList().getSelectedValue();
		if (artistSelected == null) {
			return;
		}

		getComponentControlled().getCoversPanel().removeAll();

		getComponentControlled().getList().setEnabled(false);
		getComponentControlled().getCoversButton().setEnabled(false);
		getComponentControlled().setCursor(
				Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		SwingWorker<Void, IntermediateResult> generateAndShowAlbumPanels = new GenerateAndShowAlbumPanelsSwingWorker(
				getComponentControlled(), this.audioObjectImageLocator,
				artistSelected, this.controlsBuilder);
		generateAndShowAlbumPanels.execute();
	}

}
