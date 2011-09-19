/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.actions;

import java.awt.event.ActionEvent;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.model.AbstractCommonColumnModel;
import net.sourceforge.atunes.gui.views.controls.ColumnSetPopupMenu;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Arranges play list columns
 * 
 * @author fleax
 * 
 */
public class ArrangePlayListColumnsAction extends CustomAbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 3866441529401824151L;

    public ArrangePlayListColumnsAction() {
        super(I18nUtils.getString("ARRANGE_COLUMNS"));
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
    	ColumnSetPopupMenu.selectColumns((AbstractCommonColumnModel) Context.getBean(IPlayListHandler.class).getPlayListTable().getColumnModel());
    }

}
