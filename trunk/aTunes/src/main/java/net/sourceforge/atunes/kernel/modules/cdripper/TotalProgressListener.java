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

package net.sourceforge.atunes.kernel.modules.cdripper;

import java.io.File;
import java.util.List;

import net.sourceforge.atunes.model.IRipperProgressDialog;
import net.sourceforge.atunes.utils.StringUtils;

final class TotalProgressListener implements ProgressListener {

	private final IRipperProgressDialog dialog;
	private final List<File> filesImported;

	TotalProgressListener(final IRipperProgressDialog dialog,
			final List<File> filesImported) {
		this.dialog = dialog;
		this.filesImported = filesImported;
	}

	@Override
	public void notifyFileFinished(final File file) {
		this.filesImported.add(file);
	}

	@Override
	public void notifyProgress(final int value) {
		this.dialog.setTotalProgressValue(value);
		this.dialog.setDecodeProgressValue(0);
		this.dialog.setDecodeProgressValue(StringUtils.getString(0, "%"));
		this.dialog.setEncodeProgressValue(0);
		this.dialog.setEncodeProgressValue(StringUtils.getString(0, "%"));
	}
}