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

import net.sourceforge.atunes.gui.model.SearchResultColumnModel;
import net.sourceforge.atunes.gui.views.dialogs.CustomSearchDialog;
import net.sourceforge.atunes.gui.views.dialogs.SearchResultsDialog;
import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.modules.search.searchableobjects.DeviceSearchableObject;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.model.ISearchHandler;
import net.sourceforge.atunes.model.ISearchableObject;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

public final class SearchHandler extends AbstractHandler implements ISearchHandler {

    /** Dummy lucene field to retrieve all elements. */
    private static final String INDEX_FIELD_DUMMY = "dummy";

    private final class RefreshSearchIndexSwingWorker extends
			SwingWorker<Void, Void> {
		private final ISearchableObject searchableObject;
		private IndexWriter indexWriter;

		private RefreshSearchIndexSwingWorker(ISearchableObject searchableObject) {
			this.searchableObject = searchableObject;
		}

		@Override
		protected Void doInBackground() {
		    ReadWriteLock searchIndexLock = indexLocks.get(searchableObject);
		    try {
		        searchIndexLock.writeLock().lock();
		        initSearchIndex();
		        updateSearchIndex(searchableObject.getElementsToIndex());
		        finishSearchIndex();
		        return null;
		    } finally {
		        searchIndexLock.writeLock().unlock();
		        ClosingUtils.close(indexWriter);
		        currentIndexingWorks.put(searchableObject, Boolean.FALSE);
		    }
		}

		@Override
		protected void done() {
		    // Nothing to do
		}

		private void initSearchIndex() {
		    Logger.info("Updating index for " + searchableObject.getClass());
		    try {
		        FileUtils.deleteDirectory(searchableObject.getIndexDirectory().getFile());
		        indexWriter = new IndexWriter(searchableObject.getIndexDirectory(), new SimpleAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
		    } catch (CorruptIndexException e) {
		        Logger.error(e);
		    } catch (LockObtainFailedException e) {
		        Logger.error(e);
		    } catch (IOException e) {
		        Logger.error(e);
		    }
		}

		private void updateSearchIndex(List<IAudioObject> audioObjects) {
		    Logger.info("update search index");
		    if (indexWriter != null) {
		        for (IAudioObject audioObject : audioObjects) {
		            Document d = searchableObject.getDocumentForElement(audioObject);
		            // Add dummy field
		            d.add(new Field(INDEX_FIELD_DUMMY, INDEX_FIELD_DUMMY, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));

		            try {
		                indexWriter.addDocument(d);
		            } catch (CorruptIndexException e) {
		                Logger.error(e);
		            } catch (IOException e) {
		                Logger.error(e);
		            }
		        }
		    }
		}

		private void finishSearchIndex() {
		    Logger.info(StringUtils.getString("Update index for ", searchableObject.getClass(), " finished"));
		    if (indexWriter != null) {
		        try {
		            indexWriter.optimize();
		            indexWriter.close();

		            indexWriter = null;
		        } catch (CorruptIndexException e) {
		            Logger.error(e);
		        } catch (IOException e) {
		            Logger.error(e);
		        }
		    }
		}
	}

	/**
     * Logical operators used to create complex rules.
     */
    public enum LogicalOperator {
        AND, OR, NOT
    }

    /** List of searchable objects available. */
    private List<ISearchableObject> searchableObjects = new ArrayList<ISearchableObject>();

    /** List of operators for simple rules. */
    private List<String> searchOperators;

    /**
     * List which indicates if an indexing process is currently running for a
     * given searchable object.
     */
    private volatile Map<ISearchableObject, Boolean> currentIndexingWorks = new HashMap<ISearchableObject, Boolean>();

    /** Map with locks for each index. */
    private Map<ISearchableObject, ReadWriteLock> indexLocks = new HashMap<ISearchableObject, ReadWriteLock>();

	private CustomSearchController customSearchController;

	private SearchResultsController searchResultsController;
    
    /**
     * Gets the custom search controller.
     * 
     * @return the custom search controller
     */
    private CustomSearchController getSearchController() {
        if (customSearchController == null) {
            customSearchController = new CustomSearchController(new CustomSearchDialog(getFrame().getFrame(), getBean(ILookAndFeelManager.class)), getState(), getFrame(), this);
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
            searchResultsController = new SearchResultsController(new SearchResultsDialog(getFrame().getFrame(), getBean(ILookAndFeelManager.class)), getState(), getBean(IPlayListHandler.class), getBean(ILookAndFeelManager.class), getBean(IPlayerHandler.class));
        }
        return searchResultsController;
    }

    @Override
    protected void initHandler() {
        searchOperators = new ArrayList<String>();
        searchOperators.add(":");
    }

    @Override
	public void registerSearchableObject(ISearchableObject so) {
        currentIndexingWorks.put(so, Boolean.FALSE);
        indexLocks.put(so, new ReentrantReadWriteLock(true));
        searchableObjects.add(so);
    }

    @Override
	public void unregisterSearchableObject(ISearchableObject so) {
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
	public List<IAudioObject> search(ISearchableObject searchableObject, String queryStr) throws SearchIndexNotAvailableException, SearchQuerySyntaxException {
        ReadWriteLock searchIndexLock = indexLocks.get(searchableObject);
        Searcher searcher = null;
        try {
            searchIndexLock.readLock().lock();

            String queryString = applyQueryTransformations(queryStr);

            Query query = new QueryParser(Version.LUCENE_30, DEFAULT_INDEX, new SimpleAnalyzer()).parse(queryString);
            searcher = new IndexSearcher(searchableObject.getIndexDirectory(), true);
            TopDocs topDocs = searcher.search(query, 1000);
            List<RawSearchResult> rawSearchResults = new ArrayList<RawSearchResult>();
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                rawSearchResults.add(new RawSearchResult(searcher.doc(scoreDoc.doc), scoreDoc.score));
            }
            List<IAudioObject> result = searchableObject.getSearchResult(rawSearchResults);
            Logger.debug("Query: ", queryString, " (", result.size(), " search results)");
            return result;
        } catch (IOException e) {
            throw new SearchIndexNotAvailableException();
        } catch (ParseException e) {
            throw new SearchQuerySyntaxException();
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
    private String applyQueryTransformations(String query) {
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
        SwingWorker<Void, Void> refreshSearchIndex = new RefreshSearchIndexSwingWorker(searchableObject);
        if (currentIndexingWorks.get(searchableObject) == null || !currentIndexingWorks.get(searchableObject)) {
            currentIndexingWorks.put(searchableObject, Boolean.TRUE);
            refreshSearchIndex.execute();
        }
    }

    @Override
    public void showSearchResults(ISearchableObject searchableObject, List<IAudioObject> result) {
        // Open search results dialog
        getSearchResultsController().showSearchResults(searchableObject, result);
    }

	@Override
	public void refreshSearchResultColumns() {
        ((SearchResultColumnModel) getSearchResultsController().getComponentControlled().getSearchResultsTable().getColumnModel())
        .arrangeColumns(false);
	}

	@Override
	public void deviceReady(String location) {
		registerSearchableObject(DeviceSearchableObject.getInstance());
	}
	
	@Override
	public void deviceDisconnected(String location) {
		unregisterSearchableObject(DeviceSearchableObject.getInstance());
	}
}
