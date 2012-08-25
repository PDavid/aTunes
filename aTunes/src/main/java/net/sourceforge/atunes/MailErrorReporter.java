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

package net.sourceforge.atunes;

import java.io.IOException;

import net.sourceforge.atunes.model.ErrorReport;
import net.sourceforge.atunes.model.IApplicationStateGenerator;
import net.sourceforge.atunes.model.IErrorReporter;
import net.sourceforge.atunes.model.INetworkHandler;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.commons.codec.binary.Base64;

/**
 * Reports errors by mail
 * @author alex
 *
 */
public class MailErrorReporter implements IErrorReporter, Runnable {
	
	private IApplicationStateGenerator applicationStateGenerator;
	
	private INetworkHandler networkHandler;
	
	private ITaskService taskService;

	private ErrorReport report;
	
	private String url;
	
	/**
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * @param taskService
	 */
	public void setTaskService(ITaskService taskService) {
		this.taskService = taskService;
	}
	
	/**
	 * @param networkHandler
	 */
	public void setNetworkHandler(INetworkHandler networkHandler) {
		this.networkHandler = networkHandler;
	}
	
	/**
	 * @param applicationStateGenerator
	 */
	public void setApplicationStateGenerator(IApplicationStateGenerator applicationStateGenerator) {
		this.applicationStateGenerator = applicationStateGenerator;
	}
	
	@Override
	public void reportError(ErrorReport errorReport) {
		this.report = errorReport;
		taskService.submitNow("Report Error", this);
	}
	
	@Override
	public void run() {
		String reportString = Base64.encodeBase64String(report.toString().getBytes());
		String fullUrl = StringUtils.getString(url, reportString);
		try {
			networkHandler.readURL(networkHandler.getConnection(fullUrl));
		} catch (IOException e) {
			Logger.error(e);
		}
	}
	
	@Override
	public ErrorReport createReport(String descriptionError, Throwable throwable) {
		ErrorReport result = new ErrorReport();
		result.setState(applicationStateGenerator.generateState());
		result.setThrowable(throwable);
		result.setErrorDescrition(descriptionError);
		return result;
	}
}
