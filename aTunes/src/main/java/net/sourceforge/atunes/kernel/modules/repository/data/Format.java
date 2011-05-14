/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.kernel.modules.repository.data;

public enum Format {

    MP3("mp3"), OGG("ogg"), MP4_1("m4a"), MP4_2("mp4"), WAV("wav"), WMA("wma"), FLAC("flac"), APE("ape"), MPC("mpc"), REAL_1("ra"), REAL_2("rm"), MPPLUS("mp+"), MAC("mac");

    private String extension;

    private Format(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }
}
