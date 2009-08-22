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

import javax.swing.UIDefaults;
import javax.swing.plaf.FontUIResource;

import net.sourceforge.atunes.gui.views.dialogs.FontChooserDialog.FontSettings;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.state.beans.FontBean;

import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.fonts.FontPolicy;
import org.jvnet.substance.fonts.FontSet;

/**
 * All fonts that are used and that are different from the default font.
 */
public final class Fonts {

    private static final boolean USE_FONT_SMOOTHING_SETTINGS_FROM_OS_DEFAULT_VALUE = false;
    private static final boolean USE_FONT_SMOOTHING_DEFAULT_VALUE = true;

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
        FontSettings fontSettings = ApplicationState.getInstance().getFontSettings();
        if (fontSettings != null) {
            font = fontSettings.getFont().toFont();
            setFontPolicy(font);
        } else {
            /*
             * Get appropriate font for the currently selected language. For
             * Chinese or Japanese we should use default font.
             */
            if ("zh".equals(ApplicationState.getInstance().getLocale().getLanguage()) || "ja".equals(ApplicationState.getInstance().getLocale().getLanguage())) {
                font = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
                setFontPolicy(font);
            } else {
                FontSet fs = SubstanceLookAndFeel.getFontPolicy().getFontSet("Substance", null);
                FontUIResource controlFont = fs.getControlFont();
                font = controlFont;
            }
            ApplicationState.getInstance().setFontSettings(
                    new FontSettings(new FontBean(font), USE_FONT_SMOOTHING_DEFAULT_VALUE, USE_FONT_SMOOTHING_SETTINGS_FROM_OS_DEFAULT_VALUE));
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

    private static void setFontPolicy(final Font baseFont) {
        SubstanceLookAndFeel.setFontPolicy(new FontPolicy() {

            @Override
            public FontSet getFontSet(String arg0, UIDefaults arg1) {
                return new FontSet() {

                    @Override
                    public FontUIResource getWindowTitleFont() {
                        return new FontUIResource(baseFont.deriveFont(Font.BOLD, baseFont.getSize() + 1f));
                    }

                    @Override
                    public FontUIResource getTitleFont() {
                        return new FontUIResource(baseFont.deriveFont((float) baseFont.getSize()));
                    }

                    @Override
                    public FontUIResource getSmallFont() {
                        return new FontUIResource(baseFont.deriveFont(baseFont.getSize() - 1f));
                    }

                    @Override
                    public FontUIResource getMessageFont() {
                        return new FontUIResource(baseFont.deriveFont(baseFont.getSize() - 1f));
                    }

                    @Override
                    public FontUIResource getMenuFont() {
                        return new FontUIResource(baseFont.deriveFont((float) baseFont.getSize()));
                    }

                    @Override
                    public FontUIResource getControlFont() {
                        return new FontUIResource(baseFont.deriveFont((float) baseFont.getSize()));
                    }
                };
            }
        });
    }

    public static void setFontSmoothing() {
        FontSettings fontSettings = ApplicationState.getInstance().getFontSettings();
        if (fontSettings != null && !fontSettings.isUseFontSmoothingSettingsFromOs()) {
            if (fontSettings.isUseFontSmoothing()) {
                System.setProperty("awt.useSystemAAFontSettings", "lcd");
            } else {
                System.setProperty("awt.useSystemAAFontSettings", "false");
            }
        } else {
            System.setProperty("awt.useSystemAAFontSettings", "lcd");
        }
    }

}
