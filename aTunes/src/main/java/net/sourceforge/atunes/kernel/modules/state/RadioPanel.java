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

package net.sourceforge.atunes.kernel.modules.state;

import java.awt.GridBagConstraints;

import javax.swing.JCheckBox;

import net.sourceforge.atunes.model.IStateRadio;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The Class RadioPanel.
 */
public final class RadioPanel extends AbstractPreferencesPanel {

	private static final long serialVersionUID = 4489293347321979288L;

	/** The read info from radio stream. */
	private final JCheckBox readInfoFromRadioStream;

	private IStateRadio stateRadio;

	/**
	 * @param stateRadio
	 */
	public void setStateRadio(final IStateRadio stateRadio) {
		this.stateRadio = stateRadio;
	}

	/**
	 * Instantiates a new radio panel.
	 */
	public RadioPanel() {
		super(I18nUtils.getString("RADIO"));
		this.readInfoFromRadioStream = new JCheckBox(
				I18nUtils.getString("READ_INFO_FROM_RADIO_STREAM"));
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		add(this.readInfoFromRadioStream, c);
	}

	@Override
	public boolean applyPreferences() {
		this.stateRadio.setReadInfoFromRadioStream(this.readInfoFromRadioStream
				.isSelected());
		return false;
	}

	/**
	 * Sets the read info from radio stream.
	 * 
	 * @param animate
	 *            the new read info from radio stream
	 */
	private void setReadInfoFromRadioStream(final boolean animate) {
		this.readInfoFromRadioStream.setSelected(animate);
	}

	@Override
	public void updatePanel() {
		setReadInfoFromRadioStream(this.stateRadio.isReadInfoFromRadioStream());
	}

	@Override
	public void resetImmediateChanges() {
		// Do nothing
	}

	@Override
	public void validatePanel() throws PreferencesValidationException {
	}

	@Override
	public void dialogVisibilityChanged(final boolean visible) {
		// Do nothing
	}

}
