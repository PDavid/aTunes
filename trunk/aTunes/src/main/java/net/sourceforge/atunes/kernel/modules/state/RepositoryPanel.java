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
import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.sourceforge.atunes.gui.views.controls.CustomTextField;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The preferences panel for respository settings.
 */
public final class RepositoryPanel extends AbstractPreferencesPanel {

    private static final long serialVersionUID = 3331810461314007217L;

    /** 
     * The refresh time of repository 
     */
    private JComboBox refreshTime;

    /**
     * Text field to specify command to execute before access repository
     */
    private JTextField commandBeforeAccessRepository;

    /**
     * Text field to specify command to execute after access repository (when
     * application finish)
     */
    private JTextField commandAfterAccessRepository;
    
    /**
     * Instantiates a new repository panel.
     */
    public RepositoryPanel() {
        super(I18nUtils.getString("REPOSITORY"));
        refreshTime = new JComboBox(new Integer[] { 0, 5, 10, 15, 30, 60 });
        commandBeforeAccessRepository = new CustomTextField(20);
        commandAfterAccessRepository = new CustomTextField(20);
        setupPanel();
    }

	/**
	 * Add components to panel
	 */
	private void setupPanel() {
		GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 10, 0, 0);
        add(new JLabel(I18nUtils.getString("REPOSITORY_REFRESH_TIME")), c);
        c.gridx = 1;
        c.weightx = 1;
        c.insets = new Insets(0, 10, 0, 0);
        add(refreshTime, c);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 0;
        c.weightx = 1;
        c.insets = new Insets(10, 10, 0, 0);
        add(new JLabel(I18nUtils.getString("COMMAND_BEFORE_REPOSITORY_ACCESS")), c);
        c.gridx = 1;
        c.weightx = 1;
        add(commandBeforeAccessRepository, c);
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0;
        c.weighty = 1;
        c.anchor = GridBagConstraints.NORTHWEST;
        add(new JLabel(I18nUtils.getString("COMMAND_AFTER_REPOSITORY_ACCESS")), c);
        c.gridx = 1;
        c.weightx = 1;
        add(commandAfterAccessRepository, c);
	}

    @Override
    public boolean applyPreferences(IState state) {
        state.setAutoRepositoryRefreshTime((Integer) refreshTime.getSelectedItem());
        
        state.setCommandBeforeAccessRepository(commandBeforeAccessRepository.getText());
        state.setCommandAfterAccessRepository(commandAfterAccessRepository.getText());
        return false;
    }

    /**
     * Sets the refresh time.
     * 
     * @param time
     *            the new refresh time
     */
    private void setRefreshTime(int time) {
        refreshTime.setSelectedItem(time);
    }

    /**
     * Sets the command to execute before access repository
     * 
     * @param command
     */
    private void setCommandBeforeAccessRepository(String command) {
        commandBeforeAccessRepository.setText(command);
    }

    /**
     * Sets the command to execute after access repository
     * 
     * @param command
     */
    private void setCommandAfterAccessRepository(String command) {
        commandAfterAccessRepository.setText(command);
    }

    @Override
    public void updatePanel(IState state) {
        setRefreshTime(state.getAutoRepositoryRefreshTime());
        setCommandBeforeAccessRepository(state.getCommandBeforeAccessRepository());
        setCommandAfterAccessRepository(state.getCommandAfterAccessRepository());
    }

    @Override
    public void validatePanel() throws PreferencesValidationException {
    }

    @Override
    public void dialogVisibilityChanged(boolean visible) {
        // Do nothing
    }

    @Override
    public void resetImmediateChanges(IState state) {
        // Do nothing
    }
}
