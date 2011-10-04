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

package net.sourceforge.atunes.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import net.sourceforge.atunes.Constants;

public final class I18nUtils {

    private I18nUtils() {
    }

    /** The base names for all resource bundles */
    private static final String BUNDLE_BASE_NAME = StringUtils.getString(Constants.TRANSLATIONS_DIR, ".MainBundle");

    /** Class loader for finding resources in working directory. */
    private static final ClassLoader WD_CLASS_LOADER;

    static {
        WD_CLASS_LOADER = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run() {
                return new ClassLoader() {
                    @Override
                    public URL getResource(String name) {
                        try {
                            URL result = super.getResource(name);
                            if (result != null) {
                                return result;
                            }
                            return (new File(name)).toURI().toURL();
                        } catch (MalformedURLException ex) {
                            return null;
                        }
                    }
                };
            }
        });
    }

    /** Language file resource bundle. */
    private static ResourceBundle languageBundle;

    /** Locale selected in dialog. */
    private static Locale selectedLocale;

    /**
     * Return all available languages.
     * 
     * @return the languages
     * 
     * Hebrew translation and image files must have "iw" ID, not "he". Bug in Java?
     * Official ISO code is "he", "iw" being used previously.
     * 
     */
    public static List<Locale> getLanguages() {
        return Arrays.asList(new Locale("ar"), new Locale("ca"), new Locale("cs"), new Locale("da"), new Locale("de"), new Locale("el"), new Locale("en"), new Locale("es"),
                new Locale("fr"), new Locale("gl"), new Locale("he"), new Locale("hr"), new Locale("hu"), new Locale("it"), new Locale("ja"), new Locale("nl"), new Locale("no"), new Locale("pl"),
                new Locale("pt", "BR"), new Locale("pt"), new Locale("ru"), new Locale("sk"), new Locale("sv"), new Locale("tr"), new Locale("uk"), new Locale("zh", "TW"),
                new Locale("zh"));
    }

    /**
     * Get a string for a given key for current language.
     * 
     * @param key
     *            the key
     * 
     * @return the string
     */
    public static String getString(String key) {
        if (languageBundle != null) {
            String result;
            try {
                result = languageBundle.getString(key);
            } catch (MissingResourceException e) {
                return key;
            }
            return result;
        }
        return key;
    }

    /**
     * Sets locale to use in application.
     * 
     * @param locale
     *            the locale
     */
    public static void setLocale(Locale locale) {
        if (locale == null) {
            languageBundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, Locale.getDefault(), WD_CLASS_LOADER);
        } else {
            languageBundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale, WD_CLASS_LOADER);
        }
        selectedLocale = languageBundle.getLocale();
    }

    /**
     * Return the name of the selected language.
     * 
     * @return the selected locale
     */
    public static Locale getSelectedLocale() {
        return selectedLocale;
    }

}
