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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.atunes.model.IErrorReport;
import net.sourceforge.atunes.model.IErrorReporter;
import net.sourceforge.atunes.model.INetworkHandler;
import net.sourceforge.atunes.model.IStateCore;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.utils.Logger;

/**
 * Reports errors by mail
 * 
 * @author alex
 * 
 */
public class MailErrorReporter implements IErrorReporter, Runnable {

	private INetworkHandler networkHandler;

	private ITaskService taskService;

	private IErrorReport report;

	private IStateCore stateCore;

	private String url;

	/**
	 * @param stateCore
	 */
	public void setStateCore(IStateCore stateCore) {
		this.stateCore = stateCore;
	}

	/**
	 * @param url
	 */
	public void setUrl(final String url) {
		this.url = url;
	}

	/**
	 * @param taskService
	 */
	public void setTaskService(final ITaskService taskService) {
		this.taskService = taskService;
	}

	/**
	 * @param networkHandler
	 */
	public void setNetworkHandler(final INetworkHandler networkHandler) {
		this.networkHandler = networkHandler;
	}

	@Override
	public void reportError(String mail, final IErrorReport errorReport) {
		this.stateCore.setErrorReportsResponseMail(mail);
		this.report = errorReport;
		this.report.setResponseMail(mail);
		taskService.submitNow("Report Error", this);
	}

	@Override
	public void run() {
		try {
			Map<String, String> map = new HashMap<String, String>();
			map.put("report", report.toString());
			map.put("build", Integer.toString(BuildNumber.getBuildNumber()));
			map.put("version", Constants.VERSION.toString());
			networkHandler.readURL(url, map, "UTF-8");
		} catch (IOException e) {
			Logger.error(e);
		}
	}
}
