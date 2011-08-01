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

package net.sourceforge.atunes.gui.views.dialogs;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.gui.views.controls.CustomFrame;
import net.sourceforge.atunes.gui.views.controls.ScrollableFlowPanel;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;

public final class CoverNavigatorFrame extends CustomFrame {

    private static final long serialVersionUID = -1744765531225480303L;

    private JList list;
    private JPanel coversPanel;
    private JButton coversButton;

    /**
     * Instantiates a new cover navigator frame.
     * 
     * @param artists
     *            the artists
     */
    public CoverNavigatorFrame(List<Artist> artists, Component owner) {
        super(I18nUtils.getString("COVER_NAVIGATOR"), GuiUtils.getComponentWidthForResolution(0.75f), GuiUtils.getComponentHeightForResolution(0.75f), owner);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setContent(artists);
        GuiUtils.applyComponentOrientation(this);
        enableCloseActionWithEscapeKey();
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
     */
    private void setContent(List<Artist> artists) {
        JPanel panel = new JPanel(new GridBagLayout());

        coversPanel = new ScrollableFlowPanel();
        coversPanel.setOpaque(false);
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.LEADING);
        coversPanel.setLayout(flowLayout);

        list = LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getList();
        list.setListData(artists.toArray());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane listScrollPane = LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getListScrollPane(list);
        listScrollPane.setMinimumSize(new Dimension(200, 0));

        JScrollPane coversScrollPane = new JScrollPane(coversPanel);
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
