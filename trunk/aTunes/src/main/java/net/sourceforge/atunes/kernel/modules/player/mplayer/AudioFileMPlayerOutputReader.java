/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

package net.sourceforge.atunes.kernel.modules.player.mplayer;

import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.audio.CueTrack;
import net.sourceforge.atunes.misc.log.LogCategories;

/**
 * The Class AudioFileMPlayerOutputReader.
 */
class AudioFileMPlayerOutputReader extends MPlayerOutputReader {

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
    AudioFileMPlayerOutputReader(MPlayerEngine engine, Process process, AudioFile audioFile) {
        super(engine, process);
        this.audioFile = audioFile;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void read(String line) {
        super.read(line);

        // Read length
        if (line.matches(".*ANS_LENGTH.*")) {
            // Length still inaccurate with mp3 VBR files!
            // Apply workaround to get length from audio file properties (read by jaudiotagger) instead of mplayer
            if (AudioFile.isMp3File(audioFile.getFile())) {
                length = (int) (audioFile.getDuration() * 1000);
            } else if (AudioFile.isCueFile(audioFile.getFile())) {
                // If this audio file is a track in cue-sheet, its length should be read from cue-sheet.
                length = (int) (((CueTrack) audioFile).getDuration() * 1000);
            } else {
                length = (int) (Float.parseFloat(line.substring(line.indexOf('=') + 1)) * 1000.0);
                if (length == 0) {
                    // Length zero is unlikely, so try if tagging library did not do a better job
                    length = (int) (audioFile.getDuration() * 1000);
                }
            }
            engine.setCurrentLength(length);
        }

        // MPlayer bug: Workaround (for audio files) for "mute bug" [1868482] 
        if (engine.isMute() && length > 0 && length - time < 2000) {
            logger.debug(LogCategories.PLAYER, "MPlayer 'mute bug' workaround applied");
            engine.playNextAudioObject(true);
            interrupt();
        }
    }
}
