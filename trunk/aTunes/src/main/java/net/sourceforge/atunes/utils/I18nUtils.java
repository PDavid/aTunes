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

package net.sourceforge.atunes.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import net.sourceforge.atunes.Constants;

/**
 * The Class LanguageTool.
 * 
 * @author fleax
 */
public final class I18nUtils {

    private I18nUtils() {
    }

    /** The Constant BUNDLE_NAME. */
    private static final String BUNDLE_NAME = StringUtils.getString(Constants.TRANSLATIONS_DIR, ".MainBundle");

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

    /** Language selected in dialog. */
    private static Locale languageSelected;

    /**
     * Return all available languages.
     * 
     * @return the languages
     */
    public static List<Locale> getLanguages() {
        return Arrays
                .asList(new Locale("ar"), new Locale("cs"), new Locale("da"), new Locale("de"), new Locale("el"), new Locale("en"), new Locale("es"), new Locale("fr"), new Locale(
                        "gl"), new Locale("hr"), new Locale("hu"), new Locale("it"), new Locale("ja"), new Locale("nl"), new Locale("no"), new Locale("pl"),
                        new Locale("pt", "BR"), new Locale("pt"), new Locale("ru"), new Locale("sk"), new Locale("sv"), new Locale("tr"), new Locale("uk"), new Locale("zh", "TW"),
                        new Locale("zh"));
    }

    /**
     * Return the name of the selected language.
     * 
     * @return the language selected
     */
    public static Locale getLanguageSelected() {
        return languageSelected;
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
     * Sets language to use in application.
     * 
     * @param locale
     *            the locale
     */
    public static void setLanguage(Locale locale) {
        if (locale == null) {
            languageBundle = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault(), WD_CLASS_LOADER);
        } else {
            languageBundle = ResourceBundle.getBundle(BUNDLE_NAME, locale, WD_CLASS_LOADER);
        }
        languageSelected = languageBundle.getLocale();
    }

    /**
     * Utility to compare two translation files. Displays keys missing in both
     * files
     * 
     * @param trans1
     *            Filename of translation 1
     * @param trans2
     *            Filename of translation 2
     */
    public static void compareTwoTranslations(String trans1, String trans2) {
        try {
            PropertyResourceBundle translation1 = new PropertyResourceBundle(new FileInputStream(trans1));
            PropertyResourceBundle translation2 = new PropertyResourceBundle(new FileInputStream(trans2));

            Enumeration<String> keys1 = translation1.getKeys();
            Enumeration<String> keys2 = translation2.getKeys();

            List<String> missingKeys2 = new ArrayList<String>();

            while (keys1.hasMoreElements()) {
                String key1 = keys1.nextElement();
                if (!translation2.containsKey(key1)) {
                    missingKeys2.add(key1);
                }
            }

            List<String> missingKeys1 = new ArrayList<String>();

            while (keys2.hasMoreElements()) {
                String key2 = keys2.nextElement();
                if (!translation1.containsKey(key2)) {
                    missingKeys1.add(key2);
                }
            }

            Collections.sort(missingKeys1);
            Collections.sort(missingKeys2);

            for (String key1 : missingKeys1) {
                System.out.println(trans1 + ": Missing key: " + key1);
            }

            for (String key2 : missingKeys2) {
                System.out.println(trans2 + ": Missing key: " + key2);
            }

            System.out.println((missingKeys1.size() + missingKeys2.size()) + " differences");

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
