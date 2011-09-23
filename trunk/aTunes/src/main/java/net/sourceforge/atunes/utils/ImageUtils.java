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

package net.sourceforge.atunes.utils;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


import org.commonjukebox.plugins.model.PluginApi;
import org.jdesktop.swingx.graphics.GraphicsUtilities;

/**
 * The Class ImageUtils.
 */
@PluginApi
public final class ImageUtils {

    /** The Constant FILES_EXTENSION. */
    public static final String FILES_EXTENSION = "png";

    private ImageUtils() {
    }

    /**
     * Resizes an ImageIcon object. If any size is -1 returns image with no
     * modification
     * 
     * @param image
     *            the image
     * @param width
     *            the width
     * @param height
     *            the height
     * 
     * @return the image icon
     */
    public static ImageIcon resize(ImageIcon image, int width, int height) {
        if (width == -1 || height == -1) {
            return image;
        }
        if (width == image.getIconWidth() && height == image.getIconHeight()) {
            return image;
        }

        int maxSize = (image.getIconWidth() > image.getIconHeight()) ? image.getIconWidth() : image.getIconHeight();
        int newWidth = (int) ((float) image.getIconWidth() / (float) maxSize * width);
        int newHeight = (int) ((float) image.getIconHeight() / (float) maxSize * height);
        return ImageUtils.scaleImageBicubic(image.getImage(), newWidth, newHeight);
    }

    /**
     * Scales an image with Bicubic algorithm.
     * 
     * @param image
     *            the image
     * @param width
     *            the width
     * @param height
     *            the height
     * 
     * @return the image icon
     */
    public static ImageIcon scaleImageBicubic(Image image, int width, int height) {
        if (image == null) {
            return null;
        }

        double thumbRatio = (double) width / (double) height;
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);
        double imageRatio = (double) imageWidth / (double) imageHeight;
        int calculatedWidth = width;
        int calculatedHeight = height;
        if (thumbRatio < imageRatio) {
            calculatedHeight = (int) (width / imageRatio);
        } else {
            calculatedWidth = (int) (height * imageRatio);
        }

        if (imageWidth <= calculatedWidth && imageHeight <= calculatedHeight) {
            BufferedImage thumbImage = new BufferedImage(calculatedWidth, calculatedHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics2D = thumbImage.createGraphics();
            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics2D.drawImage(image, 0, 0, calculatedWidth, calculatedHeight, null);
            graphics2D.dispose();
            return new ImageIcon(thumbImage);
        } else {
            // If scaled image is smaller then use SwingX utilities (looks much better)
            BufferedImage bi = GraphicsUtilities.createThumbnail(toBufferedImage(image), calculatedWidth, calculatedHeight);
            return new ImageIcon(bi);
        }
    }
    
    /**
     * Scales an image with Bicubic algorithm.
     * 
     * @param image
     *            the image
     * @param width
     *            the width
     * @param height
     *            the height
     * 
     * @return the image icon
     */
    public static BufferedImage scaleBufferedImageBicubic(Image image, int width, int height) {
        if (image == null) {
            return null;
        }

        double thumbRatio = (double) width / (double) height;
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);
        double imageRatio = (double) imageWidth / (double) imageHeight;
        int calculatedWidth = width;
        int calculatedHeight = height;
        if (thumbRatio < imageRatio) {
            calculatedHeight = (int) (width / imageRatio);
        } else {
            calculatedWidth = (int) (height * imageRatio);
        }

        if (imageWidth <= calculatedWidth && imageHeight <= calculatedHeight) {
            BufferedImage thumbImage = new BufferedImage(calculatedWidth, calculatedHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics2D = thumbImage.createGraphics();
            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics2D.drawImage(image, 0, 0, calculatedWidth, calculatedHeight, null);
            graphics2D.dispose();
            return thumbImage;
        } else {
            // If scaled image is smaller then use SwingX utilities (looks much better)
            return GraphicsUtilities.createThumbnail(toBufferedImage(image), calculatedWidth, calculatedHeight);
        }
    }


    /**
     * Scales an image with Bilinear algorithm (faster than bicubic)
     * 
     * @param image
     *            the image
     * @param width
     *            the width
     * @param height
     *            the height
     * 
     * @return the image icon
     */
    public static ImageIcon scaleImageBilinear(Image image, int width, int height) {
        if (image == null) {
            return null;
        }

        double thumbRatio = (double) width / (double) height;
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);
        double imageRatio = (double) imageWidth / (double) imageHeight;
        int calculatedWidth = width;
        int calculatedHeight = height;
        if (thumbRatio < imageRatio) {
            calculatedHeight = (int) (width / imageRatio);
        } else {
            calculatedWidth = (int) (height * imageRatio);
        }

        BufferedImage thumbImage = new BufferedImage(calculatedWidth, calculatedHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = thumbImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(image, 0, 0, calculatedWidth, calculatedHeight, null);
        graphics2D.dispose();
        return new ImageIcon(thumbImage);
    }

    /**
     * Gets a BufferedImage from an Image object.
     * 
     * @param image
     *            the image
     * 
     * @return the buffered image
     */
    public static BufferedImage toBufferedImage(Image img) {
        BufferedImage bufferedImage;
        try {
            Image image = img;
            bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = bufferedImage.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.drawImage(image, 0, 0, null);
            g.dispose();
        } catch (IllegalArgumentException e) {
            Logger.info("Maybe picture file with wrong ending?");
            Logger.error(e);
            return null;
        }

        return bufferedImage;
    }

    /**
     * Writes an image into a file in png format.
     * 
     * @param image
     *            The image that should be written to a file
     * @param fileName
     *            The name of the file
     * 
     * @throws IOException
     *             If an IO exception occurs
     */
    public static void writeImageToFile(Image image, String fileName) throws IOException {
        if (image == null) {
            return;
        }

        ImageIcon img = new ImageIcon(image);

        BufferedImage buf = new BufferedImage(img.getIconWidth(), img.getIconHeight(), BufferedImage.TYPE_INT_BGR);
        Graphics g = buf.createGraphics();
        g.drawImage(image, 0, 0, null);
        String fileNameWithExtension = fileName;
        if (!fileName.toUpperCase().endsWith(StringUtils.getString(".", ImageUtils.FILES_EXTENSION).toUpperCase())) {
            fileNameWithExtension = StringUtils.getString(fileName, ".", ImageUtils.FILES_EXTENSION);
        }

        ImageIO.write(buf, ImageUtils.FILES_EXTENSION, new FileOutputStream(fileNameWithExtension));
    }

}
