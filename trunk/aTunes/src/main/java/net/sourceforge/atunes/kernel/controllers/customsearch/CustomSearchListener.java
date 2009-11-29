/*
 * aTunes 2.0.0-SNAPSHOT
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
package net.sourceforge.atunes.kernel.controllers.customsearch;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.sourceforge.atunes.gui.views.dialogs.CustomSearchDialog;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;

/**
 * Listener of CustomSearchDialog controls.
 */
public final class CustomSearchListener implements ActionListener {

    /** Dialog controlled. */
    private CustomSearchDialog dialog;

    /**
     * Constructor.
     * 
     * @param dialog
     *            the dialog
     */
    CustomSearchListener(CustomSearchDialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(dialog.getSearchAtComboBox())) {
            // When changing combo box selected value change attributes list
            ControllerProxy.getInstance().getCustomSearchController().updateAttributesList();
        } else if (e.getSource().equals(dialog.getSimpleRulesAddButton())) {
            // Pressed Add button
            ControllerProxy.getInstance().getCustomSearchController().createSimpleRule();
        } else if (e.getSource().equals(dialog.getComplexRulesAndButton())) {
            // Pressed AND button
            ControllerProxy.getInstance().getCustomSearchController().addAndOperator();
        } else if (e.getSource().equals(dialog.getComplexRulesOrButton())) {
            // Pressed OR button
            ControllerProxy.getInstance().getCustomSearchController().addOrOperator();
        } else if (e.getSource().equals(dialog.getComplexRulesNotButton())) {
            // Pressed NOT button
            ControllerProxy.getInstance().getCustomSearchController().addNotOperator();
        } else if (e.getSource().equals(dialog.getComplexRulesRemoveButton())) {
            // Pressed Remove button
            ControllerProxy.getInstance().getCustomSearchController().removeRuleNode();
        } else if (e.getSource().equals(dialog.getSearchButton())) {
            // Pressed Search button
            ControllerProxy.getInstance().getCustomSearchController().search();
        } else if (e.getSource().equals(dialog.getAdvancedSearchTextField())) {
            // Pressed Enter in advanced search text field
            ControllerProxy.getInstance().getCustomSearchController().search();
        } else if (e.getSource().equals(dialog.getAdvancedSearchCheckBox())) {
            // Pressed Advanced Search Check Box
            ControllerProxy.getInstance().getCustomSearchController().enableAdvancedSearch(dialog.getAdvancedSearchCheckBox().isSelected());
            ApplicationState.getInstance().setEnableAdvancedSearch(dialog.getAdvancedSearchCheckBox().isSelected());
        } else if (e.getSource().equals(dialog.getCancelButton())) {
            // Pressed cancel button
            dialog.setVisible(false);
        }

    }

}
