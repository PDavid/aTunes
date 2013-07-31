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

import java.util.Collection;

import net.sourceforge.atunes.model.IApplicationArguments;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IKernel;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Access beans of application context
 * 
 * @author alex
 * 
 */
public class BeanFactory implements IBeanFactory, ApplicationContextAware {

	private ApplicationContext context;

	private IApplicationArguments applicationArguments;

	/**
	 * @param applicationArguments
	 */
	public void setApplicationArguments(
			final IApplicationArguments applicationArguments) {
		this.applicationArguments = applicationArguments;
	}

	@Override
	public void setApplicationContext(final ApplicationContext context) {
		this.context = context;
	}

	@Override
	public <T> T getBean(final Class<T> beanType) {
		try {
			return this.context.getBean(beanType);
		} catch (BeansException e) {
			if (this.applicationArguments.isDebug()) {
				this.context.getBean(IKernel.class).terminateWithError(e);
			}
			throw e;
		}
	}

	@Override
	public <T> T getBean(final String name, final Class<T> clazz) {
		try {
			return this.context.getBean(name, clazz);
		} catch (BeansException e) {
			if (this.applicationArguments.isDebug()) {
				this.context.getBean(IKernel.class).terminateWithError(e);
			}
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getBeanByClassName(final String className, final Class<T> clazz) {
		try {
			Class<?> classLoaded = Class.forName(className);
			return (T) this.context.getBean(classLoaded);
		} catch (BeansException e) {
			if (this.applicationArguments.isDebug()) {
				this.context.getBean(IKernel.class).terminateWithError(e);
			}
			throw e;
		} catch (ClassNotFoundException e) {
			if (this.applicationArguments.isDebug()) {
				this.context.getBean(IKernel.class).terminateWithError(e);
			}
			return null;
		}
	}

	@Override
	public <T> Collection<T> getBeans(final Class<T> beanType) {
		try {
			return this.context.getBeansOfType(beanType).values();
		} catch (BeansException e) {
			if (this.applicationArguments.isDebug()) {
				this.context.getBean(IKernel.class).terminateWithError(e);
			}
			throw e;
		}
	}
}
