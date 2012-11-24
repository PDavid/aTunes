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

package net.sourceforge.atunes.gui;

import net.sourceforge.atunes.model.IDialog;
import net.sourceforge.atunes.model.IDialogFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * A factory of dialogs
 * 
 * @author alex
 * 
 */
public class DialogFactory implements IDialogFactory, ApplicationContextAware {

	private ApplicationContext context;

	@Override
	public void setApplicationContext(
			final ApplicationContext applicationContext) {
		this.context = applicationContext;
	}

	@Override
	public <T extends IDialog> T newDialog(final Class<T> dialogClass) {
		return this.context.getBean(dialogClass);
	}

	@Override
	public <T extends IDialog> T newDialog(final String dialogName,
			final Class<T> dialogClass) {
		return this.context.getBean(dialogName, dialogClass);
	}
}
