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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.atunes.utils.StringUtils;

/**
 * Description of an error
 * @author alex
 *
 */
public class ErrorReport {

	private String errorDescrition;
	
	private Throwable throwable;
	
	private Map<String, String> state;

	/**
	 * @return the errorDescrition
	 */
	public String getErrorDescrition() {
		return errorDescrition;
	}

	/**
	 * @param errorDescrition the errorDescrition to set
	 */
	public void setErrorDescrition(String errorDescrition) {
		this.errorDescrition = errorDescrition;
	}

	/**
	 * @return the throwable
	 */
	public Throwable getThrowable() {
		return throwable;
	}

	/**
	 * @param throwable the throwable to set
	 */
	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}

	/**
	 * @return the state
	 */
	public Map<String, String> getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(Map<String, String> state) {
		this.state = state;
	}
	
	@Override
	public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("ERROR REPORT\n");
    	sb.append("\n---------------------------------------------\n");
    	sb.append(StringUtils.getString("Message: ", StringUtils.isEmpty(errorDescrition) ? "Unknown error" : errorDescrition));
    	sb.append("\n---------------------------------------------\n");
    	sb.append(getStateString(state));
    	sb.append("\n---------------------------------------------\n");
    	sb.append(getThrowableString(throwable));
    	return sb.toString();
	}
	
	/**
	 * Returns string information about throwable
	 * @param throwble
	 * @return
	 */
	private String getThrowableString(Throwable throwble) {
		List<String> values = new ArrayList<String>();
		values.add(StringUtils.getString(throwable.getClass().getCanonicalName(), ": ", throwable.getMessage()));
		for (StackTraceElement ste : throwable.getStackTrace()) {
			values.add(StringUtils.getString(ste.toString()));
		}
		Throwable cause = throwable.getCause();
		if (cause != null && cause.getStackTrace() != null) {
			values.add("Cause: ");
			for (StackTraceElement ste : cause.getStackTrace()) {
				values.add(StringUtils.getString(ste.toString()));
			}
			
		}
		return org.apache.commons.lang.StringUtils.join(values, "\n");
	}
	
	/**
	 * Returns string in format "key1=value1\nkey2=value2\nkey3=value3"
	 * @param state
	 * @return
	 */
	private String getStateString(Map<String, String> state) {
		List<String> values = new ArrayList<String>();
		for (Entry<String, String> key : state.entrySet()) {
			values.add(StringUtils.getString(key.getKey(), "=", key.getValue()));
		}
		return org.apache.commons.lang.StringUtils.join(values, "\n");
	}
}
