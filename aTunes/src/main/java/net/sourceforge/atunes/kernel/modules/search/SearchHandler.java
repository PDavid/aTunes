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

package net.sourceforge.atunes.kernel.modules.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.atunes.gui.SearchResultColumnModel;
import net.sourceforge.atunes.gui.views.dialogs.CustomSearchDialog;
import net.sourceforge.atunes.gui.views.dialogs.SearchResultsDialog;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.ILogicalSearchOperator;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.ISearchField;
import net.sourceforge.atunes.model.ISearchHandler;
import net.sourceforge.atunes.model.ISearchNode;
import net.sourceforge.atunes.utils.Logger;

/**
 * Responsible of searches
 * 
 * @author alex
 * 
 */
public final class SearchHandler extends AbstractHandler implements
		ISearchHandler {

	private IDialogFactory dialogFactory;

	private SearchResultsController searchResultsController;

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * Gets the custom search controller.
	 * 
	 * @return the custom search controller
	 */
	private CustomSearchController getSearchController() {
		CustomSearchController customSearchController = new CustomSearchController(
				this.dialogFactory.newDialog(CustomSearchDialog.class), this,
				this.dialogFactory, getBean(IControlsBuilder.class),
				getBean(ComplexRuleTreeBuilder.class), getBean(
						"notLogicalSearchOperator",
						ILogicalSearchOperator.class));
		return customSearchController;
	}

	/**
	 * Gets the search results controller.
	 * 
	 * @return the search results controller
	 */
	private SearchResultsController getSearchResultsController() {
		if (this.searchResultsController == null) {
			this.searchResultsController = new SearchResultsController(
					getBeanFactory(),
					this.dialogFactory.newDialog(SearchResultsDialog.class),
					getBean(IPlayListHandler.class),
					getBean(ILookAndFeelManager.class));
		}
		return this.searchResultsController;
	}

	@Override
	public void startSearch() {
		CustomSearchController controller = getSearchController();
		// Set list of search fields
		List<ISearchField<?, ?>> fields = new ArrayList<ISearchField<?, ?>>();
		for (ISearchField<?, ?> field : getBeanFactory().getBeans(
				ISearchField.class)) {
			fields.add(field);
		}
		controller.setSearchFieldsList(fields);

		// Show dialog to start search
		controller.showSearchDialog();
	}

	@Override
	public void showSearchResults(final Collection<IAudioObject> result) {
		// Open search results dialog
		getSearchResultsController().showSearchResults(result);
	}

	@Override
	public void refreshSearchResultColumns() {
		((SearchResultColumnModel) getSearchResultsController()
				.getComponentControlled().getSearchResultsTable()
				.getColumnModel()).arrangeColumns(false);
	}

	@Override
	public Collection<IAudioObject> search(ISearchNode query) {
		Logger.debug("Executing query ", query);
		return query.evaluate();
	}
}
