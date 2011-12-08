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

package net.sourceforge.atunes.gui.views.dialogs;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.gui.views.controls.ScrollableFlowPanel;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.utils.I18nUtils;

public final class CoverNavigatorFrame extends AbstractCustomDialog {

    private static final long serialVersionUID = -1744765531225480303L;

    private JList list;
    private JPanel coversPanel;
    private JButton coversButton;

    /**
     * Instantiates a new cover navigator frame.
     * 
     * @param artists
     * @param owner
     * @param lookAndFeelManager
     */
    public CoverNavigatorFrame(List<Artist> artists, Window owner, ILookAndFeelManager lookAndFeelManager) {
        super(owner, GuiUtils.getComponentWidthForResolution(0.75f), GuiUtils.getComponentHeightForResolution(0.75f), true, CloseAction.DISPOSE, lookAndFeelManager.getCurrentLookAndFeel());
        setTitle(I18nUtils.getString("COVER_NAVIGATOR"));
        setContent(artists, lookAndFeelManager.getCurrentLookAndFeel());
    }

    /**
     * Gets the covers button.
     * 
     * @return the coversButton
     */
    public JButton getCoversButton() {
        return coversButton;
    }

    /**
     * Gets the covers panel.
     * 
     * @return the coversPanel
     */
    public JPanel getCoversPanel() {
        return coversPanel;
    }

    /**
     * Gets the list.
     * 
     * @return the list
     */
    public JList getList() {
        return list;
    }

    /**
     * Sets the content.
     * 
     * @param artists
     *            the new content
     * @param iLookAndFeel 
     */
    private void setContent(List<Artist> artists, ILookAndFeel iLookAndFeel) {
        JPanel panel = new JPanel(new GridBagLayout());

        coversPanel = new ScrollableFlowPanel();
        coversPanel.setOpaque(false);
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.LEADING);
        coversPanel.setLayout(flowLayout);

        list = iLookAndFeel.getList();
        list.setListData(artists.toArray());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane listScrollPane = iLookAndFeel.getListScrollPane(list);
        listScrollPane.setMinimumSize(new Dimension(200, 0));

        JScrollPane coversScrollPane = iLookAndFeel.getScrollPane(coversPanel);
        coversScrollPane.setBorder(BorderFactory.createLineBorder(GuiUtils.getBorderColor()));
        coversScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        coversScrollPane.getVerticalScrollBar().setUnitIncrement(20);

        coversButton = new JButton(I18nUtils.getString("GET_COVERS"));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        c.weighty = 1;
        c.fill = GridBagConstraints.VERTICAL;
        panel.add(listScrollPane, c);
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.8;
        c.fill = GridBagConstraints.BOTH;
        panel.add(coversScrollPane, c);
        c.gridx = 1;
        c.gridy = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        panel.add(coversButton, c);

        add(panel);
    }

}
