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

package net.sourceforge.atunes.kernel.modules.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.swing.SwingWorker;

import net.sourceforge.atunes.gui.SearchResultColumnModel;
import net.sourceforge.atunes.gui.views.dialogs.CustomSearchDialog;
import net.sourceforge.atunes.gui.views.dialogs.SearchResultsDialog;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectComparator;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.ISearchHandler;
import net.sourceforge.atunes.model.ISearchResult;
import net.sourceforge.atunes.model.ISearchableObject;
import net.sourceforge.atunes.model.IStateCore;
import net.sourceforge.atunes.model.SearchIndexNotAvailableException;
import net.sourceforge.atunes.model.SearchQuerySyntaxException;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;

/**
 * Responsible of searches
 * @author alex
 *
 */
public final class SearchHandler extends AbstractHandler implements ISearchHandler {

	/** Dummy lucene field to retrieve all elements. */
	static final String INDEX_FIELD_DUMMY = "dummy";

	/**
	 * Logical operators used to create complex rules.
	 */
	public enum LogicalOperator {
		/**
		 * And logical operator
		 */
		AND,

		/**
		 * Or logical operator
		 */
		OR,

		/**
		 * Not logical operator
		 */
		NOT
	}

	private IDialogFactory dialogFactory;

	/** List of searchable objects available. */
	private final List<ISearchableObject> searchableObjects = new ArrayList<ISearchableObject>();

	/** List of operators for simple rules. */
	private List<String> searchOperators;

	/**
	 * List which indicates if an indexing process is currently running for a
	 * given searchable object.
	 */
	private volatile Map<ISearchableObject, Boolean> currentIndexingWorks = new HashMap<ISearchableObject, Boolean>();

	/** Map with locks for each index. */
	private final Map<ISearchableObject, ReadWriteLock> indexLocks = new HashMap<ISearchableObject, ReadWriteLock>();

	private CustomSearchController customSearchController;

	private SearchResultsController searchResultsController;

	private IAudioObjectComparator audioObjectComparator;

	private IStateCore stateCore;

	private DeviceSearchableObject deviceSearchableObject;

	/**
	 * @param deviceSearchableObject
	 */
	public void setDeviceSearchableObject(
			final DeviceSearchableObject deviceSearchableObject) {
		this.deviceSearchableObject = deviceSearchableObject;
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param stateCore
	 */
	public void setStateCore(final IStateCore stateCore) {
		this.stateCore = stateCore;
	}

	/**
	 * @param audioObjectComparator
	 */
	public void setAudioObjectComparator(final IAudioObjectComparator audioObjectComparator) {
		this.audioObjectComparator = audioObjectComparator;
	}

	/**
	 * Gets the custom search controller.
	 * 
	 * @return the custom search controller
	 */
	private CustomSearchController getSearchController() {
		if (customSearchController == null) {
			customSearchController = new CustomSearchController(dialogFactory.newDialog(CustomSearchDialog.class), stateCore, this);
		}
		return customSearchController;
	}

	/**
	 * Gets the search results controller.
	 * 
	 * @return the search results controller
	 */
	private SearchResultsController getSearchResultsController() {
		if (searchResultsController == null) {
			searchResultsController = new SearchResultsController(getBeanFactory(), dialogFactory.newDialog(SearchResultsDialog.class), getBean(IPlayListHandler.class), getBean(ILookAndFeelManager.class), getBean(IPlayerHandler.class), audioObjectComparator);
		}
		return searchResultsController;
	}

	@Override
	protected void initHandler() {
		searchOperators = new ArrayList<String>();
		searchOperators.add(":");
	}

	@Override
	public void registerSearchableObject(final ISearchableObject so) {
		currentIndexingWorks.put(so, Boolean.FALSE);
		indexLocks.put(so, new ReentrantReadWriteLock(true));
		searchableObjects.add(so);
	}

	@Override
	public void unregisterSearchableObject(final ISearchableObject so) {
		currentIndexingWorks.remove(so);
		indexLocks.remove(so);
		searchableObjects.remove(so);
	}

	@Override
	public void startSearch() {
		// Updates indexes
		for (ISearchableObject searchableObject : searchableObjects) {
			updateSearchIndex(searchableObject);
		}

		// Set list of searchable objects
		getSearchController().setListOfSearchableObjects(searchableObjects);

		// Set list of operators
		getSearchController().setListOfOperators(searchOperators);

		// Show dialog to start search
		getSearchController().showSearchDialog();
	}

	@Override
	public List<IAudioObject> search(final ISearchableObject searchableObject, final String queryStr) throws SearchIndexNotAvailableException, SearchQuerySyntaxException {
		ReadWriteLock searchIndexLock = indexLocks.get(searchableObject);
		Searcher searcher = null;
		try {
			searchIndexLock.readLock().lock();

			String queryString = applyQueryTransformations(queryStr);

			Query query = new QueryParser(Version.LUCENE_30, DEFAULT_INDEX, new SimpleAnalyzer()).parse(queryString);
			searcher = new IndexSearcher(searchableObject.getIndexDirectory(), true);
			TopDocs topDocs = searcher.search(query, 1000);
			List<ISearchResult> rawSearchResults = new ArrayList<ISearchResult>();
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				rawSearchResults.add(new RawSearchResult(searcher.doc(scoreDoc.doc), scoreDoc.score));
			}
			List<IAudioObject> result = searchableObject.getSearchResult(rawSearchResults);
			Logger.debug("Query: ", queryString, " (", result.size(), " search results)");
			return result;
		} catch (IOException e) {
			throw new SearchIndexNotAvailableException(e);
		} catch (ParseException e) {
			throw new SearchQuerySyntaxException(e);
		} finally {
			ClosingUtils.close(searcher);
			searchIndexLock.readLock().unlock();
		}
	}

