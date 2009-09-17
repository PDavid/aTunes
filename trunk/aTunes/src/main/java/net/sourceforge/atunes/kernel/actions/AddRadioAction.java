/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.kernel.modules.radio.RadioHandler;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Opens dialog to add a radio
 * 
 * @author fleax
 * 
 */
public class AddRadioAction extends Action {

    private static final long serialVersionUID = -5764149587317233484L;

    public AddRadioAction() {
        super(I18nUtils.getString("ADD_RADIO"), ImageLoader.getImage(ImageLoader.RADIO_ADD));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("ADD_RADIO"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        RadioHandler.getInstance().addRadio();
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        return true;
    }

}
