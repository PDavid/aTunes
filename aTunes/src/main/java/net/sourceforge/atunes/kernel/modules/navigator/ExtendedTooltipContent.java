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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JDialog;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.views.dialogs.ExtendedToolTip;
import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IFolder;
import net.sourceforge.atunes.model.IGenre;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IPodcastFeed;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IStatisticsHandler;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.model.IYear;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Sets content of extended tooltip
 * 
 * @author alex
 * 
 */
public class ExtendedTooltipContent {

	private static final Dimension IMAGE_DIMENSION = new Dimension(
			Constants.TOOLTIP_IMAGE_WIDTH + 300,
			Constants.TOOLTIP_IMAGE_HEIGHT + 10);

	private static final Dimension NO_IMAGE_DIMENSION = new Dimension(200, 65);

	private static final String SONG = "SONG";

	private static final String SONGS2 = "SONGS";

	private ExtendedToolTip extendedTooltip;

	private IStatisticsHandler statisticsHandler;

	private IUnknownObjectChecker unknownObjectChecker;

	private ILookAndFeelManager lookAndFeelManager;

	private IControlsBuilder controlsBuilder;

	/** The current extended tool tip content. */
	private volatile Object currentExtendedToolTipContent;

	/**
	 * @param controlsBuilder
	 */
	public void setControlsBuilder(final IControlsBuilder controlsBuilder) {
		this.controlsBuilder = controlsBuilder;
	}

	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(
			final ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param statisticsHandler
	 */
	public void setStatisticsHandler(final IStatisticsHandler statisticsHandler) {
		this.statisticsHandler = statisticsHandler;
	}

	/**
	 * Fills tool tip with data from object
	 * 
	 * @param obj
	 */
	public void setToolTipContent(final ITreeObject<? extends IAudioObject> obj) {
		// Picture is set asynchronously
		getExtendedToolTip().setImage(null);
		setExtendedToolTipFromTreeObject(obj);
	}

	/**
	 * Fills tool tip from tree object
	 * 
	 * @param object
	 */
	private void setExtendedToolTipFromTreeObject(
			final ITreeObject<? extends IAudioObject> object) {
		if (object instanceof IAlbum) {
			setFromAlbum((IAlbum) object);
		} else if (object instanceof IPodcastFeed) {
			setFromPodcast((IPodcastFeed) object);
		} else if (object instanceof IFolder) {
			setFromFolder((IFolder) object);
		} else if (object instanceof IGenre) {
			setFromGenre((IGenre) object);
		} else if (object instanceof IYear) {
			setFromYear((IYear) object);
		} else if (object instanceof IArtist) {
			setFromArtist((IArtist) object);
		}
	}

	/**
	 * @param object
	 */
	private void setFromArtist(final IArtist a) {
		getExtendedToolTip().setLine1(a.getName());
		int albumNumber = a.getAlbums().size();
		getExtendedToolTip().setLine2(
				StringUtils.getString(albumNumber, " ",
						(albumNumber > 1 ? I18nUtils.getString("ALBUMS")
								: I18nUtils.getString("ALBUM"))));
		getExtendedToolTip().setLine3(
				StringUtils.getString(I18nUtils.getString("TIMES_PLAYED"),
						": ", this.statisticsHandler.getArtistTimesPlayed(a)));
	}

	/**
	 * @param object
	 */
	private void setFromYear(final IYear y) {
		getExtendedToolTip().setLine1(y.getName(this.unknownObjectChecker));
		int songs = y.size();
		getExtendedToolTip().setLine2(
				StringUtils.getString(
						songs,
						" ",
						(songs > 1 ? I18nUtils.getString(SONGS2) : I18nUtils
								.getString(SONG))));
	}

	/**
	 * @param object
	 */
	private void setFromGenre(final IGenre g) {
		getExtendedToolTip().setLine1(g.getName());
		int songs = g.size();
		getExtendedToolTip().setLine2(
				StringUtils.getString(
						songs,
						" ",
						(songs > 1 ? I18nUtils.getString(SONGS2) : I18nUtils
								.getString(SONG))));
	}

	/**
	 * @param object
	 */
	private void setFromFolder(final IFolder f) {
		getExtendedToolTip().setLine1(f.getName());
		int folderNumber = f.getFolders().size();
		if (folderNumber > 0) {
			getExtendedToolTip().setLine2(
					StringUtils.getString(folderNumber, " ",
							(folderNumber > 1 ? I18nUtils.getString("FOLDERS")
									: I18nUtils.getString("FOLDER"))));
		} else {
			getExtendedToolTip().setLine2(null);
		}
		int songs = f.getAudioObjects().size();
		getExtendedToolTip().setLine3(
				StringUtils.getString(
						songs,
						" ",
						(songs > 1 ? I18nUtils.getString(SONGS2) : I18nUtils
								.getString(SONG))));
	}

	/**
	 * @param object
	 */
	private void setFromPodcast(final IPodcastFeed p) {
		getExtendedToolTip().setLine1(p.getName());
		getExtendedToolTip().setLine2(
				StringUtils.getString(I18nUtils.getString("PODCAST_ENTRIES"),
						": ", p.getPodcastFeedEntries().size()));
		getExtendedToolTip().setLine3(
				StringUtils.getString(
						I18nUtils.getString("NEW_PODCAST_ENTRIES_TOOLTIP"),
						": ", p.getNewEntriesCount()));
	}

	/**
	 * @param object
	 */
	private void setFromAlbum(final IAlbum a) {
		getExtendedToolTip().setLine1(a.getName());
		getExtendedToolTip().setLine2(a.getArtist().getName());
		int songNumber = a.size();
		getExtendedToolTip().setLine3(
				StringUtils.getString(songNumber, " ",
						(songNumber > 1 ? I18nUtils.getString(SONGS2)
								: I18nUtils.getString(SONG))));
	}

	/**
	 * Returns <code>true</code> if given object can be shown in extended
	 * tooltip
	 * 
	 * @param object
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean canObjectBeShownInExtendedToolTip(final Object object) {
		if (object instanceof ITreeObject) {
			return isExtendedTooltipSupported((ITreeObject<? extends IAudioObject>) object);
		}
		return false;
	}

	/**
	 * @param object
	 * @return <code>true</code> if this object supports extended tool tip
	 */
	private boolean isExtendedTooltipSupported(
			final ITreeObject<? extends IAudioObject> object) {
		if (object instanceof IAlbum) {
			return true;
		} else if (object instanceof IPodcastFeed) {
			return true;
		} else if (object instanceof IFolder) {
			return true;
		} else if (object instanceof IGenre) {
			return true;
		} else if (object instanceof IYear) {
			return true;
		} else if (object instanceof IArtist) {
			return true;
		} else if (object instanceof IRadio) {
			return false;
		} else {
			throw new IllegalArgumentException(object.getClass()
					.getCanonicalName());
		}
	}

	/**
	 * Adjust size of extended tool tip if it's going to show an image or not
	 * 
	 * @param currentAlbumToolTipContent
	 */
	public void setSizeToFitImage(final Object currentAlbumToolTipContent) {
		boolean image = currentAlbumToolTipContent instanceof ITreeObject
				&& isExtendedTooltipImageSupported(currentAlbumToolTipContent);
		getExtendedToolTip().setSize(
				image ? IMAGE_DIMENSION : NO_IMAGE_DIMENSION);
	}

	/**
	 * Returns <code>true</code> if this object supports image in extended tool
	 * tip
	 * 
	 * @param object
	 * @return
	 */
	private boolean isExtendedTooltipImageSupported(final Object object) {
		if (object instanceof IAlbum) {
			return true;
		} else if (object instanceof IPodcastFeed) {
			return false;
		} else if (object instanceof IFolder) {
			return false;
		} else if (object instanceof IGenre) {
			return false;
		} else if (object instanceof IYear) {
			return false;
		} else if (object instanceof IArtist) {
			return true;
		} else if (object instanceof IRadio) {
			return false;
		} else {
			throw new IllegalArgumentException(object.getClass()
					.getCanonicalName());
		}
	}

	/**
	 * Gets the album tool tip.
	 * 
	 * @return the album tool tip
	 */
	private ExtendedToolTip getExtendedToolTip() {
		if (this.extendedTooltip == null) {
			JDialog.setDefaultLookAndFeelDecorated(false);
			this.extendedTooltip = new ExtendedToolTip(this.controlsBuilder,
					IMAGE_DIMENSION.width, IMAGE_DIMENSION.height);
			JDialog.setDefaultLookAndFeelDecorated(this.lookAndFeelManager
					.getCurrentLookAndFeel().isDialogUndecorated());
		}
		return this.extendedTooltip;
	}

	/**
	 * Shows or hides tooltip
	 * 
	 * @param b
	 */
	public void setVisible(final boolean b) {
		getExtendedToolTip().setVisible(b);
	}

	/**
	 * @return if tooltip is visible
	 */
	public boolean isVisible() {
		return getExtendedToolTip().isVisible();
	}

	/**
	 * Sets location
	 * 
	 * @param x
	 * @param y
	 */
	public void setLocation(final int x, final int y) {
		getExtendedToolTip().setLocation(x, y);
	}

	/**
	 * Sets the current tool tip content.
	 * 
	 * @param currentAlbumToolTipContent
	 *            the new current album tool tip content
	 */
	public void setCurrentExtendedToolTipContent(
			final Object currentAlbumToolTipContent) {
		this.currentExtendedToolTipContent = currentAlbumToolTipContent;
		setSizeToFitImage(currentAlbumToolTipContent);
	}

	/**
	 * Gets the last album tool tip content.
	 * 
	 * @return the last album tool tip content
	 */
	public Object getCurrentExtendedToolTipContent() {
		return this.currentExtendedToolTipContent;
	}

	/**
	 * Sets image to tooltip
	 * 
	 * @param imageIcon
	 */
	public void setImage(final ImageIcon imageIcon) {
		getExtendedToolTip().setImage(imageIcon);
	}

}
