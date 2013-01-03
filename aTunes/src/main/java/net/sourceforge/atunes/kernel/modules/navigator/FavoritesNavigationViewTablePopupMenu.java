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
import net.sourceforge.atunes.kernel.actions.RemoveFromFavoritesAction;
import net.sourceforge.atunes.kernel.actions.RenameAudioFileInNavigationTableAction;
import net.sourceforge.atunes.kernel.actions.SetAsPlayListAction;
import net.sourceforge.atunes.kernel.actions.ShowNavigatorTableItemInfoAction;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.INavigationView;

/**
 * Table popup menu for favorites navigation view
 * 
 * @author alex
 * 
 */
public class FavoritesNavigationViewTablePopupMenu extends JPopupMenu {

    /**
	 * 
	 */
    private static final long serialVersionUID = -4861386762867007261L;

    private IBeanFactory beanFactory;

    private INavigationView favoritesNavigationView;

    /**
     * Initializes menu
     */
    public void initialize() {
	AbstractActionOverSelectedObjects<IAudioObject> addToPlayListAction = beanFactory
		.getBean("addToPlayListFromFavoritesNavigationView",
			AddToPlayListAction.class);
	addToPlayListAction.setAudioObjectsSource(favoritesNavigationView);
	add(addToPlayListAction);

	AbstractActionOverSelectedObjects<IAudioObject> addToPlayListAfterCurrentAudioObjectAction = beanFactory
		.getBean(
			"addToPlayListAfterCurrentAudioObjectFromFavoritesNavigationView",
			AddToPlayListAfterCurrentAudioObjectAction.class);
	addToPlayListAfterCurrentAudioObjectAction
		.setAudioObjectsSource(favoritesNavigationView);
	add(addToPlayListAfterCurrentAudioObjectAction);

	SetAsPlayListAction setAsPlayListAction = beanFactory.getBean(
		"setAsPlaylistFromFavoritesNavigationView",
		SetAsPlayListAction.class);
	setAsPlayListAction.setAudioObjectsSource(favoritesNavigationView);
	add(setAsPlayListAction);

	add(beanFactory.getBean(PlayNowAction.class));
	add(new JSeparator());
	add(beanFactory.getBean(ShowNavigatorTableItemInfoAction.class));
	add(new JSeparator());

	OpenFolderFromNavigatorTableAction openFolderFromNavigatorAction = beanFactory
		.getBean("openFolderFromFavoritesNavigationTable",
			OpenFolderFromNavigatorTableAction.class);
	openFolderFromNavigatorAction
		.setAudioObjectsSource(favoritesNavigationView);
	add(openFolderFromNavigatorAction);

	add(new JSeparator());
	add(new EditTagMenu(false, favoritesNavigationView, beanFactory));

	ExtractPictureAction extractPictureAction = beanFactory.getBean(
		"extractPictureFromFavoritesNavigationView",
		ExtractPictureAction.class);
	extractPictureAction.setAudioObjectsSource(favoritesNavigationView);
	add(extractPictureAction);

	add(new JSeparator());
	add(beanFactory.getBean(RemoveFromDiskAction.class));
	add(beanFactory.getBean(RenameAudioFileInNavigationTableAction.class));
	add(new JSeparator());

	AbstractActionOverSelectedObjects<IAudioObject> exportAction = beanFactory
		.getBean("exportNavigatorSelectionFromFavoritesViewAction",
			ExportNavigatorSelectionAction.class);
	exportAction.setAudioObjectsSource(favoritesNavigationView);
	add(exportAction);

	AbstractActionOverSelectedObjects<IAudioObject> copyToDeviceAction = beanFactory
		.getBean("copyToDeviceFromFavoritesNavigationView",
			CopyToDeviceAction.class);
	copyToDeviceAction.setAudioObjectsSource(favoritesNavigationView);
	add(copyToDeviceAction);

	add(new JSeparator());
	add(beanFactory.getBean(RemoveFromFavoritesAction.class));
    }

    /**
     * @param beanFactory
     */
    public void setBeanFactory(final IBeanFactory beanFactory) {
	this.beanFactory = beanFactory;
    }

    /**
     * @param favoritesNavigationView
     */
    public void setFavoritesNavigationView(
	    final INavigationView favoritesNavigationView) {
	this.favoritesNavigationView = favoritesNavigationView;
    }

}
