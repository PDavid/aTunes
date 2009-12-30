/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.gui.views.controls;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import net.sourceforge.atunes.utils.ImageUtils;

import com.jhlabs.image.PerspectiveFilter;

/**
 * A three dimensional cover.
 */
public final class Cover3D extends JPanel {

    private static final long serialVersionUID = -3836270786764203330L;

    private static final int GAP = 10;
    private static final float OPACITY = 0.5f;
    private static final float FADE_HEIGHT = 0.4f;

    private int angle;
    private BufferedImage image;
    private BufferedImage reflectedImage;

    /**
     * Instantiates a new three dimensional cover.
     */
    public Cover3D(int angle) {
        super();
        this.angle = angle;
        setOpaque(false);
    }

    @Override
    public void paintComponent(Graphics g) {
        if (image == null) {
            super.paintComponent(g);
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        g2d.translate((width - imageWidth) / 2, height / 2 - imageHeight / 2);
        g2d.drawRenderedImage(image, null);

        g2d.translate(0, 2 * imageHeight + GAP);
        g2d.scale(1, -1);
        g2d.drawRenderedImage(reflectedImage, null);
    }

    /**
     * Sets the image.
     * 
     * @param image
     *            the image to set
     */
    public void setImage(ImageIcon image) {
        if (image != null) {
            // IMAGE
            this.image = ImageUtils.toBufferedImage(image.getImage());
            int width = image.getIconWidth();
            int height = image.getIconHeight();
            if (angle != 0) {
                PerspectiveFilter filter1 = new PerspectiveFilter(0, angle, width - angle / 2, (int) (angle * (5.0 / 3.0)), width - angle / 2, height, 0, height + angle);
                this.image = filter1.filter(this.image, null);
            }

            // REFLECTED IMAGE
            int imageWidth = this.image.getWidth();
            int imageHeight = this.image.getHeight();
            BufferedImage reflection = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D rg = reflection.createGraphics();
            rg.drawRenderedImage(this.image, null);
            rg.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_IN));
            rg.setPaint(new GradientPaint(0, imageHeight * FADE_HEIGHT, new Color(0.0f, 0.0f, 0.0f, 0.0f), 0, imageHeight, new Color(0.0f, 0.0f, 0.0f, OPACITY)));
            rg.fillRect(0, 0, imageWidth, imageHeight);
            rg.dispose();

            if (angle != 0) {
                PerspectiveFilter filter2 = new PerspectiveFilter(0, 0, width - angle / 2, angle * 2, width - angle / 2, height + angle * 2, 0, height);
                reflectedImage = filter2.filter(reflection, null);
            } else {
                reflectedImage = reflection;
            }
        } else {
            this.image = null;
            this.reflectedImage = null;
        }
        this.invalidate();
        this.repaint();
    }
}
