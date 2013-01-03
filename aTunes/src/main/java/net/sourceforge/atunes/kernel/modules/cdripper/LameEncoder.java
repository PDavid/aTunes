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
 * The Class LameEncoder.
 */
public class LameEncoder extends AbstractEncoder {

    /** The format name of this encoder */
    private static final String FORMAT_NAME = "MP3";
    private static final String LAME = "lame";
    private static final String QUALITY = "-b";
    private static final String PRESET = "--preset";
    private static final String VERSION = "--version";
    private static final String[] MP3_QUALITIES = { "insane", "extreme",
	    "medium", "standard", "128", "160", "192", "224", "256", "320" };
    static final String MP3_DEFAULT_QUALITY = "medium";

    private Process process;

    /**
     * Creates a new lame encoder
     */
    public LameEncoder() {
	super("mp3", MP3_QUALITIES, MP3_DEFAULT_QUALITY, FORMAT_NAME);
    }

    @Override
    public boolean testEncoder() {
	if (getOsManager().isWindows()) {
	    return true;
	}

	BufferedReader stdInput = null;
	try {
	    Process p = new ProcessBuilder(StringUtils.getString(getOsManager()
		    .getExternalToolsPath(), LAME), VERSION).start();
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
	    return false;
	} catch (InterruptedException e) {
	    return false;
	} finally {
	    ClosingUtils.close(stdInput);
	}
    }

    @Override
    public boolean encode(final File wavFile, final File mp3File) {
	Logger.info(StringUtils.getString("Mp3 encoding process started... ",
		wavFile.getName(), " -> ", mp3File.getName()));
	BufferedReader stdInput = null;
	try {
	    // Prepare and execute the lame command
	    List<String> command = createCommand(wavFile, mp3File);
	    process = new ProcessBuilder(command).start();
	    stdInput = new BufferedReader(new InputStreamReader(
		    process.getErrorStream()));
	    String s = null;
	    int percent = -1;
	    while ((s = stdInput.readLine()) != null) {
		percent = processOutput(s, percent);
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
	    Logger.error(e);
	    return false;
	} catch (InterruptedException e) {
	    Logger.error(StringUtils.getString(
		    "Process execution caused exception ", e));
	    Logger.error(e);
	    return false;
	} finally {
	    ClosingUtils.close(stdInput);
	}
    }

    /**
     * @param s
     * @param percent
     * @return
     */
    private int processOutput(final String s, final int percent) {
	int percentResult = percent;
	if (getListener() != null) {
	    if (s.matches(".*\\(..%\\).*")) {
		int aux = Integer.parseInt((s.substring(s.indexOf('(') + 1,
			s.indexOf('%'))).trim());
		if (aux != percentResult) {
		    percentResult = aux;
		    final int percentHelp = percentResult;

		    SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
			    getListener().notifyProgress(percentHelp);
			}
		    });
		}
	    } else if (s.matches(".*\\(100%\\).*") && percentResult != 100) {
		percentResult = 100;
		SwingUtilities.invokeLater(new Runnable() {
		    @Override
		    public void run() {
			getListener().notifyProgress(100);
		    }
		});
	    }
	}
	return percentResult;
    }

    /**
     * @param wavFile
     * @param mp3File
     * @return
     */
    private List<String> createCommand(final File wavFile, final File mp3File) {
	List<String> command = new ArrayList<String>();
	command.add(StringUtils.getString(
		getOsManager().getExternalToolsPath(), LAME));
	// Presets don't need the -b option, but --preset, so check if preset is
	// used
	if (getQuality().contains("insane") || getQuality().contains("extreme")
		|| getQuality().contains("medium")
		|| getQuality().contains("standard")) {
	    command.add(PRESET);
	} else {
	    command.add(QUALITY);
	}
	command.add(getQuality());

	command.add(FileUtils.getPath(wavFile));
	command.add(FileUtils.getPath(mp3File));
	return command;
    }

    @Override
    public void stop() {
	if (process != null) {
	    process.destroy();
	}
    }
}
