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

final class Cdda2wavConstants {

	static final String CDDA2WAV_COMMAND_STRING = "cdda2wav";
	static final String ICEDAX_COMMAND_STRING = "icedax";
	static final String SCAN_BUS = "-scanbus";
	static final String SCANDEVICES = "--devices";
	static final String ATA = "dev=ATA";
	static final String DEVICE = "-D";
	static final String LIST_TRACKS = "-J";
	static final String GUI = "-g";
	static final String NO_INFO_FILE = "-H";
	static final String CDDB = "--cddb=1";
	static final String PARANOIA = "-paranoia";
	static final String TRACKS = "-t";
	static final String VERBOSE = "-verbose-level=summary,toc,sectors,titles";
	static final String VERSION = "--version";
	static final String WAVFORMAT = "-output-format=wav";
	
	private Cdda2wavConstants() {}

}
