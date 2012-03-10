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

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomWindow;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.utils.ImageUtils;

/**
 * The Class OSDDialog.
 */
public final class OSDDialog extends AbstractCustomWindow {

    private static final long serialVersionUID = 8991547440913162267L;
    private static final int IMAGE_SIZE = 80;

    private int width;
    private int height = 100;
    private JLabel line1;
    private JLabel line2;
    private JLabel line3;
    private JLabel image;

    /**
     * Vertical position of line 1
     */
    private static final int LINE1_Y_POSITION = 15;

    /**
     * Vertical position of line 2
     */
    private static final int LINE2_Y_POSITION = 37;

    /**
     * Vertical position of line 3
     */
    private static final int LINE3_Y_POSITION = 60;

    private ILookAndFeel lookAndFeel;
    
    /**
     * Instantiates a new osd dialog.
     * @param width
     * @param lookAndFeel
     */
    public OSDDialog(int width, ILookAndFeel lookAndFeel) {
        super(null, 0, 0);
        this.width = width;
        this.lookAndFeel = lookAndFeel;
        setSize(width, height);
        setFocusableWindowState(false);
        setAlwaysOnTop(true);
        add(getContent());
    }

    public void setWidth(int width) {
        this.width = width;
        setSize(width, height);
    }

    /**
     * Gets the content.
     * 
     * @return the content
     */
    private JPanel getContent() {
        JPanel panel = new JPanel(null);
        panel.setSize(width, height);
        image = new JLabel();
        image.setOpaque(true);
        line1 = new JLabel();
        line2 = new JLabel();
        line3 = new JLabel();

        line1.setFont(lookAndFeel.getOsdLine1Font());
        line2.setFont(lookAndFeel.getOsdLine2Font());
        line3.setFont(lookAndFeel.getOsdLine3Font());

        line1.setHorizontalAlignment(SwingConstants.CENTER);
        line2.setHorizontalAlignment(SwingConstants.CENTER);
        line3.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(image);
        panel.add(line1);
        panel.add(line2);
        panel.add(line3);
        return panel;
    }

    /**
     * Sets the image.
     * 
     * @param img
     *            the new image
     */
    public void setImage(ImageIcon img) {
        ImageIcon imgResized;
        if (img != null && (imgResized = ImageUtils.resize(img, IMAGE_SIZE, IMAGE_SIZE)) != null) {
            image.setIcon(imgResized);
            image.setSize(imgResized.getIconWidth() + 5, imgResized.getIconHeight() + 5);
            image.setLocation(10, (height - IMAGE_SIZE) / 2);
            line1.setSize(width - 100, 20);
            line1.setLocation(90, LINE1_Y_POSITION);
            line2.setSize(width - 100, 20);
            line2.setLocation(90, LINE2_Y_POSITION);
            line3.setSize(width - 100, 20);
            line3.setLocation(90, LINE3_Y_POSITION);
        } else {
            image.setSize(0, 0);
            line1.setSize(width - 20, 20);
            line1.setLocation(10, LINE1_Y_POSITION);
            line2.setSize(width - 20, 20);
            line2.setLocation(10, LINE2_Y_POSITION);
            line3.setSize(width - 20, 20);
            line3.setLocation(10, LINE3_Y_POSITION);
        }
    }

    /**
     * Sets the line1.
     * 
     * @param text
     *            the new line1
     */
    public void setLine1(String text) {
        line1.setText(text);
    }

    /**
     * Sets the line2.
     * 
     * @param text
     *            the new line2
     */
    public void setLine2(String text) {
        line2.setText(text);
    }

    /**
     * Sets the line3.
     * 
     * @param text
     *            the new line3
     */
    public void setLine3(String text) {
        line3.setText(text);
    }

    /**
     * Sets rounded borders.
     * 
     * @param set
     *            the set
     */
    public void setRoundedBorders(boolean set) {
        Shape mask = null;
        if (set) {
            mask = new Area(new RoundRectangle2D.Float(2, 2, width - 3, height - 3, 20, 25));
        }
        GuiUtils.setWindowShape(this, mask);
    }
}
