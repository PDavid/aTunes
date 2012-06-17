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

import java.awt.Window;

import net.sourceforge.atunes.gui.views.dialogs.IndeterminateProgressDialog;
import net.sourceforge.atunes.model.IDialog;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IIndeterminateProgressDialog;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class DialogFactory implements IDialogFactory, ApplicationContextAware {
	
	private ApplicationContext context;
	
	private IFrame frame;
	
	/**
	 * @param frame
	 */
	public void setFrame(IFrame frame) {
		this.frame = frame;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.context = applicationContext;
	}
	
	@Override
	public <T extends IDialog> T newDialog(Class<T> dialogClass) {
		return context.getBean(dialogClass);
	}
	
	@Override
	public <T extends IDialog> T newDialog(String dialogName, Class<T> dialogClass) {
		return context.getBean(dialogName, dialogClass);
	}
	
	@Override
	public IIndeterminateProgressDialog newIndeterminateProgressDialog(Window parent) {
		return new IndeterminateProgressDialog(parent);
	}

	@Override
	public IIndeterminateProgressDialog newIndeterminateProgressDialog() {
		return new IndeterminateProgressDialog(frame);
	}
}
