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

import net.sourceforge.atunes.model.IApplicationStateGenerator;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IErrorReport;
import net.sourceforge.atunes.model.IErrorReportCreator;
import net.sourceforge.atunes.model.IJavaVirtualMachineStatistic;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.IState;

/**
 * Creates error reports
 * 
 * @author alex
 * 
 */
public class ErrorReportCreator implements IErrorReportCreator {

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	@Override
	public IErrorReport createReport(final String descriptionError,
			final Throwable throwable) {
		IErrorReport result = new ErrorReport();
		result.setBasicEnvironmentState(this.beanFactory.getBean(
				IApplicationStateGenerator.class).generateState());
		result.setThrowable(throwable);
		result.setErrorDescription(descriptionError);

		for (IState state : this.beanFactory.getBeans(IState.class)) {
			result.addStateDescription(state.getClass().getName(),
					state.describeState());
		}

		for (IJavaVirtualMachineStatistic metric : this.beanFactory
				.getBeans(IJavaVirtualMachineStatistic.class)) {
			result.addJVMState(metric);
		}

		result.setRepositorySize(this.beanFactory.getBean(
				IRepositoryHandler.class).getNumberOfFiles());

		return result;
	}
}
