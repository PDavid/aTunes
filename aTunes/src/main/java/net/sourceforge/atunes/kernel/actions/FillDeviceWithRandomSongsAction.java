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

package net.sourceforge.atunes.kernel.actions;

import java.util.List;

import net.sourceforge.atunes.model.IDeviceHandler;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IErrorDialog;
import net.sourceforge.atunes.model.IInputDialog;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Selects random audio objects and adds to device
 * 
 * @author alex
 * 
 */
public class FillDeviceWithRandomSongsAction extends CustomAbstractAction {

	private static final long serialVersionUID = -201250351035880261L;

	/**
	 * Default constructor
	 */
	FillDeviceWithRandomSongsAction() {
		super(I18nUtils.getString("FILL_DEVICE_WITH_RANDOM_SONGS"));
	}

	private String freeMemory;

	private IDeviceHandler deviceHandler;

	private IDialogFactory dialogFactory;

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	@Override
	protected void executeAction() {
		// Ask how much memory should be left free
		IInputDialog inputDialog = this.dialogFactory
				.newDialog(IInputDialog.class);
		inputDialog.setTitle(I18nUtils.getString("MEMORY_TO_LEAVE_FREE"));
		inputDialog.setText(this.freeMemory);
		inputDialog.showDialog();
		this.freeMemory = inputDialog.getResult();
		if (!StringUtils.isEmpty(this.freeMemory)) {
			try {
				this.deviceHandler.fillWithRandomSongs(Long
						.parseLong(this.freeMemory.trim()));
			} catch (NumberFormatException e) {
				// User did not enter numerical value. Show error dialog
				this.dialogFactory
						.newDialog(IErrorDialog.class)
						.showErrorDialog(
								I18nUtils.getString("ERROR_NO_NUMERICAL_VALUE"));
			}
		}
	}

	@Override
	public boolean isEnabledForNavigationTreeSelection(
			final boolean rootSelected, final List<ITreeNode> selection) {
		return this.deviceHandler.isDeviceConnected();
	}

	/**
	 * @param freeMemory
	 *            the freeMemory to set
	 */
	public void setFreeMemory(final String freeMemory) {
		this.freeMemory = freeMemory;
	}

	/**
	 * @param deviceHandler
	 *            the deviceHandler to set
	 */
	public void setDeviceHandler(final IDeviceHandler deviceHandler) {
		this.deviceHandler = deviceHandler;
	}
}
