/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

package net.sourceforge.atunes.kernel.modules.notify;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.modules.player.PlaybackState;
import net.sourceforge.atunes.kernel.modules.player.PlaybackStateListener;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.model.AudioObject;

public final class NotifyHandler extends AbstractHandler implements PlaybackStateListener {

    private static NotifyHandler instance;

    private NotifyHandler() {
    }

    @Override
    public void applicationFinish() {
    }

    @Override
    public void applicationStateChanged(ApplicationState newState) {
    }

    @Override
    protected void initHandler() {
    };

    public static NotifyHandler getInstance() {
        if (instance == null) {
            instance = new NotifyHandler();
        }
        return instance;
    }

    @Override
    public void applicationStarted() {
    }

    /**
     * Show notification
     */
    public void showNotification(AudioObject audioObject) {
        ApplicationState state = ApplicationState.getInstance();
        boolean useLibNotify = state.isUseLibnotify();
        Notifications notifications = null;
        if (useLibNotify) {
            notifications = new LibnotifyNotifications();
        } else {
            notifications = new DefaultNotifications();
        }
        notifications.showNotification(audioObject);
    }

    @Override
    public void playbackStateChanged(PlaybackState newState, AudioObject currentAudioObject) {
        if (ApplicationState.getInstance().isShowOSD() && newState == PlaybackState.PLAYING) {
            // Playing
            NotifyHandler.getInstance().showNotification(currentAudioObject);
        }
    }
}
