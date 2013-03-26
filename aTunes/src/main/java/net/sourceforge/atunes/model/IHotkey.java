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

package net.sourceforge.atunes.model;

import java.io.Serializable;

/**
 * A hotkey: a key typed which fires an action in application
 * 
 * @author alex
 * 
 */
public interface IHotkey extends Serializable {

    /**
     * @return id of hotkey
     */
    public int getId();

    /**
     * @return modifier
     */
    public int getMod();

    /**
     * @return key
     */
    public int getKey();

    /**
     * @return description
     */
    public String getDescription();

    /**
     * @param mod
     */
    public void setMod(int mod);

    /**
     * @param key
     */
    public void setKey(int key);

    /**
     * @return description of key
     */
    public String getKeyDescription();

    /**
     * @return true if hotkey is recommended (default)
     */
    public boolean isRecommended();

}