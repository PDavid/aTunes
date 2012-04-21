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

package net.sourceforge.atunes.kernel.modules.fullscreen;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.Logger;

/**
 * A cover.
 */
public final class Cover extends JPanel {

    private static final long serialVersionUID = -3836270786764203330L;

    private static final int GAP = 10;
    private static final float OPACITY = 0.3f;
    private static final float FADE_HEIGHT = 0f;

    private BufferedImage image;
    private BufferedImage reflectedImage;
    
    private String previousArtist;
    private String previousAlbum;

    /**
     * Instantiates a cover.
     */
    public Cover() {
        super(false);
        setOpaque(false);
    }

    @Override
    public void paintComponent(Graphics g) {
    	if (image == null || !(g instanceof Graphics2D)) {
            super.paintComponent(g);
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate((getWidth() - image.getWidth()) / 2, getHeight() / 2 - image.getHeight() * 2 / 3 - 100 );
        g2d.drawRenderedImage(image, null);
        g2d.translate(0, 2 * image.getHeight() + GAP);
        g2d.scale(1, -1);
        g2d.drawRenderedImage(reflectedImage, null);
        g2d.dispose();
    }

    /**
     * Sets the image.
     * 
     * @param image
     *            the image to set
     */
    void setImage(Image image, int width, int height) {
        if (image != null) {
            // IMAGE
            this.image = ImageUtils.scaleBufferedImageBicubic(image, width, height);

            // REFLECTED IMAGE
            BufferedImage reflection = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D rg = reflection.createGraphics();
            rg.drawRenderedImage(this.image, null);
            rg.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_IN));
            rg.setPaint(new GradientPaint(0, height * FADE_HEIGHT, new Color(0.0f, 0.0f, 0.0f, 0.0f), 0, height, new Color(0.0f, 0.0f, 0.0f, OPACITY)));
            rg.fillRect(0, 0, width, height);
            rg.dispose();

            reflectedImage = reflection;
        } else {
            this.image = null;
            this.reflectedImage = null;
        }
    }
    
    void paint(final IAudioObject audioObject, int index, IOSManager osManager, int coverSize) {
        // No object
        if (audioObject == null) {
            return;
        }
        
        if (previousArtist == null || previousAlbum == null || !previousArtist.equals(audioObject.getArtist()) || !previousAlbum.equals(audioObject.getAlbum())) {
            // Fetch cover
            new PaintCoversSwingWorker(this, audioObject, index, osManager, coverSize).execute();
            
            previousArtist = audioObject.getArtist();
            previousAlbum = audioObject.getAlbum();
        } else {
        	Logger.debug("Not updating cover: ", audioObject.getArtist(), " ", audioObject.getAlbum());
        }
    }

}
