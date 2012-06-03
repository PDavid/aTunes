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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;

import net.sourceforge.atunes.gui.views.controls.ToggleButtonFlowPanel;
import net.sourceforge.atunes.gui.views.panels.PlayListSelectorPanel;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IStatePlaylist;

public class PlayListSelectorWrapper {

	private JComboBox playListCombo;
	
	private ToggleButtonFlowPanel playListButtonFlowPanel;
	
	private ILookAndFeelManager lookAndFeelManager;
	
	private IStatePlaylist statePlaylist;
	
	private IPlayListHandler playListHandler;
	
	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}
	
	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

	/**
	 * @param statePlaylist
	 */
	public void setStatePlaylist(IStatePlaylist statePlaylist) {
		this.statePlaylist = statePlaylist;
	}
	
	/**
	 * Creates both type of selectors
	 */
	public void initialize() {
    	playListCombo = new JComboBox();
    	playListCombo.setMaximumRowCount(30);

    	playListButtonFlowPanel = new ToggleButtonFlowPanel(false, lookAndFeelManager);
	}
	
	/**
	 * Arrange components in play list selector panel
	 * @param selectorPanel
	 * @param options
	 * @param playListFilterPanel
	 */
	public void arrangeComponents(PlayListSelectorPanel selectorPanel) {
    	GridBagConstraints c = new GridBagConstraints();
    	
    	c.weighty = 1;
    	c.fill = GridBagConstraints.VERTICAL;
    	c.insets = new Insets(1, 0, 1, 0);
    	selectorPanel.add(selectorPanel.getOptions().getSwingComponent(), c);
    	
    	c.gridx = 1;
        c.weightx = 1;
		c.anchor = GridBagConstraints.WEST;
    	if (statePlaylist.isShowPlayListSelectorComboBox()) {
        	c.fill = GridBagConstraints.VERTICAL;
    		selectorPanel.add(playListCombo, c);
    	} else {
        	c.fill = GridBagConstraints.BOTH;
        	c.insets = new Insets(0, 0, 0, 10);
    		selectorPanel.add(playListButtonFlowPanel, c);
    	}
        
        c.gridx = 2;
        c.weightx = 0;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(0, 0, 0, 5);
        selectorPanel.add(selectorPanel.getPlayListFilterPanel().getSwingComponent(), c);
	}

	/**
	 * Initializes both components
	 * @param l
	 */
	void addBindings(PlayListTabListener l) {
        playListCombo.addItemListener(l);
    	playListCombo.setModel(PlayListComboModel.getNewComboModel());
    	
    	playListButtonFlowPanel.addItemListener(l);
	}

	void deletePlayList(int index) {
   		((PlayListComboModel)playListCombo.getModel()).removeItemAt(index);		
   		playListButtonFlowPanel.removeButton(index);
	}

	/**
	 * @param index
	 */
	void forceSwitchTo(int index) {
        playListCombo.setSelectedIndex(index);
        
        playListButtonFlowPanel.setSelectedButton(index);
	}

	void newPlayList(final String name) {
    	((PlayListComboModel)playListCombo.getModel()).addItem(name);
    	
    	playListButtonFlowPanel.addButton(name, name, null, new AbstractAction() {
			
			private static final long serialVersionUID = -8487582617110724128L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int index = playListButtonFlowPanel.getIndexOfButton(name);
				forceSwitchTo(index);
			}
		}, name);
	}

	void renamePlayList(int index, String newName) {
    	((PlayListComboModel)playListCombo.getModel()).rename(index, newName);
    	// Forces update of combo box by selecting again current play list
    	playListCombo.setSelectedIndex(index);
    	
    	playListButtonFlowPanel.renameButton(index, newName);
	}

	List<String> getNamesOfPlayLists() {
    	return ((PlayListComboModel)playListCombo.getModel()).getItems();
	}
	
    int getSelectedPlayListIndex() {
        return playListCombo.getSelectedIndex() != -1 ? playListCombo.getSelectedIndex() : 0;
    }

    String getPlayListName(int index) {
        return ((PlayListComboModel)playListCombo.getModel()).getElementAt(index);
    }

	/**
	 * Switches to playlist
	 * @param selectedPlayListIndex
	 */
	public void switchToPlaylist(int selectedPlayListIndex) {
		playListHandler.switchToPlaylist(getSelectedPlayListIndex());
		// This is called when selecting item in combo so set selected button too
		playListButtonFlowPanel.setSelectedButton(getSelectedPlayListIndex());
	}

}
