/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.kernel.modules.search;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.sourceforge.atunes.gui.views.dialogs.CustomSearchDialog;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;

/**
 * Listener of CustomSearchDialog controls.
 */
final class CustomSearchListener implements ActionListener {

	/**
	 * Controller
	 */
	private CustomSearchController controller;
	
    /** Dialog controlled. */
    private CustomSearchDialog dialog;

    /**
     * Constructor.
     * 
     * @param controller
     * @param dialog
     */
    CustomSearchListener(CustomSearchController controller, CustomSearchDialog dialog) {
    	this.controller = controller;
        this.dialog = dialog;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(dialog.getSearchAtComboBox())) {
            // When changing combo box selected value change attributes list
            controller.updateAttributesList();
        } else if (e.getSource().equals(dialog.getSimpleRulesAddButton())) {
            // Pressed Add button
            controller.createSimpleRule();
        } else if (e.getSource().equals(dialog.getComplexRulesAndButton())) {
            // Pressed AND button
            controller.addAndOperator();
        } else if (e.getSource().equals(dialog.getComplexRulesOrButton())) {
            // Pressed OR button
            controller.addOrOperator();
        } else if (e.getSource().equals(dialog.getComplexRulesNotButton())) {
            // Pressed NOT button
            controller.addNotOperator();
        } else if (e.getSource().equals(dialog.getComplexRulesRemoveButton())) {
            // Pressed Remove button
            controller.removeRuleNode();
        } else if (e.getSource().equals(dialog.getSearchButton())) {
            // Pressed Search button
            controller.search();
        } else if (e.getSource().equals(dialog.getAdvancedSearchTextField())) {
            // Pressed Enter in advanced search text field
            controller.search();
        } else if (e.getSource().equals(dialog.getAdvancedSearchCheckBox())) {
            // Pressed Advanced Search Check Box
            controller.enableAdvancedSearch(dialog.getAdvancedSearchCheckBox().isSelected());
            ApplicationState.getInstance().setEnableAdvancedSearch(dialog.getAdvancedSearchCheckBox().isSelected());
        } else if (e.getSource().equals(dialog.getCancelButton())) {
            // Pressed cancel button
            dialog.setVisible(false);
        }

    }

}
