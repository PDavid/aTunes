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
package net.sourceforge.atunes.kernel.controllers.searchResults;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.TableRowSorter;

import net.sourceforge.atunes.gui.views.dialogs.SearchResultsDialog;
import net.sourceforge.atunes.kernel.controllers.model.Controller;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.search.SearchResult;
import net.sourceforge.atunes.kernel.modules.search.SearchResultTableModel;
import net.sourceforge.atunes.kernel.modules.search.SearchableObject;
import net.sourceforge.atunes.kernel.modules.visual.VisualHandler;
import net.sourceforge.atunes.model.AudioObject;

/**
 * Controller for the search result dialog.
 */
public class SearchResultsController extends Controller<SearchResultsDialog> {

    private List<SearchResult> results;

    public SearchResultsController(SearchResultsDialog dialog) {
        super(dialog);
        addBindings();
    }

    /**
     * Shows dialog with search results.
     * 
     * @param searchableObject
     *            the searchable object
     * @param resultsList
     *            the results
     */
    public void showSearchResults(SearchableObject searchableObject, List<SearchResult> resultsList) {
        this.results = resultsList;
        SearchResultTableModel model = searchableObject.getSearchResultsTableModel(resultsList);
        getComponentControlled().getSearchResultsTable().setModel(model);
        getComponentControlled().getSearchResultsTable().setRowSorter(new TableRowSorter<SearchResultTableModel>(model));
        getComponentControlled().setVisible(true);
    }

    @Override
    protected void addBindings() {
        SearchResultsListener listener = new SearchResultsListener(this, getComponentControlled());
        getComponentControlled().getShowElementInfo().addActionListener(listener);
        getComponentControlled().getAddToCurrentPlayList().addActionListener(listener);
        getComponentControlled().getAddToNewPlayList().addActionListener(listener);

    }

    /**
     * Displays info of first selected item.
     */
    protected void showInfo() {
        List<AudioObject> selectedResults = getSelectedResults();
        if (selectedResults == null) {
            return;
        }
        VisualHandler.getInstance().showPropertiesDialog(selectedResults.get(0));
    }

    /**
     * Adds selected results to current play list.
     */
    protected void addToPlayList() {
        List<AudioObject> selectedResults = getSelectedResults();
        if (selectedResults == null) {
            return;
        }
        PlayListHandler.getInstance().addToPlayList(selectedResults);
    }

    /**
     * Adds selected results to a new play list.
     */
    protected void addToNewPlayList() {
        List<AudioObject> selectedResults = getSelectedResults();
        if (selectedResults == null) {
            return;
        }
        PlayListHandler.getInstance().newPlayList(selectedResults);
    }

    /**
     * Returns selected results as audio objects.
     * 
     * @return the selected results
     */
    private List<AudioObject> getSelectedResults() {
        int[] selectedRows = getComponentControlled().getSearchResultsTable().getSelectedRows();
        if (selectedRows.length == 0) {
            return null;
        }
        List<AudioObject> selectedResults = new ArrayList<AudioObject>();
        JTable table = getComponentControlled().getSearchResultsTable();
        for (int element : selectedRows) {
            selectedResults.add(results.get(table.convertRowIndexToModel(element)).getAudioObject());
        }
        return selectedResults;
    }

    @Override
    protected void addStateBindings() {
        // Nothing to do
    }

    @Override
    protected void notifyReload() {
        // Nothing to do
    }
}
