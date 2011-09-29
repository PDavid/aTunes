/*
 * aTunes 2.1.0-SNAPSHOT
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

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable;
import net.sourceforge.atunes.kernel.modules.draganddrop.PlayListTableTransferHandler;
import net.sourceforge.atunes.kernel.modules.draganddrop.PlayListToDeviceDragAndDropListener;
import net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler;
import net.sourceforge.atunes.model.IColumnSet;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayListTable;

/**
 * The playlist panel.
 * 
 * @author fleax
 */
public final class PlayListPanel extends JPanel {

    private static final long serialVersionUID = -1886323054505118303L;

    /** The play list tab panel. */
    private PlayListTabPanel playListTabPanel;

    /** The play list table. */
    private IPlayListTable playListTable;

    /** The play list table scroll. */
    private JScrollPane playListTableScroll;
    
    private IPlayListHandler playListHandler;
    
    private IRepositoryHandler repositoryHandler;

    /**
     * Instantiates a new play list panel.
     * @param playListHandler
     * @param lookAndFeelManager
     */
    public PlayListPanel(IPlayListHandler playListHandler, ILookAndFeelManager lookAndFeelManager, IRepositoryHandler repositoryHandler) {
        super(new BorderLayout());
        this.playListHandler = playListHandler;
        this.repositoryHandler = repositoryHandler;
        addContent(lookAndFeelManager);
    }

    /**
     * Adds the content.
     * @param lookAndFeelManager 
     */
    private void addContent(ILookAndFeelManager lookAndFeelManager) {
        playListTabPanel = new PlayListTabPanel(lookAndFeelManager);
        playListTable = new PlayListTable((IColumnSet) Context.getBean("playlistColumnSet"), playListHandler, lookAndFeelManager, repositoryHandler);
        playListTableScroll = lookAndFeelManager.getCurrentLookAndFeel().getTableScrollPane(playListTable.getSwingComponent());

        add(playListTabPanel, BorderLayout.NORTH);
        add(playListTableScroll, BorderLayout.CENTER);
    }

    /**
     * Gets the play list table.
     * 
     * @return the play list table
     */
    public IPlayListTable getPlayListTable() {
        return playListTable;
    }

    /**
     * Gets the play list tab panel.
     * 
     * @return the playListTabPanel
     */
    public PlayListTabPanel getPlayListTabPanel() {
        return playListTabPanel;
    }
    
    /**
     * Prepares play list for drag and drop operations
     * @param playListTableTransferHandler
     */
    public void enableDragAndDrop(PlayListTableTransferHandler playListTableTransferHandler) {
        playListTable.setTransferHandler(playListTableTransferHandler);
        playListTableScroll.setTransferHandler(playListTableTransferHandler);
        new PlayListToDeviceDragAndDropListener(Context.getBean(INavigationHandler.class));
    }

}
