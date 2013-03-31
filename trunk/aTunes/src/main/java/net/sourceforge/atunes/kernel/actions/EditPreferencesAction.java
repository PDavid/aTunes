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

import net.sourceforge.atunes.model.IStateService;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This action shows preferences dialog
 * 
 * @author fleax
 * 
 */
public class EditPreferencesAction extends CustomAbstractAction {

	private static final long serialVersionUID = -6303396973997577995L;

	private IStateService stateService;

	/**
	 * @param stateService
	 */
	public void setStateService(final IStateService stateService) {
		this.stateService = stateService;
	}

	/**
	 * Default constructor
	 */
	public EditPreferencesAction() {
		super(StringUtils.getString(I18nUtils.getString("PREFERENCES"), "..."));
	}

	@Override
	protected void executeAction() {
		this.stateService.editPreferences();
	}
}
