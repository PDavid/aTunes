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

package net.sourceforge.atunes.kernel.modules.notify;

import net.sourceforge.atunes.kernel.modules.notify.Notify.NotifyNotification;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IStateUI;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;

/**
 * Notification for Linux using libnotify
 * @author alex
 *
 */
public class LibnotifyNotificationEngine extends CommonNotificationEngine {

	private IOSManager osManager;

	private IUnknownObjectChecker unknownObjectChecker;

	private final class ShowNotificationRunnable implements Runnable {
		private final IAudioObject audioObject;

		private ShowNotificationRunnable(final IAudioObject audioObject) {
			this.audioObject = audioObject;
		}

		@Override
		public void run() {
			String image = getTemporalImage(audioObject, osManager);
			NotifyNotification n = Notify.newNotification(audioObject.getTitle(), audioObject.getArtist(unknownObjectChecker), image);
			if (!Notify.show(n)) {
				Logger.error("could not show notification - libnotify");
			}
			// Remove image after use
			removeTemporalImage(image);
		}
	}

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	@Override
	public boolean testEngineAvailable() {
		if (osManager.isLinux() || osManager.isSolaris()) {
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
	public void showNotification(final IAudioObject audioObject) {
		new Thread(new ShowNotificationRunnable(audioObject)).start();
	}

	@Override
	public void disposeNotifications() {
		// This method can only be called when closing application
		Notify.uninit();
	}

	@Override
	public void updateNotification(final IStateUI newState) {
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
