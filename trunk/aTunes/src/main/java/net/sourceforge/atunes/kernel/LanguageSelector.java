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
package net.sourceforge.atunes.kernel;

import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.state.beans.LocaleBean;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.DateUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This class is responsible of setting the language.
 */
final class LanguageSelector {

    private LanguageSelector() {
    	
    }
    
    /**
     * Sets application language. If a language is defined in the state, it's
     * used. If not, a dialog is shown to let the user choose. The language
     * selected is used and stored in state
     */
    static void setLanguage() {
        LocaleBean locale = ApplicationState.getInstance().getLocale();
        Logger logger = new Logger();
        if (locale != null) {
            I18nUtils.setLocale(locale.getLocale());
            logger.info(LogCategories.START, StringUtils.getString("Setting language: ", locale.getLocale()));
        } else {
            logger.info(LogCategories.START, "Language not configured; using default language");
            I18nUtils.setLocale(null);
            ApplicationState.getInstance().setLocale(new LocaleBean(I18nUtils.getSelectedLocale()));
        }
        // Set Locale for DateUtils
        DateUtils.setLocale(ApplicationState.getInstance().getLocale().getLocale());
    }    
}
