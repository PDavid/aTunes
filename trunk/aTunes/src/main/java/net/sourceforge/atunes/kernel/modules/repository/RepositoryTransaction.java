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

package net.sourceforge.atunes.kernel.modules.repository;

import net.sourceforge.atunes.model.IRepository;
import net.sourceforge.atunes.model.IRepositoryListener;
import net.sourceforge.atunes.utils.Logger;

import org.joda.time.DateTime;

public final class RepositoryTransaction {
	
	private IRepository repository;
	private IRepositoryListener listener;
	private volatile boolean pending;
	
	public RepositoryTransaction(IRepository repository, IRepositoryListener listener) {
		this.repository = repository;
		this.listener = listener;
		this.pending = true;
		Logger.debug("Creating new repository transaction: ", new DateTime().toString());
	}
	
	public void finishTransaction() {
		if (listener != null) {
			listener.repositoryChanged(this.repository);
		}
		this.pending = false;
		Logger.debug("Finished repository transaction: ", new DateTime().toString());
	}
	
	public boolean isPending() {
		return this.pending;
	}
}