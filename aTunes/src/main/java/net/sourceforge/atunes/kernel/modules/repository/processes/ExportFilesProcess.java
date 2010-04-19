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

package net.sourceforge.atunes.kernel.modules.repository.processes;

import java.util.List;

import net.sourceforge.atunes.kernel.modules.process.AbstractAudioFileTransferProcess;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Exports (song) files to a partition/device and checks if filename is valid.
 */
public class ExportFilesProcess extends AbstractAudioFileTransferProcess {

    /** The path. */
    private String path;

    /**
     * Exports songs from the repository to an external path, call via
     * BackgroundExecutor.java . Sets the values
     * 
     * @param songs
     *            List with songs to export
     * @param path
     *            Path to where the files should be exported
     */
    public ExportFilesProcess(List<AudioFile> songs, String path) {
        super(songs);
        this.path = path;
    }

    @Override
    public String getProgressDialogTitle() {
        return I18nUtils.getString("EXPORTING");
    }

    @Override
    protected String getDestination() {
        return this.path;
    }
}
