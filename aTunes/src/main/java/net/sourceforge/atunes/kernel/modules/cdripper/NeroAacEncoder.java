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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.FileUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class NeroAacEncoder.
 */
public class NeroAacEncoder extends AbstractEncoder {

    /** The format name of this encoder */
    private static final String FORMAT_NAME = "Nero_AAC";
    private static final String NERO_AAC = "neroAacEnc";
    private static final String INPUT = "-if";
    private static final String OUTPUT = "-of";
    // public static final String WRAP = "-w";
    private static final String QUALITY = "-q";
    private static final String VERSION = "-help";
    private static final String[] NERO_AAC_QUALITY = { "0.3", "0.4", "0.5",
	    "0.6", "0.7", "0.8", "0.9", "1.0" };
    private static final String DEFAULT_NERO_AAC_QUALITY = "0.4";

    private Process process;

    @Override
    public boolean testEncoder() {
	// Test for Nero Aac encoder
	BufferedReader stdInput = null;
	try {
	    Process p = new ProcessBuilder(StringUtils.getString(getOsManager()
		    .getExternalToolsPath(), NERO_AAC), VERSION).start();
	    stdInput = new BufferedReader(new InputStreamReader(
		    p.getErrorStream()));

	    String line = null;
	    while ((line = stdInput.readLine()) != null) {
		Logger.debug(line);
	    }

	    int code = p.waitFor();
	    if (code != 0) {
		return false;
	    }
	    return true;
	} catch (IOException e) {
	    Logger.error(e);
	    return false;
	} catch (InterruptedException e) {
	    Logger.error(e);
	    return false;
	} finally {
	    ClosingUtils.close(stdInput);
	}
    }

    /**
     * Creates a new nero aac encoder
     */
    public NeroAacEncoder() {
	super("m4a", NERO_AAC_QUALITY, DEFAULT_NERO_AAC_QUALITY, FORMAT_NAME);
    }

    @Override
    public boolean encode(final File wavFile, final File mp4File) {
	Logger.info(StringUtils.getString("Mp4 encoding process started... ",
		wavFile.getName(), " -> ", mp4File.getName()));
	BufferedReader stdInput = null;
	try {
	    List<String> command = new ArrayList<String>();
	    command.add(StringUtils.getString(getOsManager()
		    .getExternalToolsPath(), NERO_AAC));
	    command.add(QUALITY);
	    command.add(getQuality());
	    // command.add(IGNORE_LENGTH);
	    command.add(INPUT);
	    command.add(FileUtils.getPath(wavFile));
	    command.add(OUTPUT);
	    command.add(FileUtils.getPath(mp4File));
	    process = new ProcessBuilder(command).start();
	    stdInput = new BufferedReader(new InputStreamReader(
		    process.getErrorStream()));

	    // Required to avoid deadlook under Windows
	    String line = null;
	    while ((line = stdInput.readLine()) != null) {
		Logger.debug(line);
		// Enable indeterminate progress bar
		if (getListener() != null) {
		    SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
			    getListener().notifyProgress(-1);
			}
		    });
		}
	    }
	    int code = process.waitFor();
	    if (code != 0) {
		Logger.error(StringUtils.getString("Process returned code ",
			code));
		return false;
	    }
	    Logger.info("Encoded ok!!");
	    return true;
	} catch (IOException e) {
	    Logger.error(StringUtils.getString(
		    "Process execution caused exception ", e));
	    return false;
	} catch (InterruptedException e) {
	    Logger.error(StringUtils.getString(
		    "Process execution caused exception ", e));
	    return false;
	} finally {
	    ClosingUtils.close(stdInput);
	}
    }

    @Override
    public void stop() {
	if (process != null) {
	    process.destroy();
	}
    }
}
