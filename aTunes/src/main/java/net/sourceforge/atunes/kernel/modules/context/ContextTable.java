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

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.model.ILookAndFeel;

/**
 * <p>
 * This class is needed for Substance look and feel 4.1+
 * </p>
 * <a href ="https://substance.dev.java.net/servlets/ProjectForumMessageView?messageID=22522&forumID=1484"
 * >https://substance.dev.java.net/servlets/ProjectForumMessageView?messageID=
 * 22522&forumID=1484</a>
 */
public class ContextTable extends JTable {

    private static final long serialVersionUID = 339974237840854168L;

    /**
     * @param lookAndFeel
     */
    public ContextTable(ILookAndFeel lookAndFeel) {
    	super();
    	setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setColumnSelectionAllowed(false);
        setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        getTableHeader().setReorderingAllowed(false);
        lookAndFeel.decorateTable(this);
    }
    
    @Override
    public void setRowHeight(int heigth) {
        super.setRowHeight(Constants.THUMB_IMAGE_HEIGHT + 5);
    }

	/**
	 * Adds row panel used to render and edit cells
	 * @param rowPanel
	 */
	public void addContextRowPanel(ContextTableRowPanelRendererCode<?> rowPanel) {
		rowPanel.bind(this);
	}

}
