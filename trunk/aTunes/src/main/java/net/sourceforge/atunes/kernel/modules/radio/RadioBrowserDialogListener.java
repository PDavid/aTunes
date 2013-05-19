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

package net.sourceforge.atunes.kernel.modules.radio;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IRadioHandler;

/**
 * The listener interface for receiving radioBrowserDialog events.
 */
final class RadioBrowserDialogListener extends MouseAdapter {

	/** The radio browser dialog. */
	private final RadioBrowserDialog radioBrowserDialog;

	private final IRadioHandler radioHandler;

	/**
	 * Instantiates a new radio browser dialog listener.
	 * 
	 * @param radioBrowserDialog
	 * @param radioHandler
	 */
	RadioBrowserDialogListener(final RadioBrowserDialog radioBrowserDialog,
			final IRadioHandler radioHandler) {
		this.radioBrowserDialog = radioBrowserDialog;
		this.radioHandler = radioHandler;
	}

	@Override
	public void mousePressed(final MouseEvent e) {
		if (e.getSource() == this.radioBrowserDialog.getList()) {
			String label = (String) this.radioBrowserDialog.getList()
					.getSelectedValue();
			((RadioBrowserTableModel) this.radioBrowserDialog.getTable()
					.getModel()).setSelectedLabel(label);
		} else if (e.getSource() == this.radioBrowserDialog.getTable()) {
			if (e.getClickCount() == 2) {
				int row = this.radioBrowserDialog.getTable().getSelectedRow();
				if (row != -1) {
					IRadio radio = ((RadioBrowserTableModel) this.radioBrowserDialog
							.getTable().getModel()).getRadioAt(row);
					this.radioHandler.addRadio(radio);
				}
			}
		}
	}
}
