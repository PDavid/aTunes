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

package net.sourceforge.atunes.kernel.modules.navigator;

import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import net.sourceforge.atunes.gui.views.menus.EditTagMenu;
import net.sourceforge.atunes.kernel.actions.AbstractActionOverSelectedObjects;
import net.sourceforge.atunes.kernel.actions.AddToPlayListAction;
import net.sourceforge.atunes.kernel.actions.AddToPlayListAfterCurrentAudioObjectAction;
import net.sourceforge.atunes.kernel.actions.CopyToDeviceAction;
import net.sourceforge.atunes.kernel.actions.ExportNavigatorSelectionAction;
import net.sourceforge.atunes.kernel.actions.ExtractPictureAction;
import net.sourceforge.atunes.kernel.actions.OpenFolderFromNavigatorTableAction;
import net.sourceforge.atunes.kernel.actions.PlayNowAction;
import net.sourceforge.atunes.kernel.actions.RemoveFromDiskAction;
import net.sourceforge.atunes.kernel.actions.RenameAudioFileInNavigationTableAction;
import net.sourceforge.atunes.kernel.actions.SetAsPlayListAction;
import net.sourceforge.atunes.kernel.actions.SetFavoriteSongFromNavigatorAction;
import net.sourceforge.atunes.kernel.actions.ShowNavigatorTableItemInfoAction;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.INavigationView;

/**
 * popup menu for repository navigation table
 * 
 * @author alex
 * 
 */
public class RepositoryNavigationViewTablePopupMenu extends JPopupMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2696766030610743331L;

	private IBeanFactory beanFactory;

	private INavigationView repositoryNavigationView;

	/**
	 * 
	 * Initializes menu
	 */
	public void initialize() {
		AbstractActionOverSelectedObjects<IAudioObject> addToPlayListAction = this.beanFactory
				.getBean("addToPlayListFromRepositoryNavigationView",
						AddToPlayListAction.class);
		addToPlayListAction
				.setAudioObjectsSource(this.repositoryNavigationView);
		add(addToPlayListAction);

		AbstractActionOverSelectedObjects<IAudioObject> addToPlayListAfterCurrentAudioObjectAction = this.beanFactory
				.getBean(
						"addToPlayListAfterCurrentAudioObjectFromRepositoryNavigationView",
						AddToPlayListAfterCurrentAudioObjectAction.class);
		addToPlayListAfterCurrentAudioObjectAction
				.setAudioObjectsSource(this.repositoryNavigationView);
		add(addToPlayListAfterCurrentAudioObjectAction);

		SetAsPlayListAction setAsPlayListAction = this.beanFactory.getBean(
				"setAsPlaylistFromRepositoryNavigationView",
				SetAsPlayListAction.class);
		setAsPlayListAction
				.setAudioObjectsSource(this.repositoryNavigationView);
		add(setAsPlayListAction);

		add(this.beanFactory.getBean(PlayNowAction.class));
		add(new JSeparator());
		add(this.beanFactory.getBean(ShowNavigatorTableItemInfoAction.class));
		add(new JSeparator());

		OpenFolderFromNavigatorTableAction openFolderFromNavigatorAction = this.beanFactory
				.getBean("openFolderFromRepositoryNavigationTable",
						OpenFolderFromNavigatorTableAction.class);
		openFolderFromNavigatorAction
				.setAudioObjectsSource(this.repositoryNavigationView);
		add(openFolderFromNavigatorAction);

		add(new JSeparator());
		add(new EditTagMenu(false, this.repositoryNavigationView,
				this.beanFactory));

		ExtractPictureAction extractPictureAction = this.beanFactory.getBean(
				"extractPictureFromRepositoryNavigationView",
				ExtractPictureAction.class);
		extractPictureAction
				.setAudioObjectsSource(this.repositoryNavigationView);
		add(extractPictureAction);

		add(new JSeparator());
		add(this.beanFactory.getBean(RemoveFromDiskAction.class));
		add(this.beanFactory
				.getBean(RenameAudioFileInNavigationTableAction.class));
		add(new JSeparator());

		AbstractActionOverSelectedObjects<IAudioObject> exportAction = this.beanFactory
				.getBean("exportNavigatorSelectionFromRepositoryViewAction",
						ExportNavigatorSelectionAction.class);
		exportAction.setAudioObjectsSource(this.repositoryNavigationView);
		add(exportAction);

		AbstractActionOverSelectedObjects<IAudioObject> copyToDeviceAction = this.beanFactory
				.getBean("copyToDeviceFromRepositoryNavigationView",
						CopyToDeviceAction.class);
		copyToDeviceAction.setAudioObjectsSource(this.repositoryNavigationView);
		add(copyToDeviceAction);

		add(new JSeparator());

		SetFavoriteSongFromNavigatorAction setFavoriteSongFromNavigatorAction = this.beanFactory
				.getBean(SetFavoriteSongFromNavigatorAction.class);
		setFavoriteSongFromNavigatorAction
				.setAudioObjectsSource(this.repositoryNavigationView);
		add(setFavoriteSongFromNavigatorAction);
	}

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param repositoryNavigationView
	 */
	public void setRepositoryNavigationView(
			final INavigationView repositoryNavigationView) {
		this.repositoryNavigationView = repositoryNavigationView;
	}
}
