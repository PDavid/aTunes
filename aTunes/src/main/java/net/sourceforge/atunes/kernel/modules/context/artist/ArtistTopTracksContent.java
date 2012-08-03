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
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JTable;

import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanelContent;
import net.sourceforge.atunes.kernel.modules.context.ContextInformationTableFactory;
import net.sourceforge.atunes.kernel.modules.context.ITracksTableListener;
import net.sourceforge.atunes.model.IArtistTopTracks;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.ITrackInfo;
import net.sourceforge.atunes.utils.CollectionUtils;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Popular tracks of an artist
 * 
 * @author alex
 * 
 */
public class ArtistTopTracksContent extends AbstractContextPanelContent<ArtistPopularTracksDataSource> {

    private static final long serialVersionUID = -5538266144953409867L;

    private JTable tracksTable;
    
    private JMenuItem createPlayList;
    
    private IArtistTopTracks lastTopTracks;
    
    private IRepositoryHandler repositoryHandler;
    
    private IPlayListHandler playListHandler;
    
    private ContextInformationTableFactory contextInformationTableFactory;
    
    private ITracksTableListener contextTableURLOpener;
    
    /**
     * @param contextTableURLOpener
     */
    public void setContextTableURLOpener(ITracksTableListener contextTableURLOpener) {
		this.contextTableURLOpener = contextTableURLOpener;
	}
    
    private class CreatePlaylistWithPopularTracksActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
        	// Get titles for top tracks
        	List<String> artistTopTracks = new ArrayList<String>();
        	for (ITrackInfo track : lastTopTracks.getTracks()) {
        		artistTopTracks.add(track.getTitle());
        	}
        	
        	// Find in repository
        	List<ILocalAudioObject> audioObjectsInRepository = repositoryHandler.getAudioObjectsByTitle(lastTopTracks.getArtist(), artistTopTracks);
        	if (!CollectionUtils.isEmpty(audioObjectsInRepository)) {
    			// Create a new play list with artist as name and audio objects selected
    			playListHandler.newPlayList(lastTopTracks.getArtist(), audioObjectsInRepository);
        	}
        }
    }
    
    /**
     * @param contextInformationTableFactory
     */
    public void setContextInformationTableFactory(ContextInformationTableFactory contextInformationTableFactory) {
		this.contextInformationTableFactory = contextInformationTableFactory;
	}

    /**
     * Default constructor
     */
    public ArtistTopTracksContent() {
    	createPlayList = new JMenuItem(I18nUtils.getString("CREATE_PLAYLIST_WITH_TOP_TRACKS"));
    	createPlayList.addActionListener(new CreatePlaylistWithPopularTracksActionListener());
    }
    
    /**
     * @param playListHandler
     */
    public void setPlayListHandler(IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

    @Override
    public String getContentName() {
        return I18nUtils.getString("SONGS");
    }

    @Override
    public void updateContentFromDataSource(ArtistPopularTracksDataSource source) {
        lastTopTracks = source.getTopTracks();
        tracksTable.setModel(new ContextArtistTracksTableModel(lastTopTracks));
    }

    @Override
    public void clearContextPanelContent() {
        super.clearContextPanelContent();
        tracksTable.setModel(new ContextArtistTracksTableModel(null));
    }

    @Override
    public Component getComponent() {
        // Create components
    	tracksTable = contextInformationTableFactory.getNewTracksTable(contextTableURLOpener);
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
    
    /**
     * @param repositoryHandler
     */
    public void setRepositoryHandler(IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}
}
