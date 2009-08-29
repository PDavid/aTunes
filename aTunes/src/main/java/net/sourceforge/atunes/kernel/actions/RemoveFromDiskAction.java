/*
 * aTunes 1.14.0
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

package net.sourceforge.atunes.kernel.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.model.NavigationTableModel;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.controllers.navigation.NavigationController.ViewMode;
import net.sourceforge.atunes.kernel.modules.navigator.DeviceNavigationView;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.navigator.PodcastNavigationView;
import net.sourceforge.atunes.kernel.modules.navigator.RepositoryNavigationView;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedHandler;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.model.Folder;
import net.sourceforge.atunes.kernel.modules.visual.VisualHandler;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.LanguageTool;

public class RemoveFromDiskAction extends Action {

    private static final long serialVersionUID = -6958409532399604195L;

    public RemoveFromDiskAction() {
        super(LanguageTool.getString("REMOVE_FROM_DISK"), ImageLoader.getImage(ImageLoader.DELETE_FILE));
        putValue(SHORT_DESCRIPTION, LanguageTool.getString("REMOVE_FROM_DISK"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Show confirmation
        if (VisualHandler.getInstance().showConfirmationDialog(LanguageTool.getString("REMOVE_CONFIRMATION"), LanguageTool.getString("CONFIRMATION")) == JOptionPane.OK_OPTION) {

            // Podcast view
            if (NavigationHandler.getInstance().getCurrentView() instanceof PodcastNavigationView) {
                int[] rows = ControllerProxy.getInstance().getNavigationController().getNavigationPanel().getNavigationTable().getSelectedRows();
                if (rows.length > 0) {
                    List<AudioObject> songs = new ArrayList<AudioObject>();
                    for (int element : rows) {
                        songs.add(((NavigationTableModel) ControllerProxy.getInstance().getNavigationController().getNavigationPanel().getNavigationTable().getModel())
                                .getSongAt(element));
                    }
                    for (int i = 0; i < songs.size(); i++) {
                        PodcastFeedEntry podcastFeedEntry = (PodcastFeedEntry) songs.get(i);
                        PodcastFeedHandler.getInstance().deleteDownloadedPodcastFeedEntry(podcastFeedEntry);
                    }
                }
                // Repository or device view with folder view mode, folder selected: delete folders instead of content
            } else if ((NavigationHandler.getInstance().getCurrentView() instanceof RepositoryNavigationView || NavigationHandler.getInstance().getCurrentView() instanceof DeviceNavigationView)
                    && NavigationHandler.getInstance().getCurrentViewMode() == ViewMode.FOLDER
                    && ControllerProxy.getInstance().getNavigationController().getPopupMenuCaller() instanceof JTree) {
                TreePath[] paths = NavigationHandler.getInstance().getCurrentView().getTree().getSelectionPaths();
                List<Folder> foldersToRemove = new ArrayList<Folder>();
                if (paths != null) {
                    for (TreePath path : paths) {
                        Object treeNode = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
                        if (treeNode instanceof Folder) {
                            foldersToRemove.add((Folder) treeNode);
                        }
                    }
                }
                RepositoryHandler.getInstance().removeFoldersPhysically(foldersToRemove);

            } else {
                List<AudioFile> files = ControllerProxy.getInstance().getNavigationController().getFilesSelectedInNavigator();
                RepositoryHandler.getInstance().removePhysically(files);
            }
        }
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        return !rootSelected && !selection.isEmpty();
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(List<AudioObject> selection) {
        if (NavigationHandler.getInstance().getCurrentView().equals(NavigationHandler.getInstance().getView(PodcastNavigationView.class))) {
            for (AudioObject ao : selection) {
                if (!((PodcastFeedEntry) ao).isDownloaded()) {
                    return false;
                }
            }
            return true;
        }
        return !selection.isEmpty();
    }
}
