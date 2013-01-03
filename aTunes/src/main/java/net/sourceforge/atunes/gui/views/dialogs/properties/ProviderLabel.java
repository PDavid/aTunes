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

package net.sourceforge.atunes.gui.views.dialogs.properties;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.ILocalAudioObject;

class ProviderLabel extends JPanel {

	private static final long serialVersionUID = -2928151775717411054L;

	private final JLabel label;

	private final JTextField value;

	private final IValueProvider provider;

	/**
	 * @param provider
	 * @param controlsBuilder
	 */
	public ProviderLabel(IValueProvider provider,
			IControlsBuilder controlsBuilder) {
		super(new BorderLayout(10, 0));
		if (provider == null) {
			throw new IllegalArgumentException(
					"provider pointer should not be null");
		}
		this.provider = provider;
		label = new JLabel();
		value = controlsBuilder.createTextField();
		value.setEditable(false);
		add(label, BorderLayout.WEST);
		add(value, BorderLayout.CENTER);
	}

	/**
	 * @param file
	 */
	public void fillText(ILocalAudioObject file) {
		label.setText(provider.getLabel());
		value.setText(provider.getValue(file));
	}
}