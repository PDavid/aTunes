/*
 * aTunes 2.1.0-SNAPSHOT
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

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.kernel.modules.navigator.DeviceNavigationView;
import net.sourceforge.atunes.kernel.modules.navigator.INavigationHandler;
import net.sourceforge.atunes.kernel.modules.navigator.PodcastNavigationView;
import net.sourceforge.atunes.kernel.modules.navigator.RepositoryNavigationView;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedHandler;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.Folder;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IConfirmationDialog;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IIndeterminateProgressDialog;
import net.sourceforge.atunes.model.IIndeterminateProgressDialogFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.ViewMode;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.commons.io.FileUtils;

public class RemoveFromDiskAction extends CustomAbstractAction {

	private IIndeterminateProgressDialog dialog;
	
	private INavigationHandler navigationHandler;
	
    private final class DeleteFilesWorker extends SwingWorker<Void, Void> {
        private final List<ILocalAudioObject> files;

        private DeleteFilesWorker(List<ILocalAudioObject> files) {
            this.files = files;
        }

        @Override
        protected Void doInBackground() {
            for (ILocalAudioObject audioFile : files) {
                File file = audioFile.getFile();
                if (file != null) {
                    if (!file.delete()) {
                    	Logger.error(StringUtils.getString(file, " not deleted"));
                    }
                }
            }
            return null;
        }

        @Override
        protected void done() {
            dialog.hideDialog();
        }
    }

    private static final long serialVersionUID = -6958409532399604195L;

    public RemoveFromDiskAction() {
        super(I18nUtils.getString("REMOVE_FROM_DISK"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("REMOVE_FROM_DISK"));
        navigationHandler = getBean(INavigationHandler.class);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Show confirmation
        if (getBean(IConfirmationDialog.class).showDialog(I18nUtils.getString("REMOVE_CONFIRMATION"))) {

            // Podcast view
            if (navigationHandler.getCurrentView() instanceof PodcastNavigationView) {
                fromPodcastView();
                // Repository or device view with folder view mode, folder selected: delete folders instead of content
            } else if ((navigationHandler.getCurrentView() instanceof RepositoryNavigationView || navigationHandler.getCurrentView() instanceof DeviceNavigationView)
                    && navigationHandler.getCurrentViewMode() == ViewMode.FOLDER
                    && navigationHandler.isActionOverTree()) {
                fromRepositoryOrDeviceView();
            } else {
                fromOtherViews();
            }
        }
    }

    private void fromOtherViews() {
        final List<IAudioObject> files = navigationHandler.getFilesSelectedInNavigator();
        RepositoryHandler.getInstance().startTransaction();
        RepositoryHandler.getInstance().remove(AudioFile.getAudioFiles(files));
        RepositoryHandler.getInstance().endTransaction();
        SwingUtilities.invokeLater(new Runnable() {
        	@Override
        	public void run() {
        		dialog = getBean(IIndeterminateProgressDialogFactory.class).newDialog(getBean(IFrame.class));
        		dialog.setTitle(I18nUtils.getString("PLEASE_WAIT"));
        		dialog.showDialog();
        	}
        });
        new DeleteFilesWorker(AudioFile.getAudioFiles(files)).execute();
    }

    private void fromRepositoryOrDeviceView() {
        TreePath[] paths = navigationHandler.getCurrentView().getTree().getSelectionPaths();
        final List<Folder> foldersToRemove = new ArrayList<Folder>();
        if (paths != null) {
            for (TreePath path : paths) {
                Object treeNode = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
                if (treeNode instanceof Folder) {
                    foldersToRemove.add((Folder) treeNode);
                }
            }
        }
        RepositoryHandler.getInstance().startTransaction();
        RepositoryHandler.getInstance().removeFolders(foldersToRemove);
        RepositoryHandler.getInstance().endTransaction();
        SwingUtilities.invokeLater(new Runnable() {
        	@Override
        	public void run() {
        		dialog = getBean(IIndeterminateProgressDialogFactory.class).newDialog(getBean(IFrame.class));
        		dialog.setTitle(I18nUtils.getString("PLEASE_WAIT"));
        		dialog.showDialog();
        	}
        });
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                for (Folder folder : foldersToRemove) {
                    try {
                        FileUtils.deleteDirectory(folder.getFolderPath(getBean(IOSManager.class)));
                        Logger.info(StringUtils.getString("Removed folder ", folder));
                    } catch (IOException e) {
                        Logger.info(StringUtils.getString("Could not remove folder ", folder, e.getMessage()));
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
    	List<IAudioObject> songsAudioObjects = navigationHandler.getSelectedAudioObjectsInNavigationTable();
        if (!songsAudioObjects.isEmpty()) {
            for (IAudioObject ao : songsAudioObjects) {
                PodcastFeedHandler.getInstance().deleteDownloadedPodcastFeedEntry((PodcastFeedEntry) ao);
            }
        }
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        return !rootSelected && !selection.isEmpty();
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(List<IAudioObject> selection) {
        if (navigationHandler.getCurrentView().equals(navigationHandler.getView(PodcastNavigationView.class))) {
            for (IAudioObject ao : selection) {
                if (!((IPodcastFeedEntry) ao).isDownloaded()) {
                    return false;
                }
            }
            return true;
        }
        return !selection.isEmpty();
    }
}
