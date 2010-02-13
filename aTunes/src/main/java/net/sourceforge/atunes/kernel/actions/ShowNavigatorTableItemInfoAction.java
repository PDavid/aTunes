/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
import java.util.List;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.model.NavigationTableModel;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * This action opens a window which shows information about the current selected
 * tow in navigator
 * 
 * @author fleax
 * 
 */
public class ShowNavigatorTableItemInfoAction extends Action {

    private static final long serialVersionUID = -2006569851431046347L;

    ShowNavigatorTableItemInfoAction() {
        super(I18nUtils.getString("INFO"), Images.getImage(Images.INFO));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("INFO_BUTTON_TOOLTIP"));
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int selectedRow = ControllerProxy.getInstance().getNavigationController().getNavigationTablePanel().getNavigationTable().getSelectedRow();
        AudioObject audioObject = ((NavigationTableModel) ControllerProxy.getInstance().getNavigationController().getNavigationTablePanel().getNavigationTable().getModel())
                .getAudioObjectAt(selectedRow);
        GuiHandler.getInstance().showPropertiesDialog(audioObject);
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(List<AudioObject> selection) {
        return selection.size() == 1;
    }

}
