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
package net.sourceforge.atunes.utils;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.datatype.Artwork;

/**
 * This class gets images associated to audio files Images can be internal (like
 * in ID3v2) or external (in the same folder than audio file).
 * 
 * <b>Consider using icon methods from AudioObject<b/>
 * 
 * @author fleax
 */
public final class AudioFilePictureUtils {

    private AudioFilePictureUtils() {
    }

    /**
     * Export picture.
     * 
     * @param song
     *            the song
     */
    public static void exportPicture(AudioFile song, Component parent) {
        try {
            JFileChooser fileChooser = new JFileChooser();
            FileFilter filter = new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().toUpperCase().endsWith("PNG");
                }

                @Override
                public String getDescription() {
                    return "PNG files";
                }
            };
            fileChooser.setFileFilter(filter);
            if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().toUpperCase().endsWith("PNG")) {
                    file = new File(StringUtils.getString(file.getAbsolutePath(), ".png"));
                }
                if (!file.exists() || (file.exists() && JOptionPane.showConfirmDialog(null, I18nUtils.getString("OVERWRITE_FILE")) == JOptionPane.OK_OPTION)) {
                    savePictureToFile(song, file);
                }
            }
        } catch (Exception e) {
            new Logger().error(LogCategories.IMAGE, e);
        }
    }

    /**
     * Gets the external picture.
     * 
     * @param file
     *            the file
     * @param width
     *            the width
     * @param height
     *            the height
     * 
     * @return the external picture
     */
    public static ImageIcon getExternalPicture(AudioFile file, int width, int height) {
        return getExternalPicture(file, 0, width, height);
    }

    /**
     * Gets the external picture.
     * 
     * @param file
     *            the file
     * @param index
     *            the index
     * @param width
     *            the width
     * @param height
     *            the height
     * 
     * @return the external picture
     */
    private static ImageIcon getExternalPicture(AudioFile file, int index, int width, int height) {
    	// Try first to get picture with file name "ARTIST_ALBUM_COVER" pattern
    	String coverFileName = getFileNameForCover(file); 
    	ImageIcon image = null;
    	if (new File(coverFileName).exists()) {
    		image = new ImageIcon(coverFileName);
    	} else if (file != null && file.getExternalPictures() != null && file.getExternalPictures().size() > index) {
            File firstPicture = file.getExternalPictures().get(index);
            image = new ImageIcon(firstPicture.getAbsolutePath());
    	}
    	if (image != null) {
            if (width == -1 || height == -1) {
                return image;
            }
            int maxSize = (image.getIconWidth() > image.getIconHeight()) ? image.getIconWidth() : image.getIconHeight();
            int newWidth = (int) ((float) image.getIconWidth() / (float) maxSize * width);
            int newHeight = (int) ((float) image.getIconHeight() / (float) maxSize * height);
            return ImageUtils.scaleImageBicubic(image.getImage(), newWidth, newHeight);
        }
        return image;
    }

    /**
     * Returns a file name to save an external image associated to an audio
     * file.
     * 
     * @param file
     *            the file
     * 
     * @return the file name for cover
     */

    public static String getFileNameForCover(AudioFile file) {
        if (file == null) {
            return null;
        }
        return StringUtils.getString(file.getFile().getParentFile().getAbsolutePath(), SystemProperties.FILE_SEPARATOR, file.getArtist(), '_', file.getAlbum(), "_Cover.",
                ImageUtils.FILES_EXTENSION);
    }

    /**
     * Returns image stored into audio file, if exists.
     * 
     * @param file
     *            the file
     * @param width
     *            Width in pixels or -1 to keep original width
     * @param height
     *            Height in pixels or -1 to keep original height
     * 
     * @return the inside picture
     */
    public static ImageIcon getInsidePicture(AudioFile file, int width, int height) {
        try {
            org.jaudiotagger.tag.Tag tag = AudioFileIO.read(file.getFile()).getTag();
            if (tag == null) {
                return null;
            }
            Artwork artwork = tag.getFirstArtwork();
            byte[] imageRawData = artwork != null ? artwork.getBinaryData() : null;

            if (imageRawData != null) {
                BufferedImage bi = ImageIO.read(new ByteArrayInputStream(imageRawData));
                if (bi != null) {
                    ImageIcon imageIcon = new ImageIcon(bi);
                    if (width != -1 || height != -1) {
                        int maxSize = (imageIcon.getIconWidth() > imageIcon.getIconHeight()) ? imageIcon.getIconWidth() : imageIcon.getIconHeight();
                        int newWidth = (int) ((float) imageIcon.getIconWidth() / (float) maxSize * width);
                        int newHeight = (int) ((float) imageIcon.getIconHeight() / (float) maxSize * height);

                        BufferedImage resizedImage = ImageUtils.toBufferedImage(ImageUtils.scaleImageBicubic(imageIcon.getImage(), newWidth, newHeight).getImage());
                        if (resizedImage != null) {
                            return new ImageIcon(resizedImage);
                        }
                    } else {
                        return new ImageIcon(bi);
                    }
                }
            }
            return null;
        } catch (FileNotFoundException e) {
            new Logger().error(LogCategories.IMAGE, StringUtils.getString("File not found: ", file.getFile().getAbsolutePath()));
            return null;        	
        } catch (Exception e) {
            new Logger().error(LogCategories.IMAGE, e);
            return null;
        }
    }

    /**
     * Returns all pictures associated to an audio file.
     * 
     * @param file
     *            the file
     * @param width
     *            Width in pixels or -1 to keep original width
     * @param height
     *            Height in pixels or -1 to keep original height
     * 
     * @return the pictures for file
     */
    public static ImageIcon[] getPicturesForFile(AudioFile file, int width, int height) {
        int size = 0;
        ImageIcon image = getInsidePicture(file, width, height);
        if (image != null) {
            size++;
        }
        size = size + file.getExternalPicturesCount();

        ImageIcon[] result = new ImageIcon[size];
        int firstExternalIndex = 0;
        if (image != null) {
            result[0] = image;
            firstExternalIndex++;
        }
        for (int i = firstExternalIndex; i < size; i++) {
            result[i] = getExternalPicture(file, i, width, height);
        }

        return result;
    }

    /**
     * Returns true if file represents a valid picture (jpg or png).
     * 
     * @param file
     *            the file
     * 
     * @return true, if checks if is valid picture
     */
    public static boolean isValidPicture(File file) {
        return file.getName().toUpperCase().endsWith("JPG") || file.getName().toUpperCase().endsWith("JPEG") || file.getName().toUpperCase().endsWith("PNG");
    }

    /**
     * Saves and internal image of an audio file to a file.
     * 
     * @param song
     *            the song
     * @param file
     *            the file
     * 
     * @throws FileNotFoundException
     *             the file not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private static void savePictureToFile(AudioFile song, File file) throws FileNotFoundException, IOException {
        ImageIcon image = getInsidePicture(song, -1, -1);
        ImageUtils.writeImageToFile(image.getImage(), file.getAbsolutePath());
    }
    
}
