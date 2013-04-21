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

package net.sourceforge.atunes.kernel.modules.context.audioobject;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectStatistics;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IContextInformationSource;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectImageHandler;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IStatisticsHandler;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Data Source for basic audio object information
 * 
 * @author alex
 * 
 */
public class AudioObjectBasicInfoDataSource implements
		IContextInformationSource {

	private IWebServicesHandler webServicesHandler;

	private ILookAndFeelManager lookAndFeelManager;

	private IIconFactory rssMediumIcon;

	private IIconFactory radioMediumIcon;

	private IAudioObject audioObject;

	private ImageIcon image;

	private String title;

	private String artist;

	private String lastPlayDate;

	private IUnknownObjectChecker unknownObjectChecker;

	private IStatisticsHandler statisticsHandler;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param statisticsHandler
	 */
	public void setStatisticsHandler(final IStatisticsHandler statisticsHandler) {
		this.statisticsHandler = statisticsHandler;
	}

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param radioMediumIcon
	 */
	public void setRadioMediumIcon(final IIconFactory radioMediumIcon) {
		this.radioMediumIcon = radioMediumIcon;
	}

	/**
	 * @param rssMediumIcon
	 */
	public void setRssMediumIcon(final IIconFactory rssMediumIcon) {
		this.rssMediumIcon = rssMediumIcon;
	}

	@Override
	public void getData(final IAudioObject audioObject) {
		this.audioObject = audioObject;
		this.image = getImageData(audioObject);
		if (audioObject instanceof ILocalAudioObject) {
			this.title = audioObject.getTitleOrFileName();
			this.artist = audioObject.getArtist(this.unknownObjectChecker);
			this.lastPlayDate = getLastPlayDateData(audioObject);
		} else if (audioObject instanceof IRadio) {
			this.title = ((IRadio) audioObject).getName();
			this.artist = ((IRadio) audioObject).getUrl();
		} else if (audioObject instanceof IPodcastFeedEntry) {
			this.title = ((IPodcastFeedEntry) audioObject).getTitle();
		}
	}

	/**
	 * @return
	 */
	public IAudioObject getAudioObject() {
		return this.audioObject;
	}

	/**
	 * @return
	 */
	public ImageIcon getImage() {
		return this.image;
	}

	/**
	 * @return
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * @return
	 */
	public String getArtist() {
		return this.artist;
	}

	/**
	 * @return
	 */
	public String getLastPlayDate() {
		return this.lastPlayDate;
	}

	/**
	 * Returns image for audio object
	 * 
	 * @param audioObject
	 * @return
	 */
	private ImageIcon getImageData(final IAudioObject audioObject) {
		if (audioObject instanceof ILocalAudioObject) {
			ImageIcon localImage = this.beanFactory.getBean(
					ILocalAudioObjectImageHandler.class).getImage(audioObject,
					Constants.ALBUM_IMAGE_SIZE);
			if (localImage == null) {
				ImageIcon albumImage = this.webServicesHandler.getAlbumImage(
						audioObject.getArtist(this.unknownObjectChecker),
						audioObject.getAlbum(this.unknownObjectChecker));
				if (albumImage != null) {
					localImage = ImageUtils.resize(albumImage,
							Constants.ALBUM_IMAGE_SIZE.getSize(),
							Constants.ALBUM_IMAGE_SIZE.getSize());
				}
			}
			return localImage;
		} else if (audioObject instanceof IRadio) {
			return this.radioMediumIcon.getIcon(this.lookAndFeelManager
					.getCurrentLookAndFeel().getPaintForSpecialControls());
		} else if (audioObject instanceof IPodcastFeedEntry) {
			return this.rssMediumIcon.getIcon(this.lookAndFeelManager
					.getCurrentLookAndFeel().getPaintForSpecialControls());
		}
		return null;
	}

	/**
	 * Return last play date for given audio object
	 * 
	 * @param audioObject
	 * @return
	 */
	private String getLastPlayDateData(final IAudioObject audioObject) {
		// Get last date played
		IAudioObjectStatistics stats = this.statisticsHandler
				.getAudioObjectStatistics(audioObject);
		if (stats == null) {
			return I18nUtils.getString("SONG_NEVER_PLAYED");
		} else {
			DateTime date = stats.getLastPlayed();
			// If date is null -> never played
			if (date == null) {
				return I18nUtils.getString("SONG_NEVER_PLAYED");
			} else {
				return StringUtils.getString("<html>", I18nUtils
						.getString("LAST_DATE_PLAYED"), ":<br/><center> ",
						DateTimeFormat.shortDateTime().print(date),
						"<center></html>");
			}
		}
	}

	/**
	 * @param webServicesHandler
	 */
	public final void setWebServicesHandler(
			final IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}

	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(
			final ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

	@Override
	public void cancel() {
	}

}
