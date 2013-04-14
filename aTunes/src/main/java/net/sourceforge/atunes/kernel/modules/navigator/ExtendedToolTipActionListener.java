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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.sourceforge.atunes.model.IBeanFactory;

/**
 * Listener to show navigator tree tool tip
 * 
 * @author alex
 * 
 */
public final class ExtendedToolTipActionListener implements ActionListener {

	private IBeanFactory beanFactory;

	private ExtendedTooltipContent extendedTooltipContent;

	/**
	 * @param extendedTooltipContent
	 */
	public void setExtendedTooltipContent(
			final ExtendedTooltipContent extendedTooltipContent) {
		this.extendedTooltipContent = extendedTooltipContent;
	}

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	@Override
	public void actionPerformed(final ActionEvent arg0) {
		this.extendedTooltipContent.setVisible(true);
		ExtendedToolTipGetAndSetImageBackgroundWorker worker = this.beanFactory
				.getBean(ExtendedToolTipGetAndSetImageBackgroundWorker.class);
		worker.setCurrentObject(this.extendedTooltipContent
				.getCurrentExtendedToolTipContent());
		worker.execute();
	}
}