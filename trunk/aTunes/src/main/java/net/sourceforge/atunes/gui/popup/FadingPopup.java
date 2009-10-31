/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.gui.popup;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JWindow;
import javax.swing.Popup;

import net.sourceforge.atunes.gui.WindowFader;
import net.sourceforge.atunes.utils.GuiUtils;

import org.jvnet.lafwidget.utils.ShadowPopupBorder;

/**
 * Based on a fading popup with shadow border by Kirill Grouchnikov.
 */
final class FadingPopup extends Popup {

    JWindow popupWindow;
    int currOpacity;
    WindowFader windowFader;

    /**
     * Instantiates a new fading popup.
     * 
     * @param owner
     *            the owner
     * @param contents
     *            the contents
     * @param ownerX
     *            the owner x
     * @param ownerY
     *            the owner y
     */
    FadingPopup(Component owner, Component contents, int ownerX, int ownerY, boolean shadowBorder) {
        // create a new heavyweighht window
        this.popupWindow = new JWindow();
        // determine the popup location
        popupWindow.setLocation(ownerX, ownerY);
        // add the contents to the popup
        popupWindow.getContentPane().add(contents, BorderLayout.CENTER);
        contents.invalidate();
        JComponent parent = (JComponent) contents.getParent();
        // set the shadow border
        if (shadowBorder) {
            parent.setBorder(ShadowPopupBorder.getInstance());
        }
        this.windowFader = new WindowFader(popupWindow, 40) {
            @Override
            protected void fadeOutFinished() {
                popupWindow.removeAll();
                super.fadeOutFinished();
            }
        };
    }

    @Override
    public void show() {
        // mark the popup with 0% opacity
        this.currOpacity = 0;
        GuiUtils.setWindowOpacity(popupWindow, 0.0f);

        this.popupWindow.setVisible(true);
        this.popupWindow.pack();

        // mark the window as non-opaque, so that the
        // shadow border pixels take on the per-pixel
        // translucency
        GuiUtils.setWindowOpaque(this.popupWindow, false);

        // start fading in
        windowFader.fadeIn();
    }

    @Override
    public void hide() {
        // start fading out
        windowFader.fadeOut();
    }
}
