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



import net.sourceforge.atunes.gui.views.panels.FilterPanel;
import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.model.IFilter;
import net.sourceforge.atunes.model.IFilterHandler;
import net.sourceforge.atunes.model.IFilterPanel;

/**
 * A controller for a filter panel
 * @author alex
 *
 */
public class FilterController extends AbstractSimpleController<FilterPanel> {

	private FilterTextFieldDocumentListener listener;

	private boolean filterApplied = false;

	private IFilterHandler filterHandler;

	/**
	 * Filter to apply when user types in filter panel managed by this controller
	 */
	private IFilter filter;

	/**
	 * @param panel
	 */
	public FilterController(final IFilterPanel panel) {
		super((FilterPanel)panel.getSwingComponent());
	}

	/**
	 * Initializes controller
	 */
	public void initialize() {
		this.listener = new FilterTextFieldDocumentListener(this);
		addBindings();
	}

	/**
	 * @return
	 */
	FilterTextFieldDocumentListener getListener() {
		return listener;
	}

	/**
	 * @return
	 */
	boolean isFilterApplied() {
		return filterApplied;
	}

	/**
	 * Set filter to apply
	 * @param filter
	 */
	public void setFilter(final IFilter filter) {
		this.filter = filter;
	}

	/**
	 * @param filterHandler
	 */
	public void setFilterHandler(final IFilterHandler filterHandler) {
		this.filterHandler = filterHandler;
	}

	@Override
	public void addBindings() {
		getComponentControlled().getFilterTextField().addFocusListener(new FilterControllerFocusListener(this));
		getComponentControlled().getFilterTextField().addKeyListener(new FilterControllerKeyAdapter(this));
		getComponentControlled().getClearButton().addActionListener(new FilterControllerActionListener(this));
	}

	/**
	 * Applies filter by calling FilterHandler
	 * 
	 * @param filterText
	 */
	void applyFilter(final String filterText) {
		filterApplied = filterText != null && !filterText.equals("");
		filterHandler.applyFilter(filter, filterText);
	}

	/**
	 * Returns filter text or null
	 * 
	 * @return
	 */
	String getFilterText() {
		String filterText = getComponentControlled().getFilterTextField().getText();
		return filterText.trim().equals("") ? null : filterText;
	}
}
