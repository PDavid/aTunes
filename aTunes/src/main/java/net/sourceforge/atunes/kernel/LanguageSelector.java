/*
 * aTunes 2.2.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel;

import java.util.Locale;

import net.sourceforge.atunes.kernel.modules.state.beans.LocaleBean;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
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
     * @param state
     */
    static void setLanguage(IState state) {
        LocaleBean localeBean = state.getLocale();
        if (localeBean != null) {
            I18nUtils.setLocale(localeBean.getLocale());
            Logger.info(StringUtils.getString("Setting language: ", localeBean.getLocale()));
        } else {
            Logger.info("Language not configured; using default language");
            I18nUtils.setLocale(null);
            state.setLocale(new LocaleBean(I18nUtils.getSelectedLocale()));
        }
        Locale locale = state.getLocale().getLocale();
        Locale.setDefault(locale);
    }
}
