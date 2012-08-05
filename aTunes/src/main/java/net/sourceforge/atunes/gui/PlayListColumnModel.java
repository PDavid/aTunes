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


import javax.swing.table.TableColumn;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.model.IColumnSet;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayListTable;
import net.sourceforge.atunes.model.ITableCellRendererCode;
import net.sourceforge.atunes.model.ITaskService;

/**
 * The Class PlayListColumnModel.
 */
public final class PlayListColumnModel extends AbstractCommonColumnModel {

    private static final long serialVersionUID = -2211160302611944001L;

    private IPlayListHandler playListHandler;
    
    /**
     * Instantiates a new play list column model.
     * @param playList
     * @param playListHandler
     * @param lookAndFeel
     * @param taskService
     */
    public PlayListColumnModel(IPlayListTable playList, IPlayListHandler playListHandler, ILookAndFeel lookAndFeel, ITaskService taskService) {
        super(playList.getSwingComponent(), (IColumnSet) Context.getBean("playListColumnSet"), taskService, lookAndFeel);
        this.playListHandler = playListHandler;
        enableColumnChange(true);
    }

    @Override
    protected void reapplyFilter() {
    	playListHandler.reapplyFilter();
    }

    @Override
    public void addColumn(TableColumn aColumn) {
        super.addColumn(aColumn);
        updateColumnSettings(aColumn);

        // No header renderer is added to play list since user can change order of table manually by adding, removing or moving rows
        // so keep ordering has no sense
    }

    @Override
    public ITableCellRendererCode<?, ?> getRendererCodeFor(Class<?> clazz) {
        if (clazz.equals(Integer.class)) {
            return new PlayListIntegerTableCellRendererCode(getLookAndFeel(), playListHandler, this);
        } else if (clazz.equals(String.class)) {
            return new PlayListStringTableCellRendererCode(this, getLookAndFeel(), playListHandler);
        } else if (clazz.equals(TextAndIcon.class)) {
            return new PlayListTextAndIconTableCellRendererCode(this, getLookAndFeel(), playListHandler);
        } else {
            return super.getRendererCodeFor(clazz);
        }
    }    
}
