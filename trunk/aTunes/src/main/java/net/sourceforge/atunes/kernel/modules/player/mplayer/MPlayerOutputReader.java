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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.ClosingUtils;

/**
 * The Class MPlayerOutputReader.
 */
abstract class MPlayerOutputReader extends Thread {

    protected static final Logger logger = new Logger();

    protected MPlayerEngine engine;
    protected BufferedReader in;
    protected boolean submitted;
    protected boolean started;
    protected int length;
    protected int time;

    /** Pattern of end of play back */
    private static final Pattern endPattern = Pattern.compile(".*\\x2e\\x2e\\x2e\\x20\\(.*\\x20.*\\).*");

    /**
     * Instantiates a new mplayer output reader.
     * 
     * @param engine
     *            the engine
     * @param process
     *            the process
     */
    protected MPlayerOutputReader(MPlayerEngine engine, Process process) {
        this.engine = engine;
        this.in = new BufferedReader(new InputStreamReader(process.getInputStream()));
    }

    /**
     * New instance.
     * 
     * @param handler
     *            the handler
     * @param process
     *            the process
     * @param ao
     *            the ao
     * 
     * @return the mplayer output reader
     */
    static MPlayerOutputReader newInstance(MPlayerEngine handler, Process process, AudioObject ao) {
        if (ao instanceof AudioFile) {
            return new AudioFileMPlayerOutputReader(handler, process, (AudioFile) ao);
        } else if (ao instanceof Radio) {
            return new RadioMPlayerOutputReader(handler, process, (Radio) ao);
        } else if (ao instanceof PodcastFeedEntry) {
            return new PodcastFeedEntryMPlayerOutputReader(handler, process, (PodcastFeedEntry) ao);
        } else {
            throw new IllegalArgumentException("audio object is not from type AudioFile, Radio or PodcastFeedEntry");
        }
    }

    /**
     * Init
     */
    protected void init() {
        // Nothing to do
    }

    /**
     * Reads a line
     * 
     * @param line
     *            the line
     */
    protected void read(String line) {
        // Read progress			
        // MPlayer bug: Duration still inaccurate with mp3 VBR files! Flac duration bug
        if (line.contains("ANS_TIME_POSITION")) {
            time = (int) (Float.parseFloat(line.substring(line.indexOf("=") + 1)) * 1000.0);
            engine.setTime(time);
        }

        // End
        if (endPattern.matcher(line).matches()) {
            engine.currentAudioObjectFinished();
        }
    }

    @Override
    public final void run() {
        String line = null;
        try {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    init();
                }
            });
            while ((line = in.readLine()) != null && !isInterrupted()) {
                final String lineHelp = line;
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        read(lineHelp);
                    }
                });
            }
        } catch (final IOException e) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    engine.handlePlayerEngineError(e);
                }
            });
        } finally {
            ClosingUtils.close(in);
        }
    }

}
