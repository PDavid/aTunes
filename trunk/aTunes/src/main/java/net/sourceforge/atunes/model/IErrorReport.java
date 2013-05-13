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

import java.util.Map;

/**
 * Description of an error
 * 
 * @author alex
 * 
 */
public interface IErrorReport {

	/**
	 * @param mail
	 * @return
	 */
	void setResponseMail(String mail);

	/**
	 * @return response mail
	 */
	String getResponseMail();

	/**
	 * @return the errorDescrition
	 */
	String getErrorDescrition();

	/**
	 * @param errorDescription
	 */
	void setErrorDescription(String errorDescription);

	/**
	 * @return the throwable
	 */
	Throwable getThrowable();

	/**
	 * @param throwable
	 *            the throwable to set
	 */
	void setThrowable(Throwable throwable);

	/**
	 * @return the state
	 */
	Map<String, String> getState();

	/**
	 * @param state
	 *            the state to set
	 */
	void setBasicEnvironmentState(Map<String, String> state);

	/**
	 * @param state
	 * @param description
	 */
	void addStateDescription(String state, Map<String, String> description);

	/**
	 * Add a JVM metric
	 * 
	 * @param metric
	 */
	void addJVMState(IJavaVirtualMachineStatistic metric);

	/**
	 * Set number of files
	 * 
	 * @param numberOfFiles
	 */
	void setRepositorySize(int numberOfFiles);

}