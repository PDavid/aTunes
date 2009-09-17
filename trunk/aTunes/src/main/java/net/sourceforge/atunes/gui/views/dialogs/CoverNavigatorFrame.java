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

package net.sourceforge.atunes.gui.views.dialogs;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

import net.sourceforge.atunes.gui.views.controls.CustomFrame;
import net.sourceforge.atunes.kernel.modules.repository.model.Artist;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The Class CoverNavigatorFrame.
 */
public class CoverNavigatorFrame extends CustomFrame {

    private static final long serialVersionUID = -1744765531225480303L;

    /** The list. */
    private JList list;

    /** The covers panel. */
    private JPanel coversPanel;

    /** The covers button. */
    private JButton coversButton;

    /** The width. */
    private static int width = GuiUtils.getComponentWidthForResolution(1280, 1150);

    /** The height. */
    private static int height = GuiUtils.getComponentHeightForResolution(1024, 650);

    /** The covers scroll pane width. */
    private int coversScrollPaneWidth = (int) (width * 0.7) - 50;

    /**
     * Instantiates a new cover navigator frame.
     * 
     * @param artists
     *            the artists
     */
    public CoverNavigatorFrame(List<Artist> artists, Component owner) {
        super(I18nUtils.getString("COVER_NAVIGATOR"), width, height, owner);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setContent(artists);
        setResizable(false);
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
     * Gets the covers scroll pane width.
     * 
     * @return the coversScrollPaneWidth
     */
    public int getCoversScrollPaneWidth() {
        return coversScrollPaneWidth;
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
        JPanel panel = new JPanel(null);

        coversPanel = new JPanel(new GridBagLayout());

        list = new JList(artists.toArray());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane listScrollPane = new JScrollPane(list);
        listScrollPane.setLocation(10, 10);
        listScrollPane.setSize((int) (width * 0.3), height - 50);

        JScrollPane coversScrollPane = new JScrollPane(coversPanel);
        coversScrollPane.setBorder(BorderFactory.createLineBorder(GuiUtils.getBorderColor()));
        coversScrollPane.setLocation(10 + (int) (width * 0.3) + 20, 10);
        coversScrollPane.setSize(coversScrollPaneWidth, height - 90);
        coversScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        coversScrollPane.getVerticalScrollBar().setUnitIncrement(20);

        coversButton = new JButton(I18nUtils.getString("GET_COVERS"));
        coversButton.setSize(new Dimension(150, 25));
        coversButton.setLocation(10 + (int) (width * 0.3) + 20, height - 70);

        panel.add(listScrollPane);
        panel.add(coversScrollPane);
        panel.add(coversButton);

        add(panel);
    }

}
