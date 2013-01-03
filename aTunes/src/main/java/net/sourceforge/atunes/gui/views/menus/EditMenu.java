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

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JSeparator;

import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Edit menu
 * @author alex
 *
 */
public class EditMenu extends JMenu {

	private static final long serialVersionUID = -3624790857729577320L;

	private IOSManager osManager;

	private Action showEqualizerAction;
	private Action volumeDownAction;
	private Action volumeUpAction;
	private Action muteAction;
	private Action repairTrackNumbersAction;
	private Action repairGenresAction;
	private Action repairAlbumNamesAction;
	private Action editPreferencesAction;

	/**
	 * @param i18nKey
	 */
	public EditMenu(final String i18nKey) {
		super(I18nUtils.getString(i18nKey));
	}

	/**
	 * @param showEqualizerAction
	 */
	public void setShowEqualizerAction(final Action showEqualizerAction) {
		this.showEqualizerAction = showEqualizerAction;
	}

	/**
	 * @param volumeDownAction
	 */
	public void setVolumeDownAction(final Action volumeDownAction) {
		this.volumeDownAction = volumeDownAction;
	}

	/**
	 * @param volumeUpAction
	 */
	public void setVolumeUpAction(final Action volumeUpAction) {
		this.volumeUpAction = volumeUpAction;
	}

	/**
	 * @param muteAction
	 */
	public void setMuteAction(final Action muteAction) {
		this.muteAction = muteAction;
	}

	/**
	 * @param repairTrackNumbersAction
	 */
	public void setRepairTrackNumbersAction(final Action repairTrackNumbersAction) {
		this.repairTrackNumbersAction = repairTrackNumbersAction;
	}

	/**
	 * @param repairAlbumNamesAction
	 */
	public void setRepairAlbumNamesAction(final Action repairAlbumNamesAction) {
		this.repairAlbumNamesAction = repairAlbumNamesAction;
	}

	/**
	 * @param repairGenresAction
	 */
	public void setRepairGenresAction(final Action repairGenresAction) {
		this.repairGenresAction = repairGenresAction;
	}

	/**
	 * @param editPreferencesAction
	 */
	public void setEditPreferencesAction(final Action editPreferencesAction) {
		this.editPreferencesAction = editPreferencesAction;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * Initializes menu
	 */
	public void initialize() {
		JMenu player = new JMenu(I18nUtils.getString("VOLUME"));
		player.add(showEqualizerAction);
		player.add(volumeUpAction);
		player.add(volumeDownAction);
		player.add(new JCheckBoxMenuItem(muteAction));
		JMenu repair = new JMenu(I18nUtils.getString("REPAIR"));
		repair.add(repairTrackNumbersAction);
		repair.add(repairGenresAction);
		repair.add(repairAlbumNamesAction);
		add(player);
		if (!osManager.areMenuEntriesDelegated()) {
			add(editPreferencesAction);
		}
		add(new JSeparator());
		add(repair);
	}
}
