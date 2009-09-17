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

package net.sourceforge.atunes.kernel.modules.device;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import net.sourceforge.atunes.kernel.modules.process.AudioFileTransferProcess;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.commons.io.FileUtils;

/**
 * Transfer files to a partition/device and checks if filename is valid.
 */
public class TransferToDeviceProcess extends AudioFileTransferProcess {

    private String path;

    /**
     * @param collection
     * @param path
     */
    public TransferToDeviceProcess(Collection<AudioFile> collection, String path) {
        super(collection);
        this.path = path;
    }

    @Override
    public String getProgressDialogTitle() {
        return I18nUtils.getString("COPYING_TO_DEVICE");
    }

    @Override
    protected String getDestination() {
        return this.path;
    }

    @Override
    protected File transferAudioFile(File destination, AudioFile file, List<Exception> thrownExceptions) {
        String destDir = getDirectory(file, destination, true);
        String newName = getName(file, true);
        File destFile = new File(StringUtils.getString(destDir, SystemProperties.FILE_SEPARATOR, newName));

        try {
            // Now that we (supposedly) have a valid filename write file
            FileUtils.copyFile(file.getFile(), destFile);
        } catch (IOException e) {
            thrownExceptions.add(e);
        }

        return destFile;
    }

    @Override
    public String getDirectory(AudioFile song, File destination, boolean isMp3Device) {
        return getDirectory(song, destination, isMp3Device, ApplicationState.getInstance().getDeviceFolderPathPattern());
    }

    @Override
    public String getName(AudioFile file, boolean isMp3Device) {
        return getName(file, isMp3Device, ApplicationState.getInstance().getDeviceFileNamePattern());
    }
}
