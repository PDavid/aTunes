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

package net.sourceforge.atunes.kernel.modules.state;

import java.util.Map;

import net.sourceforge.atunes.model.IStateRadio;
import net.sourceforge.atunes.utils.ReflectionUtils;

/**
 * This class represents the application settings that are stored at application
 * shutdown and loaded at application startup.
 * <p>
 * <b>NOTE: All classes that are used as properties must be Java Beans!</b>
 * </p>
 */
public class ApplicationStateRadio implements IStateRadio {

	private PreferenceHelper preferenceHelper;

	/**
	 * @param preferenceHelper
	 */
	public void setPreferenceHelper(final PreferenceHelper preferenceHelper) {
		this.preferenceHelper = preferenceHelper;
	}

	@Override
	public boolean isReadInfoFromRadioStream() {
		return this.preferenceHelper.getPreference(
				Preferences.READ_INFO_FROM_RADIO_STREAM, Boolean.class, true);
	}

	@Override
	public void setReadInfoFromRadioStream(final boolean readInfoFromRadioStream) {
		this.preferenceHelper.setPreference(
				Preferences.READ_INFO_FROM_RADIO_STREAM,
				readInfoFromRadioStream);
	}

	@Override
	public Map<String, String> describeState() {
		return ReflectionUtils.describe(this);
	}

}
