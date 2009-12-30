/*
 * aTunes 2.0.0-SNAPSHOT
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
package net.sourceforge.atunes.kernel.modules.player.vlcplayer;

import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;

class AudioFileVlcPlayerOutputReader extends VlcPlayerOutputReader {

    private AudioFile audioFile;

    /**
     * Instantiates a new audio file m player output reader.
     * 
     * @param engine
     *            the engine
     * @param process
     *            the process
     * @param audioFile
     *            the audio file
     */
    AudioFileVlcPlayerOutputReader(VlcPlayerEngine engine, AudioFile audioFile) {
        super(engine);
        this.audioFile = audioFile;
    }

    @Override
    protected void init() {
        super.init();
        //System.out.println("length " + (audioFile.getDuration() * 1000));
        if (AudioFile.isValidAudioFile(audioFile.getFile())) {
            engine.setCurrentLength(audioFile.getDuration() * 1000);
            super.lenght = ((int) audioFile.getDuration());
        }
    }

}
