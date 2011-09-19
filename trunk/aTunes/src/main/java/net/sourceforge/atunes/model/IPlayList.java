/*
 * aTunes 2.1.0-SNAPSHOT
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

import java.io.Serializable;


public interface IPlayList extends Serializable, Cloneable {

	public void setState(IState state);

	/**
	 * Moves one row in play list
	 * 
	 * @param sourceRow
	 * @param targetRow
	 */
	public void moveRowTo(int sourceRow, int targetRow);

	/**
	 * Returns first index position of given audio object
	 * 
	 * @param audioObject
	 * @return
	 */
	public int indexOf(IAudioObject audioObject);

	/**
	 * Returns size of this play list
	 * 
	 * @return
	 */
	public int size();

	/**
	 * Returns <code>true</code> if this play list is empty
	 * 
	 * @return
	 */
	public boolean isEmpty();

	/**
	 * Returns AudioObject at given index
	 * 
	 * @param index
	 * @return
	 */
	public IAudioObject get(int index);

	/**
	 * Returns <code>true</code> if audio object is in play list
	 * 
	 * @param audioObject
	 * @return
	 */
	public boolean contains(IAudioObject audioObject);

	public IAudioObject getNextAudioObject(int index);

	public IAudioObject getPreviousAudioObject(int index);

	/**
	 * Returns play list length in string format.
	 * 
	 * @return the length
	 */
	public String getLength();

	/**
	 * Resets play list
	 */
	public void reset();

}