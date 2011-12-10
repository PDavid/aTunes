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

package net.sourceforge.atunes.gui.views.dialogs.editPreferences;

import java.net.URL;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.AbstractListCellRendererCode;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.StringUtils;

public class LanguageListCellRendererCode extends AbstractListCellRendererCode<JLabel, Locale> {
	
	private IState state;
	
	public LanguageListCellRendererCode(IState state) {
		this.state = state;
	}
	
    @Override
    public JComponent getComponent(JLabel c, JList list, Locale displayingLocale, int index, boolean isSelected, boolean cellHasFocus) {
        Locale currentLocale = state.getLocale().getLocale();

        String name = displayingLocale.getDisplayName(currentLocale);
        name = StringUtils.getString(String.valueOf(name.charAt(0)).toUpperCase(currentLocale), name.substring(1));
        c.setText(name);

        // The name of flag file should be flag_<locale>.png
        // if the name of bundle is MainBundle_<locale>.properties
        String flag = StringUtils.getString("flag_", displayingLocale.toString(), ".png");
        
        URL flagURL = GeneralPanel.class.getResource(StringUtils.getString("/", Constants.TRANSLATIONS_DIR, "/", flag));
        if (flagURL != null) {
        	c.setIcon(new ImageIcon(flagURL));
        }
        return c;
    }
}