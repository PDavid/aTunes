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

package net.sourceforge.atunes.gui.views.panels;

import java.awt.Component;
import java.awt.GridBagLayout;

import javax.swing.AbstractAction;
import javax.swing.JPanel;

import net.sourceforge.atunes.gui.views.controls.PopUpButton;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IFilterPanel;
import net.sourceforge.atunes.model.IPlayListSelectorPanel;
import net.sourceforge.atunes.model.IPopUpButton;

/**
 * Selects between different play lists
 * 
 * @author alex
 * 
 */
public final class PlayListSelectorPanel extends JPanel implements
		IPlayListSelectorPanel {

	private static final long serialVersionUID = 7382098268271937439L;

	private PopUpButton options;

	private IFilterPanel playListFilterPanel;

	private AbstractAction newPlayListAction;

	private AbstractAction renamePlayListAction;

	private AbstractAction closePlayListAction;

	private AbstractAction closeOtherPlayListsAction;

	private AbstractAction arrangePlayListColumnsAction;

	private AbstractAction copyPlayListToDeviceAction;

	private AbstractAction syncDeviceWithPlayListAction;

	private IControlsBuilder controlsBuilder;

	/**
	 * @param controlsBuilder
	 */
	public void setControlsBuilder(final IControlsBuilder controlsBuilder) {
		this.controlsBuilder = controlsBuilder;
	}

	/**
	 * Instantiates a new play list tab panel.
	 */
	public PlayListSelectorPanel() {
		super(new GridBagLayout());
	}

	/**
	 * @param syncDeviceWithPlayListAction
	 */
	public void setSyncDeviceWithPlayListAction(
			final AbstractAction syncDeviceWithPlayListAction) {
		this.syncDeviceWithPlayListAction = syncDeviceWithPlayListAction;
	}

	/**
	 * @param copyPlayListToDeviceAction
	 */
	public void setCopyPlayListToDeviceAction(
			final AbstractAction copyPlayListToDeviceAction) {
		this.copyPlayListToDeviceAction = copyPlayListToDeviceAction;
	}

	/**
	 * @param arrangePlayListColumnsAction
	 */
	public void setArrangePlayListColumnsAction(
			final AbstractAction arrangePlayListColumnsAction) {
		this.arrangePlayListColumnsAction = arrangePlayListColumnsAction;
	}

	/**
	 * @param closeOtherPlayListsAction
	 */
	public void setCloseOtherPlayListsAction(
			final AbstractAction closeOtherPlayListsAction) {
		this.closeOtherPlayListsAction = closeOtherPlayListsAction;
	}

	/**
	 * @param closePlayListAction
	 */
	public void setClosePlayListAction(final AbstractAction closePlayListAction) {
		this.closePlayListAction = closePlayListAction;
	}

	/**
	 * @param renamePlayListAction
	 */
	public void setRenamePlayListAction(
			final AbstractAction renamePlayListAction) {
		this.renamePlayListAction = renamePlayListAction;
	}

	/**
	 * @param newPlayListAction
	 */
	public void setNewPlayListAction(final AbstractAction newPlayListAction) {
		this.newPlayListAction = newPlayListAction;
	}

	/**
	 * @param playListFilterPanel
	 */
	public void setPlayListFilterPanel(final IFilterPanel playListFilterPanel) {
		this.playListFilterPanel = playListFilterPanel;
	}

	/**
	 * Adds the content.
	 */
	public void initialize() {
		this.options = this.controlsBuilder
				.createPopUpButton(PopUpButton.BOTTOM_RIGHT);

		addActions();
	}

	/**
	 * Add actions to popup
	 */
	private void addActions() {
		this.options.add(this.newPlayListAction);
		this.options.add(this.renamePlayListAction);
		this.options.add(this.closePlayListAction);
		this.options.add(this.closeOtherPlayListsAction);
		this.options.addSeparator();
		this.options.add(this.arrangePlayListColumnsAction);
		this.options.addSeparator();
		this.options.add(this.copyPlayListToDeviceAction);
		this.options.add(this.syncDeviceWithPlayListAction);
	}

	@Override
	public IPopUpButton getOptions() {
		return this.options;
	}

	/**
	 * @return
	 */
	public IFilterPanel getPlayListFilterPanel() {
		return this.playListFilterPanel;
	}

	@Override
	public Component getSwingComponent() {
		return this;
	}
}
