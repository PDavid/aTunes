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

import javax.swing.JFrame;

/**
 * The Class FrameController.
 * 
 * @author fleax
 */
public abstract class FrameController<T extends JFrame> extends Controller {

    /** The frame controlled. */
    private final T frameControlled;

    /**
     * Instantiates a new frame controller.
     * 
     * @param frameControlled
     *            the frame controlled
     */
    public FrameController(T frameControlled) {
        super();
        this.frameControlled = frameControlled;
    }

    /**
     * Returns frame controlled
     * 
     * @return
     */
    protected T getFrameControlled() {
        return frameControlled;
    }
}
