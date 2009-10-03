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
package net.sourceforge.atunes.kernel.modules.webservices.lyrics.engines;

import java.beans.ConstructorProperties;

/**
 * Class with info about lyrics engines.
 * 
 * @author Thomas
 */
public class LyricsEngineInfo {

    private String name;
    private String clazz;
    private boolean enabled;

    @ConstructorProperties( { "name", "clazz", "enabled" })
    public LyricsEngineInfo(String name, String clazz, boolean enabled) {
        this.enabled = enabled;
        this.clazz = clazz;
        this.name = name;
    }

    /**
     * @return the lyricsEngine
     */
    public String getName() {
        return name;
    }

    /**
     * @return the class
     */
    public String getClazz() {
        return clazz;
    }

    /**
     * @return the enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled
     *            the enabled to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
