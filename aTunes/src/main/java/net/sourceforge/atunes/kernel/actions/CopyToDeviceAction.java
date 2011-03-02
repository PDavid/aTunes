/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

import java.io.File;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.kernel.modules.device.DeviceHandler;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.navigator.PodcastNavigationView;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedHandler;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.I18nUtils;

public class CopyToDeviceAction extends AbstractActionOverSelectedObjects<AudioObject> {

    private static final long serialVersionUID = -7689483210176624995L;

    CopyToDeviceAction() {
        super(I18nUtils.getString("COPY_TO_DEVICE"), Images.getImage(Images.DEVICE), AudioObject.class);
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("COPY_TO_DEVICE"));
    }

    @Override
    protected AudioObject preprocessObject(AudioObject audioObject) {
        if (audioObject instanceof AudioFile) {
            return audioObject;
        } else if (audioObject instanceof PodcastFeedEntry && ((PodcastFeedEntry) audioObject).isDownloaded()) {
            String downloadPath = PodcastFeedHandler.getInstance().getDownloadPath((PodcastFeedEntry) audioObject);
            return new AudioFile(new File(downloadPath));
        }

        return null;
    }

    @Override
    protected void performAction(List<AudioObject> objects) {
        DeviceHandler.getInstance().copyFilesToDevice(AudioFile.getAudioFiles(objects));
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        return DeviceHandler.getInstance().isDeviceConnected() && !rootSelected && !selection.isEmpty();
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(List<AudioObject> selection) {
        if (!DeviceHandler.getInstance().isDeviceConnected()) {
            return false;
        }

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
