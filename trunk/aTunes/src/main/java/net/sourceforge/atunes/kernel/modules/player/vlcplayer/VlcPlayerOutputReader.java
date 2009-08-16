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

package net.sourceforge.atunes.kernel.modules.player.vlcplayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.audio.CueTrack;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.ClosingUtils;

class VlcPlayerOutputReader extends Thread {

    protected VlcPlayerEngine engine;
    protected BufferedReader in;
    /** Flags for the length and position of media */
    protected int lenght = 0;
    protected int position = 0;
    protected boolean endReached = false;

    protected static boolean isCueSheet = false;
    private static int cueStartPosition;

    private static long cueTrackDuration;

    /**
     * Instantiates a new m player output reader.
     * 
     * @param engine
     *            the handler
     * @param process
     *            the process
     */
    public VlcPlayerOutputReader(VlcPlayerEngine engine) {
        this.engine = engine;
        try {
            this.in = new BufferedReader(new InputStreamReader(engine.getCommandWriter().getVlcTelnetClient().getIn()));
        } catch (NullPointerException npe) {
            //bad to leave that empty but I don't want to pollute the log
        }
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
     * @return the m player output reader
     */
    static VlcPlayerOutputReader newInstance(VlcPlayerEngine handler, AudioObject ao) {
        isCueSheet = false;
        if (ao instanceof AudioFile) {
            if (AudioFile.isCueFile(((AudioFile) ao).getFile())) {
                cueStartPosition = ((CueTrack) ao).getTrackStartPositionAsInt();
                cueTrackDuration = ((CueTrack) ao).getDuration();
                isCueSheet = true;
            }
            return new AudioFileVlcPlayerOutputReader(handler, (AudioFile) ao);
        } else if (ao instanceof Radio) {
            return new RadioVlcPlayerOutputReader(handler, (Radio) ao);
        } else if (ao instanceof PodcastFeedEntry) {
            return new PodcastFeedEntryVlcPlayerOutputReader(handler, (PodcastFeedEntry) ao);
        } else {
            throw new IllegalArgumentException("audio object is not from type AudioFile, Radio or PodcastFeedEntry");
        }
    }

    protected void init() {
    }

    protected void read(String line) {

        //retrieve the length of the media
        if (lenght == 0) {
            try {
                int retrievedValue = new Long(line).intValue();
                if (retrievedValue > 0) {
                    if (isCueSheet) {
                        retrievedValue = (int) cueTrackDuration;
                    }
                    engine.setCurrentLength(retrievedValue * 1000);
                    lenght = retrievedValue * 1000;
                } else {
                    if (engine.getCommandWriter() != null) {
                        engine.getCommandWriter().sendGetDurationCommand();
                    }
                }
            } catch (Exception ex) {
                if (engine.getCommandWriter() != null) {
                    engine.getCommandWriter().sendGetDurationCommand();
                }
            }

            //retrieve the position of the media
        } else {

            try {
                int retrievedValue = new Integer(line).intValue();
                if (retrievedValue <= lenght) {
                    if (isCueSheet) {
                        retrievedValue = retrievedValue - cueStartPosition;
                    }
                    engine.setTime(retrievedValue * 1000);
                    position = retrievedValue;
                }
            } catch (Exception ex) {
                //ex.printStackTrace();
            }
        }

        //System.out.println(position + " / " + lenght);

        //automatically play next audio if end is reached and player stops 
        if (lenght > 0) {
            endReached = position >= lenght - 1;
        }
        if (isCueSheet && endReached) {
            engine.playNextAudioObject(true);
        }
        if (line.equals("status change: ( stop state: 0 )")) {
            if (endReached) {
                engine.playNextAudioObject(true);
            }
        }

    }

    @Override
    public final void run() {

        String line;

        try {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    init();
                }
            });
            while (in != null && (line = in.readLine()) != null) {
                final String readed = line;
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        read(readed);
                    }
                });
            }
        } catch (final IOException e) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (!(e instanceof SocketException)) {
                        engine.handlePlayerEngineError(e);
                    }
                }
            });
        } finally {
            ClosingUtils.close(in);
        }
    }

}
