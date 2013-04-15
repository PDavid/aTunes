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

package net.sourceforge.atunes.kernel.modules.state;

/**
 * Result of preferences validation and processing
 * 
 * @author alex
 * 
 */
class PreferencesValidationResult {

	private PreferencesValidationException error;

	private boolean needsRestart;

	/**
	 * @return the error
	 */
	public PreferencesValidationException getError() {
		return error;
	}

	/**
	 * @param error
	 *            the error to set
	 */
	public void setError(PreferencesValidationException error) {
		this.error = error;
	}

	/**
	 * @return the needsRestart
	 */
	public boolean isNeedsRestart() {
		return needsRestart;
	}

	/**
	 * @param needsRestart
	 *            the needsRestart to set
	 */
	public void setNeedsRestart(boolean needsRestart) {
		this.needsRestart = needsRestart;
	}

	/**
	 * @return if validating error happened
	 */
	public boolean hasError() {
		return this.error != null;
	}

}
