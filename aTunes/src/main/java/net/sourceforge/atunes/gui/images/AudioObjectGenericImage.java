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

package net.sourceforge.atunes.gui.images;



import net.sourceforge.atunes.model.GenericImageSize;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectGenericImageFactory;
import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IRadio;

/**
 * Returns generic image for audio objects
 * @author alex
 *
 */
public class AudioObjectGenericImage implements IAudioObjectGenericImageFactory {

	private LocalAudioObjectSmallColorMutableImageIcon localAudioObjectSmallIcon;
	private LocalAudioObjectMediumColorMutableImageIcon localAudioObjectMediumIcon;
	private LocalAudioObjectBigColorMutableImageIcon localAudioObjectBigIcon;
	
	private PodcastFeedEntrySmallColorMutableImageIcon podcastFeedEntrySmallIcon;
	private PodcastFeedEntryMediumColorMutableImageIcon podcastFeedEntryMediumIcon;
	private PodcastFeedEntryBigColorMutableImageIcon podcastFeedEntryBigIcon;
	
	private RadioSmallColorMutableImageIcon radioSmallIcon;
	private RadioMediumColorMutableImageIcon radioMediumIcon;
	private RadioBigColorMutableImageIcon radioBigIcon;
	
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
		case SMALL:  return localAudioObjectSmallIcon;         
		case MEDIUM: return localAudioObjectMediumIcon;     
		case BIG:    return localAudioObjectBigIcon;     
		}
		return null;
	}

	/**
	 * @param imageSize
	 * @return color mutable icon for podcast
	 */
	private IColorMutableImageIcon getPodcastFeedEntryIcon(GenericImageSize imageSize) {
		switch (imageSize) {
		case SMALL:  return podcastFeedEntrySmallIcon;
		case MEDIUM: return podcastFeedEntryMediumIcon;
		case BIG:    return podcastFeedEntryBigIcon;
		}
		return null;
	}

	/**
	 * @param imageSize
	 * @return color mutable icon for radio
	 */
	private IColorMutableImageIcon getRadioIcon(GenericImageSize imageSize) {
		switch (imageSize) {
		case SMALL:  return radioSmallIcon;
		case MEDIUM: return radioMediumIcon;
		case BIG:    return radioBigIcon;
		}
		return null;
	}
	
	/**
	 * @param localAudioObjectBigIcon
	 */
	public void setLocalAudioObjectBigIcon(LocalAudioObjectBigColorMutableImageIcon localAudioObjectBigIcon) {
		this.localAudioObjectBigIcon = localAudioObjectBigIcon;
	}
	
	/**
	 * @param localAudioObjectMediumIcon
	 */
	public void setLocalAudioObjectMediumIcon(LocalAudioObjectMediumColorMutableImageIcon localAudioObjectMediumIcon) {
		this.localAudioObjectMediumIcon = localAudioObjectMediumIcon;
	}
	
	/**
	 * @param localAudioObjectSmallIcon
	 */
	public void setLocalAudioObjectSmallIcon(LocalAudioObjectSmallColorMutableImageIcon localAudioObjectSmallIcon) {
		this.localAudioObjectSmallIcon = localAudioObjectSmallIcon;
	}
	
	/**
	 * @param podcastFeedEntryBigIcon
	 */
	public void setPodcastFeedEntryBigIcon(PodcastFeedEntryBigColorMutableImageIcon podcastFeedEntryBigIcon) {
		this.podcastFeedEntryBigIcon = podcastFeedEntryBigIcon;
	}
	
	/**
	 * @param podcastFeedEntryMediumIcon
	 */
	public void setPodcastFeedEntryMediumIcon(PodcastFeedEntryMediumColorMutableImageIcon podcastFeedEntryMediumIcon) {
		this.podcastFeedEntryMediumIcon = podcastFeedEntryMediumIcon;
	}
	
	/**
	 * @param podcastFeedEntrySmallIcon
	 */
	public void setPodcastFeedEntrySmallIcon(PodcastFeedEntrySmallColorMutableImageIcon podcastFeedEntrySmallIcon) {
		this.podcastFeedEntrySmallIcon = podcastFeedEntrySmallIcon;
	}
	
	/**
	 * @param radioBigIcon
	 */
	public void setRadioBigIcon(RadioBigColorMutableImageIcon radioBigIcon) {
		this.radioBigIcon = radioBigIcon;
	}
	
	/**
	 * @param radioMediumIcon
	 */
	public void setRadioMediumIcon(RadioMediumColorMutableImageIcon radioMediumIcon) {
		this.radioMediumIcon = radioMediumIcon;
	}
	
	/**
	 * @param radioSmallIcon
	 */
	public void setRadioSmallIcon(RadioSmallColorMutableImageIcon radioSmallIcon) {
		this.radioSmallIcon = radioSmallIcon;
	}
}
