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
package net.sourceforge.atunes.kernel.modules.context;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.GuiUtils;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.border.DropShadowBorder;

/**
 * This class represents a little graphic component used in a context panel to
 * show information about an audio object Information shown is retrieved from a
 * context data source
 * 
 * @author alex
 * 
 */
public abstract class ContextPanelContent {

    private static final long serialVersionUID = 7059398864514654378L;

    /**
     * Data Source used by this content to retrieve context information
     */
    private ContextInformationDataSource dataSource;

    /**
     * Worker used to retrieve data
     */
    private ContextInformationSwingWorker worker;

    /**
     * JXTaskPane that handles this content
     */
    private JXTaskPane parentTaskPane;

    /**
     * Creates a new content with its custom data source
     * 
     * @param dataSource
     */
    protected ContextPanelContent(ContextInformationDataSource dataSource) {
        super();
        this.dataSource = dataSource;
    }

    /**
     * Updates the context panel content with information of the given audio
     * object
     * 
     * @param audioObject
     */
    protected final void updateContextPanelContent(AudioObject audioObject) {
        // Get data source parameters and call data source
        callDataSource(getDataSourceParameters(audioObject));
    }

    /**
     * Calls data source to get context information
     * 
     * @param parameters
     */
    private void callDataSource(Map<String, ?> parameters) {
        // Cancel previous worker if it's not done
        if (worker != null && !worker.isDone()) {
            worker.cancel(true);
        }

        // Create a new worker and call it
        worker = new ContextInformationSwingWorker(this, this.dataSource, parameters);
        worker.execute();
    }

    /**
     * Removes content from the context panel content This method must clear all
     * previous information retrieved for previous audio object
     */
    protected void clearContextPanelContent() {
        parentTaskPane.setCollapsed(true);
        if (worker != null) {
            worker.cancel(true);
        }
    }

    /**
     * By default contents don't need special scroll
     * 
     * @return
     */
    protected boolean isScrollNeeded() {
        return false;
    }

    /**
     * Given an audio object returns a map with necessary parameters to call
     * data source
     * 
     * @param audioObject
     * @return Map with parameters to call data source
     */
    protected abstract Map<String, ?> getDataSourceParameters(AudioObject audioObject);

    /**
     * Given a map containing result from data source updates this content
     * 
     * @param result
     */
    protected abstract void updateContentWithDataSourceResult(Map<String, ?> result);

    /**
     * Returns the content name to be shown in context panel
     * 
     * @return
     */
    protected abstract String getContentName();

    /**
     * Method to return a Swing component with panel content
     */
    protected abstract Component getComponent();

    /**
     * Returns a list of components to be shown in a popup button If this method
     * returns <code>null</code> or empty list button will not be visible
     * (default behaviour)
     * 
     * @return
     */
    protected List<Component> getOptions() {
        return null;
    }

    /**
     * Creates a panel to be shown in each row of a panel table
     * 
     * @param image
     * @param text
     * @param backgroundColor
     * @param imageMaxWidth
     * @param imageMaxHeight
     * @return
     */
    protected static JPanel getPanelForTableRenderer(ImageIcon image, String text, Color backgroundColor, int imageMaxWidth, int imageMaxHeight) {
        // This renderer is a little tricky because images have no the same size so we must add two labels with custom insets to
        // get desired alignment of images and text. Other ways to achieve this like setPreferredSize doesn't work because when width of panel is low
        // preferred size is ignored, but insets don't
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(true);
        GridBagConstraints c = new GridBagConstraints();

        JLabel imageLabel = new JLabel(image);
        imageLabel.setOpaque(true);
        imageLabel.setBorder(image != null ? new DropShadowBorder() : null);
        JLabel textLabel = new JLabel(text);
        textLabel.setOpaque(true);
        textLabel.setVerticalAlignment(SwingConstants.TOP);

        if (backgroundColor != null) {
            textLabel.setBackground(backgroundColor);
            panel.setBackground(backgroundColor);
            imageLabel.setBackground(backgroundColor);
        }

        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(2, (imageMaxWidth + 20) / 2 - (image != null ? image.getIconWidth() : 0) / 2, 0, 0);
        panel.add(imageLabel, c);
        c.gridx = 1;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, (imageMaxWidth + 20) / 2 - (image != null ? image.getIconWidth() : 0) / 2, 0, 0);
        panel.add(textLabel, c);

        GuiUtils.applyComponentOrientation(panel);
        return panel;
    }

    /**
     * @param parentTaskPane
     *            the parentTaskPane to set
     */
    protected void setParentTaskPane(JXTaskPane parentTaskPane) {
        this.parentTaskPane = parentTaskPane;
    }

    /**
     * @return the parentTaskPane
     */
    protected JXTaskPane getParentTaskPane() {
        return parentTaskPane;
    }

}
