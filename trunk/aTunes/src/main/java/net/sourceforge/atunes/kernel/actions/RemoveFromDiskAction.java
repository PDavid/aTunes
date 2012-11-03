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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.kernel.modules.repository.DeleteFilesWorker;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IConfirmationDialog;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IFolder;
import net.sourceforge.atunes.model.IIndeterminateProgressDialog;
import net.sourceforge.atunes.model.ILocalAudioObjectFilter;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IPodcastFeedHandler;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.CollectionUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.commons.io.FileUtils;

/**
 * Removes elements from disk
 * @author alex
 *
 */
/**
 * @author alex
 * 
 */
public class RemoveFromDiskAction extends CustomAbstractAction {

    private static final long serialVersionUID = -6958409532399604195L;

    private IIndeterminateProgressDialog dialog;

    private INavigationHandler navigationHandler;

    private IRepositoryHandler repositoryHandler;

    private IOSManager osManager;

    private IPodcastFeedHandler podcastFeedHandler;

    private INavigationView deviceNavigationView;

    private INavigationView podcastNavigationView;

    private INavigationView repositoryNavigationView;

    private IDialogFactory dialogFactory;

    private ILocalAudioObjectFilter localAudioObjectFilter;

    /**
     * @param localAudioObjectFilter
     */
    public void setLocalAudioObjectFilter(
	    final ILocalAudioObjectFilter localAudioObjectFilter) {
	this.localAudioObjectFilter = localAudioObjectFilter;
    }

    /**
     * Constructor
     */
    public RemoveFromDiskAction() {
	super(I18nUtils.getString("REMOVE_FROM_DISK"));
	putValue(SHORT_DESCRIPTION, I18nUtils.getString("REMOVE_FROM_DISK"));
    }

    /**
     * @param dialogFactory
     */
    public void setDialogFactory(final IDialogFactory dialogFactory) {
	this.dialogFactory = dialogFactory;
    }

    /**
     * @param deviceNavigationView
     */
    public void setDeviceNavigationView(
	    final INavigationView deviceNavigationView) {
	this.deviceNavigationView = deviceNavigationView;
    }

    /**
     * @param podcastNavigationView
     */
    public void setPodcastNavigationView(
	    final INavigationView podcastNavigationView) {
	this.podcastNavigationView = podcastNavigationView;
    }

    /**
     * @param repositoryNavigationView
     */
    public void setRepositoryNavigationView(
	    final INavigationView repositoryNavigationView) {
	this.repositoryNavigationView = repositoryNavigationView;
    }

    @Override
    protected void executeAction() {
	// Show confirmation
	IConfirmationDialog confirmationDialog = dialogFactory
		.newDialog(IConfirmationDialog.class);
	confirmationDialog.setMessage(I18nUtils
		.getString("REMOVE_CONFIRMATION"));
	confirmationDialog.showDialog();
	if (confirmationDialog.userAccepted()) {
	    // Podcast view
	    if (navigationHandler.getCurrentView()
		    .equals(podcastNavigationView)) {
		fromPodcastView();
		// Repository or device view with folder view mode, folder
		// selected: delete folders instead of content
	    } else if ((navigationHandler.getCurrentView().equals(
		    repositoryNavigationView) || navigationHandler
		    .getCurrentView().equals(deviceNavigationView))
		    && navigationHandler.getCurrentViewMode() == ViewMode.FOLDER
		    && navigationHandler.isActionOverTree()) {
		fromRepositoryOrDeviceView(repositoryHandler);
	    } else {
		fromOtherViews(repositoryHandler);
	    }
	}
    }

    private void fromOtherViews(final IRepositoryHandler repositoryHandler) {
	final List<IAudioObject> files = navigationHandler
		.getFilesSelectedInNavigator();
	new DeleteFilesWorker(dialogFactory, repositoryHandler,
		localAudioObjectFilter.getLocalAudioObjects(files)).execute();
    }

    private void fromRepositoryOrDeviceView(
	    final IRepositoryHandler repositoryHandler) {
	List<ITreeNode> nodes = navigationHandler.getCurrentView().getTree()
		.getSelectedNodes();
	final List<IFolder> foldersToRemove = new ArrayList<IFolder>();
	if (!CollectionUtils.isEmpty(nodes)) {
	    for (ITreeNode node : nodes) {
		Object treeNode = node.getUserObject();
		if (treeNode instanceof IFolder) {
		    foldersToRemove.add((IFolder) treeNode);
		}
	    }
	}
	repositoryHandler.removeFolders(foldersToRemove);
	SwingUtilities.invokeLater(new Runnable() {
	    @Override
	    public void run() {
		dialog = dialogFactory
			.newDialog(IIndeterminateProgressDialog.class);
		dialog.setTitle(I18nUtils.getString("PLEASE_WAIT"));
		dialog.showDialog();
	    }
	});
	new SwingWorker<Void, Void>() {
	    @Override
	    protected Void doInBackground() {
		for (IFolder folder : foldersToRemove) {
		    try {
			FileUtils.deleteDirectory(folder
				.getFolderPath(osManager));
			Logger.info(StringUtils.getString("Removed folder ",
				folder));
		    } catch (IOException e) {
			Logger.info(StringUtils.getString(
				"Could not remove folder ", folder,
				e.getMessage()));
		    }
		}
		return null;
	    }

	    @Override
	    protected void done() {
		dialog.hideDialog();
	    }
	}.execute();
    }

    private void fromPodcastView() {
	List<IAudioObject> songsAudioObjects = navigationHandler
		.getSelectedAudioObjectsInNavigationTable();
	if (!songsAudioObjects.isEmpty()) {
	    for (IAudioObject ao : songsAudioObjects) {
		podcastFeedHandler
			.deleteDownloadedPodcastFeedEntry((IPodcastFeedEntry) ao);
	    }
	}
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(
	    final boolean rootSelected, final List<ITreeNode> selection) {
	return !rootSelected && !selection.isEmpty();
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(
	    final List<IAudioObject> selection) {
	if (navigationHandler.getCurrentView().equals(podcastNavigationView)) {
	    for (IAudioObject ao : selection) {
		if (!((IPodcastFeedEntry) ao).isDownloaded()) {
		    return false;
		}
	    }
	    return true;
	}
	return !selection.isEmpty();
    }

    /**
     * @param navigationHandler
     */
    public void setNavigationHandler(final INavigationHandler navigationHandler) {
	this.navigationHandler = navigationHandler;
    }

    /**
     * @param repositoryHandler
     */
    public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
	this.repositoryHandler = repositoryHandler;
    }

    /**
     * @param osManager
     */
    public void setOsManager(final IOSManager osManager) {
	this.osManager = osManager;
    }

    /**
     * @param podcastFeedHandler
     */
    public void setPodcastFeedHandler(
	    final IPodcastFeedHandler podcastFeedHandler) {
	this.podcastFeedHandler = podcastFeedHandler;
    }
}
