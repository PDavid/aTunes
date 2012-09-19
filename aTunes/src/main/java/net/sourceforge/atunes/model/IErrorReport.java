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

import java.util.Map;

/**
 * Description of an error
 * @author alex
 *
 */
public interface IErrorReport {

	/**
	 * @return the errorDescrition
	 */
	public String getErrorDescrition();

	/**
	 * @param errorDescription
	 */
	public void setErrorDescription(String errorDescription);

	/**
	 * @return the throwable
	 */
	public Throwable getThrowable();

	/**
	 * @param throwable the throwable to set
	 */
	public void setThrowable(Throwable throwable);

	/**
	 * @return the state
	 */
	public Map<String, String> getState();

	/**
	 * @param state the state to set
	 */
	public void setState(Map<String, String> state);

}