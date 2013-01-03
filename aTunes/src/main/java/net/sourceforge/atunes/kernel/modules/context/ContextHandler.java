/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

import java.util.Collection;
import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IContextHandler;
import net.sourceforge.atunes.model.IContextPanel;
import net.sourceforge.atunes.model.IContextPanelsContainer;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IPluginsHandler;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IStateContext;
import net.sourceforge.atunes.model.IStatePlaylist;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.model.IWebServicesHandler;
import net.sourceforge.atunes.utils.Logger;

import org.commonjukebox.plugins.exceptions.PluginSystemException;
import org.commonjukebox.plugins.model.Plugin;
import org.commonjukebox.plugins.model.PluginInfo;
import org.commonjukebox.plugins.model.PluginListener;

/**
 * Responsible of showing context information
 * @author alex
 *
 */
public final class ContextHandler extends AbstractHandler implements PluginListener, IContextHandler {

    /**
     * The current audio object used to retrieve information
     */
    private IAudioObject currentAudioObject;

    /**
     * Time stamp when audio object was modified. Used to decide if context info
     * must be updated
     */
    private long lastAudioObjectModificationTime = 0;

    /**
     * Context panels defined
     */
    private List<IContextPanel> contextPanels;
    
    private IContextPanelsContainer contextPanelsContainer;
    
    private IPlayListHandler playListHandler;
    
    private ITaskService taskService;
    
    private IWebServicesHandler webServicesHandler;
    
    private IStateContext stateContext;
    
    private IStatePlaylist statePlaylist;
    
    /**
     * Task Service
     */
    private ITaskService contextTaskService;
    
    /**
     * @param contextTaskService
     */
    public void setContextTaskService(ITaskService contextTaskService) {
		this.contextTaskService = contextTaskService;
	}

    /**
     * @param statePlaylist
     */
    public void setStatePlaylist(IStatePlaylist statePlaylist) {
		this.statePlaylist = statePlaylist;
	}
    
    /**
     * @param stateContext
     */
    public void setStateContext(IStateContext stateContext) {
		this.stateContext = stateContext;
	}
    
    /**
     * @param webServicesHandler
     */
    public void setWebServicesHandler(IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}
    
    /**
     * @param taskService
     */
    public void setTaskService(ITaskService taskService) {
		this.taskService = taskService;
	}

	@Override
    public void applicationStarted() {
    	addContextPanels(contextPanels);
    	
        // Set previous selected tab
    	setContextTab(stateContext.getSelectedContextTab());
    	
    	getFrame().showContextPanel(stateContext.isUseContext());
    }
    
    /**
     * Called when user changes context tab
     */
    public void contextPanelChanged() {
        // Update selected tab
        stateContext.setSelectedContextTab(getContextTab() != null ? getContextTab().getContextPanelName() : null);
        // Call to fill information: Don't force update since audio object can be the same
        retrieveInfo(currentAudioObject, false);
    }

    /**
     * Clears all context panels
     * 
     */
    private void clearContextPanels() {
        clearTabsContent();
        currentAudioObject = null;

        // Select first tab
        stateContext.setSelectedContextTab(null);
        setContextTab(null);
    }

    /**
     * Clears tabs content
     */
    private void clearTabsContent() {
        // Clear all context panels
        for (IContextPanel panel : contextPanels) {
            panel.clearContextPanel();
        }
    }
    
    @Override
	public void retrieveInfoAndShowInPanel(IAudioObject ao) {
        boolean audioObjectModified = false;
        // Avoid retrieve information about the same audio object twice except if is an LocalAudioObject and has been recently changed
        if (currentAudioObject != null && currentAudioObject.equals(ao)) {
            if (ao instanceof ILocalAudioObject) {
                if (((ILocalAudioObject) ao).getFile().lastModified() == lastAudioObjectModificationTime) {
                    return;
                } else {
                    audioObjectModified = true;
                }
            } else if (!(ao instanceof IRadio)) {
                return;
            }
        }
        currentAudioObject = ao;

        // Update modification time if necessary
        updateModificationTimeOfLastAudioObject(ao);

        // Call to retrieve and show information
        if (stateContext.isUseContext()) {
            retrieveInfoAndShowInPanel(ao, audioObjectModified);
        }
    }

	/**
	 * @param ao
	 */
	private void updateModificationTimeOfLastAudioObject(IAudioObject ao) {
		if (ao instanceof ILocalAudioObject) {
        	if ( ((ILocalAudioObject) ao).getFile() != null) {
            	lastAudioObjectModificationTime = ((ILocalAudioObject) ao).getFile().lastModified();
        	}
        } else {
            lastAudioObjectModificationTime = 0;
        }
	}

