/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.sanselan.ImageWriteException;
import org.jdesktop.swingx.util.GraphicsUtilities;

/**
 * The Class ImageUtils.
 */

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
	public static ImageIcon resize(final ImageIcon image, final int width,
			final int height) {
		if (image == null) {
			throw new IllegalArgumentException("null image");
		}
		if (width == -1 || height == -1) {
			return image;
		}
		if (width == image.getIconWidth() && height == image.getIconHeight()) {
			return image;
		}

		int maxSize = (image.getIconWidth() > image.getIconHeight()) ? image
				.getIconWidth() : image.getIconHeight();
		int newWidth = (int) ((float) image.getIconWidth() / (float) maxSize * width);
		int newHeight = (int) ((float) image.getIconHeight() / (float) maxSize * height);

		return scaleImageBicubic(image.getImage(), newWidth, newHeight);
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
	public static ImageIcon scaleImageBicubic(final Image image,
			final int width, final int height) {
		int imageWidth = image.getWidth(null);
		int imageHeight = image.getHeight(null);

		if (imageWidth == width && imageHeight == height) {
			return new ImageIcon(image);
		}

		BufferedImage bi = scaleBufferedImageBicubic(image, width, height);
		return bi != null ? new ImageIcon(bi) : null;
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
	public static BufferedImage scaleBufferedImageBicubic(final Image image,
			final int width, final int height) {
		if (image == null) {
			return null;
		}

		int imageWidth = image.getWidth(null);
		int imageHeight = image.getHeight(null);

		if (imageWidth == width && imageHeight == height) {
			return toBufferedImage(image);
		}

		double thumbRatio = (double) width / (double) height;
		double imageRatio = (double) imageWidth / (double) imageHeight;
		int calculatedWidth = width;
		int calculatedHeight = height;
		if (thumbRatio < imageRatio) {
			calculatedHeight = (int) (width / imageRatio);
		} else {
			calculatedWidth = (int) (height * imageRatio);
		}

		if (imageWidth <= calculatedWidth || imageHeight <= calculatedHeight) {
			BufferedImage thumbImage = new BufferedImage(calculatedWidth,
					calculatedHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics2D = thumbImage.createGraphics();
			graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			graphics2D.drawImage(image, 0, 0, calculatedWidth,
					calculatedHeight, null);
			graphics2D.dispose();
			return thumbImage;
		} else {
			// If scaled image is smaller then use SwingX utilities (looks much
			// better)
			return GraphicsUtilities.createThumbnailFast(
					toBufferedImage(image), calculatedWidth, calculatedHeight);
		}
	}

	/**
	 * Scales an image with bilinear algorithm.
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
	public static BufferedImage scaleBufferedImageBilinear(final Image image,
			final int width, final int height) {
		if (image == null) {
			return null;
		}

		int imageWidth = image.getWidth(null);
		int imageHeight = image.getHeight(null);

		if (imageWidth == width && imageHeight == height) {
			return toBufferedImage(image);
		}

		double thumbRatio = (double) width / (double) height;
		double imageRatio = (double) imageWidth / (double) imageHeight;
		int calculatedWidth = width;
		int calculatedHeight = height;
		if (thumbRatio < imageRatio) {
			calculatedHeight = (int) (width / imageRatio);
		} else {
			calculatedWidth = (int) (height * imageRatio);
		}

		BufferedImage thumbImage = new BufferedImage(calculatedWidth,
				calculatedHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2D = thumbImage.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2D.drawImage(image, 0, 0, calculatedWidth, calculatedHeight,
				null);
		graphics2D.dispose();
		return thumbImage;
	}

	/**
	 * Gets a BufferedImage from an Image object.
	 * 
	 * @param img
	 * @return
	 */
	public static BufferedImage toBufferedImage(final Image img) {
		if (img == null) {
			return null;
		}
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}
		return GraphicsUtilities.convertToBufferedImage(img);
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
	 * @throws ImageWriteException
	 */
	public static void writeImageToFile(final Image image, final String fileName)
			throws IOException, ImageWriteException {
		if (image == null) {
			return;
		}
		String fileNameWithExtension = fileName;
		if (!fileName.toUpperCase().endsWith(
				StringUtils.getString(".", FILES_EXTENSION).toUpperCase())) {
			fileNameWithExtension = StringUtils.getString(fileName, ".",
					FILES_EXTENSION);
		}

		BufferedImage bi = toBufferedImage(image);
		if (bi != null) {
			ImageIO.write(bi, FILES_EXTENSION, new File(fileNameWithExtension));
		}
	}
}
