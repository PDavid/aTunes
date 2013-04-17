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

package net.sourceforge.atunes.kernel.modules.os;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.kernel.BackgroundWorker;
import net.sourceforge.atunes.utils.Logger;

/**
 * Searches for player engine and calls dialog to show search results
 * 
 * @author alex
 * 
 */
public final class MacOSXPlayerEngineSelectionDialogSearchPlayerEngineBackgroundWorker
		extends BackgroundWorker<List<String>, Void> {

	private MacOSXPlayerSelectionDialog dialog;

	/**
	 * @param dialog
	 */
	public void setDialog(final MacOSXPlayerSelectionDialog dialog) {
		this.dialog = dialog;
	}

	@Override
	protected void before() {
	}

	@Override
	protected void whileWorking(List<Void> chunks) {
	}

	@Override
	protected List<String> doInBackground() {
		List<String> matches = new ArrayList<String>();
		// first try path where MPlayerX is installed to find faster, if not,
		// then search all applications path
		try {
			if (executeFind(matches, "find", "/Applications/MPlayerX.app/",
					"-name", "mplayer") != 0) {
				executeFind(matches, "find", "/Applications/", "-name",
						"mplayer");
			}
		} catch (InterruptedException e) {
			Logger.error(e);
		} catch (IOException e) {
			Logger.error(e);
		}
		return matches;
	}

	@Override
	protected void done(final List<String> result) {
		this.dialog.showSearchResults(result);
	}

	/**
	 * Executes a find command and fills list of results, returning process
	 * return code
	 * 
	 * @param matches
	 * @param command
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private int executeFind(final List<String> matches, final String... command)
			throws InterruptedException, IOException {
		if (matches == null || command == null) {
			throw new IllegalArgumentException();
		}
		matches.clear();
		ProcessBuilder pb = new ProcessBuilder(command);
		Process process = pb.start();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				process.getInputStream()));

		String match = null;
		try {
			while ((match = br.readLine()) != null) {
				Logger.debug(match);
				matches.add(match);
			}
		} finally {
			br.close();
		}
		int rc = process.waitFor();
		Logger.debug("Process to search player engine returned code: ", rc);
		return rc;
	}
}