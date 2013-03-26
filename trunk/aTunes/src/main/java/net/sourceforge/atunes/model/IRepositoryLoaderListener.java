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

package net.sourceforge.atunes.model;


/**
 * The listener interface for receiving loader events.
 */
public interface IRepositoryLoaderListener {

    /**
     * Notify current path.
     * 
     * @param path
     *            the path
     */
    public void notifyCurrentPath(String path);
    
    /**
     * Notifies a new artist / album found
     * @param artist
     * @param album
     */
    public void notifyCurrentAlbum(String artist, String album);

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
    public void notifyFinishRead(IRepositoryLoader loader);

    /**
     * Notify finish refresh.
     * 
     * @param loader
     *            the loader
     */
    public void notifyFinishRefresh(IRepositoryLoader loader);

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
