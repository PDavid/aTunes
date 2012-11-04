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

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.controls.PopUpButton;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * A panel to render each row of a context table
 * 
 * @author alex
 * 
 * @param <T>
 */
public class ContextTableRowPanel<T> extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 3227801852177772588L;

    private JLabel imageLabel;

    private JLabel textLabel;

    private PopUpButton button;

    /**
     * Default constructor
     */
    public ContextTableRowPanel() {
	setLayout(new GridBagLayout());
	setOpaque(false);
    }

    /**
     * Updates colors
     * 
     * @param backgroundColor
     * @param foregroundColor
     */
    public void setColors(final Color backgroundColor,
	    final Color foregroundColor) {
	if (backgroundColor != null) {
	    textLabel.setBackground(backgroundColor);
	    setBackground(backgroundColor);
	    imageLabel.setBackground(backgroundColor);
	}
	if (foregroundColor != null) {
	    textLabel.setForeground(foregroundColor);
	}
    }

    /**
     * Sets image
     * 
     * @param image
     */
    public void setImage(final ImageIcon image) {
	imageLabel = new JLabel(image);
	imageLabel.setOpaque(false);
    }

    /**
     * Sets text
     * 
     * @param text
     */
    public void setText(final String text) {
	textLabel = new JLabel(text);
	textLabel.setOpaque(false);
	textLabel.setVerticalAlignment(SwingConstants.TOP);
    }

    /**
     * @param imageMaxWidth
     * @param actions
     * @param table
     */
    public void build(final int imageMaxWidth,
	    final List<ContextTableAction<T>> actions, final ContextTable table) {
	GridBagConstraints c = new GridBagConstraints();
	c.gridx = 0;
	c.gridy = 0;
	c.anchor = GridBagConstraints.WEST;
	c.insets = new Insets(2, (imageMaxWidth + 20)
		/ 2
		- (imageLabel.getIcon() != null ? imageLabel.getIcon()
			.getIconWidth() : 0) / 2, 0, 0);
	add(imageLabel, c);
	c.gridx = 1;
	c.weightx = 1;
	c.weighty = 1;
	c.fill = GridBagConstraints.BOTH;
	c.insets = new Insets(0, (imageMaxWidth + 20)
		/ 2
		- (imageLabel.getIcon() != null ? imageLabel.getIcon()
			.getIconWidth() : 0) / 2, 0, 0);
	add(textLabel, c);

	GuiUtils.applyComponentOrientation(this);

	if (!net.sourceforge.atunes.utils.CollectionUtils.isEmpty(actions)) {
	    button = new PopUpButton(
		    GuiUtils.getComponentOrientationAsSwingConstant() == SwingConstants.LEFT ? PopUpButton.TOP_LEFT
			    : PopUpButton.TOP_RIGHT, I18nUtils
			    .getString("OPTIONS"));
	    for (AbstractAction action : actions) {
		button.add(action);
	    }
	    c.gridx = 2;
	    c.fill = GridBagConstraints.NONE;
	    c.anchor = GridBagConstraints.EAST;
	    c.insets = new Insets(0, 0, 0, 10);
	    add(button, c);

	    addMouseListener(new ContextTableRowPanelMouseAdapter(this, table,
		    button));
	    button.addPopupMenuListener(new ContextTableRowPopupMenuListener<T>(
		    actions, table));
	}
    }

    /**
     * Shows or hides options if panel has focus
     * 
     * @param hasFocus
     */
    public void setFocus(final boolean hasFocus) {
	if (button != null) {
	    button.setVisible(hasFocus);
	}
    }
}
