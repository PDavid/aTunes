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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.ClosingUtils;

class VlcPlayerOutputReader extends Thread {

    private VlcPlayerEngine engine;
    private  BufferedReader in;
    /** Flags for the length and position of media */
    private  int length = 0;
    private  int position = 0;
    private  boolean endReached = false;

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
        if (ao instanceof AudioFile) {
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
        if (length == 0) {
            try {
                int retrievedValue = new Long(line).intValue();
                if (retrievedValue > 0) {
                    engine.setCurrentLength(retrievedValue * 1000);
                    length = retrievedValue * 1000;
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
                if (retrievedValue <= length) {
                    engine.setTime(retrievedValue * 1000);
                    position = retrievedValue;
                }
            } catch (Exception ex) {
                //ex.printStackTrace();
            }
        }

        //System.out.println(position + " / " + lenght);

        //automatically play next audio if end is reached and player stops 
        if (length > 0) {
            endReached = position >= length - 1;
        }
        if (line.equals("status change: ( stop state: 0 )")) {
            if (endReached) {
                engine.currentAudioObjectFinished();
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
        } catch (IOException e) {
        	engine.handlePlayerEngineError(e);
        } finally {
            ClosingUtils.close(in);
        }
    }

	/**
	 * @return the engine
	 */
	protected VlcPlayerEngine getEngine() {
		return engine;
	}

	/**
	 * @return the length
	 */
	protected int getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	protected void setLength(int length) {
		this.length = length;
	}

}
