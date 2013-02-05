package net.sourceforge.atunes.model;

import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;

import org.apache.sanselan.ImageWriteException;

/**
 * Handles images of local audio objects
 * 
 * @author alex
 * 
 */
public interface ILocalAudioObjectImageHandler {

	/**
	 * Gets the external picture.
	 * 
	 * @param audioObject
	 * @param width
	 * @param height
	 * @return
	 */
	ImageIcon getExternalPicture(final IAudioObject audioObject,
			final int width, final int height);

	/**
	 * Returns a file name to save an external image associated to an audio
	 * file.
	 * 
	 * @param file
	 * @return
	 */
	String getFileNameForCover(final ILocalAudioObject file);

	/**
	 * Returns image stored into audio file, if exists.
	 * 
	 * @param audioObject
	 *            the audioObject
	 * @param width
	 *            Width in pixels or -1 to keep original width
	 * @param height
	 *            Height in pixels or -1 to keep original height
	 * 
	 * @return the inside picture
	 */
	ImageIcon getInsidePicture(final IAudioObject audioObject, final int width,
			final int height);

	/**
	 * Saves and internal image of an audio file to a file.
	 * 
	 * @param song
	 * @param file
	 * @throws IOException
	 * @throws ImageWriteException
	 */
	void savePictureToFile(final ILocalAudioObject song, final File file)
			throws IOException, ImageWriteException;
}
