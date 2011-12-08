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

package net.sourceforge.atunes.gui;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;

import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.IPlayListHandler;

final class PlayListStringTableCellRendererCode extends StringTableCellRendererCode {

	private IPlayListHandler playListHandler;
	
    PlayListStringTableCellRendererCode(AbstractCommonColumnModel model, ILookAndFeel lookAndFeel, IPlayListHandler playListHandler) {
        super(model, lookAndFeel);
        this.playListHandler = playListHandler;
    }

    @Override
    public JComponent getComponent(JComponent superComponent, JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    	JComponent c = super.getComponent(superComponent, t, value, isSelected, hasFocus, row, column);
    	if (playListHandler.isCurrentVisibleRowPlaying(row)) {
    		if (getLookAndFeel().getPlayListSelectedItemFont() != null) {
    			 ((JLabel) c).setFont(getLookAndFeel().getPlayListSelectedItemFont());
    		} else if (getLookAndFeel().getPlayListFont() != null) {
                ((JLabel) c).setFont(getLookAndFeel().getPlayListFont());
    		}
    	}
        return c;
    }
}