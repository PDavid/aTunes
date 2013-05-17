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
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * "Playlist" application menu
 * 
 * @author alex
 * 
 */
public class PlayListMenu extends JMenu {

	private static final long serialVersionUID = -3624790857729577320L;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param i18nKey
	 */
	public PlayListMenu(final String i18nKey) {
		super(I18nUtils.getString(i18nKey));
	}

	/**
	 * Initializes menu
	 */
	public void initialize() {
		beanFactory.getBean(PlayListMenuFiller.class).fillMenu(this);
		addMenuListener(new MenuListener() {

			@Override
			public void menuSelected(MenuEvent e) {
				PlayListMenu.this.beanFactory.getBean(PlayListMenuFiller.class)
						.updatePlayListMenuItems();
			}

			@Override
			public void menuDeselected(MenuEvent e) {
			}

			@Override
			public void menuCanceled(MenuEvent e) {
			}
		});
	}
}
