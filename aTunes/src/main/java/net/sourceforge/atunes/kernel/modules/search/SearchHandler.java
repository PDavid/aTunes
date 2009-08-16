/*
 * aTunes 1.14.0
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

package net.sourceforge.atunes.kernel.modules.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.swing.SwingWorker;

import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;
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

/**
 * The Class SearchHandler.
 */
public final class SearchHandler {

    /** Logger. */
    static Logger logger = new Logger();

    /** Default lucene field. */
    public static final String DEFAULT_INDEX = "any";

    /** Dummy lucene field to retrieve all elements. */
    private static final String INDEX_FIELD_DUMMY = "dummy";

    /** Singleton instance. */
    private static SearchHandler instance = new SearchHandler();

    /**
     * Logical operators used to create complex rules.
     */
    public enum LogicalOperator {

        /** The AND. */
        AND,

        /** The OR. */
        OR,

        /** The NOT. */
        NOT
    }

    /** List of searchable objects available. */
    private List<SearchableObject> searchableObjects;

    /** List of operators for simple rules. */
    private List<String> searchOperators;

    /**
     * List which indicates if an indexing process is currently running for a
     * given searchable object.
     */
    volatile Map<SearchableObject, Boolean> currentIndexingWorks;

    /** Map with locks for each index. */
    Map<SearchableObject, ReadWriteLock> indexLocks;

    /**
     * Constructor.
     */
    private SearchHandler() {
        currentIndexingWorks = new HashMap<SearchableObject, Boolean>();
        searchableObjects = new ArrayList<SearchableObject>();
        indexLocks = new HashMap<SearchableObject, ReadWriteLock>();

        searchOperators = new ArrayList<String>();
        searchOperators.add(":");
    }

    /**
     * Method to register a searchable object. All searchable objects must call
     * this method to initialize searches
     * 
     * @param so
     *            the so
     */
    public void registerSearchableObject(SearchableObject so) {
        currentIndexingWorks.put(so, Boolean.FALSE);
        indexLocks.put(so, new ReentrantReadWriteLock(true));
        searchableObjects.add(so);
    }

    /**
     * Method to unregister a searchable object. After this method is called,
     * searchable object is not searchable, and must be registered again
     * 
     * @param so
     */
    public void unregisterSearchableObject(SearchableObject so) {
        currentIndexingWorks.remove(so);
        indexLocks.remove(so);
        searchableObjects.remove(so);
    }

    /**
     * Returns SearchHandler unique instance.
     * 
     * @return the instance
     */
    public static SearchHandler getInstance() {
        return instance;
    }

    /**
     * Starts a search by showing search dialog.
     */
    public void startSearch() {
        // Set list of searchable objects
        ControllerProxy.getInstance().getCustomSearchController().setListOfSearchableObjects(searchableObjects);

        // Set list of operators
        ControllerProxy.getInstance().getCustomSearchController().setListOfOperators(searchOperators);

        ControllerProxy.getInstance().getCustomSearchController().showSearchDialog();
    }

    /**
     * Evaluates a search query and returns a result list of audio objects and
     * scores (0 <= score <= 1).
     * 
     * @param queryStr
     *            The search query
     * @param searchableObject
     *            the searchable object
     * 
     * @return The search result
     * 
     * @throws SearchIndexNotAvailableException
     *             If no search index was found
     * @throws SearchQuerySyntaxException
     *             If the search query has invalid syntax
     */
    public List<SearchResult> search(SearchableObject searchableObject, String queryStr) throws SearchIndexNotAvailableException, SearchQuerySyntaxException {
        ReadWriteLock searchIndexLock = indexLocks.get(searchableObject);
        try {
            searchIndexLock.readLock().lock();

            String queryString = applyQueryTransformations(queryStr);

            Query query = new QueryParser(DEFAULT_INDEX, new SimpleAnalyzer()).parse(queryString);
            Searcher searcher = new IndexSearcher(searchableObject.getPathToIndex());
            TopDocs topDocs = searcher.search(query, 1000);
            List<RawSearchResult> rawSearchResults = new ArrayList<RawSearchResult>();
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                rawSearchResults.add(new RawSearchResult(searcher.doc(scoreDoc.doc), scoreDoc.score));
            }
            List<SearchResult> result = searchableObject.getSearchResult(rawSearchResults);
            logger.debug(LogCategories.REPOSITORY, "Query: " + queryString + " (" + result.size() + " search results)");
            return result;
        } catch (IOException e) {
            throw new SearchIndexNotAvailableException();
        } catch (ParseException e) {
            throw new SearchQuerySyntaxException();
        } finally {
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

    public void updateSearchIndex(final SearchableObject searchableObject) {
        SwingWorker<Void, Void> refreshSearchIndex = new SwingWorker<Void, Void>() {
            private IndexWriter indexWriter;

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
                    currentIndexingWorks.put(searchableObject, Boolean.FALSE);
                }
            }

            @Override
            protected void done() {
                // Nothing to do
            }

            private void initSearchIndex() {
                logger.info(LogCategories.HANDLER, "Updating index for " + searchableObject.getClass());
                try {
                    FileUtils.deleteDirectory(new File(searchableObject.getPathToIndex()));
                    indexWriter = new IndexWriter(searchableObject.getPathToIndex(), new SimpleAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
                } catch (CorruptIndexException e) {
                    logger.error(LogCategories.HANDLER, e);
                } catch (LockObtainFailedException e) {
                    logger.error(LogCategories.HANDLER, e);
                } catch (IOException e) {
                    logger.error(LogCategories.HANDLER, e);
                }
            }

            private void updateSearchIndex(List<AudioObject> audioObjects) {
                logger.info(LogCategories.HANDLER, "update search index");
                if (indexWriter != null) {
                    for (AudioObject audioObject : audioObjects) {
                        Document d = searchableObject.getDocumentForElement(audioObject);
                        // Add dummy field
                        d.add(new Field(INDEX_FIELD_DUMMY, INDEX_FIELD_DUMMY, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));

                        try {
                            indexWriter.addDocument(d);
                        } catch (CorruptIndexException e) {
                            logger.error(LogCategories.HANDLER, e);
                        } catch (IOException e) {
                            logger.error(LogCategories.HANDLER, e);
                        }
                    }
                }
            }

            private void finishSearchIndex() {
                logger.info(LogCategories.HANDLER, StringUtils.getString("Update index for ", searchableObject.getClass(), " finished"));
                if (indexWriter != null) {
                    try {
                        indexWriter.optimize();
                        indexWriter.close();
                    } catch (CorruptIndexException e) {
                        logger.error(LogCategories.HANDLER, e);
                    } catch (IOException e) {
                        logger.error(LogCategories.HANDLER, e);
                    }
                }
            }

        };
        if (currentIndexingWorks.get(searchableObject) == null || !currentIndexingWorks.get(searchableObject)) {
            currentIndexingWorks.put(searchableObject, Boolean.TRUE);
            refreshSearchIndex.execute();
        }

    }

}
