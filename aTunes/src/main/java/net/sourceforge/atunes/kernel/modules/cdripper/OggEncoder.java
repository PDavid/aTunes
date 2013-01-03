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
 * The Class OggEncoder.
 */
public class OggEncoder extends AbstractEncoder {

    /** The format name of this encoder */
    private static final String FORMAT_NAME = "OGG";
    private static final String OGGENC = "oggenc";
    private static final String OUTPUT = "-o";
    private static final String QUALITY = "-q";
    private static final String VERSION = "--version";
    private static final String[] OGG_QUALITIES = { "-1", "0", "1", "2", "3",
	    "4", "5", "6", "7", "8", "9", "10" };
    private static final String DEFAULT_OGG_QUALITY = "5";

    private Process process;

    @Override
    public boolean testEncoder() {
	if (getOsManager().isWindows()) {
	    return true;
	}

	BufferedReader stdInput = null;
	// Test for oggenc
	try {
	    Process p = new ProcessBuilder(StringUtils.getString(getOsManager()
		    .getExternalToolsPath(), OGGENC), VERSION).start();
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

    /**
     * Creates a new ogg encoder
     */
    public OggEncoder() {
	super("ogg", OGG_QUALITIES, DEFAULT_OGG_QUALITY, FORMAT_NAME);
    }

    @Override
    public boolean encode(final File wavFile, final File oggFile) {
	Logger.info(StringUtils.getString("Ogg encoding process started... ",
		wavFile.getName(), " -> ", oggFile.getName()));
	BufferedReader stdInput = null;
	try {
	    // Encode the file using oggenc. We could pass the infos for the
	    // tag, but
	    // oggenc is very difficult with special characters so we don't use
	    // it.
	    List<String> command = createCommand(wavFile, oggFile);
	    process = new ProcessBuilder(command).start();
	    stdInput = new BufferedReader(new InputStreamReader(
		    process.getErrorStream()));
	    String s = null;
	    int percent = -1;

	    // Read progress
	    while ((s = stdInput.readLine()) != null) {
		percent = analizeLine(s, percent);
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

    /**
     * @param s
     * @param percent
     * @return
     */
    private int analizeLine(final String s, final int percent) {
	int percentAux = percent;
	if (getListener() != null) {
	    if (s.matches("\t\\[.....%.*")) {
		// Percent values can be for example 0.3% or 0,3%, so be careful
		// with "." and ","
		int decimalPointPosition = s.indexOf('.');
		if (decimalPointPosition == -1) {
		    decimalPointPosition = s.indexOf(',');
		}
		int aux = Integer.parseInt((s.substring(s.indexOf('[') + 1,
			decimalPointPosition).trim()));
		if (aux != percentAux) {
		    percentAux = aux;
		    final int percentHelp = percentAux;
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
	return percentAux;
    }

    /**
     * @param wavFile
     * @param oggFile
     * @return
     */
    private List<String> createCommand(final File wavFile, final File oggFile) {
	List<String> command = new ArrayList<String>();
	command.add(StringUtils.getString(
		getOsManager().getExternalToolsPath(), OGGENC));
	command.add(FileUtils.getPath(wavFile));
	command.add(OUTPUT);
	command.add(FileUtils.getPath(oggFile));
	command.add(QUALITY);
	command.add(getQuality());
	return command;
    }

    @Override
    public void stop() {
	if (process != null) {
	    process.destroy();
	}
    }
}
