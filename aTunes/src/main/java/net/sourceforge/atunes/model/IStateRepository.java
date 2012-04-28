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

package net.sourceforge.atunes.model;

public interface IStateRepository {

	/**
	 * used in RepositoryFiller to build repository structure keys case sensitive or not
	 * @return true if case structures genre and artist handled sensitive, default = false for convenience
	 */
	public boolean isKeyAlwaysCaseSensitiveInRepositoryStructure();

	/**
	 * enable case sensitive tree structure of artist and genre or merge keys case insensitive
	 * @param caseSensitiveRepositoryStructureKeys
	 */
	public void setKeyAlwaysCaseSensitiveInRepositoryStructure(boolean caseSensitiveRepositoryStructureKeys);
	
	/**
	 * Refresh repository automatically
	 * @return
	 */
	public int getAutoRepositoryRefreshTime();

	/**
	 * Refresh repository automatically
	 * @param autoRepositoryRefreshTime
	 */
	public void setAutoRepositoryRefreshTime(int autoRepositoryRefreshTime);


}
