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

package net.sourceforge.atunes.gui.views.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import net.sourceforge.atunes.gui.views.controls.PopUpButton;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.CloseOtherPlaylistsAction;
import net.sourceforge.atunes.kernel.actions.ClosePlaylistAction;
import net.sourceforge.atunes.kernel.actions.CopyPlayListToDeviceAction;
import net.sourceforge.atunes.kernel.actions.NewPlayListAction;
import net.sourceforge.atunes.kernel.actions.RenamePlaylistAction;
import net.sourceforge.atunes.kernel.actions.SynchronizeDeviceWithPlayListAction;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListTableModel;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;

public final class PlayListTabPanel extends JPanel {

    public static class TabReorderer extends MouseInputAdapter {

        private JTabbedPane tabPane;
        private int draggedTabIndex;

        public TabReorderer(JTabbedPane pane) {
            this.tabPane = pane;
            draggedTabIndex = -1;
        }

        public void enableReordering(JTabbedPane pane) {
            pane.addMouseListener(this);
            pane.addMouseMotionListener(this);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            draggedTabIndex = tabPane.getUI().tabForCoordinate(tabPane, e.getX(), e.getY());
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (draggedTabIndex == -1) {
                return;
            }

            int targetTabIndex = tabPane.getUI().tabForCoordinate(tabPane, e.getX(), e.getY());
            if (targetTabIndex != -1 && targetTabIndex != draggedTabIndex) {

                ControllerProxy.getInstance().getPlayListTabController().switchPlayListTabs(draggedTabIndex, targetTabIndex);
                PlayListHandler.getInstance().movePlaylistToPosition(draggedTabIndex, targetTabIndex);

                ((PlayListTableModel) GuiHandler.getInstance().getPlayListTable().getModel()).setVisiblePlayList(PlayListHandler.getInstance().getCurrentPlayList(true));
                ControllerProxy.getInstance().getPlayListTabController().forceSwitchTo(targetTabIndex);

                draggedTabIndex = -1;

                SwingUtilities.invokeLater(new SwitchPlayList(targetTabIndex));
            }
        }

    }

    private static final long serialVersionUID = 7382098268271937439L;

    private PopUpButton playListsPopUpButton;

    /** Button to create a new play list. */
    private JMenuItem newPlayListMenuItem;

    /** Button to arrange columns */
    private JMenuItem arrangeColumnsMenuItem;

    /** TabbedPane of play lists. */
    private JTabbedPane playListTabbedPane;

    private JPopupMenu popupMenu;

    /**
     * Instantiates a new play list tab panel.
     */
    public PlayListTabPanel() {
        super(new BorderLayout());
        addContent();
    }

    /**
     * Adds the content.
     */
    private void addContent() {
        playListsPopUpButton = new PopUpButton(I18nUtils.getString("PLAYLIST"), PopUpButton.BOTTOM_RIGHT);
        newPlayListMenuItem = new JMenuItem(Actions.getAction(NewPlayListAction.class));
        arrangeColumnsMenuItem = new JMenuItem(I18nUtils.getString("ARRANGE_COLUMNS"));
        playListTabbedPane = new JTabbedPane();
        
        setPreferredSize(new Dimension(0, playListsPopUpButton.getPreferredSize().height + 5));
        
        playListTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        new TabReorderer(playListTabbedPane).enableReordering(playListTabbedPane);

        JPanel auxPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(2, 0, 0, 0);
        auxPanel.add(playListsPopUpButton, c);

        add(auxPanel, GuiUtils.getComponentOrientation().isLeftToRight() ? BorderLayout.WEST : BorderLayout.EAST);
        add(playListTabbedPane, BorderLayout.CENTER);

        popupMenu = new JPopupMenu();
        popupMenu.add(new JMenuItem(Actions.getAction(RenamePlaylistAction.class)));
        popupMenu.add(new JSeparator());
        popupMenu.add(new JMenuItem(Actions.getAction(CopyPlayListToDeviceAction.class)));
        popupMenu.add(new JMenuItem(Actions.getAction(SynchronizeDeviceWithPlayListAction.class)));
        popupMenu.add(new JSeparator());
        popupMenu.add(new JMenuItem(Actions.getAction(ClosePlaylistAction.class)));
        popupMenu.add(new JMenuItem(Actions.getAction(CloseOtherPlaylistsAction.class)));
        GuiUtils.applyComponentOrientation(this, popupMenu);
    }

    /**
     * Adds the new play list menu item.
     */
    public void addFixedMenuItems() {
        playListsPopUpButton.add(newPlayListMenuItem);
        playListsPopUpButton.add(arrangeColumnsMenuItem);
        playListsPopUpButton.add(new JSeparator());
    }

    /**
     * Gets the play list tabbed pane.
     * 
     * @return the playListTabbedPane
     */
    public JTabbedPane getPlayListTabbedPane() {
        return playListTabbedPane;
    }

    /**
     * Gets the popup menu.
     * 
     * @return the popupMenu
     */
    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    /**
     * Gets the play lists pop up button.
     * 
     * @return the playListsPopUpButton
     */
    public PopUpButton getPlayListsPopUpButton() {
        return playListsPopUpButton;
    }

    /**
     * @return the arrangeColumnsMenuItem
     */
    public JMenuItem getArrangeColumnsMenuItem() {
        return arrangeColumnsMenuItem;
    }

    private static class SwitchPlayList implements Runnable {

        private int targetTabIndex;

        public SwitchPlayList(int targetTabIndex) {
            this.targetTabIndex = targetTabIndex;
        }

        @Override
        public void run() {
            PlayListHandler.getInstance().switchToPlaylist(targetTabIndex);
        }
    }

}
