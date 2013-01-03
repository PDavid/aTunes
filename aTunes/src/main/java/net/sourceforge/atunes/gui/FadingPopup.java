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

package net.sourceforge.atunes.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;

import javax.swing.JComponent;
import javax.swing.JWindow;
import javax.swing.Popup;
import javax.swing.border.Border;

import net.sourceforge.atunes.model.ILookAndFeel;

/**
 * Based on a fading popup with shadow border by Kirill Grouchnikov.
 */
final class FadingPopup extends Popup {

	private final JWindow popupWindow;
	private final WindowFader windowFader;

	/**
	 * Instantiates a new fading popup.
	 * 
	 * @param contents
	 * @param ownerX
	 * @param ownerY
	 * @param shadowBorder
	 * @param lookAndFeel
	 */
	FadingPopup(final Component contents, final int ownerX, final int ownerY,
			final boolean shadowBorder, final ILookAndFeel lookAndFeel) {
		// create a new heavyweighht window
		this.popupWindow = new JWindow();

		// Solution for the problem
		// "java.lang.IllegalStateException: Trying to unblock window blocked by another dialog"
		// in Linux
		this.popupWindow
				.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);

		// determine the popup location
		this.popupWindow.setLocation(ownerX, ownerY);
		// add the contents to the popup
		this.popupWindow.getContentPane().add(contents, BorderLayout.CENTER);
		contents.invalidate();
		JComponent parent = (JComponent) contents.getParent();
		// set the shadow border
		if (shadowBorder) {
			Border shadow = lookAndFeel.getShadowBorder();
			if (shadow != null) {
				parent.setBorder(shadow);
			}
		}
		this.windowFader = new WindowFader(this.popupWindow, 40) {
			@Override
			protected void fadeOutFinished() {
				FadingPopup.this.popupWindow.removeAll();
				super.fadeOutFinished();
			}
		};
	}

	@Override
	public void show() {
		// mark the popup with 0% opacity
		GuiUtils.setWindowOpacity(this.popupWindow, 0.0f);

		this.popupWindow.setVisible(true);
		this.popupWindow.pack();

		// mark the window as non-opaque, so that the
		// shadow border pixels take on the per-pixel
		// translucency
		GuiUtils.setWindowOpaque(this.popupWindow, false);

		// start fading in
		this.windowFader.fadeIn();
	}

	@Override
	public void hide() {
		// start fading out
		this.windowFader.fadeOut();
	}
}
