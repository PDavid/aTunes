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

package net.sourceforge.atunes.kernel.modules.context;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.kernel.modules.plugins.PluginsHandler;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IContextHandler;
import net.sourceforge.atunes.model.IContextPanel;
import net.sourceforge.atunes.model.IContextPanelsContainer;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.Logger;

import org.commonjukebox.plugins.exceptions.PluginSystemException;
import org.commonjukebox.plugins.model.Plugin;
import org.commonjukebox.plugins.model.PluginInfo;
import org.commonjukebox.plugins.model.PluginListener;

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

	@Override
    public void applicationStarted(List<IAudioObject> playList) {
    	addContextPanels(contextPanels);
    	
        // Set previous selected tab
    	setContextTab(getState().getSelectedContextTab());
    	
    	// Enable listener for user selections
    	enableContextComboListener();
    	
    	getFrame().showContextPanel(getState().isUseContext());
    }
    
    @Override
    public void allHandlersInitialized() {
    	if (getState().isUseContext()) {
    		retrieveInfoAndShowInPanel(playListHandler.getCurrentAudioObjectFromVisiblePlayList());
    	}
    }
    
    /**
     * Called when user changes context tab
     */
    public void contextPanelChanged() {
        // Update selected tab
        getState().setSelectedContextTab(getContextTab() != null ? getContextTab().getContextPanelName() : null);
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
        getState().setSelectedContextTab(null);
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
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextHandler#retrieveInfoAndShowInPanel(net.sourceforge.atunes.model.IAudioObject)
	 */
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
        if (ao instanceof ILocalAudioObject) {
        	if ( ((ILocalAudioObject) ao).getFile() != null)
            	lastAudioObjectModificationTime = ((ILocalAudioObject) ao).getFile().lastModified();
        } else {
            lastAudioObjectModificationTime = 0;
        }

        if (getState().isUseContext()) {
            // Enable or disable tabs
            updateContextTabs();

            if (ao == null) {
                // Clear all tabs
                clearContextPanels();
            } else {
                if (audioObjectModified) {
                    clearTabsContent();
                }
                // Retrieve data for audio object. Force Update since audio file is different or has been modified
                retrieveInfo(ao, true);
            }
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
        String selectedTab = getState().getSelectedContextTab();
        // Update current context panel
        for (IContextPanel panel : contextPanels) {
        	if (panel.getContextPanelName().equals(selectedTab)) {
        		panel.updateContextPanel(audioObject, forceUpdate);
        		break;
        	}
        }
    }

    @Override
    public void applicationStateChanged(IState newState) {
        // Show or hide context panel
        showContextPanel(newState.isUseContext());
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
        	IContextPanel newPanel = (IContextPanel) PluginsHandler.getInstance().getNewInstance(plugin);
            contextPanels.add(newPanel);
        } catch (PluginSystemException e) {
            Logger.error(e);
        }
    }

    @Override
    public void pluginDeactivated(PluginInfo plugin, Collection<Plugin> createdInstances) {
        for (Plugin instance : createdInstances) {
        	contextPanels.remove(instance);
            removeContextPanel((IContextPanel) instance);
        }
    }
    
    @Override
    public void selectedAudioObjectChanged(IAudioObject audioObject) {
        if (getState().isUseContext()) {
            retrieveInfoAndShowInPanel(audioObject);
        }
    }
    
    @Override
    public void playListCleared() {
        if (getState().isUseContext()) {
            retrieveInfoAndShowInPanel(null);
            
            if (getState().isStopPlayerOnPlayListClear()) {
            	clearContextPanels();
            }
        }
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextHandler#showContextPanel(boolean)
	 */
    @Override
	public void showContextPanel(boolean show) {
        getState().setUseContext(show);
        getFrame().showContextPanel(show);
        if (show) {
            retrieveInfoAndShowInPanel(playListHandler.getCurrentAudioObjectFromVisiblePlayList());
        }
    }

	/**
	 * Selects context tab
	 * @param selectedContextTab
	 */
	private void setContextTab(String selectedContextTab) {
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
		contextPanelsContainer.updateContextPanels();
	}

	private void removeContextPanel(IContextPanel instance) {
		contextPanelsContainer.removeContextPanel(instance);
	}

	/**
	 * Adds context panels
	 * @param contextPanels
	 */
	private void addContextPanels(List<IContextPanel> contextPanels) {
		for (IContextPanel panel : contextPanels) {
			contextPanelsContainer.addContextPanel(panel);
		}		
	}

	/**
	 * Enables listening for combo box selections by user
	 */
	private void enableContextComboListener() {
		// Wait until initial context panel selection is done
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				contextPanelsContainer.enableContextPanelSelection(new ItemListener() {
					
					@Override
					public void itemStateChanged(ItemEvent e) {
						if (e.getStateChange() == ItemEvent.SELECTED) {
							contextPanelsContainer.showContextPanel((IContextPanel)e.getItem());
							contextPanelChanged();
						}
		            }
		        });	
			}
		});
	}
	
	public void setContextPanels(List<IContextPanel> contextPanels) {
		this.contextPanels = contextPanels;
	}
	
	public void setContextPanelsContainer(IContextPanelsContainer contextPanelsContainer) {
		this.contextPanelsContainer = contextPanelsContainer;
	}
	
	public void setPlayListHandler(IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}
}
