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

package net.sourceforge.atunes.kernel.modules.repository;

import java.util.Collection;

import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IRepositoryListener;
import net.sourceforge.atunes.model.IRepositoryTransaction;
import net.sourceforge.atunes.utils.Logger;

import org.joda.time.DateTime;

/**
 * A transaction to make changes in repository
 * 
 * @author alex
 * 
 */
final class RepositoryTransaction implements IRepositoryTransaction {

	private final IRepository repository;
	private final Collection<IRepositoryListener> listeners;
	private volatile boolean pending;

	/**
	 * Creates and starts a new repository transaction
	 * 
	 * @param repository
	 * @param listeners
	 */
	public RepositoryTransaction(final IRepository repository,
			final Collection<IRepositoryListener> listeners) {
		this.repository = repository;
		this.listeners = listeners;
		this.pending = true;
		Logger.debug("Creating new repository transaction: ",
				new DateTime().toString());
	}

	/**
	 * Called when transaction is finished
	 */
	@Override
	public void finishTransaction() {
		if (this.listeners != null) {
			for (IRepositoryListener listener : this.listeners) {
				listener.repositoryChanged(this.repository);
			}
		}
		this.pending = false;
		Logger.debug("Finished repository transaction: ",
				new DateTime().toString());
	}

	/**
	 * Returns if transaction has finished (returns false) or not (returns true)
	 * 
	 * @return
	 */
	@Override
	public boolean isPending() {
		return this.pending;
	}
}