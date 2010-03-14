/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.kernel.modules.state.beans;

import java.awt.Font;
import java.beans.ConstructorProperties;

/**
 * Bean for java.awt.Font
 */
public final class FontBean {

    private String name;
    private int style;
    private int size;

    @ConstructorProperties( { "name", "style", "size" })
    public FontBean(String name, int style, int size) {
        this.name = name;
        this.style = style;
        this.size = size;
    }

    public FontBean(Font font) {
        this.name = font.getName();
        this.style = font.getStyle();
        this.size = font.getSize();
    }

    public String getName() {
        return name;
    }

    public int getStyle() {
        return style;
    }

    public int getSize() {
        return size;
    }

    public Font toFont() {
        return new Font(name, style, size);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + size;
        result = prime * result + style;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FontBean other = (FontBean) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (size != other.size)
            return false;
        if (style != other.style)
            return false;
        return true;
    }

}
