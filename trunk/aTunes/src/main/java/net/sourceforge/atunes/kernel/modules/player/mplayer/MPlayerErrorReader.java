/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.ClosingUtils;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The Class MPlayerErrorReader.
 */
class MPlayerErrorReader extends Thread {

    private MPlayerEngine engine;
    private BufferedReader in;
    private AudioObject audioObject;
    private AbstractMPlayerOutputReader outputReader;

    /**
     * Instantiates a new m player error reader.
     * 
     * @param engine
     *            the engine
     * @param process
     *            the process
     * @param audioObject
     *            the audio object
     */
    MPlayerErrorReader(MPlayerEngine engine, Process process, AbstractMPlayerOutputReader outputReader, AudioObject audioObject) {
        this.engine = engine;
        in = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        this.audioObject = audioObject;
        this.outputReader = outputReader;
    }

    @Override
    public void run() {
        String line = null;
        try {
            while ((line = in.readLine()) != null && !isInterrupted()) {
                if (line.startsWith("File not found")) {
                    // Stop output reader
                    outputReader.stopRead();
                    // Playback finished with error
                    engine.currentAudioObjectFinished(false, I18nUtils.getString("FILE_NOT_FOUND"), ": ", audioObject.getUrl());
                }
            }
        } catch (final IOException e) {
            engine.handlePlayerEngineError(e);
        } finally {
            ClosingUtils.close(in);
        }
    }

}
