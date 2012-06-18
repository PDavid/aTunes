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
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;

import javax.swing.SwingWorker;

import net.sourceforge.atunes.model.IAudioObject;
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
import org.apache.lucene.store.LockObtainFailedException;

final class RefreshSearchIndexSwingWorker extends SwingWorker<Void, Void> {
	
    private final Map<ISearchableObject, Boolean> currentIndexingWorks;

    private final Map<ISearchableObject, ReadWriteLock> indexLocks;

	private final ISearchableObject searchableObject;
	private IndexWriter indexWriter;

	/**
	 * @param currentIndexingWorks
	 * @param indexLocks
	 * @param searchableObject
	 */
	RefreshSearchIndexSwingWorker(Map<ISearchableObject, Boolean> currentIndexingWorks, Map<ISearchableObject, ReadWriteLock> indexLocks, ISearchableObject searchableObject) {
		this.currentIndexingWorks = currentIndexingWorks;
		this.indexLocks = indexLocks;
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
	            d.add(new Field(SearchHandler.INDEX_FIELD_DUMMY, SearchHandler.INDEX_FIELD_DUMMY, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));

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