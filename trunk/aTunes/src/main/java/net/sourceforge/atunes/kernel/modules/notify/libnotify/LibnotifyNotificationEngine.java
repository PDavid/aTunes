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

package net.sourceforge.atunes.kernel.modules.notify.libnotify;

import net.sourceforge.atunes.kernel.OsManager;
import net.sourceforge.atunes.kernel.modules.notify.CommonNotificationEngine;
import net.sourceforge.atunes.kernel.modules.notify.libnotify.Notify.NotifyNotification;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.I18nUtils;

public class LibnotifyNotificationEngine extends CommonNotificationEngine {

    private final class ShowNotificationRunnable implements Runnable {
		private final AudioObject audioObject;

		private ShowNotificationRunnable(AudioObject audioObject) {
			this.audioObject = audioObject;
		}

		@Override
		public void run() {
			String image = getTemporalImage(audioObject);
			NotifyNotification n = Notify.newNotification(audioObject.getTitle(), audioObject.getArtist(), image);
			if (!Notify.show(n)) {
				Logger.error("could not show notification - libnotify");
			}
			// Remove image after use
			removeTemporalImage(image);
		}
	}

    public LibnotifyNotificationEngine() {
    }

    @Override
    public boolean testEngineAvailable() {
    	if (OsManager.osType.isLinux() || OsManager.osType.isSolaris()) {
    		if (!Notify.isNotifyPresent()) {
    			Logger.error("libnotify is not available");
    			return false;
    		}
    		if (!Notify.init("aTunes")) {
    			Logger.error("could not init libnotify");
    			return false;
    		}
    		return true;
    	} else {
    		return false;
    	}
    }
    
    @Override
    public String getName() {
        return "Libnotify";
    }

    @Override
    public void showNotification(final AudioObject audioObject) {
        new Thread(new ShowNotificationRunnable(audioObject)).start();
    }
    
    @Override
    public void disposeNotifications() {
    	// This method can only be called when closing application
	    Notify.uninit();
    }
    
    @Override
    public void updateNotification(ApplicationState newState) {
    }
    
    @Override
    public String getDescription() {
    	return I18nUtils.getString("NOTIFICATION_ENGINE_LIBNOTIFY_DESCRIPTION");
    }
    
    @Override
    public String getUrl() {
    	return "http://developer.gnome.org/libnotify/";
    }

}
