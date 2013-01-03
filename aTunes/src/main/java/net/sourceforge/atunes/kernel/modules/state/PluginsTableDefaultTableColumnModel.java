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

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import net.sourceforge.atunes.utils.I18nUtils;

class PluginsTableDefaultTableColumnModel extends DefaultTableColumnModel {
	private static final long serialVersionUID = -4128210690358582389L;

	@Override
	public void addColumn(TableColumn column) {
		super.addColumn(column);
		if (column.getHeaderValue().equals(I18nUtils.getString("ACTIVE"))) {
			column.setMinWidth(80);
			column.setMaxWidth(100);
		}
	}
}