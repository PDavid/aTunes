/*
 * aTunes 1.14.0
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

package net.sourceforge.atunes.gui.images;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

/**
 * The Class ThemePreviewLoader.
 */
public class ThemePreviewLoader {

    /** Map of skin names and name of the image files */
    private static Map<String, String> preview = new HashMap<String, String>();

    static {
        /*
         * toned down skins
         */
        preview.put("BusinessBlackSteel", "BusinessBlackSteel.png");
        preview.put("Creme", "Creme.png");
        preview.put("Business", "Business.png");
        preview.put("BusinessBlueSteel", "BusinessBlueSteel.png");
        preview.put("CremeCoffee", "CremeCoffee.png");
        preview.put("Sahara", "Sahara.png");
        preview.put("Moderate", "Moderate.png");
        preview.put("OfficeSilver2007", "OfficeSilver2007.png");
        preview.put("Nebula", "Nebula.png");
        preview.put("NebulaBrickWall", "NebulaBrickWall.png");
        preview.put("Autumn", "Autumn.png");
        preview.put("MistSilver", "MistSilver.png");
        preview.put("MistAqua", "MistAqua.png");
        preview.put("Dust", "Dust.png");
        preview.put("DustCoffee", "DustCoffee.png");

        /*
         * dark skins
         */
        preview.put("RavenGraphite", "RavenGraphite.png");
        preview.put("RavenGraphiteGlass", "RavenGraphiteGlass.png");
        preview.put("Raven", "Raven.png");
        preview.put("Magma", "Magma.png");
        preview.put("ChallengerDeep", "ChallengerDeep.png");
        preview.put("EmeraldDusk", "EmeraldDusk.png");
        preview.put("Twilight", "Twilight.png");

        /*
         * satured skins
         */
        preview.put("OfficeBlue2007", "OfficeBlue2007.png");

        /*
         * custom skins
         */
        preview.put("aTunes Blue", "aTunesBlue.png");
        preview.put("aTunes Dark", "aTunesDark.png");
        preview.put("aTunes Gray", "aTunesGray.png");
    }

    /**
     * Returns an image.
     * 
     * @param imgName
     *            the img name
     * 
     * @return An ImageIcon
     */
    public static ImageIcon getImage(String imgName) {
        if (!preview.containsKey(imgName)) {
            return null;
        }
        URL imgURL = ThemePreviewLoader.class.getResource("/images/themes/" + preview.get(imgName));
        return imgURL != null ? new ImageIcon(imgURL) : null;
    }

}
