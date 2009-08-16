/*
 * aTunes 1.14.0
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

import java.awt.Component;

import javax.swing.JToolTip;
import javax.swing.Popup;
import javax.swing.PopupFactory;

import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.misc.SystemProperties.OperatingSystem;

/**
 * A factory for creating FadingPopup objects.
 */
public class FadingPopupFactory extends PopupFactory {

    /** The popup factory. */
    private static PopupFactory popupFactory;

    /** If popups should have a shadow border */
    private boolean shadowBorderForToolTips;

    /**
     * Instantiates a new fading popup factory.
     */
    FadingPopupFactory() {
        super();
        shadowBorderForToolTips = SystemProperties.OS == OperatingSystem.WINDOWS;
    }

    /**
     * Install.
     */
    public static void install() {
        PopupFactory pf = PopupFactory.getSharedInstance();
        if (pf instanceof FadingPopupFactory) {
            return;
        }
        popupFactory = pf;
        PopupFactory.setSharedInstance(new FadingPopupFactory());
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.PopupFactory#getPopup(java.awt.Component,
     * java.awt.Component, int, int)
     */
    @Override
    public Popup getPopup(Component owner, Component contents, int x, int y) throws IllegalArgumentException {
        if (contents instanceof JToolTip) {
            return new FadingPopup(owner, contents, x, y, shadowBorderForToolTips);
        }
        return popupFactory.getPopup(owner, contents, x, y);
    }
}
