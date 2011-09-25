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

package net.sourceforge.atunes.kernel.modules.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.model.SearchResultColumnModel;
import net.sourceforge.atunes.gui.renderers.ColumnRenderers;
import net.sourceforge.atunes.gui.views.controls.ColumnSetPopupMenu;
import net.sourceforge.atunes.gui.views.controls.ColumnSetRowSorter;
import net.sourceforge.atunes.gui.views.dialogs.SearchResultsDialog;
import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.kernel.modules.repository.AudioObjectComparator;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectPropertiesDialogFactory;
import net.sourceforge.atunes.model.IColumn;
import net.sourceforge.atunes.model.IColumnSet;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IState;

/**
 * Controller for the search result dialog.
 */
final class SearchResultsController extends AbstractSimpleController<SearchResultsDialog> {

    private List<IAudioObject> results;
    
    private IColumnSet columnSet;
    
    private IPlayListHandler playListHandler;
    
    private ILookAndFeelManager lookAndFeelManager;
    
    SearchResultsController(SearchResultsDialog dialog, IState state, IPlayListHandler playListHandler, ILookAndFeelManager lookAndFeelManager) {
        super(dialog, state);
        this.columnSet = (IColumnSet) Context.getBean("searchResultsColumnSet");
        this.playListHandler = playListHandler;
        this.lookAndFeelManager = lookAndFeelManager;
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
    public void showSearchResults(ISearchableObject searchableObject, List<IAudioObject> resultsList) {
        this.results = resultsList;

        SearchResultTableModel tableModel = (SearchResultTableModel) getComponentControlled().getSearchResultsTable().getModel();

        IColumn sortedColumn = columnSet.getSortedColumn();
        if (sortedColumn != null) {
            Collections.sort(resultsList, sortedColumn.getComparator(false));
        } else {
            AudioObjectComparator.sort(resultsList);
        }

        tableModel.setResults(resultsList);
        tableModel.refresh(TableModelEvent.UPDATE);
        getComponentControlled().setVisible(true);
    }

    @Override
	public void addBindings() {
        JTable table = getComponentControlled().getSearchResultsTable();
        SearchResultTableModel tableModel = new SearchResultTableModel(columnSet);
        table.setModel(tableModel);

        SearchResultColumnModel columnModel = new SearchResultColumnModel(table, columnSet, lookAndFeelManager.getCurrentLookAndFeel());
        table.setColumnModel(columnModel);

        // Set sorter
        new ColumnSetRowSorter(table, tableModel, columnModel);

        // Bind column set popup menu
        new ColumnSetPopupMenu(getComponentControlled().getSearchResultsTable(), columnModel);

        // Set renderers
        ColumnRenderers.addRenderers(getComponentControlled().getSearchResultsTable(), columnModel, lookAndFeelManager.getCurrentLookAndFeel());

        SearchResultsListener listener = new SearchResultsListener(this, getComponentControlled());
        getComponentControlled().getShowElementInfo().addActionListener(listener);
        getComponentControlled().getAddToCurrentPlayList().addActionListener(listener);
        getComponentControlled().getAddToNewPlayList().addActionListener(listener);

    }

    /**
     * Displays info of first selected item.
     */
    protected void showInfo() {
        List<IAudioObject> selectedResults = getSelectedResults();
        if (selectedResults == null) {
            return;
        }
        Context.getBean(IAudioObjectPropertiesDialogFactory.class).newInstance(selectedResults.get(0), lookAndFeelManager).showDialog();
    }

    /**
     * Adds selected results to current play list.
     */
    protected void addToPlayList() {
        List<IAudioObject> selectedResults = getSelectedResults();
        if (selectedResults == null) {
            return;
        }
        playListHandler.addToPlayList(selectedResults);
    }

    /**
     * Adds selected results to a new play list.
     */
    protected void addToNewPlayList() {
        List<IAudioObject> selectedResults = getSelectedResults();
        if (selectedResults == null) {
            return;
        }
        playListHandler.newPlayList(selectedResults);
    }

    /**
     * Returns selected results as audio objects.
     * 
     * @return the selected results
     */
    private List<IAudioObject> getSelectedResults() {
        int[] selectedRows = getComponentControlled().getSearchResultsTable().getSelectedRows();
        if (selectedRows.length == 0) {
            return null;
        }
        List<IAudioObject> selectedResults = new ArrayList<IAudioObject>();
        JTable table = getComponentControlled().getSearchResultsTable();
        for (int row : selectedRows) {
            selectedResults.add(results.get(table.convertRowIndexToModel(row)));
        }
        return selectedResults;
    }
}
