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

import net.sourceforge.atunes.model.IRadioHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Shows radio browser
 * 
 * @author fleax
 * 
 */
public class ShowRadioBrowserAction extends CustomAbstractAction {

	private static final long serialVersionUID = 531135150461152301L;

	private IRadioHandler radioHandler;

	/**
	 * @param radioHandler
	 */
	public void setRadioHandler(final IRadioHandler radioHandler) {
		this.radioHandler = radioHandler;
	}

	/**
	 * Default constructor
	 */
	public ShowRadioBrowserAction() {
		super(I18nUtils.getString("RADIO_BROWSER"));
	}

	@Override
	protected void executeAction() {
		radioHandler.showRadioBrowser();
	}

}
