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

import java.awt.Font;

import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.utils.GuiUtils;

import org.jvnet.substance.SubstanceLookAndFeel;

/**
 * All fonts that are used and that are different from the default font.
 */
public final class Fonts {

    public static Font ABOUT_BIG_FONT;
    public static Font APP_VERSION_TITLE_FONT;
    public static Font BUTTON_FONT;
    public static Font GENERAL_FONT_BOLD;
    public static Font SMALL_FONT;
    public static Font PLAY_LIST_FONT;
    public static Font PLAY_LIST_FONT_SELECTED_ITEM;
    public static Font CONTEXT_INFORMATION_BIG_FONT;
    public static Font PROPERTIES_DIALOG_BIG_FONT;
    public static Font DOCKABLE_WINDOW_TITLE_FONT;
    public static Font CHART_TITLE_FONT;
    public static Font CHART_TICK_LABEL_FONT;
    public static Font OSD_LINE1_FONT;
    public static Font OSD_LINE2_FONT;
    public static Font OSD_LINE3_FONT;
    public static Font FULL_SCREEN_LINE1_FONT;
    public static Font FULL_SCREEN_LINE2_FONT;
    private static Font font;

    private Fonts() {
    }

    /**
     * <p>
     * Initializes fonts
     * </p>
     * 
     * <p>
     * <b>Must be called after language has been selected and before the GUI is
     * created!</b>
     * </p>
     */
    public static void initializeFonts() {
        /*
         * Get appropriate font for the currently selected language. For Chinese
         * or Japanese we should use default font.
         */
        if ("zh".equals(ApplicationState.getInstance().getLocale().getLanguage()) || "ja".equals(ApplicationState.getInstance().getLocale().getLanguage())
                || ApplicationState.getInstance().isUseDefaultFont()) {
            font = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
            GuiUtils.setUIFont(new FontUIResource(font));
        } else {
            font = SubstanceLookAndFeel.getFontPolicy().getFontSet("Substance", UIManager.getDefaults()).getControlFont();
        }

        ABOUT_BIG_FONT = font.deriveFont(font.getSize() + 8f);
        APP_VERSION_TITLE_FONT = font.deriveFont(font.getSize() + 2f);
        BUTTON_FONT = font;
        GENERAL_FONT_BOLD = font.deriveFont(Font.BOLD, font.getSize() + 1);
        SMALL_FONT = font.deriveFont(font.getSize() - 1f);
        PLAY_LIST_FONT = font;
        PLAY_LIST_FONT_SELECTED_ITEM = PLAY_LIST_FONT.deriveFont(Font.BOLD);
        CONTEXT_INFORMATION_BIG_FONT = font.deriveFont(font.getSize() + 8f);
        PROPERTIES_DIALOG_BIG_FONT = font.deriveFont(font.getSize() + 4f);
        DOCKABLE_WINDOW_TITLE_FONT = font.deriveFont(font.getSize() - 3f);
        CHART_TITLE_FONT = font.deriveFont(font.getSize() - 1f);
        CHART_TICK_LABEL_FONT = font.deriveFont(font.getSize() - 2f);
        OSD_LINE1_FONT = font.deriveFont(Font.BOLD, font.getSize() + 4f);
        OSD_LINE2_FONT = font.deriveFont(font.getSize() + 2f);
        OSD_LINE3_FONT = OSD_LINE2_FONT;
        FULL_SCREEN_LINE1_FONT = font.deriveFont(font.getSize() + 25f);
        FULL_SCREEN_LINE2_FONT = font.deriveFont(font.getSize() + 15f);
    }

    public static void setFontSmoothing() {
        if (!ApplicationState.getInstance().isUseOSSettingsForFontSmoothing()) {
            if (ApplicationState.getInstance().isUseFontSmoothing()) {
                System.setProperty("awt.useSystemAAFontSettings", "lcd");
            } else {
                System.setProperty("awt.useSystemAAFontSettings", "false");
            }
        }
    }

}
