/*
 * aTunes 2.0.2-SNAPSHOT
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

package net.sourceforge.atunes.gui.views.dialogs.editPreferences;

import java.awt.Component;
import java.net.URL;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.lookandfeel.AbstractListCellRendererCode;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.utils.StringUtils;

public class LanguageListCellRendererCode extends AbstractListCellRendererCode {
	
    @Override
    public Component getComponent(Component superComponent, JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (!(value instanceof Locale)) {
            throw new IllegalArgumentException("Argument value must be instance of Locale");
        }

        Component c = superComponent;

        Locale displayingLocale = (Locale) value;
        Locale currentLocale = ApplicationState.getInstance().getLocale().getLocale();

        String name = displayingLocale.getDisplayName(currentLocale);
        name = StringUtils.getString(String.valueOf(name.charAt(0)).toUpperCase(currentLocale), name.substring(1));
        ((JLabel) c).setText(name);

        // The name of flag file should be flag_<locale>.png
        // if the name of bundle is MainBundle_<locale>.properties
        String flag = StringUtils.getString("flag_", displayingLocale.toString(), ".png");
        
        URL flagURL = GeneralPanel.class.getResource(StringUtils.getString("/", Constants.TRANSLATIONS_DIR, "/", flag));
        if (flagURL != null) {
        	((JLabel) c).setIcon(new ImageIcon(flagURL));
        }
        return c;
    }
}