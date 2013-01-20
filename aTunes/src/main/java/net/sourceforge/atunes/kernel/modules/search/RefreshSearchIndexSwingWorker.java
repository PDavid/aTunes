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
	private final AudioObjectIndexCreator audioObjectIndexCreator;

	/**
	 * @param currentIndexingWorks
	 * @param indexLocks
	 * @param searchableObject
	 * @param audioObjectIndexCreator
	 */
	RefreshSearchIndexSwingWorker(
			final Map<ISearchableObject, Boolean> currentIndexingWorks,
			final Map<ISearchableObject, ReadWriteLock> indexLocks,
			final ISearchableObject searchableObject,
			final AudioObjectIndexCreator audioObjectIndexCreator) {
		this.currentIndexingWorks = currentIndexingWorks;
		this.indexLocks = indexLocks;
		this.searchableObject = searchableObject;
		this.audioObjectIndexCreator = audioObjectIndexCreator;
		Logger.debug(this.getClass().getName(), " for searchable object: ",
				searchableObject.getClass().getName());
	}

	@Override
	protected Void doInBackground() {
		ReadWriteLock searchIndexLock = this.indexLocks
				.get(this.searchableObject);
		try {
			searchIndexLock.writeLock().lock();
			initSearchIndex();
			updateSearchIndex(this.searchableObject.getElementsToIndex());
			finishSearchIndex();
			return null;
		} finally {
			searchIndexLock.writeLock().unlock();
			ClosingUtils.close(this.indexWriter);
			this.currentIndexingWorks.put(this.searchableObject, Boolean.FALSE);
		}
	}

	@Override
	protected void done() {
		// Nothing to do
	}

	private void initSearchIndex() {
		Logger.info("Updating index for " + this.searchableObject.getClass());
		try {
			FileUtils.deleteDirectory(this.searchableObject.getIndexDirectory()
					.getFile());
			this.indexWriter = new IndexWriter(
					this.searchableObject.getIndexDirectory(),
					new SimpleAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
		} catch (CorruptIndexException e) {
			Logger.error(e);
		} catch (LockObtainFailedException e) {
			Logger.error(e);
		} catch (IOException e) {
			Logger.error(e);
		}
	}

	private void updateSearchIndex(final List<IAudioObject> audioObjects) {
		Logger.info("update search index");
		if (this.indexWriter != null) {
			for (IAudioObject audioObject : audioObjects) {
				Document d = this.audioObjectIndexCreator
						.getDocumentForElement(audioObject);
				// Add dummy field
				d.add(new Field(SearchHandler.INDEX_FIELD_DUMMY,
						SearchHandler.INDEX_FIELD_DUMMY, Field.Store.YES,
						Field.Index.NOT_ANALYZED_NO_NORMS));

				try {
					this.indexWriter.addDocument(d);
				} catch (CorruptIndexException e) {
					Logger.error(e);
				} catch (IOException e) {
					Logger.error(e);
				}
			}
		}
	}

	private void finishSearchIndex() {
		Logger.info(StringUtils.getString("Update index for ",
				this.searchableObject.getClass(), " finished"));
		if (this.indexWriter != null) {
			try {
				this.indexWriter.optimize();
				this.indexWriter.close();

				this.indexWriter = null;
			} catch (CorruptIndexException e) {
				Logger.error(e);
			} catch (IOException e) {
				Logger.error(e);
			}
		}
	}
}