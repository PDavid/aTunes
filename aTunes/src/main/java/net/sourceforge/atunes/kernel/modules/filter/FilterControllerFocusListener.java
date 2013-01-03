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

package net.sourceforge.atunes.kernel.modules.filter;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

final class FilterControllerFocusListener implements FocusListener {
	
	private FilterController controller;
	
	/**
	 * @param controller
	 */
	public FilterControllerFocusListener(FilterController controller) {
		this.controller = controller;
	}
	
	@Override
	public void focusLost(FocusEvent e) {
		if (!controller.isFilterApplied()) {
			controller.getComponentControlled().getFilterTextField().getDocument().removeDocumentListener(controller.getListener());
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		if (!controller.isFilterApplied()) {
			controller.getComponentControlled().getFilterTextField().setText("");
			controller.getComponentControlled().getFilterTextField().getDocument().addDocumentListener(controller.getListener());
		}
	}
}