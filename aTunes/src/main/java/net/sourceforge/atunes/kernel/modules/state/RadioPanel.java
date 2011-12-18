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

package net.sourceforge.atunes.kernel.modules.state;

import java.awt.GridBagConstraints;

import javax.swing.JCheckBox;

import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The Class RadioPanel.
 */
public final class RadioPanel extends AbstractPreferencesPanel {

    private static final long serialVersionUID = 4489293347321979288L;

    /** The read info from radio stream. */
    private JCheckBox readInfoFromRadioStream;

    /** The show all radio stations. */
    private JCheckBox showAllRadioStations;

    /**
     * Instantiates a new radio panel.
     */
    public RadioPanel() {
        super(I18nUtils.getString("RADIO"));
        readInfoFromRadioStream = new JCheckBox(I18nUtils.getString("READ_INFO_FROM_RADIO_STREAM"));
        showAllRadioStations = new JCheckBox(I18nUtils.getString("SHOW_ALL_RADIO_STATIONS"));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 0;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        add(readInfoFromRadioStream, c);
        c.gridy = 1;
        c.weighty = 1;
        add(showAllRadioStations, c);
    }

    @Override
    public boolean applyPreferences(IState state) {
        state.setReadInfoFromRadioStream(readInfoFromRadioStream.isSelected());
        state.setShowAllRadioStations(showAllRadioStations.isSelected());
        return false;
    }

    /**
     * Sets the read info from radio stream.
     * 
     * @param animate
     *            the new read info from radio stream
     */
    private void setReadInfoFromRadioStream(boolean animate) {
        readInfoFromRadioStream.setSelected(animate);
    }

    /**
     * Sets the show all radio stations.
     * 
     * @param showAll
     *            the new show all radio stations
     */
    private void setShowAllRadioStations(boolean showAll) {
        showAllRadioStations.setSelected(showAll);
    }

    @Override
    public void updatePanel(IState state) {
        setReadInfoFromRadioStream(state.isReadInfoFromRadioStream());
        setShowAllRadioStations(state.isShowAllRadioStations());
    }

    @Override
    public void resetImmediateChanges(IState state) {
        // Do nothing
    }

    @Override
    public void validatePanel() throws PreferencesValidationException {
    }

    @Override
    public void dialogVisibilityChanged(boolean visible) {
        // Do nothing
    }

}
