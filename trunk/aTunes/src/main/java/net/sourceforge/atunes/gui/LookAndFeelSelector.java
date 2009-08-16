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

package net.sourceforge.atunes.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.GuiUtils;

import org.jvnet.lafwidget.LafWidget;
import org.jvnet.lafwidget.utils.LafConstants;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.api.SubstanceConstants;

/**
 * The Class LookAndFeelSelector.
 */
public final class LookAndFeelSelector {

    public static Logger logger = new Logger();

    /** The map of skin names and class names */
    private static Map<String, String> skins = setMapOfSkins();

    /** The default skin */
    public static final String DEFAULT_SKIN = "aTunes Blue";

    private LookAndFeelSelector() {
    }

    /**
     * Gets the list of skins.
     * 
     * @return the list of skins
     */
    public static List<String> getListOfSkins() {
        List<String> result = new ArrayList<String>(skins.keySet());
        Collections.sort(result, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.toLowerCase().compareTo(o2.toLowerCase());
            }
        });
        return result;
    }

    /**
     * Sets the list of skins.
     * 
     * @return the map< string, string>
     */
    private static Map<String, String> setMapOfSkins() {
        Map<String, String> result = new HashMap<String, String>();

        /*
         * toned down skins
         */
        result.put("BusinessBlackSteel", "org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel");
        result.put("Creme", "org.jvnet.substance.skin.SubstanceCremeLookAndFeel");
        result.put("Business", "org.jvnet.substance.skin.SubstanceBusinessLookAndFeel");
        result.put("BusinessBlueSteel", "org.jvnet.substance.skin.SubstanceBusinessBlueSteelLookAndFeel");
        result.put("CremeCoffee", "org.jvnet.substance.skin.SubstanceCremeCoffeeLookAndFeel");
        result.put("Sahara", "org.jvnet.substance.skin.SubstanceSaharaLookAndFeel");
        result.put("Moderate", "org.jvnet.substance.skin.SubstanceModerateLookAndFeel");
        result.put("OfficeSilver2007", "org.jvnet.substance.skin.SubstanceOfficeSilver2007LookAndFeel");
        result.put("Nebula", "org.jvnet.substance.skin.SubstanceNebulaLookAndFeel");
        result.put("NebulaBrickWall", "org.jvnet.substance.skin.SubstanceNebulaBrickWallLookAndFeel");
        result.put("Autumn", "org.jvnet.substance.skin.SubstanceAutumnLookAndFeel");
        result.put("MistSilver", "org.jvnet.substance.skin.SubstanceMistSilverLookAndFeel");
        result.put("MistAqua", "org.jvnet.substance.skin.SubstanceMistAquaLookAndFeel");
        result.put("DustCoffee", "org.jvnet.substance.skin.SubstanceDustCoffeeLookAndFeel");
        result.put("Dust", "org.jvnet.substance.skin.SubstanceDustLookAndFeel");

        /*
         * dark skins
         */
        result.put("RavenGraphite", "org.jvnet.substance.skin.SubstanceRavenGraphiteLookAndFeel");
        result.put("RavenGraphiteGlass", "org.jvnet.substance.skin.SubstanceRavenGraphiteGlassLookAndFeel");
        result.put("Raven", "org.jvnet.substance.skin.SubstanceRavenLookAndFeel");
        result.put("Magma", "org.jvnet.substance.skin.SubstanceMagmaLookAndFeel");
        result.put("ChallengerDeep", "org.jvnet.substance.skin.SubstanceChallengerDeepLookAndFeel");
        result.put("EmeraldDusk", "org.jvnet.substance.skin.SubstanceEmeraldDuskLookAndFeel");
        result.put("Twilight", "org.jvnet.substance.skin.SubstanceTwilightLookAndFeel");

        /*
         * satured skins
         */
        result.put("OfficeBlue2007", "org.jvnet.substance.skin.SubstanceOfficeBlue2007LookAndFeel");

        /*
         * custom skins
         */
        result.put("aTunes Blue", "net.sourceforge.atunes.gui.substance.SubstanceATunesBlueLookAndFeel");
        result.put("aTunes Dark", "net.sourceforge.atunes.gui.substance.SubstanceATunesDarkLookAndFeel");
        result.put("aTunes Gray", "net.sourceforge.atunes.gui.substance.SubstanceATunesGrayLookAndFeel");

        return result;
    }

    /**
     * Sets the look and feel.
     * 
     * @param theme
     *            the new look and feel
     */
    public static void setLookAndFeel(String theme) {
        if (Kernel.IGNORE_LOOK_AND_FEEL) {
            return;
        }

        try {
            if (skins.containsKey(theme)) {
                UIManager.setLookAndFeel(skins.get(theme));
            } else {
                UIManager.setLookAndFeel(skins.get(DEFAULT_SKIN));
            }

            // Get border color
            GuiUtils.setBorderColor(SubstanceLookAndFeel.getCurrentSkin().getMainActiveColorScheme().getMidColor());

            UIManager.put(LafWidget.ANIMATION_KIND, LafConstants.AnimationKind.NONE);
            UIManager.put(SubstanceLookAndFeel.TABBED_PANE_CONTENT_BORDER_KIND, SubstanceConstants.TabContentPaneBorderKind.SINGLE_FULL);

            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
        } catch (ClassNotFoundException e) {
            logger.internalError(e);
        } catch (InstantiationException e) {
            logger.internalError(e);
        } catch (IllegalAccessException e) {
            logger.internalError(e);
        } catch (UnsupportedLookAndFeelException e) {
            logger.internalError(e);
        }
    }
}
