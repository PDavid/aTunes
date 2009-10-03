/*
 * aTunes 2.0.0-SNAPSHOT
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
package net.sourceforge.atunes.kernel.modules.notify;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.ImageIcon;

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
                StringBuilder sb = new StringBuilder();

                sb.append("--icon=");
                ImageIcon imageForAudioObject = audioObject.getImage(ImageSize.SIZE_200);
                if (imageForAudioObject == null) {
                    imageForAudioObject = audioObject.getGenericImage(GenericImageSize.MEDIUM);
                }
                sb.append(TempFolder.getInstance().writeImageToTempFolder(ImageUtils.toBufferedImage(imageForAudioObject.getImage()), UUID.randomUUID().toString())
                        .getAbsolutePath());

                String image = sb.toString();
                sb = new StringBuilder();
                sb.append(audioObject.getArtist());
                sb.append(" - ");
                sb.append(audioObject.getTitle());
                String text = sb.toString();
                ProcessBuilder processBuilder = new ProcessBuilder("notify-send", image, text);

                try {
                    processBuilder.start();
                } catch (IOException e) {
                    logger.error(LogCategories.NOTIFICATIONS, "libnotify is not available");
                }
            }
        };
        executorService.execute(r);
    }
}
