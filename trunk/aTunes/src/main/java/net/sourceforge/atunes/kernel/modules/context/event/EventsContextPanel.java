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

package net.sourceforge.atunes.kernel.modules.context.event;

import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanel;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Context panel to show events of artist
 * 
 * @author alex
 * 
 */
public class EventsContextPanel extends AbstractContextPanel {

	private IIconFactory eventIcon;

	private IUnknownObjectChecker unknownObjectChecker;

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param eventIcon
	 */
	public void setEventIcon(IIconFactory eventIcon) {
		this.eventIcon = eventIcon;
	}

	@Override
	public IColorMutableImageIcon getContextPanelIcon(IAudioObject audioObject) {
		return eventIcon.getColorMutableIcon();
	}

	@Override
	public String getContextPanelName() {
		return "EVENTS";
	}

	@Override
	public String getContextPanelTitle(IAudioObject audioObject) {
		return I18nUtils.getString("EVENTS");
	}

	@Override
	public boolean isPanelVisibleForAudioObject(IAudioObject audioObject) {
		return (audioObject instanceof ILocalAudioObject)
				|| (audioObject instanceof IRadio && ((IRadio) audioObject)
						.isSongInfoAvailable());
	}

	@Override
	public boolean panelNeedsToBeUpdated(IAudioObject previousAudioObject,
			IAudioObject newAudioObject) {
		if (previousAudioObject == null || newAudioObject == null) {
			return true;
		}

		return !previousAudioObject.getArtist(unknownObjectChecker)
				.equalsIgnoreCase(
						newAudioObject.getArtist(unknownObjectChecker));
	}
}
