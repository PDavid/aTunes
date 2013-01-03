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
 * The Class FlacEncoder.
 */
public class FlacEncoder extends AbstractEncoder {

	/** The format name of this encoder */
	private static final String FORMAT_NAME = "FLAC";
	private static final String FLAC = "flac";
	private static final String OUTPUT = "-o";
	private static final String FORCE = "-f";
	private static final String VERSION = "--version";
	private static final String[] FLAC_QUALITY = { "-0", "-1", "-2", "-3", "-4", "-5", "-6", "-7", "-8" };
	private static final String DEFAULT_FLAC_QUALITY = "-5";

	private Process process;

	/**
	 * Creates a new FlacEncoder
	 */
	public FlacEncoder() {
		super("flac", FLAC_QUALITY, DEFAULT_FLAC_QUALITY, FORMAT_NAME);
	}

	@Override
	public boolean testEncoder() {
		// Test for flac
		BufferedReader stdInput = null;
		try {
			Process p = new ProcessBuilder(StringUtils.getString(getOsManager().getExternalToolsPath(), FLAC), VERSION).start();
			stdInput = new BufferedReader(new InputStreamReader(p.getErrorStream()));

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
	public boolean encode(final File wavFile, final File file) {
		Logger.info(StringUtils.getString("Flac encoding process started... ", wavFile.getName(), " -> ", file.getName()));
		BufferedReader stdInput = null;
		try {
			// Encode the file using FLAC. We could pass the infos for the tag, but
			// FLAC is very difficult with special characters so we don't use it.
			List<String> command = new ArrayList<String>();
			command.add(StringUtils.getString(getOsManager().getExternalToolsPath(), FLAC));
			command.add(getQuality());
			command.add(FORCE);
			command.add(FileUtils.getPath(wavFile));
			command.add(OUTPUT);
			command.add(FileUtils.getPath(file));
			process = new ProcessBuilder(command).start();
			stdInput = new BufferedReader(new InputStreamReader(process.getErrorStream()));

			String s = null;
			int percent = -1;
			int aux = 0;
			// Read progress
			while ((s = stdInput.readLine()) != null) {
				int index = s.indexOf('%');
				if (getListener() != null && s.contains("% complete, ratio")) {
					aux = Integer.parseInt(s.substring(index - 2, index).trim());
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
				}
			}

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					getListener().notifyProgress(100);
				}
			});

			int code = process.waitFor();
			if (code != 0) {
				Logger.error(StringUtils.getString("Process returned code ", code));
				return false;
			}

			Logger.info("Encoded ok!!");
			return true;

		} catch (IOException e) {
			Logger.error(StringUtils.getString("Process execution caused exception ", e));
			return false;
		} catch (InterruptedException e) {
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
