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

package net.sourceforge.atunes.kernel.actions;

import net.sourceforge.atunes.gui.model.AbstractCommonColumnModel;
import net.sourceforge.atunes.model.IColumnSelectorDialog;
import net.sourceforge.atunes.model.IColumnSelectorDialogFactory;
import net.sourceforge.atunes.model.IFrame;
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

    private IFrame frame;
    
    private IColumnSelectorDialogFactory columnSelectorDialogFactory;
    
    /**
     * @param columnSelectorDialogFactory
     */
    public void setColumnSelectorDialogFactory(IColumnSelectorDialogFactory columnSelectorDialogFactory) {
		this.columnSelectorDialogFactory = columnSelectorDialogFactory;
	}
    
    /**
     * @param frame
     */
    public void setFrame(IFrame frame) {
		this.frame = frame;
	}
    
    public ArrangePlayListColumnsAction() {
        super(I18nUtils.getString("ARRANGE_COLUMNS"));
    }
    
    @Override
    protected void executeAction() {
    	AbstractCommonColumnModel model = (AbstractCommonColumnModel) frame.getPlayListTable().getColumnModel();
    	
        // Show column selector
    	IColumnSelectorDialog selector = columnSelectorDialogFactory.createDialog();
        selector.setColumnSetToSelect(model.getColumnSet());
        selector.showDialog();

        // Apply changes
        model.arrangeColumns(true);
    }
}
