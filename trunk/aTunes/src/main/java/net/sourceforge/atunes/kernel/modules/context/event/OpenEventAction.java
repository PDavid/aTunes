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

import net.sourceforge.atunes.kernel.modules.context.ContextTableAction;
import net.sourceforge.atunes.model.IEvent;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Opens an event
 * 
 * @author alex
 * 
 */
public final class OpenEventAction extends ContextTableAction<IEvent> {

	private static final long serialVersionUID = -7758596564970276630L;

	/**
     * 
     */
	public OpenEventAction() {
		super(I18nUtils.getString("OPEN_EVENT"));
	}

	@Override
	protected void execute(final IEvent event) {
		// open event url
		getDesktop().openURL(event.getUrl());
	}

	@Override
	protected IEvent getSelectedObject(final int row) {
		return ((EventsResultTableModel) getTable().getModel()).getEntry(row);
	}

	@Override
	protected boolean isEnabledForObject(final Object object) {
		return true;
	}
}