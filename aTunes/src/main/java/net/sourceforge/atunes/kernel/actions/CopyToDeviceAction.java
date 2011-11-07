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

import java.io.File;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.kernel.modules.navigator.PodcastNavigationView;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IDeviceHandler;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IPodcastFeedHandler;
import net.sourceforge.atunes.model.LocalAudioObjectFilter;
import net.sourceforge.atunes.utils.I18nUtils;

public class CopyToDeviceAction extends AbstractActionOverSelectedObjects<IAudioObject> {

    private static final long serialVersionUID = -7689483210176624995L;

    private INavigationHandler navigationHandler;
    
    private IDeviceHandler deviceHandler;
    
    private IPodcastFeedHandler podcastFeedHandler;
    
    /**
     * @param navigationHandler
     */
    public void setNavigationHandler(INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}
    
    /**
     * @param deviceHandler
     */
    public void setDeviceHandler(IDeviceHandler deviceHandler) {
		this.deviceHandler = deviceHandler;
	}
    
    /**
     * @param podcastFeedHandler
     */
    public void setPodcastFeedHandler(IPodcastFeedHandler podcastFeedHandler) {
		this.podcastFeedHandler = podcastFeedHandler;
	}
    
    public CopyToDeviceAction() {
        super(I18nUtils.getString("COPY_TO_DEVICE"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("COPY_TO_DEVICE"));
    }

    @Override
    protected boolean isPreprocessNeeded() {
    	return true;
    }
    
    @Override
    protected IAudioObject preprocessObject(IAudioObject audioObject) {
        if (audioObject instanceof AudioFile) {
            return audioObject;
        } else if (audioObject instanceof IPodcastFeedEntry && ((IPodcastFeedEntry) audioObject).isDownloaded()) {
            String downloadPath = podcastFeedHandler.getDownloadPath((IPodcastFeedEntry) audioObject);
            return new AudioFile(new File(downloadPath));
        }

        return null;
    }

    @Override
    protected void executeAction(List<IAudioObject> objects) {
    	deviceHandler.copyFilesToDevice(new LocalAudioObjectFilter().getLocalAudioObjects(objects));
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        return deviceHandler.isDeviceConnected() && !rootSelected && !selection.isEmpty();
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(List<IAudioObject> selection) {
        if (!deviceHandler.isDeviceConnected()) {
            return false;
        }

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
