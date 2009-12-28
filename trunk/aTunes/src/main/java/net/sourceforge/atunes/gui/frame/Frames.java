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
package net.sourceforge.atunes.gui.frame;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

public final class Frames {

    private Frames() {
    }

    private static final List<Class<? extends Frame>> classes;

    private static Map<Class<? extends Frame>, String> previewImages = new HashMap<Class<? extends Frame>, String>();

    static {
        classes = new ArrayList<Class<? extends Frame>>();

        add(DefaultSingleFrame.class, "1.png");
        add(MultipleFrame.class, "2.png");
        add(EnhancedSingleFrame.class, "3.png");
    }

    private static void add(Class<? extends Frame> clazz, String image) {
        classes.add(clazz);
        previewImages.put(clazz, image);
    }

    public static List<Class<? extends Frame>> getClasses() {
        return classes;
    }

    public static ImageIcon getImage(Class<? extends Frame> clazz) {
        if (clazz == null) {
            return null;
        }
        URL imgURL = Frames.class.getResource("/images/windows/" + previewImages.get(clazz));
        return imgURL != null ? new ImageIcon(imgURL) : null;
    }

}