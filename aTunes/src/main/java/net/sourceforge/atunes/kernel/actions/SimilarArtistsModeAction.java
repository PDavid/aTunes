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

package net.sourceforge.atunes.kernel.actions;

import java.util.List;

import net.sourceforge.atunes.model.IAdvancedPlayingModeHandler;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.IStatePlayer;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * This action enables or disables Advanced playing mode
 * 
 * @author Laurent Cathala
 * 
 */
public class SimilarArtistsModeAction extends ActionWithColorMutableIcon {

	private static final long serialVersionUID = -5259044473957293968L;

	private IStatePlayer statePlayer;
	private IIconFactory artistSimilarIcon;
	private IAdvancedPlayingModeHandler advancedPlayingModeHandler;

	/**
	 * @param advancedPlayingModeHandler
	 */
	public void setAdvancedPlayingModeHandler(
			final IAdvancedPlayingModeHandler advancedPlayingModeHandler) {
		this.advancedPlayingModeHandler = advancedPlayingModeHandler;
	}

	/**
	 * @param statePlayer
	 */
	public void setStatePlayer(final IStatePlayer statePlayer) {
		this.statePlayer = statePlayer;
	}

	/**
	 * @param artistSimilarIcon
	 */
	public void setArtistSimilarIcon(final IIconFactory artistSimilarIcon) {
		this.artistSimilarIcon = artistSimilarIcon;
	}

	/**
	 * Default constructor
	 */
	public SimilarArtistsModeAction() {
		super(I18nUtils.getString("SIMILAR_ARTISTS"));
	}

	@Override
	protected void initialize() {
		putValue(SELECTED_KEY, this.statePlayer.isSimilarArtistMode());
		super.initialize();
	}

	@Override
	protected void executeAction() {
		boolean enabled = (Boolean) getValue(SELECTED_KEY);
		this.statePlayer.setSimilarArtistMode(enabled);
		this.advancedPlayingModeHandler.enableSimilarArtistMode(enabled);
	}

	@Override
	public boolean isEnabledForPlayListSelection(
			final List<IAudioObject> selection) {
		// Play action is always enabled even if play list or selection are
		// empty, because this action is used in play button
		return true;
	}

	@Override
	public IColorMutableImageIcon getIcon(final ILookAndFeel lookAndFeel) {
		return this.artistSimilarIcon.getColorMutableIcon();
	}

	@Override
	protected void updateTooltip() {
		if ((Boolean) getValue(SELECTED_KEY)) {
			putValue(SHORT_DESCRIPTION,
					I18nUtils.getString("SIMILAR_ARTIST_MODE_ENABLED"));
		} else {
			putValue(SHORT_DESCRIPTION,
					I18nUtils.getString("SIMILAR_ARTIST_MODE_DISABLED"));
		}
	}

}
