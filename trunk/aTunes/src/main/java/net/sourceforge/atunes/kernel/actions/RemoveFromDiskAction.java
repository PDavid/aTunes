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

package net.sourceforge.atunes.kernel.actions;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.kernel.modules.repository.DeleteFilesTask;
import net.sourceforge.atunes.kernel.modules.repository.RemoveFoldersFromDiskBackgroundWorker;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IConfirmationDialog;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IFolder;
import net.sourceforge.atunes.model.ILocalAudioObjectFilter;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IPodcastFeedHandler;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.CollectionUtils;
import net.sourceforge.atunes.utils.I18nUtils;

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

	private INavigationHandler navigationHandler;

	private IRepositoryHandler repositoryHandler;

	private IPodcastFeedHandler podcastFeedHandler;

	private INavigationView deviceNavigationView;

	private INavigationView podcastNavigationView;

	private INavigationView repositoryNavigationView;

	private IDialogFactory dialogFactory;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Constructor
	 */
	public RemoveFromDiskAction() {
		super(I18nUtils.getString("REMOVE_FROM_DISK"));
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
		IConfirmationDialog confirmationDialog = this.dialogFactory
				.newDialog(IConfirmationDialog.class);
		confirmationDialog.setMessage(I18nUtils
				.getString("REMOVE_CONFIRMATION"));
		confirmationDialog.showDialog();
		if (confirmationDialog.userAccepted()) {
			// Podcast view
			if (this.navigationHandler.getCurrentView().equals(
					this.podcastNavigationView)) {
				fromPodcastView();
				// Repository or device view with folder view mode, folder
				// selected: delete folders instead of content
			} else if ((this.navigationHandler.getCurrentView().equals(
					this.repositoryNavigationView) || this.navigationHandler
					.getCurrentView().equals(this.deviceNavigationView))
					&& this.navigationHandler.getCurrentViewMode() == ViewMode.FOLDER
					&& this.navigationHandler.isActionOverTree()) {
				fromRepositoryOrDeviceView(this.repositoryHandler);
			} else {
				fromOtherViews(this.repositoryHandler);
			}
		}
	}

	private void fromOtherViews(final IRepositoryHandler repositoryHandler) {
		DeleteFilesTask task = this.beanFactory.getBean(DeleteFilesTask.class);
		task.setFiles(
				this.beanFactory.getBean(ILocalAudioObjectFilter.class)
						.getLocalAudioObjects(
								this.navigationHandler
										.getFilesSelectedInNavigator()));
		task.execute();
	}

	private void fromRepositoryOrDeviceView(
			final IRepositoryHandler repositoryHandler) {
		List<ITreeNode> nodes = this.navigationHandler.getCurrentView()
				.getTree().getSelectedNodes();
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
		RemoveFoldersFromDiskBackgroundWorker worker = this.beanFactory
				.getBean(RemoveFoldersFromDiskBackgroundWorker.class);
		worker.setFoldersToRemove(foldersToRemove);
		worker.execute();
	}

	private void fromPodcastView() {
		List<IAudioObject> songsAudioObjects = this.navigationHandler
				.getSelectedAudioObjectsInNavigationTable();
		if (!songsAudioObjects.isEmpty()) {
			for (IAudioObject ao : songsAudioObjects) {
				this.podcastFeedHandler
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
		if (this.navigationHandler.getCurrentView().equals(
				this.podcastNavigationView)) {
			for (IAudioObject ao : selection) {
				if (ao instanceof IPodcastFeedEntry) {
					if (!((IPodcastFeedEntry) ao).isDownloaded()) {
						return false;
					}
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
	 * @param podcastFeedHandler
	 */
	public void setPodcastFeedHandler(
			final IPodcastFeedHandler podcastFeedHandler) {
		this.podcastFeedHandler = podcastFeedHandler;
	}
}
