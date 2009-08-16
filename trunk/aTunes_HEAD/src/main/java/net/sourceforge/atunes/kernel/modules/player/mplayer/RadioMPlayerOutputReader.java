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

import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.modules.context.ContextHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.visual.VisualHandler;
import net.sourceforge.atunes.misc.log.LogCategories;

/**
 * The Class RadioMPlayerOutputReader.
 */
class RadioMPlayerOutputReader extends MPlayerOutputReader {

    /** The radio. */
    private Radio radio;
    private String lastArtist = "";
    private String lastTitle = "";

    /**
     * Instantiates a new radio m player output reader.
     * 
     * @param engine
     *            the engine
     * @param process
     *            the process
     * @param radio
     *            the radio
     */
    RadioMPlayerOutputReader(MPlayerEngine engine, Process process, Radio radio) {
        super(engine, process);
        this.radio = radio;
    }

    @Override
    protected void init() {
        super.init();

        engine.setCurrentLength(radio.getDuration() * 1000);
    }

    @Override
    protected void read(String line) {
        super.read(line);

        // When starting playback, update status bar
        if (line.startsWith("Starting playback")) {
            VisualHandler.getInstance().updateStatusBar(radio);
            if (!started) {
                engine.notifyRadioOrPodcastFeedEntry();
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
                    logger.info(LogCategories.PLAYER, "Could not read radio frequency");
                }
            }
            if (s.length >= 7) {
                try {
                    radio.setBitrate((long) Double.parseDouble(s[6]));
                } catch (NumberFormatException e) {
                    logger.info(LogCategories.PLAYER, "Could not read radio bitrate");
                }
            }
            ControllerProxy.getInstance().getPlayListController().refreshPlayList();
        }

        // Read song info from radio stream
        if (ApplicationState.getInstance().isReadInfoFromRadioStream() && line.startsWith("ICY Info:")) {
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
                ControllerProxy.getInstance().getPlayListController().refreshPlayList();
                if ((!title.equals(lastTitle) || !artist.equals(lastArtist)) && radio.equals(PlayListHandler.getInstance().getCurrentAudioObjectFromCurrentPlayList())) {
                    ContextHandler.getInstance().retrieveInfoAndShowInPanel(radio);
                }
                lastArtist = artist;
                lastTitle = title;
            } catch (IndexOutOfBoundsException e) {
                logger.info(LogCategories.PLAYER, "Could not read song info from radio");
            }
        }

        // End (Quit)
        if (line.matches(".*\\x2e\\x2e\\x2e.*\\(.*\\).*")) {
            radio.deleteSongInfo();
            ControllerProxy.getInstance().getPlayListController().refreshPlayList();
        }
    }

}
