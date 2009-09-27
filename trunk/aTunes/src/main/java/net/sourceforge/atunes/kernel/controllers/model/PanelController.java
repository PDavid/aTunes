/*
 * aTunes 2.0.0
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
package net.sourceforge.atunes.kernel.controllers.model;

import javax.swing.JPanel;

/**
 * The Class PanelController.
 * 
 * @author fleax
 */
public abstract class PanelController<T extends JPanel> extends Controller {

    /** The panel controlled. */
    private final T panelControlled;

    /**
     * Instantiates a new panel controller.
     * 
     * @param panelControlled
     *            the panel controlled
     */
    public PanelController(T panelControlled) {
        super();
        this.panelControlled = panelControlled;
    }

    /**
     * Returns panel controlled
     * 
     * @return
     */
    protected T getPanelControlled() {
        return panelControlled;
    }
}
