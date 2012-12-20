/*
 * aTunes 3.1.0
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

package net.sourceforge.atunes.kernel.modules.process;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IStateDevice;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.apache.commons.io.FileUtils;

/**
 * Transfer files to a partition/device and checks if filename is valid.
 */
public class TransferToDeviceProcess extends AbstractLocalAudioObjectTransferProcess {

	private IStateDevice stateDevice;
	
	/**
	 * @param stateDevice
	 */
	public void setStateDevice(IStateDevice stateDevice) {
		this.stateDevice = stateDevice;
	}
	
    @Override
    public String getProgressDialogTitle() {
        return I18nUtils.getString("COPYING_TO_DEVICE");
    }

    @Override
    protected File transferAudioFile(File destination, ILocalAudioObject file, List<Exception> thrownExceptions) {
        String destDir = getDirectory(file, destination, true);
        String newName = getName(file, true);
        File destFile = new File(StringUtils.getString(destDir, getOsManager().getFileSeparator(), newName));

        try {
            // Now that we (supposedly) have a valid filename write file
            FileUtils.copyFile(file.getFile(), destFile);
        } catch (IOException e) {
            thrownExceptions.add(e);
        }

        return destFile;
    }

    @Override
    public String getDirectory(ILocalAudioObject song, File destination, boolean isMp3Device) {
        return getDirectory(song, destination, isMp3Device, stateDevice.getDeviceFolderPathPattern());
    }

    @Override
    public String getName(ILocalAudioObject file, boolean isMp3Device) {
        return getName(file, isMp3Device, stateDevice.getDeviceFileNamePattern());
    }
    
    @Override
    protected String getFileNamePattern() {
    	return stateDevice.getDeviceFileNamePattern();
    }
    
    @Override
    protected String getFolderPathPattern() {
    	return stateDevice.getDeviceFolderPathPattern();
    }
}