	/**
	 * @param ao
	 * @param audioObjectModified
	 */
	private void retrieveInfoAndShowInPanel(IAudioObject ao, boolean audioObjectModified) {
		
		// Enable or disable tabs
		updateContextTabs();

		if (ao == null) {
		    // Clear all tabs
		    clearContextPanels();
		} else {
		    if (audioObjectModified) {
		        clearTabsContent();
		    }
		    // Retrieve data for audio object. Force Update since audio file has been modified
		    retrieveInfo(ao, audioObjectModified);
		}
	}

    /**
     * Retrieve info.
     * 
     * @param audioObject
     *            the audio object
     * @param forceUpdate
     *            If <code>true</code> data will be retrieved and shown even if
     *            the audio object is the same as before This is necessary when
     *            audio object is the same but has been modified so context data
     *            can be different
     */
    private void retrieveInfo(IAudioObject audioObject, boolean forceUpdate) {
        if (audioObject == null) {
            return;
        }

        // Context panel can be removed so check index
        String selectedTab = stateContext.getSelectedContextTab();
        // Update current context panel
        for (IContextPanel panel : contextPanels) {
        	if (panel.getContextPanelName().equals(selectedTab)) {
        		panel.updateContextPanel(audioObject, forceUpdate);
        		break;
        	}
        }
    }

    @Override
    public void applicationStateChanged() {
        // Show or hide context panel
        showContextPanel(stateContext.isUseContext());
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextHandler#getCurrentAudioObject()
	 */
    @Override
	public IAudioObject getCurrentAudioObject() {
        return currentAudioObject;
    }

    @Override
    public void pluginActivated(PluginInfo plugin) {
        try {
        	IContextPanel newPanel = (IContextPanel) getBean(IPluginsHandler.class).getNewInstance(plugin);
            contextPanels.add(newPanel);
        } catch (PluginSystemException e) {
            Logger.error(e);
        }
    }

    @Override
    public void pluginDeactivated(PluginInfo plugin, Collection<Plugin> createdInstances) {
        for (Plugin instance : createdInstances) {
        	contextPanels.remove((IContextPanel)instance);
            removeContextPanel((IContextPanel) instance);
        }
    }
    
    @Override
    public void selectedAudioObjectChanged(IAudioObject audioObject) {
        if (stateContext.isUseContext()) {
            retrieveInfoAndShowInPanel(audioObject);
        }
    }
    
    @Override
    public void playListCleared() {
        if (stateContext.isUseContext()) {
            retrieveInfoAndShowInPanel(null);
            
            if (statePlaylist.isStopPlayerOnPlayListClear()) {
            	clearContextPanels();
            }
        }
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextHandler#showContextPanel(boolean)
	 */
    @Override
	public void showContextPanel(boolean show) {
        stateContext.setUseContext(show);
        getFrame().showContextPanel(show);
        if (show) {
            retrieveInfoAndShowInPanel(playListHandler.getCurrentAudioObjectFromVisiblePlayList());
        }
    }

	/**
	 * Selects context tab
	 * @param selectedContextTab
	 */
    @Override
    public void setContextTab(String selectedContextTab) {
		contextPanelsContainer.setSelectedContextPanel(selectedContextTab);
		contextPanelChanged();
	}
	
	/**
	 * Returns context tab
	 * @return
	 */
	private IContextPanel getContextTab() {
		return contextPanelsContainer.getSelectedContextPanel();
	}

	private void updateContextTabs() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				contextPanelsContainer.updateContextPanels();
			}
		});
	}

	private void removeContextPanel(IContextPanel instance) {
		contextPanelsContainer.removeContextPanel(instance);
		contextPanelsContainer.updateContextPanels();
	}

	/**
	 * Adds context panels
	 * @param contextPanels
	 */
	private void addContextPanels(List<IContextPanel> contextPanels) {
		for (IContextPanel panel : contextPanels) {
			contextPanelsContainer.addContextPanel(panel);
		}		
		contextPanelsContainer.updateContextPanels();
	}

	/**
	 * @param contextPanels
	 */
	public void setContextPanels(List<IContextPanel> contextPanels) {
		this.contextPanels = contextPanels;
	}
	
	/**
	 * @param contextPanelsContainer
	 */
	public void setContextPanelsContainer(IContextPanelsContainer contextPanelsContainer) {
		this.contextPanelsContainer = contextPanelsContainer;
	}
	
	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}
	
	@Override
	public void finishedContextPanelUpdate() {
		taskService.submitNow("Consolidate Web Content", new Runnable() {
			@Override
			public void run() {
				webServicesHandler.consolidateWebContent();
			}
		});
	}
	
	@Override
	public void applicationFinish() {
		contextTaskService.shutdownService();
	}
}
