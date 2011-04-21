/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.gui.views.bars;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import net.sourceforge.atunes.gui.views.panels.ToolBarFilterPanel;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.CustomSearchAction;
import net.sourceforge.atunes.kernel.actions.EditPreferencesAction;
import net.sourceforge.atunes.kernel.actions.RipCDAction;
import net.sourceforge.atunes.kernel.actions.ShowAudioObjectPropertiesAction;
import net.sourceforge.atunes.kernel.actions.ShowContextAction;
import net.sourceforge.atunes.kernel.actions.ShowNavigationTableAction;
import net.sourceforge.atunes.kernel.actions.ShowNavigationTreeAction;
import net.sourceforge.atunes.kernel.actions.ShowStatsAction;
import net.sourceforge.atunes.kernel.modules.navigator.AbstractNavigationView;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;

public final class ToolBar extends JToolBar {

    private static final long serialVersionUID = 3146746580753998589L;

    private static final Dimension SEPARATOR = new Dimension(10, 0);
    
    private List<JToggleButton> navigationButtons;
    
    private JButton preferences;
    private JToggleButton showNavigationTree;
    private JToggleButton showNavigationTable;
    private JToggleButton showAudioObjectProperties;
    private JToggleButton showContext;
    private JButton stats;
    private JButton ripCD;
    private JButton search;
    private ToolBarFilterPanel filterPanel;

    /**
     * Instantiates a new tool bar.
     */
    public ToolBar() {
        super();
        setFloatable(false);
        setButtons();
    }

    /**
     * Sets the buttons.
     */
    private void setButtons() {    	
        showNavigationTree = new JToggleButton(Actions.getAction(ShowNavigationTreeAction.class));
        showNavigationTree.setText(null);
        add(showNavigationTree);
        
        addSeparator();

        // Add dinamically actions to show each navigation view loaded, then select the last navigation view
    	navigationButtons = new ArrayList<JToggleButton>();
    	ButtonGroup group = new ButtonGroup();
        for (AbstractNavigationView navigationView : NavigationHandler.getInstance().getNavigationViews()) {
        	JToggleButton button = new JToggleButton(navigationView.getActionToShowView());
        	button.setText(null);
        	button.setSelected(navigationView.getClass().getName().equals(ApplicationState.getInstance().getNavigationView()));
        	group.add(button);
            add(button);            
            navigationButtons.add(button);
        }

        addSeparator();

        preferences = new JButton(Actions.getAction(EditPreferencesAction.class));
        preferences.setText(null);
        add(preferences);

        addSeparator();

        showNavigationTable = new JToggleButton(Actions.getAction(ShowNavigationTableAction.class));
        showNavigationTable.setText(null);
        add(showNavigationTable);

        showAudioObjectProperties = new JToggleButton(Actions.getAction(ShowAudioObjectPropertiesAction.class));
        showAudioObjectProperties.setText(null);
        add(showAudioObjectProperties);

        showContext = new JToggleButton(Actions.getAction(ShowContextAction.class));
        showContext.setText(null);
        add(showContext);

        addSeparator();

        stats = new JButton(Actions.getAction(ShowStatsAction.class));
        stats.setText(null);
        add(stats);

        addSeparator();

        ripCD = new JButton(Actions.getAction(RipCDAction.class));
        ripCD.setText(null);
        add(ripCD);

        addSeparator();

        search = new JButton(Actions.getAction(CustomSearchAction.class));
        search.setText(null);
        add(search);

        filterPanel = new ToolBarFilterPanel();
        add(filterPanel);
    }
    
    @Override
    public void addSeparator() {
    	super.addSeparator(SEPARATOR);
    }

    public JToggleButton getShowNavigationTree() {
        return showNavigationTree;
    }

    public JToggleButton getShowNavigationTable() {
        return showNavigationTable;
    }

    public JToggleButton getShowAudioObjectProperties() {
        return showAudioObjectProperties;
    }

    public JToggleButton getShowContext() {
        return showContext;
    }

    /**
     * @return the filterPanel
     */
    public ToolBarFilterPanel getFilterPanel() {
        return filterPanel;
    }
    
    /**
     * Shows or hides navigation buttons
     * @param show
     */
    public void showNavigationButtons(boolean show) {
    	for (JToggleButton button : navigationButtons) {
    		button.setVisible(show);
    	}
    }
}
