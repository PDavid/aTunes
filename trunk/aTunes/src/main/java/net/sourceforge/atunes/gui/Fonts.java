/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.gui.views.dialogs.FontChooserDialog.FontSettings;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.state.beans.FontBean;

/**
 * All fonts that are used and that are different from the default font.
 */
public final class Fonts {

    private static final boolean USE_FONT_SMOOTHING_SETTINGS_FROM_OS_DEFAULT_VALUE = false;
    private static final boolean USE_FONT_SMOOTHING_DEFAULT_VALUE = true;

    private static Font aboutBigFont;
    private static Font appVersionLittleFont;
    private static Font buttonFont;
    private static Font generalBoldFont;
    private static Font smallFont;
    private static Font playListFont;
    private static Font playListSelectedItemFont;
    private static Font contextInformationBigFont;
    private static Font propertiesDialogBigFont;
    private static Font chartTitleFont;
    private static Font chartTickLabelFont;
    private static Font osdLine1Font;
    private static Font osdLine2Font;
    private static Font osdLine3Font;
    private static Font fullScreenLine1Font;
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
            LookAndFeelSelector.getInstance().getCurrentLookAndFeel().initializeFonts(font);
        } else {
            /*
             * Get appropriate font for the currently selected language. For
             * Chinese or Japanese we should use default font.
             */
            if ("zh".equals(ApplicationState.getInstance().getLocale().getLanguage()) || "ja".equals(ApplicationState.getInstance().getLocale().getLanguage())) {
                font = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
                LookAndFeelSelector.getInstance().getCurrentLookAndFeel().initializeFonts(font);
            } else {
                font = LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getDefaultFont();
            }
            ApplicationState.getInstance().setFontSettings(
                    new FontSettings(new FontBean(font), USE_FONT_SMOOTHING_DEFAULT_VALUE, USE_FONT_SMOOTHING_SETTINGS_FROM_OS_DEFAULT_VALUE));
        }
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

    /**
     * @return the aboutBigFont
     */
    public static Font getAboutBigFont() {
        if (aboutBigFont == null) {
            aboutBigFont = font.deriveFont(font.getSize() + 8f);
        }
        return aboutBigFont;
    }

    /**
     * @return the appVersionLittleFont
     */
    public static Font getAppVersionLittleFont() {
        if (appVersionLittleFont == null) {
            appVersionLittleFont = font.deriveFont(font.getSize() + 1f);
        }
        return appVersionLittleFont;
    }

    /**
     * @return the buttonFont
     */
    public static Font getButtonFont() {
        if (buttonFont == null) {
            buttonFont = font;
        }
        return buttonFont;
    }

    /**
     * @return the generalBoldFont
     */
    public static Font getGeneralBoldFont() {
        if (generalBoldFont == null) {
            generalBoldFont = font.deriveFont(Font.BOLD, font.getSize() + 1);
        }
        return generalBoldFont;
    }

    /**
     * @return the smallFont
     */
    public static Font getSmallFont() {
        if (smallFont == null) {
            smallFont = font.deriveFont(font.getSize() - 1f);
        }
        return smallFont;
    }

    /**
     * @return the playListFont
     */
    public static Font getPlayListFont() {
        if (playListFont == null) {
            playListFont = font;
        }
        return playListFont;
    }

    /**
     * @return the playListSelectedItemFont
     */
    public static Font getPlayListSelectedItemFont() {
        if (playListSelectedItemFont == null) {
            playListSelectedItemFont = getPlayListFont().deriveFont(Font.BOLD);
        }
        return playListSelectedItemFont;
    }

    /**
     * @return the contextInformationBigFont
     */
    public static Font getContextInformationBigFont() {
        if (contextInformationBigFont == null) {
            contextInformationBigFont = font.deriveFont(font.getSize() + 8f);
        }
        return contextInformationBigFont;
    }

    /**
     * @return the propertiesDialogBigFont
     */
    public static Font getPropertiesDialogBigFont() {
        if (propertiesDialogBigFont == null) {
            propertiesDialogBigFont = font.deriveFont(font.getSize() + 4f);
        }
        return propertiesDialogBigFont;
    }

    /**
     * @return the chartTitleFont
     */
    public static Font getChartTitleFont() {
        if (chartTitleFont == null) {
            chartTitleFont = font.deriveFont(font.getSize() - 1f);
        }
        return chartTitleFont;
    }

    /**
     * @return the chartTickLabelFont
     */
    public static Font getChartTickLabelFont() {
        if (chartTickLabelFont == null) {
            chartTickLabelFont = font.deriveFont(font.getSize() - 2f);
        }
        return chartTickLabelFont;
    }

    /**
     * @return the osdLine1Font
     */
    public static Font getOsdLine1Font() {
        if (osdLine1Font == null) {
            osdLine1Font = font.deriveFont(Font.BOLD, font.getSize() + 4f);
        }
        return osdLine1Font;
    }

    /**
     * @return the osdLine2Font
     */
    public static Font getOsdLine2Font() {
        if (osdLine2Font == null) {
            osdLine2Font = font.deriveFont(font.getSize() + 2f);
        }
        return osdLine2Font;
    }

    /**
     * @return the osdLine3Font
     */
    public static Font getOsdLine3Font() {
        if (osdLine3Font == null) {
            osdLine3Font = getOsdLine2Font();
        }
        return osdLine3Font;
    }

    /**
     * @return the fullScreenLine1Font
     */
    public static Font getFullScreenLine1Font() {
        if (fullScreenLine1Font == null) {
            fullScreenLine1Font = font.deriveFont(font.getSize() + 25f);
        }
        return fullScreenLine1Font;
    }
}
