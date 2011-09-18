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

import net.sourceforge.atunes.kernel.modules.radio.RadioHandler;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.utils.I18nUtils;

public class EditRadioAction extends AbstractActionOverSelectedObjects<IRadio> {

    private static final long serialVersionUID = -922076985505834816L;

    EditRadioAction() {
        super(I18nUtils.getString("EDIT_RADIO"), IRadio.class);
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("EDIT_RADIO"));
    }

    @Override
    protected void performAction(List<IRadio> objects) {
        IRadio radio = objects.get(0); // Guaranteed only one radio
        IRadio radioEdited = RadioHandler.getInstance().editRadio(radio);
        if (radioEdited != null) {
            RadioHandler.getInstance().replace(radio, radioEdited);
        }
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        if (rootSelected || selection.size() != 1 || !(selection.get(0).getUserObject() instanceof IRadio)) {
            return false;
        }
        return true;
    }
    
    @Override
    public boolean isEnabledForNavigationTableSelection(List<IAudioObject> selection) {
        return selection.size() == 1;
    }
}
