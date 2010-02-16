/*
 * aTunes 2.0.0-SNAPSHOT
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

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.kernel.modules.notify.Notify.NotifyNotification;
import net.sourceforge.atunes.misc.TempFolder;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.GenericImageSize;
import net.sourceforge.atunes.model.ImageSize;
import net.sourceforge.atunes.utils.ImageUtils;

public class LibnotifyNotifications implements Notifications {

    private Logger logger = new Logger();

    private ExecutorService executorService;

    public LibnotifyNotifications() {
        executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public String getName() {
        return "libnotify";
    }

    @Override
    public void showNotification(final AudioObject audioObject) {
        Runnable r = new Runnable() {

            @Override
            public void run() {
                if (!Notify.isNotifyPresent()) {
                    logger.error(LogCategories.NOTIFICATIONS, "libnotify is not available");
                    return;
                }
                if (!Notify.init("aTunes")) {
                    logger.error(LogCategories.NOTIFICATIONS, "could not init libnotify");
                    return;
                }
                ImageIcon imageForAudioObject = audioObject.getImage(ImageSize.SIZE_200);
                if (imageForAudioObject == null) {
                    imageForAudioObject = audioObject.getGenericImage(GenericImageSize.MEDIUM);
                }
                String image = TempFolder.getInstance().writeImageToTempFolder(ImageUtils.toBufferedImage(imageForAudioObject.getImage()), UUID.randomUUID().toString())
                        .getAbsolutePath();
                NotifyNotification n = Notify.newNotification(audioObject.getTitle(), audioObject.getArtist(), image);
                if (!Notify.show(n)) {
                    logger.error(LogCategories.NOTIFICATIONS, "could not show notification - libnotify");
                    return;
                }
                Notify.uninit();
            }
        };
        executorService.execute(r);
    }
}
