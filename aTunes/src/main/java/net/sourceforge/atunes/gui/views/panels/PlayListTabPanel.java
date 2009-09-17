/*
 * aTunes 1.14.0
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

package net.sourceforge.atunes.gui.views.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;

import net.sourceforge.atunes.gui.views.controls.PopUpButton;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.CloseOtherPlaylistsAction;
import net.sourceforge.atunes.kernel.actions.ClosePlaylistAction;
import net.sourceforge.atunes.kernel.actions.CopyPlayListToDeviceAction;
import net.sourceforge.atunes.kernel.actions.NewPlayListAction;
import net.sourceforge.atunes.kernel.actions.RenamePlaylistAction;
import net.sourceforge.atunes.kernel.actions.SynchronizeDeviceWithPlayListAction;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The Class PlayListTabPanel.
 */
public class PlayListTabPanel extends JPanel {

    private static final long serialVersionUID = 7382098268271937439L;

    /** The Constant TAB_HEIGHT. */
    public static final int TAB_HEIGHT = 26;

    /** The play lists pop up button. */
    private PopUpButton playListsPopUpButton;

    /** Button to create a new play list. */
    private JMenuItem newPlayListMenuItem;

    /** Button to arrange columns */
    private JMenuItem arrangeColumnsMenuItem;

    /** TabbedPane of play lists. */
    private JTabbedPane playListTabbedPane;

    /** Popup menu. */
    private JPopupMenu popupMenu;

    /**
     * Instantiates a new play list tab panel.
     */
    public PlayListTabPanel() {
        super(new BorderLayout());
        addContent();
        setPreferredSize(new Dimension(10, TAB_HEIGHT));
    }

    /**
     * Adds the content.
     */
    private void addContent() {
        playListsPopUpButton = new PopUpButton(I18nUtils.getString("PLAYLIST"), PopUpButton.BOTTOM_RIGHT);
        newPlayListMenuItem = new JMenuItem(Actions.getAction(NewPlayListAction.class));
        arrangeColumnsMenuItem = new JMenuItem(I18nUtils.getString("ARRANGE_COLUMNS"));
        playListTabbedPane = new JTabbedPane();
        playListTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        JPanel auxPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(2, 0, 0, 0);
        auxPanel.add(playListsPopUpButton, c);

        add(auxPanel, GuiUtils.getComponentOrientation().isLeftToRight() ? BorderLayout.WEST : BorderLayout.EAST);
        add(playListTabbedPane, BorderLayout.CENTER);

        playListTabbedPane.setPreferredSize(new Dimension(0, TAB_HEIGHT));

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

}
