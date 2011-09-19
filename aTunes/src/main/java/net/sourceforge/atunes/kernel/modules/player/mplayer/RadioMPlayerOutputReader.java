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

package net.sourceforge.atunes.kernel.modules.player.mplayer;

import java.util.regex.Pattern;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.IContextHandler;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IState;

class RadioMPlayerOutputReader extends AbstractMPlayerOutputReader {

    /** Pattern of end of play back */
    private static final Pattern END_PATTERN = Pattern.compile(".*\\x2e\\x2e\\x2e.*\\(.*\\).*");

    private Radio radio;
    private String lastArtist = "";
    private String lastTitle = "";
    private boolean started;
    private IState state;
    private IFrame frame;
    private IPlayListHandler playListHandler;

    /**
     * Instantiates a new radio m player output reader.
     * 
     * @param engine
     * @param process
     * @param radio
     * @param state
     * @param frame
     * @param playListHandler
     */
    RadioMPlayerOutputReader(MPlayerEngine engine, Process process, Radio radio, IState state, IFrame frame, IPlayListHandler playListHandler) {
        super(engine, process);
        this.radio = radio;
        this.state = state;
        this.frame = frame;
        this.playListHandler = playListHandler;
    }

    @Override
    protected void init() {
        super.init();

        getEngine().setCurrentLength(radio.getDuration() * 1000);
    }

    @Override
    protected void read(String line) {
        super.read(line);

        // When starting playback, update status bar
        if (line.startsWith("Starting playback")) {
        	frame.updateStatusBarWithObjectBeingPlayed(radio);
            if (!started) {
                getEngine().notifyRadioOrPodcastFeedEntry();
                started = true;
            }
        }

        // Read bitrate and frequency of radios
        if (line.startsWith("AUDIO:")) {
            final String[] s = line.split(" ");
            if (s.length >= 2) {
                try {
                    radio.setFrequency(Integer.parseInt(s[1]));
                } catch (NumberFormatException e) {
                    Logger.info("Could not read radio frequency");
                }
            }
            if (s.length >= 7) {
                try {
                    radio.setBitrate((long) Double.parseDouble(s[6]));
                } catch (NumberFormatException e) {
                    Logger.info("Could not read radio bitrate");
                }
            }
            playListHandler.refreshPlayList();
        }

        // Read song info from radio stream
        if (state.isReadInfoFromRadioStream() && line.startsWith("ICY Info:")) {
            try {
                int i = line.indexOf("StreamTitle=");
                int j = line.indexOf(';', i);
                String info = line.substring(i + 13, j - 1);
                int k = info.indexOf('-');
                String artist = info.substring(0, k).trim();
                radio.setArtist(artist);
                String title = info.substring(k + 1, info.length()).trim();
                radio.setTitle(title);
                radio.setSongInfoAvailable(true);
                playListHandler.refreshPlayList();
                if ((!title.equals(lastTitle) || !artist.equals(lastArtist)) && radio.equals(playListHandler.getCurrentAudioObjectFromCurrentPlayList())) {
                    Context.getBean(IContextHandler.class).retrieveInfoAndShowInPanel(radio);
                }
                lastArtist = artist;
                lastTitle = title;
            } catch (IndexOutOfBoundsException e) {
                Logger.info("Could not read song info from radio");
            }
        }

        // End (Quit)
        if (END_PATTERN.matcher(line).matches()) {
            radio.deleteSongInfo();
            playListHandler.refreshPlayList();
        }
    }

}
