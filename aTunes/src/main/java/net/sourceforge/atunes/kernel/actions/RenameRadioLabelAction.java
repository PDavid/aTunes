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
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.navigator.RadioNavigationView;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.radio.RadioHandler;
import net.sourceforge.atunes.utils.I18nUtils;

public class RenameRadioLabelAction extends AbstractAction {

    private static final long serialVersionUID = -606790181321223318L;

    RenameRadioLabelAction() {
        super(I18nUtils.getString("RENAME_LABEL"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("RENAME_LABEL"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        TreePath path = NavigationHandler.getInstance().getView(RadioNavigationView.class).getTree().getSelectionPath();
        Object o = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
        if (o instanceof String) {
            String label = (String) o;
            String result = GuiHandler.getInstance().showInputDialog(I18nUtils.getString("RENAME_LABEL"), label);
            if (result != null) {
                List<Radio> radios = RadioHandler.getInstance().getRadios(label);
                RadioHandler.getInstance().setLabel(radios, result);
                NavigationHandler.getInstance().refreshView(RadioNavigationView.class);
            }
        } else if (o instanceof Radio) {
            Radio radio = (Radio) o;
            String result = GuiHandler.getInstance().showInputDialog(I18nUtils.getString("RENAME_LABEL"), radio.getLabel());
            if (result != null) {
                radio.setLabel(result);
                NavigationHandler.getInstance().refreshView(RadioNavigationView.class);
            }
        }
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        return !rootSelected && selection.size() == 1;
    }

}
