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

package net.sourceforge.atunes.kernel.modules.filter;

import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Document listener for filter input text
 * 
 * @author alex
 * 
 */
final class FilterTextFieldDocumentListener implements DocumentListener {

    /**
	 * 
	 */
    private final FilterController filterController;

    /**
     * @param filterController
     */
    public FilterTextFieldDocumentListener(
	    final FilterController filterController) {
	this.filterController = filterController;
	updateFilterPanel();
    }

    @Override
    public void removeUpdate(final DocumentEvent e) {
	update();
    }

    @Override
    public void insertUpdate(final DocumentEvent e) {
	update();
    }

    @Override
    public void changedUpdate(final DocumentEvent e) {
	update();
    }

    private void update() {
	// Search as user type
	SwingUtilities.invokeLater(new Runnable() {
	    @Override
	    public void run() {
		FilterTextFieldDocumentListener.this.filterController
			.applyFilter(FilterTextFieldDocumentListener.this.filterController
				.getFilterText());
		updateFilterPanel();
	    }
	});
    }

    private void updateFilterPanel() {
	this.filterController.getComponentControlled().setFilterApplied(
		this.filterController.isFilterApplied());
    }

}