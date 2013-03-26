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

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.model.IDesktop;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Opens browser to submit a bug or feature request in sourceforge tracker
 * 
 * @author fleax
 * 
 */
public class ReportBugOrFeatureRequestAction extends CustomAbstractAction {

	private static final long serialVersionUID = -2614037760672140565L;

	private IDesktop desktop;

	/**
	 * @param desktop
	 */
	public void setDesktop(final IDesktop desktop) {
		this.desktop = desktop;
	}

	/**
	 * Default constructor
	 */
	public ReportBugOrFeatureRequestAction() {
		super(I18nUtils.getString("REPORT_BUG_OR_REQUEST_FEATURE"));
	}

	@Override
	protected void executeAction() {
		desktop.openURL(Constants.REPORT_BUG_OR_REQUEST_FEATURE_URL);
	}

}
