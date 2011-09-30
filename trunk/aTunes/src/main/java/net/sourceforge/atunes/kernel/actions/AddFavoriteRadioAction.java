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

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IRadioHandler;
import net.sourceforge.atunes.utils.I18nUtils;

public class AddFavoriteRadioAction extends AbstractActionOverSelectedObjects<Radio> {

    private static final long serialVersionUID = 82199784140877040L;

    AddFavoriteRadioAction() {
        super(I18nUtils.getString("ADD_FAVORITE_RADIO"), Radio.class);
    }
    
    @Override
    protected void initialize() {
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("RADIO_VIEW_TOOLTIP"));
    }

    @Override
    protected void performAction(List<Radio> objects) {
        for (Radio r : objects) {
            getBean(IRadioHandler.class).addRadio(r);
        }
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        if (rootSelected) {
            return false;
        }

        for (DefaultMutableTreeNode node : selection) {
            if (!(node.getUserObject() instanceof IRadio)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(List<IAudioObject> selection) {
        return true;
    }
}
