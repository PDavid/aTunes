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

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IStateNavigation;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.model.ITreeObject;

/**
 * The listener interface for receiving navigationTreeToolTip events.
 */
public final class NavigationTreeToolTipListener extends MouseAdapter {

	private NavigationController navigationController;

	private INavigationHandler navigationHandler;

	private IStateNavigation stateNavigation;

	private ExtendedTooltipContent extendedTooltipContent;

	/**
	 * @param extendedTooltipContent
	 */
	public void setExtendedTooltipContent(
			final ExtendedTooltipContent extendedTooltipContent) {
		this.extendedTooltipContent = extendedTooltipContent;
	}

	/**
	 * @param navigationController
	 */
	public void setNavigationController(
			final NavigationController navigationController) {
		this.navigationController = navigationController;
	}

	/**
	 * @param navigationHandler
	 */
	public void setNavigationHandler(final INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}

	/**
	 * @param stateNavigation
	 */
	public void setStateNavigation(final IStateNavigation stateNavigation) {
		this.stateNavigation = stateNavigation;
	}

	@Override
	public void mouseDragged(final MouseEvent arg0) {
		if (!this.stateNavigation.isShowExtendedTooltip()) {
			return;
		}

		this.extendedTooltipContent.setCurrentExtendedToolTipContent(null);
		this.extendedTooltipContent.setVisible(false);
	}

	@Override
	public void mouseMoved(final MouseEvent e) {
		if (!this.stateNavigation.isShowExtendedTooltip()) {
			return;
		}

		ITreeNode node = this.navigationHandler.getCurrentView().getTree()
				.getSelectedNode(e);
		if (node != null) {
			final Object content = node.getUserObject();

			if (content.equals(this.extendedTooltipContent
					.getCurrentExtendedToolTipContent())) {
				return;
			}

			// Show extended tooltip
			if (this.extendedTooltipContent
					.canObjectBeShownInExtendedToolTip(content)) {
				GuiUtils.callInEventDispatchThreadLater(new Runnable() {
					@Override
					public void run() {
						showExtendedTooltip(e, content);
					}
				});
			} else {
				this.extendedTooltipContent
						.setCurrentExtendedToolTipContent(null);
				this.extendedTooltipContent.setVisible(false);
				this.navigationController.getToolTipTimer().stop();
			}
		} else {
			this.extendedTooltipContent.setCurrentExtendedToolTipContent(null);
			this.extendedTooltipContent.setVisible(false);
			this.navigationController.getToolTipTimer().stop();
		}
	}

	/**
	 * @param e
	 * @param content
	 */
	@SuppressWarnings("unchecked")
	private void showExtendedTooltip(final MouseEvent e, final Object content) {
		if (!this.extendedTooltipContent.isVisible()
				|| this.extendedTooltipContent
						.getCurrentExtendedToolTipContent() == null
				|| this.extendedTooltipContent
						.getCurrentExtendedToolTipContent() != content) {
			if (this.extendedTooltipContent.isVisible()) {
				this.extendedTooltipContent.setVisible(false);
			}
			this.extendedTooltipContent.setLocation(
					(int) this.navigationHandler.getCurrentView().getTree()
							.getLocationOnScreen().getX()
							+ e.getX(), (int) this.navigationHandler
							.getCurrentView().getTree().getLocationOnScreen()
							.getY()
							+ e.getY() + 20);

			this.extendedTooltipContent
					.setToolTipContent((ITreeObject<? extends IAudioObject>) content);
			this.extendedTooltipContent
					.setCurrentExtendedToolTipContent(content);
		} else {
			this.extendedTooltipContent.setCurrentExtendedToolTipContent(null);
		}

		this.navigationController.getToolTipTimer().setInitialDelay(
				this.stateNavigation.getExtendedTooltipDelay() * 1000);
		this.navigationController.getToolTipTimer().setRepeats(false);
		this.navigationController.getToolTipTimer().start();
	}

	@Override
	public void mouseWheelMoved(final MouseWheelEvent e) {
		if (!this.stateNavigation.isShowExtendedTooltip()) {
			return;
		}

		this.extendedTooltipContent.setCurrentExtendedToolTipContent(null);
		this.extendedTooltipContent.setVisible(false);
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		super.mouseClicked(e);

		// When user does click (to popup menu for example) tool tip must be
		// hidden
		if (!this.stateNavigation.isShowExtendedTooltip()) {
			return;
		}

		this.extendedTooltipContent.setCurrentExtendedToolTipContent(null);
		this.extendedTooltipContent.setVisible(false);
	}
}
