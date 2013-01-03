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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IStateNavigation;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.model.ITreeObject;

/**
 * The listener interface for receiving navigationTreeToolTip events.
 */
public final class NavigationTreeToolTipListener extends MouseAdapter {

	private final NavigationController controller;

	private final INavigationHandler navigationHandler;

	private final IStateNavigation stateNavigation;

	private final ExtendedTooltipContent tooltipContent;

	/**
	 * Instantiates a new navigation tree tool tip listener.
	 * 
	 * @param controller
	 * @param stateNavigation
	 * @param navigationHandler
	 * @param tooltipContent
	 */
	public NavigationTreeToolTipListener(final NavigationController controller,
			final IStateNavigation stateNavigation,
			final INavigationHandler navigationHandler,
			final ExtendedTooltipContent tooltipContent) {
		this.controller = controller;
		this.stateNavigation = stateNavigation;
		this.navigationHandler = navigationHandler;
		this.tooltipContent = tooltipContent;
	}

	@Override
	public void mouseDragged(final MouseEvent arg0) {
		if (!this.stateNavigation.isShowExtendedTooltip()) {
			return;
		}

		this.tooltipContent.setCurrentExtendedToolTipContent(null);
		this.tooltipContent.setVisible(false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void mouseMoved(final MouseEvent e) {
		if (!this.stateNavigation.isShowExtendedTooltip()) {
			return;
		}

		ITreeNode node = this.navigationHandler.getCurrentView().getTree()
				.getSelectedNode(e);
		if (node != null) {
			final Object content = node.getUserObject();

			if (content.equals(this.tooltipContent
					.getCurrentExtendedToolTipContent())) {
				return;
			}

			// Show extended tooltip
			if (this.tooltipContent.canObjectBeShownInExtendedToolTip(content)) {
				if (!this.tooltipContent.isVisible()
						|| this.tooltipContent
								.getCurrentExtendedToolTipContent() == null
						|| this.tooltipContent
								.getCurrentExtendedToolTipContent() != content) {
					if (this.tooltipContent.isVisible()) {
						this.tooltipContent.setVisible(false);
					}
					this.tooltipContent.setLocation(
							(int) this.navigationHandler.getCurrentView()
									.getTree().getLocationOnScreen().getX()
									+ e.getX(), (int) this.navigationHandler
									.getCurrentView().getTree()
									.getLocationOnScreen().getY()
									+ e.getY() + 20);

					this.tooltipContent
							.setToolTipContent((ITreeObject<? extends IAudioObject>) content);
					this.tooltipContent
							.setCurrentExtendedToolTipContent(content);
				} else {
					this.tooltipContent.setCurrentExtendedToolTipContent(null);
				}

				this.controller.getToolTipTimer().setInitialDelay(
						this.stateNavigation.getExtendedTooltipDelay() * 1000);
				this.controller.getToolTipTimer().setRepeats(false);
				this.controller.getToolTipTimer().start();
			} else {
				this.tooltipContent.setCurrentExtendedToolTipContent(null);
				this.tooltipContent.setVisible(false);
				this.controller.getToolTipTimer().stop();
			}
		} else {
			this.tooltipContent.setCurrentExtendedToolTipContent(null);
			this.tooltipContent.setVisible(false);
			this.controller.getToolTipTimer().stop();
		}
	}

	@Override
	public void mouseWheelMoved(final MouseWheelEvent e) {
		if (!this.stateNavigation.isShowExtendedTooltip()) {
			return;
		}

		this.tooltipContent.setCurrentExtendedToolTipContent(null);
		this.tooltipContent.setVisible(false);
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		super.mouseClicked(e);

		// When user does click (to popup menu for example) tool tip must be
		// hidden
		if (!this.stateNavigation.isShowExtendedTooltip()) {
			return;
		}

		this.tooltipContent.setCurrentExtendedToolTipContent(null);
		this.tooltipContent.setVisible(false);
	}
}
