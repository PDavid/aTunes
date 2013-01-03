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
 * The Class Mp4Encoder.
 */
public class Mp4Encoder extends AbstractEncoder {

    /** The format name of this encoder */
    private static final String FORMAT_NAME = "FAAC_MP4";
    private static final String OGGENC = "faac";
    private static final String OUTPUT = "-o";
    private static final String WRAP = "-w";
    private static final String QUALITY = "-q";
    private static final String[] MP4_QUALITY = { "50", "100", "150", "200",
	    "250", "300", "350", "400", "450", "500" };

    static final String DEFAULT_MP4_QUALITY = "200";

    private Process process;

    /**
     * Creates a new mp4 encoder
     */
    public Mp4Encoder() {
	super("m4a", MP4_QUALITY, DEFAULT_MP4_QUALITY, FORMAT_NAME);
    }

    @Override
    public boolean testEncoder() {
	// Test for faac
	BufferedReader stdInput = null;
	try {
	    Process p = new ProcessBuilder(StringUtils.getString(getOsManager()
		    .getExternalToolsPath(), OGGENC)).start();
	    stdInput = new BufferedReader(new InputStreamReader(
		    p.getErrorStream()));

	    String line = null;
	    while ((line = stdInput.readLine()) != null) {
		Logger.debug(line);
	    }

	    int code = p.waitFor();
	    if (code != 1) {
		return false;
	    }
	    return true;
	} catch (IOException e) {
	    return false;
	} catch (InterruptedException e) {
	    return false;
	} finally {
	    ClosingUtils.close(stdInput);
	}
    }

    @Override
    public boolean encode(final File wavFile, final File mp4File) {
	Logger.info(StringUtils.getString("Mp4 encoding process started... ",
		wavFile.getName(), " -> ", mp4File.getName()));
	BufferedReader stdInput = null;
	try {
	    List<String> command = new ArrayList<String>();
	    command.add(StringUtils.getString(getOsManager()
		    .getExternalToolsPath(), OGGENC));
	    command.add(OUTPUT);
	    command.add(FileUtils.getPath(mp4File));
	    command.add(QUALITY);
	    command.add(getQuality());
	    command.add(WRAP);
	    command.add(FileUtils.getPath(wavFile));
	    process = new ProcessBuilder(command).start();
	    stdInput = new BufferedReader(new InputStreamReader(
		    process.getErrorStream()));
	    String s = null;
	    int percent = -1;

	    // Read progress
	    while ((s = stdInput.readLine()) != null) {
		if (getListener() != null) {
		    if (s.matches(".*(...%).*")) {
			// Percent values can be for example 0.3% or 0,3%, so be
			// careful with "." and ","
			int decimalPointPosition = s.indexOf('%');
			int aux = Integer.parseInt((s.substring(
				s.indexOf('(') + 1, decimalPointPosition)
				.trim()));
			if (aux != percent) {
			    percent = aux;
			    final int percentHelp = percent;
			    SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
				    getListener().notifyProgress(percentHelp);
				}
			    });
			}
		    } else if (s.startsWith("Done")) {
			SwingUtilities.invokeLater(new Runnable() {
			    @Override
			    public void run() {
				getListener().notifyProgress(100);
			    }
			});
		    }
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

	} catch (InterruptedException e) {
	    Logger.error(StringUtils.getString(
		    "Process execution caused exception ", e));
	    return false;
	} catch (IOException e) {
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
