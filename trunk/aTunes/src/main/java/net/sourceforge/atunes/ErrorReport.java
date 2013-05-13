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

package net.sourceforge.atunes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.atunes.model.IErrorReport;
import net.sourceforge.atunes.model.IJavaVirtualMachineStatistic;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Description of an error
 * 
 * @author alex
 * 
 */
public class ErrorReport implements IErrorReport {

	private String responseMail;

	private String errorDescription;

	private Throwable throwable;

	private Map<String, String> state;

	private Map<String, Map<String, String>> states;

	private Map<String, String> jvmMetrics;

	private int repositorySize;

	/**
	 * @return the errorDescrition
	 */
	@Override
	public String getErrorDescrition() {
		return this.errorDescription;
	}

	/**
	 * @param errorDescription
	 */
	@Override
	public void setErrorDescription(final String errorDescription) {
		this.errorDescription = errorDescription;
	}

	/**
	 * @return the throwable
	 */
	@Override
	public Throwable getThrowable() {
		return this.throwable;
	}

	/**
	 * @param throwable
	 *            the throwable to set
	 */
	@Override
	public void setThrowable(final Throwable throwable) {
		this.throwable = throwable;
	}

	/**
	 * @return the state
	 */
	@Override
	public Map<String, String> getState() {
		return this.state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	@Override
	public void setBasicEnvironmentState(final Map<String, String> state) {
		this.state = state;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ERROR REPORT\n");
		sb.append("\n---------------------------------------------\n");
		sb.append(StringUtils.getString("RESPONSE MAIL: ", responseMail, "\n"));
		sb.append("\n---------------------------------------------\n");
		sb.append(StringUtils.getString("Message: ", StringUtils
				.isEmpty(this.errorDescription) ? "Unknown error"
				: this.errorDescription));
		sb.append("\n---------------------------------------------\n");
		sb.append(getStateString(this.state));
		sb.append("\n---------------------------------------------\n");
		if (this.jvmMetrics != null) {
			for (Map.Entry<String, String> metric : jvmMetrics.entrySet()) {
				sb.append(StringUtils.getString(metric.getKey(), ": ",
						metric.getValue(), "\n"));
			}
		}
		sb.append("\n---------------------------------------------\n");
		sb.append(StringUtils.getString("Repository size: ",
				this.repositorySize, "\n"));
		sb.append("\n---------------------------------------------\n");
		sb.append(getThrowableString());
		sb.append("\n---------------------------------------------\n");
		if (this.states != null) {
			sb.append("\n---------------------------------------------\n");
			sb.append("FULL STATE:\n");
			for (Map.Entry<String, Map<String, String>> state : this.states
					.entrySet()) {
				sb.append("\n---------------------------------------------\n");
				sb.append("STATE: ").append(state.getKey());
				sb.append("\n---------------------------------------------\n");
				if (state.getValue() != null) {
					for (Map.Entry<String, String> descEntry : state.getValue()
							.entrySet()) {
						sb.append(StringUtils.getString(descEntry.getKey(),
								" = ", descEntry.getValue(), "\n"));
					}
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Returns string information about throwable
	 * 
	 * @return
	 */
	private String getThrowableString() {
		List<String> values = new ArrayList<String>();
		values.add(StringUtils.getString(this.throwable.getClass()
				.getCanonicalName(), ": ", this.throwable.getMessage()));
		for (StackTraceElement ste : this.throwable.getStackTrace()) {
			values.add(StringUtils.getString(ste.toString()));
		}
		Throwable cause = this.throwable.getCause();
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
	 * 
	 * @param state
	 * @return
	 */
	private String getStateString(final Map<String, String> state) {
		List<String> values = new ArrayList<String>();
		for (Entry<String, String> key : state.entrySet()) {
			values.add(StringUtils.getString(key.getKey(), "=", key.getValue()));
		}
		return org.apache.commons.lang.StringUtils.join(values, "\n");
	}

	@Override
	public void addStateDescription(final String state,
			final Map<String, String> description) {
		if (this.states == null) {
			this.states = new HashMap<String, Map<String, String>>();
		}
		this.states.put(state, description);
	}

	@Override
	public void addJVMState(IJavaVirtualMachineStatistic metric) {
		if (this.jvmMetrics == null) {
			this.jvmMetrics = new HashMap<String, String>();
		}
		this.jvmMetrics.put(metric.getDescription(), metric.getValue());
	}

	@Override
	public void setRepositorySize(int numberOfFiles) {
		this.repositorySize = numberOfFiles;
	}

	@Override
	public void setResponseMail(String mail) {
		this.responseMail = mail;
	}

	@Override
	public String getResponseMail() {
		return this.responseMail;
	}
}
