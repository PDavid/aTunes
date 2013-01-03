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

package net.sourceforge.atunes.kernel.modules.context.artist;

import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanel;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Context panel to show album information
 * 
 * @author alex
 * 
 */
public class ArtistContextPanel extends AbstractContextPanel {

	private static final long serialVersionUID = -7910261492394049289L;

	private IIconFactory artistImageIcon;

	private IUnknownObjectChecker unknownObjectChecker;

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param artistImageIcon
	 */
	public void setArtistImageIcon(final IIconFactory artistImageIcon) {
		this.artistImageIcon = artistImageIcon;
	}

	@Override
	public IColorMutableImageIcon getContextPanelIcon(final IAudioObject audioObject) {
		return artistImageIcon.getColorMutableIcon();
	}

	@Override
	public String getContextPanelName() {
		return "ARTIST";
	}

	@Override
	public String getContextPanelTitle(final IAudioObject audioObject) {
		return I18nUtils.getString("ARTIST");
	}

	@Override
	public boolean isPanelVisibleForAudioObject(final IAudioObject audioObject) {
		// Avoid unknown artist
		if (unknownObjectChecker.isUnknownArtist(audioObject.getArtist(unknownObjectChecker))) {
			return false;
		}

		// Enable panel for LocalAudioObject objects or Radios with song information available
		return audioObject instanceof ILocalAudioObject || audioObject instanceof IRadio && ((IRadio) audioObject).isSongInfoAvailable();
	}

	@Override
	public boolean panelNeedsToBeUpdated(final IAudioObject previousAudioObject, final IAudioObject newAudioObject) {
		if (previousAudioObject == null || newAudioObject == null) {
			return true;
		}

		return !previousAudioObject.getArtist(unknownObjectChecker).equalsIgnoreCase(newAudioObject.getArtist(unknownObjectChecker));
	}
}
