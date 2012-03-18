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

import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayListTable;
import net.sourceforge.atunes.model.PlayState;

final class PlayListIntegerTableCellRendererCode extends AbstractTableCellRendererCode<JLabel, Integer> {
	
	private IPlayListHandler playListHandler;
	
	private PlayListColumnModel columnModel;
	
	PlayListIntegerTableCellRendererCode(ILookAndFeel lookAndFeel, IPlayListHandler playListHandler, PlayListColumnModel columnModel) {
		super(lookAndFeel);
		this.playListHandler = playListHandler;
		this.columnModel = columnModel;
	}

	@Override
	public JLabel getComponent(JLabel c, JTable t, Integer value, boolean isSelected, boolean hasFocus, int row, int column) {
	    String name = t.getColumnName(column);
	    //Display Integer values if the column is nameless
	    if (!"".equals(name)) {
	    	if (playListHandler.isCurrentVisibleRowPlaying(row)) {
	    		if (getLookAndFeel().getPlayListSelectedItemFont() != null) {
	    			 c.setFont(getLookAndFeel().getPlayListSelectedItemFont());
	    		} else if (getLookAndFeel().getPlayListFont() != null) {
	                c.setFont(getLookAndFeel().getPlayListFont());
	    		}
	    	}
	        c.setIcon(null);
	        c.setText(value == null ? null : value.toString());
	        c.setHorizontalAlignment(SwingConstants.CENTER);
	        return c;
	    }

	    //Display an icon if playing and cell is in a "special" column
	    c.setText(null);
	    if (playListHandler.isCurrentVisibleRowPlaying(row)) {
	        c.setIcon(getPlayStateIcon(getLookAndFeel().getPaintForColorMutableIcon(c, isSelected), 
	        		((IPlayListTable) columnModel.getTable()).getPlayState(), getLookAndFeel()));
	    } else {
	        c.setIcon(null); // was using Images.getImage(Images.EMPTY) previously
	    }

	    // Get alignment from model
	    c.setHorizontalAlignment(columnModel.getColumnAlignment(column));
	    return c;
	}
	
    /**
     * @param color
     * @param state
     * @param lookAndFeel
     * @return
     */
    private ImageIcon getPlayStateIcon(Color color, PlayState state, ILookAndFeel lookAndFeel) {
        switch (state) {
        case PLAYING:
            return Context.getBean("playListPlayStateIcon", IIconFactory.class).getIcon(color);
        case STOPPED:
            return Context.getBean("playListStopStateIcon", IIconFactory.class).getIcon(color);
        case PAUSED:
            return Context.getBean("playListPauseStateIcon", IIconFactory.class).getIcon(color);
        default:
            return null;
        }
    }
}