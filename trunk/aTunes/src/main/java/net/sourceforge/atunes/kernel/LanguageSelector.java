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

package net.sourceforge.atunes.kernel;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.state.beans.LocaleBean;
import net.sourceforge.atunes.kernel.modules.visual.VisualHandler;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.DateUtils;
import net.sourceforge.atunes.utils.DesktopUtils;
import net.sourceforge.atunes.utils.LanguageTool;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This class is responsible of setting the language.
 */
public class LanguageSelector {

    /** Logger. */
    private static Logger logger = new Logger();

    /**
     * Sets application language. If a language is defined in the state, it's
     * used. If not, a dialog is shown to let the user choose. The language
     * selected is used and stored in state
     */
    public static void setLanguage() {
        LocaleBean locale = ApplicationState.getInstance().getLocale();
        if (locale != null) {
            //TODO: Update languages that are outdated. This triggers a nag screen, so only use if no translation update was avaible for a long time for the language you add to the list.
            //The message will appear three times. It begins after seven starts of the application. Don't forget to remove the language in case of success of this measure.
            if (locale.getLocale().getISO3Language().equals("zho") || locale.getLocale().getISO3Language().equals("jpn") || locale.getLocale().getISO3Language().equals("tur")) {
                logger.info(LogCategories.START, StringUtils.getString("Outdated locale: ", locale.getLocale()));
                if (ApplicationState.getInstance().getNagDialogCounter() > 6) {
                    VisualHandler.getInstance().hideSplashScreen();
                    VisualHandler
                            .getInstance()
                            .showMessage(
                                    StringUtils
                                            .getString(LanguageTool
                                                    .getString("The translation file you are using has not been updated for a long time. \nPlease contact us in order to refresh your favourite translation.")));
                    DesktopUtils.openURL(Constants.CONTRIBUTORS_WANTED);
                }
                ApplicationState.getInstance().setNagDialogCounter(ApplicationState.getInstance().getNagDialogCounter() + 1);
            }
            LanguageTool.setLanguage(locale.getLocale());
            logger.info(LogCategories.START, StringUtils.getString("Setting language: ", locale.getLocale()));
        } else {
            logger.info(LogCategories.START, "Language not configured; using default language");
            LanguageTool.setLanguage(null);
            ApplicationState.getInstance().setLocale(new LocaleBean(LanguageTool.getLanguageSelected()));
        }
        // Set Locale for DateUtils
        DateUtils.setLocale(ApplicationState.getInstance().getLocale().getLocale());
    }
}
