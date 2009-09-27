/*
 * aTunes 2.0.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.kernel.controllers.playListTab;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import net.sourceforge.atunes.gui.views.panels.ButtonTabComponent;
import net.sourceforge.atunes.gui.views.panels.PlayListTabPanel;
import net.sourceforge.atunes.kernel.controllers.model.PanelController;

/**
 * The Class PlayListTabController.
 */
public class PlayListTabController extends PanelController<PlayListTabPanel> {

    /**
     * Instantiates a new play list tab controller.
     * 
     * @param panel
     *            the panel
     */
    public PlayListTabController(PlayListTabPanel panel) {
        super(panel);
        addBindings();
        addStateBindings();
    }

    @Override
    protected void addBindings() {
        PlayListTabListener listener = new PlayListTabListener(getPanelControlled());
        getPanelControlled().getArrangeColumnsMenuItem().addActionListener(listener);
        getPanelControlled().getPlayListTabbedPane().addChangeListener(listener);
        getPanelControlled().getPlayListTabbedPane().addMouseListener(listener);
        getPanelControlled().getPlayListsPopUpButton().addActionListener(listener);
    }

    @Override
    protected void addStateBindings() {
        // Nothing to do
    }

    /**
     * Delete play list.
     * 
     * @param index
     *            the index
     */
    public void deletePlayList(int index) {
        getPanelControlled().getPlayListTabbedPane().removeTabAt(index);
    }

    /**
     * Force switch to.
     * 
     * @param index
     *            the index
     */
    public void forceSwitchTo(int index) {
        getPanelControlled().getPlayListTabbedPane().setSelectedIndex(index);
    }

    /**
     * New play list.
     * 
     * @param name
     *            the name
     */
    public void newPlayList(String name) {
        JPanel emptyPanel = new JPanel();
        emptyPanel.setPreferredSize(new Dimension(0, 0));
        emptyPanel.setSize(0, 0);
        getPanelControlled().getPlayListTabbedPane().addTab(name, emptyPanel);
        getPanelControlled().getPlayListTabbedPane().setTabComponentAt(getPanelControlled().getPlayListTabbedPane().indexOfComponent(emptyPanel),
                new ButtonTabComponent(name, getPanelControlled().getPlayListTabbedPane()));
        // Force size of tabbed pane to avoid increasing height
        getPanelControlled().getPlayListTabbedPane().setPreferredSize(new Dimension(0, PlayListTabPanel.TAB_HEIGHT));
    }

    @Override
    protected void notifyReload() {
        // Nothing to do
    }

    /**
     * Rename play list.
     * 
     * @param index
     *            the index
     * @param newName
     *            the new name
     */
    public void renamePlayList(int index, String newName) {
        getPanelControlled().getPlayListTabbedPane().setTitleAt(index, newName);
        ((ButtonTabComponent) getPanelControlled().getPlayListTabbedPane().getTabComponentAt(index)).getLabel().setText(newName);
    }

    /**
     * Return names of play lists.
     * 
     * @return the names of play lists
     */
    public List<String> getNamesOfPlayLists() {
        ArrayList<String> result = new ArrayList<String>();
        for (int i = 0; i < getPanelControlled().getPlayListTabbedPane().getTabCount(); i++) {
            result.add(getPanelControlled().getPlayListTabbedPane().getTitleAt(i));
        }
        return result;
    }

    /**
     * Returns selected play list index
     * 
     * @return
     */
    public int getSelectedTabIndex() {
        return getPanelControlled().getPlayListTabbedPane().getSelectedIndex();
    }

    /**
     * Returns name of play list at given position
     * 
     * @param index
     * @return
     */
    public String getPlayListName(int index) {
        return getPanelControlled().getPlayListTabbedPane().getTitleAt(index);
    }
}
