/*
 * aTunes 2.2.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.model;

import java.io.Serializable;

/**
 * Configuration of a look and feel: name and skin
 * @author alex
 *
 */
public class LookAndFeelBean implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3123539477818725302L;

	/**
     * Name of the look and feel
     */
    private String name;

    /**
     * Name of the selected skin
     */
    private String skin;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the skin
     */
    public String getSkin() {
        return skin;
    }

    /**
     * @param skin
     *            the skin to set
     */
    public void setSkin(String skin) {
        this.skin = skin;
    }

}
