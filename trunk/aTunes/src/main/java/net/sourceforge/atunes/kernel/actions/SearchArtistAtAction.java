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

package net.sourceforge.atunes.kernel.actions;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IDesktop;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.ISearch;
import net.sourceforge.atunes.model.ISearchDialog;
import net.sourceforge.atunes.model.IStateCore;
import net.sourceforge.atunes.model.ITreeObject;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Searches an artist in a list of web services
 * @author alex
 *
 */
public class SearchArtistAtAction extends CustomAbstractAction {

	private static final long serialVersionUID = -8934175706272236046L;

	private IDesktop desktop;

	private INavigationHandler navigationHandler;

	private IDialogFactory dialogFactory;

	private IStateCore stateCore;

	private IUnknownObjectChecker unknownObjectChecker;

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param stateCore
	 */
	public void setStateCore(final IStateCore stateCore) {
		this.stateCore = stateCore;
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param desktop
	 */
	public void setDesktop(final IDesktop desktop) {
		this.desktop = desktop;
	}

	/**
	 * @param navigationHandler
	 */
	public void setNavigationHandler(final INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}

	/**
	 * Default constructor
	 */
	public SearchArtistAtAction() {
		super(StringUtils.getString(I18nUtils.getString("SEARCH_ARTIST_AT"), "..."));
		putValue(SHORT_DESCRIPTION, StringUtils.getString(I18nUtils.getString("SEARCH_ARTIST_AT"), "..."));
	}

	@Override
	protected void executeAction() {
		TreePath path = navigationHandler.getCurrentView().getTree().getSelectionPath();
		if (((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject() instanceof IArtist) {
			IArtist a = (IArtist) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
			ISearchDialog dialog = dialogFactory.newDialog(ISearchDialog.class);
			ISearch search = navigationHandler.openSearchDialog(dialog, true);
			if (dialog.isSetAsDefault() && search != null) {
				stateCore.setDefaultSearch(search.toString());
			}
			if (search != null) {
				desktop.openSearch(search, a.getName());
			}
		}
	}

	@Override
	public boolean isEnabledForNavigationTreeSelection(final boolean rootSelected, final List<ITreeObject<?>> selection) {
		if (selection.isEmpty()) {
			return false;
		}

		for (ITreeObject<?> node : selection) {
			if (!(node instanceof IArtist) || unknownObjectChecker.isUnknownArtist((IArtist) node)) {
				return false;
			}
		}

		return true;
	}
}
