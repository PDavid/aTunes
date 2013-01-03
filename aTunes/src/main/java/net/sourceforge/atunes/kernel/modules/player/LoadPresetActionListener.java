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

package net.sourceforge.atunes.kernel.modules.player;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IEqualizer;
import net.sourceforge.atunes.model.ISelectorDialog;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

final class LoadPresetActionListener implements ActionListener {

	private final IEqualizer equalizer;

	private final IDialogFactory dialogFactory;

	private final EqualizerDialog dialog;

	@Override
	public void actionPerformed(final ActionEvent e) {
		String[] names = this.equalizer.getPresetsNames();

		// Show selector
		ISelectorDialog selector = this.dialogFactory
				.newDialog(ISelectorDialog.class);
		selector.setTitle(I18nUtils.getString("LOAD_PRESET"));
		selector.setOptions(names);
		selector.showDialog();

		String selection = selector.getSelection();
		if (!StringUtils.isEmpty(selection)) {
			// Get result
			Integer[] presets = this.equalizer
					.getPresetByNameForShowInGUI(selection);

			for (int i = 0; i < this.dialog.getBands().length; i++) {
				this.dialog.getBands()[i].setValue(presets[i]);
			}
		}
	}

	/**
	 * @param equalizer
	 * @param dialogFactory
	 * @param dialog
	 */
	LoadPresetActionListener(final IEqualizer equalizer,
			final IDialogFactory dialogFactory, final EqualizerDialog dialog) {
		super();
		this.equalizer = equalizer;
		this.dialogFactory = dialogFactory;
		this.dialog = dialog;
	}
}