/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

import net.sourceforge.atunes.kernel.actions.AbstractActionOverSelectedObjects;
import net.sourceforge.atunes.kernel.actions.AutoSetCoversAction;
import net.sourceforge.atunes.kernel.actions.AutoSetGenresAction;
import net.sourceforge.atunes.kernel.actions.AutoSetLyricsAction;
import net.sourceforge.atunes.kernel.actions.AutoSetTagFromFileNamePatternAction;
import net.sourceforge.atunes.kernel.actions.AutoSetTagFromFolderNamePatternAction;
import net.sourceforge.atunes.kernel.actions.AutoSetTitlesAction;
import net.sourceforge.atunes.kernel.actions.AutoSetTracksAction;
import net.sourceforge.atunes.kernel.actions.ClearTagNavigatorAction;
import net.sourceforge.atunes.kernel.actions.ClearTagPlaylistAction;
import net.sourceforge.atunes.kernel.actions.EditTagNavigatorAction;
import net.sourceforge.atunes.kernel.actions.EditTagPlaylistAction;
import net.sourceforge.atunes.kernel.actions.RemoveCommonPrefixFromTitlesAction;
import net.sourceforge.atunes.kernel.actions.RemoveCommonSuffixFromTitlesAction;
import net.sourceforge.atunes.model.IAudioObjectsSource;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Edit tag submenu
 * 
 * @author alex
 * 
 */
public final class EditTagMenu extends JMenu {

	private static final long serialVersionUID = -8235925186759302907L;

	/**
	 * @param playlistMenu
	 * @param audioObjectsSource
	 * @param beanFactory
	 */
	public EditTagMenu(final boolean playlistMenu,
			final IAudioObjectsSource audioObjectsSource,
			final IBeanFactory beanFactory) {
		super(I18nUtils.getString("TAGS"));
		addAction(playlistMenu ? EditTagPlaylistAction.class
				: EditTagNavigatorAction.class, audioObjectsSource, beanFactory);
		add(new JSeparator());
		addAction(AutoSetTagFromFolderNamePatternAction.class,
				audioObjectsSource, beanFactory);
		addAction(AutoSetTagFromFileNamePatternAction.class,
				audioObjectsSource, beanFactory);
		add(new JSeparator());
		addAction(RemoveCommonPrefixFromTitlesAction.class, audioObjectsSource,
				beanFactory);
		addAction(RemoveCommonSuffixFromTitlesAction.class, audioObjectsSource,
				beanFactory);
		add(new JSeparator());
		addAction(AutoSetLyricsAction.class, audioObjectsSource, beanFactory);
		addAction(AutoSetTitlesAction.class, audioObjectsSource, beanFactory);
		addAction(AutoSetTracksAction.class, audioObjectsSource, beanFactory);
		addAction(AutoSetGenresAction.class, audioObjectsSource, beanFactory);
		addAction(AutoSetCoversAction.class, audioObjectsSource, beanFactory);
		add(new JSeparator());
		addAction(playlistMenu ? ClearTagPlaylistAction.class
				: ClearTagNavigatorAction.class, audioObjectsSource,
				beanFactory);
	}

	/**
	 * Get and action binded to audio objects source and add to menu
	 * 
	 * @param actionClass
	 * @param audioObjectsSource
	 */
	private void addAction(
			final Class<? extends AbstractActionOverSelectedObjects<?>> actionClass,
			final IAudioObjectsSource audioObjectsSource,
			final IBeanFactory beanFactory) {
		AbstractActionOverSelectedObjects<?> action = beanFactory
				.getBean(actionClass);
		action.setAudioObjectsSource(audioObjectsSource);
		add(action);
	}
}