	/**
	 * Applies a set of transformations to match Lucene syntax.
	 * 
	 * @param query
	 *            the query
	 * 
	 * @return Query transformed in Lucene language
	 */
	private String applyQueryTransformations(final String query) {
		String tempQuery;

		// Replace "NOT xx" by Lucene syntax: "dummy:dummy NOT xx"
		tempQuery = query.replaceAll(LogicalOperator.NOT.toString(), StringUtils.getString(INDEX_FIELD_DUMMY, ":", INDEX_FIELD_DUMMY, " NOT"));

		// Find numeric values and replace to Lucene numeric ranges:
		// year: 2000 ---> year: [2000 TO 2000]
		StringBuilder sb = new StringBuilder();
		StringTokenizer st = new StringTokenizer(tempQuery, " ");
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (token.matches("\"[0-9]+\"")) {
				sb.append(StringUtils.getString("[", token, " TO ", token, "]"));
			} else {
				sb.append(token);
			}
			sb.append(" ");
		}

		return sb.toString();
	}

	/**
	 * Generic method to update any searchable object.
	 * 
	 * @param searchableObject
	 *            the searchable object
	 */
	private void updateSearchIndex(final ISearchableObject searchableObject) {
		if (currentIndexingWorks.get(searchableObject) == null || !currentIndexingWorks.get(searchableObject)) {
			currentIndexingWorks.put(searchableObject, Boolean.TRUE);
			SwingWorker<Void, Void> refreshSearchIndex = new RefreshSearchIndexSwingWorker(currentIndexingWorks, indexLocks, searchableObject);
			refreshSearchIndex.execute();
		}
	}

	@Override
	public void showSearchResults(final ISearchableObject searchableObject, final List<IAudioObject> result) {
		// Open search results dialog
		getSearchResultsController().showSearchResults(searchableObject, result);
	}

	@Override
	public void refreshSearchResultColumns() {
		((SearchResultColumnModel) getSearchResultsController().getComponentControlled().getSearchResultsTable().getColumnModel())
		.arrangeColumns(false);
	}

	@Override
	public void deviceReady(final String location) {
		registerSearchableObject(deviceSearchableObject);
	}

	@Override
	public void deviceDisconnected(final String location) {
		unregisterSearchableObject(deviceSearchableObject);
	}
}
