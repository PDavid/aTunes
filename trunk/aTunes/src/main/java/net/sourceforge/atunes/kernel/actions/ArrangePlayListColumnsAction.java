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

package net.sourceforge.atunes.kernel.actions;

import java.util.List;

import net.sourceforge.atunes.gui.AbstractCommonColumnModel;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColumnSelectorDialog;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IPlayListTable;
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

	private IDialogFactory dialogFactory;

	private IPlayListTable playListTable;

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param playListTable
	 */
	public void setPlayListTable(IPlayListTable playListTable) {
		this.playListTable = playListTable;
	}

	/**
	 * Default constructor
	 */
	public ArrangePlayListColumnsAction() {
		super(I18nUtils.getString("ARRANGE_COLUMNS"));
	}

	@Override
	protected void executeAction() {
		AbstractCommonColumnModel model = (AbstractCommonColumnModel) playListTable
				.getColumnModel();

		// Show column selector
		IColumnSelectorDialog selector = dialogFactory
				.newDialog(IColumnSelectorDialog.class);
		selector.setColumnSetToSelect(model.getColumnSet());
		selector.showDialog();

		// Apply changes
		model.arrangeColumns(true);
	}

	@Override
	public boolean isEnabledForPlayListSelection(List<IAudioObject> selection) {
		return true;
	}
}
