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

package net.sourceforge.atunes.gui.views.controls;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Creates JToggleButton for a flow panel
 * 
 * @author alex
 * 
 */
public class ToggleButtonOfFlowPanelCreator {

	private ILookAndFeelManager lookAndFeelManager;

	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(
			final ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

	JToggleButton createButton(final boolean iconOnly,
			final ToggleButtonOfFlowPanel button) {

		final ILookAndFeel laf = this.lookAndFeelManager
				.getCurrentLookAndFeel();
		JToggleButton toggle = new JToggleButton(iconOnly ? ""
				: StringUtils.getFirstChars(button.getButtonName(), 30, true));
		toggle.setToolTipText(button.getTooltip());
		toggle.setFocusPainted(false);
		toggle.setForeground(laf.getPaintForSpecialControls());

		laf.putClientProperties(toggle);

		if (button.getIcon() != null) {
			toggle.setIcon(button.getIcon().getIcon(
					laf.getPaintForSpecialControls()));
			toggle.setRolloverIcon(button.getIcon().getIcon(
					laf.getPaintForSpecialControlsRollover()));
			toggle.setSelectedIcon(button.getIcon().getIcon(
					laf.getPaintForSpecialControlsRollover()));
		}

		// Use action listener to encapsulate action to avoid toggle button use
		// text and icon from action object
		toggle.addActionListener(new ToggleButtonActionListener(button));

		if (!iconOnly) {
			toggle.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(final ChangeEvent evt) {
					updateForeground(false, laf, evt);
				}
			});
			toggle.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(final MouseEvent evt) {
					updateForeground(true, laf, evt);
				}

				@Override
				public void mouseExited(final MouseEvent evt) {
					updateForeground(false, laf, evt);
				}
			});
		}

		return toggle;
	}

	private void updateForeground(final boolean rollover,
			final ILookAndFeel laf, final EventObject evt) {
		if (((JToggleButton) evt.getSource()).isSelected() || rollover) {
			((JToggleButton) evt.getSource()).setForeground(laf
					.getPaintForSpecialControlsRollover());
		} else {
			((JToggleButton) evt.getSource()).setForeground(laf
					.getPaintForSpecialControls());
		}
	}
}
