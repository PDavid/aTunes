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

package net.sourceforge.atunes.gui.images;



import net.sourceforge.atunes.model.GenericImageSize;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectGenericImageFactory;
import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IRadio;

/**
 * Returns generic image for audio objects
 * @author alex
 *
 */
public class AudioObjectGenericImage implements IAudioObjectGenericImageFactory {

	private IIconFactory audioFileSmallIcon;
	private IIconFactory audioFileMediumIcon;

	private IIconFactory rssSmallIcon;
	private IIconFactory rssMediumIcon;
	private IIconFactory rssBigIcon;
	
	private IIconFactory radioSmallIcon;
	private IIconFactory radioMediumIcon;
	private IIconFactory radioBigIcon;
	
    /**
     * Returns a generic image for given audio object.
     * @param ao
     * @param imageSize the size of the generic image
     * @param lookAndFeel
     * @return the generic image or <code>null</code> if no such image is
     *         available
     */
    @Override
	public IColorMutableImageIcon getGenericImage(IAudioObject ao, GenericImageSize imageSize) {
    	if (ao instanceof IRadio) {
    		return getRadioIcon(imageSize);
    	} else if (ao instanceof IPodcastFeedEntry) {
            return getPodcastFeedEntryIcon(imageSize);
    	} else if (ao instanceof ILocalAudioObject) {
    		 return getLocalAudioObjectIcon(imageSize);
    	}
    	return null;
    }

	/**
	 * @param imageSize
	 * @return color mutable icon for local audio object
	 */
	private IColorMutableImageIcon getLocalAudioObjectIcon(GenericImageSize imageSize) {
		switch (imageSize) {
		case SMALL:  return audioFileSmallIcon.getColorMutableIcon();
		default: return audioFileMediumIcon.getColorMutableIcon();
		}
	}

	/**
	 * @param imageSize
	 * @return color mutable icon for podcast
	 */
	private IColorMutableImageIcon getPodcastFeedEntryIcon(GenericImageSize imageSize) {
		switch (imageSize) {
		case SMALL:  return rssSmallIcon.getColorMutableIcon();
		case MEDIUM: return rssMediumIcon.getColorMutableIcon();
		case BIG:    return rssBigIcon.getColorMutableIcon();
		}
		return null;
	}

	/**
	 * @param imageSize
	 * @return color mutable icon for radio
	 */
	private IColorMutableImageIcon getRadioIcon(GenericImageSize imageSize) {
		switch (imageSize) {
		case SMALL:  return radioSmallIcon.getColorMutableIcon();
		case MEDIUM: return radioMediumIcon.getColorMutableIcon();
		case BIG:    return radioBigIcon.getColorMutableIcon();
		}
		return null;
	}
	
	/**
	 * @param audioFileMediumIcon
	 */
	public void setAudioFileMediumIcon(IIconFactory audioFileMediumIcon) {
		this.audioFileMediumIcon = audioFileMediumIcon;
	}
	
	/**
	 * @param audioFileSmallIcon
	 */
	public void setAudioFileSmallIcon(IIconFactory audioFileSmallIcon) {
		this.audioFileSmallIcon = audioFileSmallIcon;
	}

	/**
	 * @param rssSmallIcon
	 */
	public void setRssSmallIcon(IIconFactory rssSmallIcon) {
		this.rssSmallIcon = rssSmallIcon;
	}
	
	/**
	 * @param rssMediumIcon
	 */
	public void setRssMediumIcon(IIconFactory rssMediumIcon) {
		this.rssMediumIcon = rssMediumIcon;
	}
	
	/**
	 * @param rssBigIcon
	 */
	public void setRssBigIcon(IIconFactory rssBigIcon) {
		this.rssBigIcon = rssBigIcon;
	}
	
	/**
	 * @param radioBigIcon
	 */
	public void setRadioBigIcon(IIconFactory radioBigIcon) {
		this.radioBigIcon = radioBigIcon;
	}
	
	/**
	 * @param radioMediumIcon
	 */
	public void setRadioMediumIcon(IIconFactory radioMediumIcon) {
		this.radioMediumIcon = radioMediumIcon;
	}
	
	/**
	 * @param radioSmallIcon
	 */
	public void setRadioSmallIcon(IIconFactory radioSmallIcon) {
		this.radioSmallIcon = radioSmallIcon;
	}
}
