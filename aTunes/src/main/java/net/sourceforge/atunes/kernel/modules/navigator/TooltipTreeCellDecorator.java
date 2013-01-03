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

import java.awt.Component;

import javax.swing.JLabel;

import net.sourceforge.atunes.gui.AbstractTreeCellDecorator;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeed;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.Album;
import net.sourceforge.atunes.kernel.modules.repository.Artist;
import net.sourceforge.atunes.kernel.modules.repository.Folder;
import net.sourceforge.atunes.kernel.modules.repository.Genre;
import net.sourceforge.atunes.kernel.modules.repository.Year;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IStateNavigation;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Shows custom tooltip
 * 
 * @author alex
 * 
 */
public class TooltipTreeCellDecorator extends
		AbstractTreeCellDecorator<JLabel, Object> {

	private IStateNavigation stateNavigation;

	private IUnknownObjectChecker unknownObjectChecker;

	private ExtendedTooltipContent extendedTooltipContent;

	/**
	 * @param extendedTooltipContent
	 */
	public void setExtendedTooltipContent(
			final ExtendedTooltipContent extendedTooltipContent) {
		this.extendedTooltipContent = extendedTooltipContent;
	}

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param stateNavigation
	 */
	public void setStateNavigation(final IStateNavigation stateNavigation) {
		this.stateNavigation = stateNavigation;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Component decorateTreeCellComponent(final JLabel component,
			final Object userObject, final boolean isSelected) {
		if (!this.stateNavigation.isShowExtendedTooltip()
				|| !this.extendedTooltipContent
						.canObjectBeShownInExtendedToolTip(userObject)) {
			if (userObject instanceof ITreeObject) {
				component
						.setToolTipText(getToolTip(((ITreeObject<? extends IAudioObject>) userObject)));
			} else {
				component.setToolTipText(userObject.toString());
			}
		} else {
			// If using extended tooltip we must set tooltip to null. If not
			// will appear the tooltip of the parent node
			component.setToolTipText(null);
		}
		return component;
	}

	private String getToolTip(
			final ITreeObject<? extends IAudioObject> iTreeObject) {
		if (iTreeObject instanceof Album) {
			Album a = (Album) iTreeObject;
			int songs = a.size();
			return StringUtils.getString(a.getName(), " - ", a.getArtist(),
					" (", songs, " ", (songs > 1 ? I18nUtils.getString("SONGS")
							: I18nUtils.getString("SONG")), ")");
		} else if (iTreeObject instanceof Artist) {
			Artist a = (Artist) iTreeObject;
			int albumSize = a.getAlbums().size();
			return StringUtils.getString(
					a.getName(),
					" (",
					albumSize,
					" ",
					(albumSize > 1 ? I18nUtils.getString("ALBUMS") : I18nUtils
							.getString("ALBUM")), ")");
		} else if (iTreeObject instanceof Folder) {
			Folder f = (Folder) iTreeObject;
			int songs = f.getAudioObjects().size();
			return StringUtils.getString(
					f.getName(),
					" (",
					songs,
					" ",
					(songs > 1 ? I18nUtils.getString("SONGS") : I18nUtils
							.getString("SONG")), ")");
		} else if (iTreeObject instanceof Genre) {
			Genre g = (Genre) iTreeObject;
			int songs = g.size();
			return StringUtils.getString(g.getName(), " (",
					I18nUtils.getString("SONGS"), ": ", songs, ")");
		} else if (iTreeObject instanceof Radio) {
			Radio r = (Radio) iTreeObject;
			return r.getName();
		} else if (iTreeObject instanceof Year) {
			Year y = (Year) iTreeObject;
			int songs = y.size();
			return StringUtils.getString(y.getName(this.unknownObjectChecker),
					" (", I18nUtils.getString("SONGS"), ": ", songs, ")");
		} else if (iTreeObject instanceof PodcastFeed) {
			PodcastFeed p = (PodcastFeed) iTreeObject;
			return p.getName();
		} else {
			throw new IllegalArgumentException(
					"Tooltip not implemented for class "
							+ iTreeObject.getClass().getCanonicalName());
		}
	}
}
