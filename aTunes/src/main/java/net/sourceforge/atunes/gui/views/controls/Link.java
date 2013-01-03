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

package net.sourceforge.atunes.gui.views.controls;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import net.sourceforge.atunes.model.IDesktop;

/**
 * A link for url labels
 */
final class Link extends AbstractAction {

    private static final long serialVersionUID = -3784927498125973809L;

    private IDesktop desktop;
    
    private String url;

    /**
     * Instantiates a new link
     * @param desktop
     * @param url
     */
    Link(IDesktop desktop, String url) {
    	this.desktop = desktop;
        this.url = url;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    	desktop.openURL(url);
    }

}
