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

package net.sourceforge.atunes.kernel.modules.process;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IStateDevice;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Transfer files to a partition/device and checks if filename is valid.
 */
public class TransferToDeviceProcess extends
		AbstractLocalAudioObjectTransferProcess {

	private IStateDevice stateDevice;

	/**
	 * @param stateDevice
	 */
	public void setStateDevice(final IStateDevice stateDevice) {
		this.stateDevice = stateDevice;
	}

	@Override
	public String getProgressDialogTitle() {
		return I18nUtils.getString("COPYING_TO_DEVICE");
	}

	@Override
	protected ILocalAudioObject transferAudioFile(final File destination,
			final ILocalAudioObject file, final List<Exception> thrownExceptions) {
		try {
			return getFileManager().copyFile(file,
					getDirectory(file, destination, true), getName(file, true));
		} catch (IOException e) {
			thrownExceptions.add(e);
			return null;
		}
	}

	@Override
	public String getDirectory(final ILocalAudioObject song,
			final File destination, final boolean isMp3Device) {
		return getDirectory(song, destination, isMp3Device,
				this.stateDevice.getDeviceFolderPathPattern());
	}

	@Override
	public String getName(final ILocalAudioObject file,
			final boolean isMp3Device) {
		return getName(file, isMp3Device,
				this.stateDevice.getDeviceFileNamePattern());
	}

	@Override
	protected String getFileNamePattern() {
		return this.stateDevice.getDeviceFileNamePattern();
	}

	@Override
	protected String getFolderPathPattern() {
		return this.stateDevice.getDeviceFolderPathPattern();
	}
}
