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

package net.sourceforge.atunes.kernel.modules.context.artist;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanelContent;
import net.sourceforge.atunes.kernel.modules.context.TracksTableFactory;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.IArtistTopTracks;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.ITrackInfo;
import net.sourceforge.atunes.utils.DesktopUtils;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Popular tracks of an artist
 * 
 * @author alex
 * 
 */
public class ArtistTopTracksContent extends AbstractContextPanelContent {

    private static final long serialVersionUID = -5538266144953409867L;

    private JTable tracksTable;
    
    private JMenuItem createPlayList;
    
    private IArtistTopTracks lastTopTracks;
    
    private IRepositoryHandler repositoryHandler;
    
    private class CreatePlaylistWithPopularTracksActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	// Get artist files from repository and match by title with top tracks to create a play list
    		Artist artist = repositoryHandler.getArtist(lastTopTracks.getArtist());
    		if (artist != null) {
    			List<ILocalAudioObject> audioObjects = artist.getAudioObjects();
    			Map<String, ILocalAudioObject> titles = new HashMap<String, ILocalAudioObject>();
    			for (ILocalAudioObject lao : audioObjects) {
    				if (lao.getTitle() != null) {
    					titles.put(lao.getTitle().toLowerCase(), lao); // Do lower case for a better match
    				}
    			}
    			List<IAudioObject> playlist = new ArrayList<IAudioObject>();
    			for (ITrackInfo track : lastTopTracks.getTracks()) {
    				if (titles.containsKey(track.getTitle().toLowerCase())) {
    					playlist.add(titles.get(track.getTitle().toLowerCase()));
    				}
    			}

    			// Create a new play list with artist as name and audio objects selected
    			Context.getBean(IPlayListHandler.class).newPlayList(artist.getName(), playlist);
    		}
        }
    }

    public ArtistTopTracksContent() {
    	createPlayList = new JMenuItem(I18nUtils.getString("CREATE_PLAYLIST_WITH_TOP_TRACKS"));
    	createPlayList.addActionListener(new CreatePlaylistWithPopularTracksActionListener());
    }

    @Override
    public String getContentName() {
        return I18nUtils.getString("SONGS");
    }

    @Override
    public Map<String, ?> getDataSourceParameters(IAudioObject audioObject) {
    	return Collections.singletonMap(ArtistPopularTracksDataSource.INPUT_AUDIO_OBJECT, audioObject);
    }

    @Override
    public void updateContentWithDataSourceResult(Map<String, ?> result) {
        if (result.containsKey(ArtistPopularTracksDataSource.OUTPUT_TRACKS)) {
        	lastTopTracks = (IArtistTopTracks) result.get(ArtistPopularTracksDataSource.OUTPUT_TRACKS);
            tracksTable.setModel(new ContextArtistTracksTableModel(lastTopTracks));
        } else {
        	lastTopTracks = null;
        }
    }

    @Override
    public void clearContextPanelContent() {
        super.clearContextPanelContent();
        tracksTable.setModel(new ContextArtistTracksTableModel(null));
    }

    @Override
    public Component getComponent() {
        // Create components
    	TracksTableFactory factory = new TracksTableFactory();
    	factory.setLookAndFeelManager(getLookAndFeelManager());
    	tracksTable = factory.getNewTracksTable(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedTrack = tracksTable.getSelectedRow();
                    if (selectedTrack != -1) {
                        ITrackInfo track = ((ContextArtistTracksTableModel) tracksTable.getModel()).getTrack(selectedTrack);
                        DesktopUtils.openURL(track.getUrl());
                    }
                }
            }
        });
        return tracksTable;
    }
    
    @Override
    public boolean isScrollNeeded() {
    	return true;
    }
    
    @Override
    public List<Component> getOptions() {
        List<Component> options = new ArrayList<Component>();
        options.add(createPlayList);
        return options;
    }
    
    public void setRepositoryHandler(IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

}
