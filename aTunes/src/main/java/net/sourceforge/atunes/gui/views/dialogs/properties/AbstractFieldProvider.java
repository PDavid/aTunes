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

package net.sourceforge.atunes.gui.views.dialogs.properties;

import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

abstract class AbstractFieldProvider implements IValueProvider {

	/**
	 * @return translated name of field
	 */
	public abstract String getI18Name();

	@Override
	public final String getValue(final ILocalAudioObject audioObject) {
		String v = getClearValue(audioObject);
		return StringUtils.isEmpty(v) ? "-" : v;
	}

	@Override
	public final String getLabel() {
		return getHtmlFormatted(I18nUtils.getString(getI18Name()));
	}

	/**
	 * Gets the html formatted (only a description)
	 * 
	 * @param desc
	 * @return
	 */
	private String getHtmlFormatted(final String desc) {
		return StringUtils.getString("<html><b>", desc, ": </b></html>");
	}
}