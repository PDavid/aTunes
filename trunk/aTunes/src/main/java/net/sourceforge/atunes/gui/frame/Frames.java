/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.gui.frame;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.model.IFrame;

public final class Frames {

    private Frames() {
    }

    private static final List<Class<? extends IFrame>> CLASSES;

    private static Map<Class<? extends IFrame>, String> images = new HashMap<Class<? extends IFrame>, String>();

    static {
        CLASSES = new ArrayList<Class<? extends IFrame>>();

        add(DefaultSingleFrame.class, "1.png");
        add(EnhancedSingleFrame.class, "2.png");
        add(NavigatorTopPlayListBottomSingleFrame.class, "3.png");
    }

    private static void add(Class<? extends IFrame> clazz, String image) {
        CLASSES.add(clazz);
        images.put(clazz, image);
    }

    public static List<Class<? extends IFrame>> getClasses() {
        return new ArrayList<Class<? extends IFrame>>(CLASSES);
    }

    public static ImageIcon getImage(Class<? extends IFrame> clazz) {
        if (clazz == null) {
            return null;
        }
        String string = images.get(clazz);
        return getImage(string);
    }

    private static ImageIcon getImage(String string) {
        URL imgURL = Frames.class.getResource("/images/windows/" + string);
        return imgURL != null ? new ImageIcon(imgURL) : null;
    }

}