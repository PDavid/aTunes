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

import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.modules.context.ContextHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;

class RadioVlcPlayerOutputReader extends VlcPlayerOutputReader {

    private static final Logger logger = new Logger();
    private Radio radio;
    /** The info line for the radio */
    private String infoLine = "NO_INFO";

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
    RadioVlcPlayerOutputReader(VlcPlayerEngine engine, Radio radio) {
        super(engine);
        this.radio = radio;
    }

    @Override
    protected void init() {
        super.init();
        engine.setCurrentLength(radio.getDuration() * 1000);
        //don't try to get length with sending command to VLC
        super.lenght = Integer.MAX_VALUE;
    }

    @Override
    protected void read(String line) {
        super.read(line);

        //stream info comes from the VLCOutputReader
        if (ApplicationState.getInstance().isReadInfoFromRadioStream() && engine.getVlcOutputReader() != null) {

            String newInfoLine = engine.getVlcOutputReader().getLastLineReaded();

            //only process if informations were updated
            if (!infoLine.equals(newInfoLine)) {
                infoLine = newInfoLine;

                if (infoLine.indexOf(VlcOutputReader.ACCESS_HTTP_DEBUG) > 0) {
                    try {
                        String artist = infoLine.substring(infoLine.indexOf('=') + 1, infoLine.indexOf('-')).trim();
                        String title = infoLine.substring(infoLine.indexOf('-') + 1, infoLine.length()).trim();
                        //System.out.println("artist : '" + artist + "' , Title : '" + title +"'" );
                        radio.setArtist(artist);
                        radio.setTitle(title);
                        radio.setSongInfoAvailable(true);
                        ControllerProxy.getInstance().getPlayListController().refreshPlayList();
                    } catch (IndexOutOfBoundsException e) {
                        logger.info(LogCategories.PLAYER, "Could not read song info from radio");
                    }
                    if (radio.equals(PlayListHandler.getInstance().getCurrentAudioObjectFromCurrentPlayList())) {
                        ContextHandler.getInstance().retrieveInfoAndShowInPanel(radio);
                    }
                }
            }
        }

        if (line.equals("status change: ( stop state: 0 )")) {
            radio.deleteSongInfo();
            ControllerProxy.getInstance().getPlayListController().refreshPlayList();
        }

    }
}
