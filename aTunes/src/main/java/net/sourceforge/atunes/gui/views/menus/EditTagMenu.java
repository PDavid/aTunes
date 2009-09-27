/*
 * aTunes 2.0.0
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
package net.sourceforge.atunes.gui.views.menus;

import javax.swing.JMenu;
import javax.swing.JSeparator;

import net.sourceforge.atunes.gui.model.AudioObjectsSource;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.AutoSetCoversAction;
import net.sourceforge.atunes.kernel.actions.AutoSetGenresAction;
import net.sourceforge.atunes.kernel.actions.AutoSetLyricsAction;
import net.sourceforge.atunes.kernel.actions.AutoSetTagFromFileNamePatternAction;
import net.sourceforge.atunes.kernel.actions.AutoSetTagFromFolderNamePatternAction;
import net.sourceforge.atunes.kernel.actions.AutoSetTitlesAction;
import net.sourceforge.atunes.kernel.actions.AutoSetTracksAction;
import net.sourceforge.atunes.kernel.actions.ClearTagAction;
import net.sourceforge.atunes.kernel.actions.EditTagAction;
import net.sourceforge.atunes.utils.I18nUtils;

public class EditTagMenu extends JMenu {

    private static final long serialVersionUID = -8235925186759302907L;

    public EditTagMenu(boolean playlistMenu, AudioObjectsSource audioObjectsSource) {
        super(I18nUtils.getString("TAGS"));
        add(Actions.getMenuItemForAction(EditTagAction.class, playlistMenu ? EditTagAction.PLAYLIST : EditTagAction.NAVIGATOR, audioObjectsSource));
        add(new JSeparator());
        add(Actions.getMenuItemForAction(AutoSetTagFromFolderNamePatternAction.class, audioObjectsSource));
        add(Actions.getMenuItemForAction(AutoSetTagFromFileNamePatternAction.class, audioObjectsSource));
        add(new JSeparator());
        add(Actions.getMenuItemForAction(AutoSetLyricsAction.class, audioObjectsSource));
        add(Actions.getMenuItemForAction(AutoSetTitlesAction.class, audioObjectsSource));
        add(Actions.getMenuItemForAction(AutoSetTracksAction.class, audioObjectsSource));
        add(Actions.getMenuItemForAction(AutoSetGenresAction.class, audioObjectsSource));
        add(Actions.getMenuItemForAction(AutoSetCoversAction.class, audioObjectsSource));
        add(new JSeparator());
        add(Actions.getMenuItemForAction(ClearTagAction.class, playlistMenu ? ClearTagAction.PLAYLIST : ClearTagAction.NAVIGATOR, audioObjectsSource));
    }
}
