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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.utils.I18nUtils;

public final class Frames {

    private Frames() {
    }

    private static final Map<String, Class<? extends Frame>> nameToClassMap;
    private static final Map<Class<? extends Frame>, String> classToNameMap;

    private static Map<String, String> previewImages = new HashMap<String, String>();

    static {
        nameToClassMap = new HashMap<String, Class<? extends Frame>>();
        classToNameMap = new HashMap<Class<? extends Frame>, String>();

        add(I18nUtils.getString("STANDARD_WINDOW"), DefaultSingleFrame.class, "");
        add(I18nUtils.getString("MULTIPLE_WINDOW"), MultipleFrame.class, "");
        add(I18nUtils.getString("ENHANCED_WINDOW"), EnhancedSingleFrame.class, "");
    }

    private static void add(String name, Class<? extends Frame> clazz, String image) {
        nameToClassMap.put(name, clazz);
        classToNameMap.put(clazz, name);
        previewImages.put(name, image);
    }

    public static String getFrameName(Class<? extends Frame> clazz) {
        return classToNameMap.get(clazz);
    }

    public static Class<? extends Frame> getFrameClass(String frameName) {
        return nameToClassMap.get(frameName);
    }

    public static Set<String> getFrameNames() {
        return nameToClassMap.keySet();
    }

    public static Set<Class<? extends Frame>> getFrameClass() {
        return classToNameMap.keySet();
    }

    public static ImageIcon getImage(String name) {
        if (!previewImages.containsKey(name)) {
            return null;
        }
        URL imgURL = Frames.class.getResource("/images/windows/" + previewImages.get(name));
        return imgURL != null ? new ImageIcon(imgURL) : null;
    }

}