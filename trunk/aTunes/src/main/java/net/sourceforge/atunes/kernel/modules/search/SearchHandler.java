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

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.model.IAudioObject;
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

	@Override
	public void startSearch() {
		// Show dialog to start search
		createController().showSearchDialog();
	}

	@Override
	public ISearchNode createQuery(final String title, final String text) {
		return createController().showSearchDialogForQueryCreation(title, text);
	}

	@Override
	public ISearchNode editQuery(final ISearchNode query, final String title,
			final String text) {
		return createController().showSearchDialogForQueryEdition(query, title,
				text);
	}

	@Override
	public void showSearchResults(final ISearchNode query,
			final Collection<IAudioObject> result) {
		// Open search results dialog
		getBean(SearchResultsController.class).showSearchResults(query, result);
	}

	@Override
	public Collection<IAudioObject> search(final ISearchNode query) {
		Logger.debug("Executing query ", query);
		return query.evaluate();
	}

	private CustomSearchController createController() {
		CustomSearchController controller = getBean(CustomSearchController.class);
		// Set list of search fields
		List<ISearchField<?, ?>> fields = new ArrayList<ISearchField<?, ?>>();
		for (ISearchField<?, ?> field : getBeanFactory().getBeans(
				ISearchField.class)) {
			fields.add(field);
		}
		controller.setSearchFieldsList(fields);
		return controller;
	}
}
