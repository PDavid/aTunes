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

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IErrorReport;
import net.sourceforge.atunes.model.IErrorReportCreator;
import net.sourceforge.atunes.model.IErrorReportDialog;
import net.sourceforge.atunes.model.IErrorReporter;
import net.sourceforge.atunes.model.IStateCore;
import net.sourceforge.atunes.utils.CollectionUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Captures exceptions
 * 
 * @author alex
 * 
 */
public final class ExceptionHandler implements Thread.UncaughtExceptionHandler {

	private IDialogFactory dialogFactory;

	private List<KnownException> knownExceptions;

	private IBeanFactory beanFactory;

	private IStateCore stateCore;

	/**
	 * @param stateCore
	 */
	public void setStateCore(IStateCore stateCore) {
		this.stateCore = stateCore;
	}

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param knownExceptions
	 */
	public void setKnownExceptions(final List<KnownException> knownExceptions) {
		this.knownExceptions = knownExceptions;
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	@Override
	public void uncaughtException(final Thread t, final Throwable e) {
		Logger.error(StringUtils.getString("Thread: ", t.getName()));
		Logger.error(e);

		if (!isKnownException(e)) {
			if (e instanceof InvocationTargetException
					&& ((InvocationTargetException) e).getCause() != null) {
				uncaughtException(t, ((InvocationTargetException) e).getCause());
			} else {
				showErrorReport(e);
			}
		}
	}

	/**
	 * Shows error report and allows user to send a report
	 * 
	 * @param e
	 */
	public void showErrorReport(final Throwable e) {
		showErrorReport(null, e);
	}

	/**
	 * Shows error report and allows user to send a report
	 * 
	 * @param errorDescription
	 * @param e
	 */
	public void showErrorReport(final String errorDescription, final Throwable e) {
		GuiUtils.callInEventDispatchThread(new Runnable() {
			@Override
			public void run() {
				IErrorReport report = beanFactory.getBean(
						IErrorReportCreator.class).createReport(
						errorDescription, e);
				ExceptionHandler.this.dialogFactory.newDialog(
						IErrorReportDialog.class).showErrorReport(
						stateCore.getErrorReportsResponseMail(), report,
						beanFactory.getBean(IErrorReporter.class));
			}
		});
	}

	private boolean isKnownException(final Throwable e) {
		if (!CollectionUtils.isEmpty(this.knownExceptions)) {
			for (KnownException exception : this.knownExceptions) {
				if (isKnownException(e, exception)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param e
	 * @param exception
	 * @return
	 */
	private boolean isKnownException(final Throwable e,
			final KnownException exception) {
		return e.getClass().getName()
				.equalsIgnoreCase(exception.getExceptionClass())
				&& e.getMessage().equalsIgnoreCase(exception.getMessage());
	}
}