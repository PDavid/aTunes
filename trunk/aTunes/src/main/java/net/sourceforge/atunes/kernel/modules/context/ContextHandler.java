/*
 * aTunes 2.0.0-SNAPSHOT
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
package net.sourceforge.atunes.kernel.modules.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.atunes.kernel.Handler;
import net.sourceforge.atunes.kernel.modules.context.album.AlbumContextPanel;
import net.sourceforge.atunes.kernel.modules.context.artist.ArtistContextPanel;
import net.sourceforge.atunes.kernel.modules.context.audioobject.AudioObjectContextPanel;
import net.sourceforge.atunes.kernel.modules.context.similar.SimilarArtistsContextPanel;
import net.sourceforge.atunes.kernel.modules.context.youtube.YoutubeContextPanel;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.visual.VisualHandler;
import net.sourceforge.atunes.kernel.modules.webservices.lastfm.LastFmService;
import net.sourceforge.atunes.kernel.modules.webservices.lyrics.LyricsService;
import net.sourceforge.atunes.kernel.modules.webservices.youtube.YoutubeService;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.model.AudioObject;

import org.commonjukebox.plugins.Plugin;
import org.commonjukebox.plugins.PluginInfo;
import org.commonjukebox.plugins.PluginListener;
import org.commonjukebox.plugins.PluginSystemException;

public final class ContextHandler extends Handler implements PluginListener {

    /**
     * Singleton instance of handler
     */
    private static ContextHandler instance;

    /**
     * The current audio object used to retrieve information
     */
    private AudioObject currentAudioObject;

    /**
     * Time stamp when audio object was modified. Used to decide if context info
     * must be updated
     */
    private long lastAudioObjectModificationTime;

    /**
     * Context panels defined
     */
    private List<ContextPanel> contextPanels;

    protected void initHandler() {
        // TODO: Move to a web services handler
        LastFmService.getInstance().updateService();
        LyricsService.getInstance().updateService();
    }

    @Override
    public void applicationStarted() {
        // Set previous selected tab
        // IMPORTANT: this method must be called before adding change listener to avoid firing events when
        // UI is being created
        VisualHandler.getInstance().getContextPanel().setSelectedIndex(ApplicationState.getInstance().getSelectedContextTab());

        // Add listener for tab changes
        VisualHandler.getInstance().getContextPanel().getTabbedPane().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                contextPanelChanged();
            }
        });
    }

    /**
     * Returns context panels
     * 
     * @return
     */
    public List<ContextPanel> getContextPanels() {
        if (contextPanels == null) {
            contextPanels = new ArrayList<ContextPanel>();
            // TODO: Put here every new context panel
            contextPanels.add(new AudioObjectContextPanel());
            contextPanels.add(new AlbumContextPanel());
            contextPanels.add(new ArtistContextPanel());
            contextPanels.add(new SimilarArtistsContextPanel());
            contextPanels.add(new YoutubeContextPanel());
        }
        return contextPanels;
    }

    /**
     * Gets the single instance of ContextHandler.
     * 
     * @return single instance of ContextHandler
     */
    public static ContextHandler getInstance() {
        if (instance == null) {
            instance = new ContextHandler();
        }
        return instance;
    }

    /**
     * Called when user changes context tab
     */
    private void contextPanelChanged() {
        // Update selected tab
        ApplicationState.getInstance().setSelectedContextTab(VisualHandler.getInstance().getContextPanel().getSelectedIndex());
        // Call to fill information
        retrieveInfo(currentAudioObject);
    }

    /**
     * Clears all context panels
     */
    public void clear() {
        // Clear all context panels
        for (ContextPanel panel : getContextPanels()) {
            panel.clearContextPanel();
        }
        currentAudioObject = null;
    }

    /**
     * Updates panel with audio object information.
     * 
     * @param ao
     *            the audio object
     */
    public void retrieveInfoAndShowInPanel(AudioObject ao) {
        // Avoid retrieve information about the same audio object twice except if is an AudioFile and has been recently changed
        if (currentAudioObject != null && currentAudioObject.equals(ao)) {
            if (ao instanceof AudioFile) {
                if (((AudioFile) ao).getFile().lastModified() == lastAudioObjectModificationTime) {
                    return;
                }
            } else if (!(ao instanceof Radio)) {
                return;
            }
        }
        currentAudioObject = ao;

        // Update modification time if necessary
        if (ao instanceof AudioFile) {
            lastAudioObjectModificationTime = ((AudioFile) ao).getFile().lastModified();
        } else {
            lastAudioObjectModificationTime = 0;
        }

        if (ApplicationState.getInstance().isUseContext()) {
            // Updates titles
            VisualHandler.getInstance().getContextPanel().updateContextTabsText();

            // Update icons
            VisualHandler.getInstance().getContextPanel().updateContextTabsIcons();

            // Enable or disable tabs
            VisualHandler.getInstance().getContextPanel().enableContextTabs();

            if (ao == null) {
                // Clear all tabs
                clear();
            } else {
                // Retrieve data for audio object
                retrieveInfo(ao);
            }
        }
    }

    /**
     * Retrieve info.
     * 
     * @param audioObject
     *            the audio object
     */
    private void retrieveInfo(AudioObject audioObject) {
        if (audioObject == null) {
            return;
        }
        
        // Context panel can be removed so check index
        int selectedTab = ApplicationState.getInstance().getSelectedContextTab();
        if (selectedTab >= getContextPanels().size()) {
        	selectedTab = 0;
        }
        // Update current context panel
        getContextPanels().get(selectedTab).updateContextPanel(audioObject);
    }

    /**
     * Finish context information
     */
    public void applicationFinish() {
        getLogger().debug(LogCategories.HANDLER);
        LastFmService.getInstance().finishService();
        LyricsService.getInstance().finishService();
    }

    @Override
    public void applicationStateChanged(ApplicationState newState) {
        // TODO: Move this to a webservices handler
        LastFmService.getInstance().updateService();
        LyricsService.getInstance().updateService();
        YoutubeService.getInstance().updateService();
    }

    /**
     * @return the lastAudioObject
     */
    public AudioObject getCurrentAudioObject() {
        return currentAudioObject;
    }
    
    @Override
    public void pluginActivated(PluginInfo plugin) {
    	try {
    		ContextPanel newPanel = (ContextPanel) plugin.getInstance();
    		getContextPanels().add(newPanel);
    		VisualHandler.getInstance().getContextPanel().addContextPanel(newPanel);
    	} catch (PluginSystemException e) {
    		getLogger().error(LogCategories.PLUGINS, e);
    	}
    }
    
    @Override
    public void pluginDeactivated(PluginInfo plugin, Collection<Plugin> createdInstances) {
    	for (Plugin instance : createdInstances) {
    		getContextPanels().remove((ContextPanel)instance);
    		VisualHandler.getInstance().getContextPanel().removeContextPanel((ContextPanel)instance);
    	}
    }
}
