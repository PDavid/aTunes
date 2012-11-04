/*
 * aTunes 2.2.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.kernel.modules.context;

import java.util.List;

import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

final class ContextTableRowPopupMenuListener<T> implements PopupMenuListener {

    private final List<ContextTableAction<T>> actions;
    private final ContextTable table;

    ContextTableRowPopupMenuListener(final List<ContextTableAction<T>> actions,
	    final ContextTable table) {
	this.actions = actions;
	this.table = table;
    }

    @Override
    public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
	for (ContextTableAction<T> action : actions) {
	    int row = table.getSelectedRow();
	    action.setEnabled(row != -1
		    && action.isEnabledForObject(action.getSelectedObject(row)));
	}
    }

    @Override
    public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) {
    }

    @Override
    public void popupMenuCanceled(final PopupMenuEvent e) {
    }
}