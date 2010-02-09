/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

/**
 * The listener interface for receiving loader events.
 */
public interface LoaderListener {

    /**
     * Notify current path.
     * 
     * @param path
     *            the path
     */
    public void notifyCurrentPath(String path);

    /**
     * Notify file loaded.
     */
    public void notifyFileLoaded();

    /**
     * Notify files in repository.
     * 
     * @param files
     *            the files
     */
    public void notifyFilesInRepository(int files);

    /**
     * Notify finish read.
     * 
     * @param loader
     *            the loader
     */
    public void notifyFinishRead(RepositoryLoader loader);

    /**
     * Notify finish refresh.
     * 
     * @param loader
     *            the loader
     */
    public void notifyFinishRefresh(RepositoryLoader loader);

    /**
     * Notify remaining time.
     * 
     * @param time
     *            the time
     */
    public void notifyRemainingTime(long time);

    /**
     * Notify read progress (not refresh)
     */
    public void notifyReadProgress();
}
