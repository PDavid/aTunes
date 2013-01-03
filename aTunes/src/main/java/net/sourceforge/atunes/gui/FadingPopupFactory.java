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

import java.awt.Component;

import javax.swing.JToolTip;
import javax.swing.Popup;
import javax.swing.PopupFactory;

import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.IOSManager;

/**
 * A factory for creating FadingPopup objects.
 */
public final class FadingPopupFactory extends PopupFactory {

    /** The popup factory. */
    private static PopupFactory popupFactory;
    
    private IOSManager osManager;

	private ILookAndFeel lookAndFeel;

    /**
     * Instantiates a new fading popup factory.
     * @param osManager
     * @param lookAndFeel
     */
    FadingPopupFactory(IOSManager osManager, ILookAndFeel lookAndFeel) {
        super();
        this.osManager = osManager;
        this.lookAndFeel = lookAndFeel;
    }

    /**
     * Install.
     * @param osManager
     * @param lookAndFeel
     */
    public static void install(IOSManager osManager, ILookAndFeel lookAndFeel) {
        PopupFactory pf = PopupFactory.getSharedInstance();
        if (pf instanceof FadingPopupFactory) {
            return;
        }
        popupFactory = pf;
        PopupFactory.setSharedInstance(new FadingPopupFactory(osManager, lookAndFeel));
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.PopupFactory#getPopup(java.awt.Component,
     * java.awt.Component, int, int)
     */
    @Override
    public Popup getPopup(Component owner, Component contents, int x, int y) {
        if (contents instanceof JToolTip) {
            return new FadingPopup(contents, x, y, osManager.areShadowBordersForToolTipsSupported(), lookAndFeel);
        }
        return popupFactory.getPopup(owner, contents, x, y);
    }
}
