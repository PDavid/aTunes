/*
 * aTunes 2.2.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.modules.player.mplayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectValidator;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.ClosingUtils;

abstract class AbstractMPlayerOutputReader extends Thread {

    /** Pattern of end of play back */
    private static final Pattern END_PATTERN = Pattern.compile(".*\\x2e\\x2e\\x2e\\x20\\(.*\\x20.*\\).*");

    private MPlayerEngine engine;
    private BufferedReader in;
    private int length;
    private int time;

    private volatile boolean readStopped = false;

    /**
     * Instantiates a new mplayer output reader.
     * 
     * @param engine
     *            the engine
     * @param process
     *            the process
     */
    protected AbstractMPlayerOutputReader(MPlayerEngine engine, Process process) {
        this.engine = engine;
        this.in = new BufferedReader(new InputStreamReader(process.getInputStream()));
    }

    /**
     * @param engine
     * @param process
     * @param ao
     * @param state
     * @param frame
     * @param playListHandler
     * @param localAudioObjectValidator
     * @return
     */
    static AbstractMPlayerOutputReader newInstance(MPlayerEngine engine, Process process, IAudioObject ao, IState state, IFrame frame, IPlayListHandler playListHandler, ILocalAudioObjectValidator localAudioObjectValidator) {
        if (ao instanceof ILocalAudioObject) {
            return new AudioFileMPlayerOutputReader(engine, process, (ILocalAudioObject) ao, localAudioObjectValidator);
        } else if (ao instanceof IRadio) {
            return new RadioMPlayerOutputReader(engine, process, (Radio) ao, state, frame, playListHandler);
        } else if (ao instanceof IPodcastFeedEntry) {
            return new PodcastFeedEntryMPlayerOutputReader(engine, process, frame, (IPodcastFeedEntry) ao);
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
            setTime((int) (Float.parseFloat(line.substring(line.indexOf('=') + 1)) * 1000.0));
            getEngine().setTime(getTime());
        }

        // End
        if (END_PATTERN.matcher(line).matches() && !readStopped) {
            // Playback finished
            getEngine().currentAudioObjectFinished(true);
        }
    }

    protected void stopRead() {
        readStopped = true;
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
            while ((line = in.readLine()) != null && getEngine().isEnginePlaying()) {
                final String lineHelp = line;
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        read(lineHelp);
                    }
                });
            }
        } catch (final IOException e) {
            getEngine().handlePlayerEngineError(e);
        } finally {
            ClosingUtils.close(in);
        }
    }

    protected void readAndApplyLength(IAudioObject audioObject, String line, boolean readOnlyFromTags) {
        if (line.contains("ANS_LENGTH")) {
            // Length still inaccurate with mp3 VBR files!
            // Apply workaround to get length from audio file properties (read by jaudiotagger) instead of mplayer
            if (readOnlyFromTags) {
                setLength((audioObject.getDuration() * 1000));
            } else {
                setLength((int) (Float.parseFloat(line.substring(line.indexOf('=') + 1)) * 1000.0));
                if (getLength() == 0) {
                    // Length zero is unlikely, so try if tagging library did not do a better job
                    setLength((audioObject.getDuration() * 1000));
                }
            }
            getEngine().setCurrentLength(getLength());
        }
    }

    protected final void setTime(int time) {
        this.time = time;
    }

    protected final int getTime() {
        return time;
    }

    protected final void setLength(int length) {
        this.length = length;
    }

    protected final int getLength() {
        return length;
    }

    protected final MPlayerEngine getEngine() {
        return engine;
    }
}
