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

package net.sourceforge.atunes.gui.views.panels;

import java.awt.Component;
import java.awt.GridBagLayout;

import javax.swing.AbstractAction;
import javax.swing.JPanel;

import net.sourceforge.atunes.gui.views.controls.PopUpButton;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListSelectorWrapper;
import net.sourceforge.atunes.model.IFilterPanel;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IPlayListSelectorPanel;
import net.sourceforge.atunes.model.IPopUpButton;

public final class PlayListSelectorPanel extends JPanel implements IPlayListSelectorPanel {

	private static final long serialVersionUID = 7382098268271937439L;

	private PopUpButton options;
	
	private ILookAndFeelManager lookAndFeelManager;
	
	private IFilterPanel playListFilterPanel;
	
	private AbstractAction newPlayListAction;
    
	private AbstractAction renamePlayListAction;
	
	private AbstractAction closePlayListAction;
	
	private AbstractAction closeOtherPlayListsAction;
	
	private AbstractAction arrangePlayListColumnsAction;
	
	private AbstractAction copyPlayListToDeviceAction;
	
	private AbstractAction syncDeviceWithPlayListAction;
	
	private PlayListSelectorWrapper playListSelectorWrapper;
	
    /**
     * Instantiates a new play list tab panel.
     */
    public PlayListSelectorPanel() {
        super(new GridBagLayout());
    }
    
    /**
     * @param playListSelectorWrapper
     */
    public void setPlayListSelectorWrapper(PlayListSelectorWrapper playListSelectorWrapper) {
		this.playListSelectorWrapper = playListSelectorWrapper;
	}
    
    /**
     * @param syncDeviceWithPlayListAction
     */
    public void setSyncDeviceWithPlayListAction(AbstractAction syncDeviceWithPlayListAction) {
		this.syncDeviceWithPlayListAction = syncDeviceWithPlayListAction;
	}
    
    /**
     * @param copyPlayListToDeviceAction
     */
    public void setCopyPlayListToDeviceAction(AbstractAction copyPlayListToDeviceAction) {
		this.copyPlayListToDeviceAction = copyPlayListToDeviceAction;
	}
    
    /**
     * @param arrangePlayListColumnsAction
     */
    public void setArrangePlayListColumnsAction(AbstractAction arrangePlayListColumnsAction) {
		this.arrangePlayListColumnsAction = arrangePlayListColumnsAction;
	}
    
    /**
     * @param closeOtherPlayListsAction
     */
    public void setCloseOtherPlayListsAction(AbstractAction closeOtherPlayListsAction) {
		this.closeOtherPlayListsAction = closeOtherPlayListsAction;
	}
    
    /**
     * @param closePlayListAction
     */
    public void setClosePlayListAction(AbstractAction closePlayListAction) {
		this.closePlayListAction = closePlayListAction;
	}
    
    /**
     * @param renamePlayListAction
     */
    public void setRenamePlayListAction(AbstractAction renamePlayListAction) {
		this.renamePlayListAction = renamePlayListAction;
	}
    
    /**
     * @param newPlayListAction
     */
    public void setNewPlayListAction(AbstractAction newPlayListAction) {
		this.newPlayListAction = newPlayListAction;
	}

    /**
     * @param playListFilterPanel
     */
    public void setPlayListFilterPanel(IFilterPanel playListFilterPanel) {
		this.playListFilterPanel = playListFilterPanel;
	}
    
    /**
     * @param lookAndFeelManager
     */
    public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

    /**
     * Adds the content.
     */
    public void initialize() {
    	options = new PopUpButton(PopUpButton.BOTTOM_RIGHT, lookAndFeelManager);
    	
    	playListSelectorWrapper.arrangeComponents(this, options, playListFilterPanel);

        addActions();
    }

	/**
	 * Add actions to popup 
	 */
	private void addActions() {
		options.add(newPlayListAction);
        options.add(renamePlayListAction);
        options.add(closePlayListAction);
        options.add(closeOtherPlayListsAction);
        options.addSeparator();
        options.add(arrangePlayListColumnsAction);
        options.addSeparator();
        options.add(copyPlayListToDeviceAction);
        options.add(syncDeviceWithPlayListAction);
	}

	@Override
	public IPopUpButton getOptions() {
		return options;
	}
	
	@Override
	public Component getSwingComponent() {
		return this;
	}

	/**
	 * Shows combo box to select play lists if necessary
	 */
	public void showPlayListSelectorComboBox() {
		removeAll();
    	playListSelectorWrapper.arrangeComponents(this, options, playListFilterPanel);
	}
}
