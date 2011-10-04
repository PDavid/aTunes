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

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.kernel.modules.navigator.RadioNavigationView;
import net.sourceforge.atunes.model.IInputDialog;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IRadioHandler;
import net.sourceforge.atunes.utils.I18nUtils;

public class RenameRadioLabelAction extends CustomAbstractAction {

    private static final long serialVersionUID = -606790181321223318L;

    RenameRadioLabelAction() {
        super(I18nUtils.getString("RENAME_LABEL"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("RENAME_LABEL"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    	INavigationHandler navigationHandler = getBean(INavigationHandler.class);
        TreePath path = navigationHandler.getView(RadioNavigationView.class).getTree().getSelectionPath();
        Object o = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
        
        IInputDialog dialog = getBean(IInputDialog.class);
        dialog.setTitle(I18nUtils.getString("RENAME_LABEL"));

        if (o instanceof String) {
            String label = (String) o;
            dialog.showDialog(label);
            String result = dialog.getResult();
            if (result != null) {
                List<IRadio> radios = getBean(IRadioHandler.class).getRadios(label);
                getBean(IRadioHandler.class).setLabel(radios, result);
                navigationHandler.refreshView(RadioNavigationView.class);
            }
        } else if (o instanceof IRadio) {
            IRadio radio = (IRadio) o;
            dialog.showDialog(radio.getLabel());
            String result = dialog.getResult();
            if (result != null) {
                radio.setLabel(result);
                navigationHandler.refreshView(RadioNavigationView.class);
            }
        }
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        return !rootSelected && selection.size() == 1;
    }

}
