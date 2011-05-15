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

package net.sourceforge.atunes.kernel.modules.notify;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.kernel.modules.notify.Notify.NotifyNotification;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.TempFolder;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.GenericImageSize;
import net.sourceforge.atunes.model.ImageSize;
import net.sourceforge.atunes.utils.ImageUtils;

public class LibnotifyNotifications implements Notifications {

    private final class ShowNotificationRunnable implements Runnable {
		private final AudioObject audioObject;

		private ShowNotificationRunnable(AudioObject audioObject) {
			this.audioObject = audioObject;
		}

		@Override
		public void run() {
			if (available) {
				ImageIcon imageForAudioObject = audioObject.getImage(ImageSize.SIZE_200);
				if (imageForAudioObject == null) {
					imageForAudioObject = audioObject.getGenericImage(GenericImageSize.MEDIUM).getIcon(null);
				}
				String image = TempFolder.getInstance().writeImageToTempFolder(ImageUtils.toBufferedImage(imageForAudioObject.getImage()), UUID.randomUUID().toString()).getAbsolutePath();
				NotifyNotification n = Notify.newNotification(audioObject.getTitle(), audioObject.getArtist(), image);
				if (!Notify.show(n)) {
					getLogger().error(LogCategories.NOTIFICATIONS, "could not show notification - libnotify");
				}
			} else {
				getLogger().error(LogCategories.NOTIFICATIONS, "libnotify is not available or could not be initialized");
			}
		}
	}

	private Logger logger;

    private ExecutorService executorService;
    
    private boolean available = false;

    public LibnotifyNotifications() {
	    if (!Notify.isNotifyPresent()) {
	        getLogger().error(LogCategories.NOTIFICATIONS, "libnotify is not available");
	        return;
	    }
	    if (!Notify.init("aTunes")) {
	        getLogger().error(LogCategories.NOTIFICATIONS, "could not init libnotify");
	        return;
	    }
        executorService = Executors.newSingleThreadExecutor();
        available = true;
    }

    @Override
    public String getName() {
        return "libnotify";
    }

    @Override
    public void showNotification(final AudioObject audioObject) {
        Runnable r = new ShowNotificationRunnable(audioObject);
        executorService.execute(r);
    }
    
    @Override
    public void disposeNotifications() {
    	// This method can only be called when closing application
	    Notify.uninit();
    }
    
    @Override
    public void updateNotification(ApplicationState newState) {
    }

    /**
     * Getter for logger
     * 
     * @return
     */
    private Logger getLogger() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }

}
