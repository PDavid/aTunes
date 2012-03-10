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

package net.sourceforge.atunes.kernel.modules.state;

import javax.swing.table.DefaultTableModel;

import net.sourceforge.atunes.kernel.modules.pattern.Patterns;
import net.sourceforge.atunes.utils.I18nUtils;

class AvailablePatternsDefaultTableModel extends DefaultTableModel {
    /**
	 * 
	 */
    private static final long serialVersionUID = -3054134384773947174L;

    @Override
    public int getRowCount() {
        return Patterns.getPatterns().size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (column == 0) {
            return Patterns.getPatterns().get(row).getPattern();
        }
        return Patterns.getPatterns().get(row).getDescription();
    }

    @Override
    public String getColumnName(int column) {
        return column == 0 ? I18nUtils.getString("PATTERN") : I18nUtils.getString("VALUE");
    }
}