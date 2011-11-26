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

package net.sourceforge.atunes.kernel.modules.context;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.views.controls.PopUpButton;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;

import org.jdesktop.swingx.border.DropShadowBorder;

class ContextTableRowPanelFactory<T> {

	/**
     * Creates a panel to be shown in each row of a panel table
     * @param actions
     * @param table
     * @param image
     * @param text
     * @param backgroundColor
     * @param foregroundColor
     * @param imageMaxWidth
     * @param imageMaxHeight
     * @param hasFocus
     * @return
     */
    public JPanel getPanelForTableRenderer(List<ContextTableAction<T>> actions,
    												final ContextTable table,
    												ImageIcon image, 
    											     String text, 
    											     Color backgroundColor, 
    											     Color foregroundColor, 
    											     int imageMaxWidth, 
    											     int imageMaxHeight,
    											     boolean hasFocus) {
    	
        // This renderer is a little tricky because images have no the same size so we must add two labels with custom insets to
        // get desired alignment of images and text. Other ways to achieve this like setPreferredSize doesn't work because when width of panel is low
        // preferred size is ignored, but insets don't
        final JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        JLabel imageLabel = setImageLabel(image);
        JLabel textLabel = setTextLabel(text);
        setColors(backgroundColor, foregroundColor, panel, imageLabel, textLabel);
        setPanel(image, imageMaxWidth, panel, c, imageLabel, textLabel);
        
        if (hasFocus && actions != null && !actions.isEmpty()) {
        	setActions(actions, table, panel, c);
        }

        GuiUtils.applyComponentOrientation(panel);
        return panel;
    }

	/**
	 * @param actions
	 * @param table
	 * @param panel
	 * @param c
	 */
	private void setActions(List<ContextTableAction<T>> actions, final ContextTable table, final JPanel panel, GridBagConstraints c) {
		final PopUpButton button = new PopUpButton(GuiUtils.getComponentOrientationAsSwingConstant() == SwingConstants.LEFT ? PopUpButton.TOP_LEFT : PopUpButton.TOP_RIGHT, I18nUtils.getString("OPTIONS"));
		for (AbstractAction action : actions) {
			button.add(action);
		}
		c.gridx = 2;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(0, 0, 0, 10);
		panel.add(button, c);
      
		panel.addMouseListener(new ContextTableRowPanelMouseAdapter(panel, table, button));
		button.addPopupMenuListener(new ContextTableRowPopupMenuListener<T>(actions, table));
	}

	/**
	 * @param image
	 * @param imageMaxWidth
	 * @param panel
	 * @param c
	 * @param imageLabel
	 * @param textLabel
	 */
	private void setPanel(ImageIcon image, int imageMaxWidth, final JPanel panel, GridBagConstraints c, JLabel imageLabel, JLabel textLabel) {
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
	}

	/**
	 * @param backgroundColor
	 * @param foregroundColor
	 * @param panel
	 * @param imageLabel
	 * @param textLabel
	 */
	private void setColors(Color backgroundColor, Color foregroundColor,
			final JPanel panel, JLabel imageLabel, JLabel textLabel) {
		if (backgroundColor != null) {
            textLabel.setBackground(backgroundColor);
            panel.setBackground(backgroundColor);
            imageLabel.setBackground(backgroundColor);
        }
        if (foregroundColor != null) {
        	textLabel.setForeground(foregroundColor);
        }
	}

	/**
	 * @param text
	 * @return
	 */
	private JLabel setTextLabel(String text) {
		JLabel textLabel = new JLabel(text);
        textLabel.setOpaque(false);
        textLabel.setVerticalAlignment(SwingConstants.TOP);
		return textLabel;
	}

	/**
	 * @param image
	 * @return
	 */
	private JLabel setImageLabel(ImageIcon image) {
		JLabel imageLabel = new JLabel(image);
        imageLabel.setOpaque(false);
        imageLabel.setBorder(image != null ? Context.getBean(DropShadowBorder.class) : null);
		return imageLabel;
	}

}
