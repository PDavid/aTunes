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

import java.awt.Component;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IContextInformationSource;
import net.sourceforge.atunes.model.IState;

import org.commonjukebox.plugins.model.PluginApi;

/**
 * This class represents a little graphic component used in a context panel to
 * show information about an audio object Information shown is retrieved from a
 * context data source
 * 
 * @author alex
 * 
 */
@PluginApi
public abstract class AbstractContextPanelContent {

    private static final long serialVersionUID = 7059398864514654378L;

    /**
     * Data Source used by this content to retrieve context information
     */
    private IContextInformationSource dataSource;

    /**
     * Worker used to retrieve data
     */
    private ContextInformationSwingWorker worker;

    /**
     * panel that handles this content
     */
    private JPanel parentPanel;
    
    /**
     * State of app
     */
    private IState state;

    /**
     * Updates the context panel content with information of the given audio
     * object
     * 
     * @param audioObject
     */
    protected final void updateContextPanelContent(IAudioObject audioObject) {
        // Get data source parameters and call data source
        callDataSource(getDataSourceParameters(audioObject));
    }

    /**
     * Calls data source to get context information
     * 
     * @param parameters
     */
    private void callDataSource(Map<String, ?> parameters) {
        // Cancel previous worker if it's not done
        if (worker != null && !worker.isDone()) {
            worker.cancel(true);
        }

        // Create a new worker and call it
        worker = new ContextInformationSwingWorker(this, this.dataSource, parameters);
        worker.execute();
    }

    /**
     * Removes content from the context panel content This method must clear all
     * previous information retrieved for previous audio object
     */
    protected void clearContextPanelContent() {
        parentPanel.setEnabled(false);
        parentPanel.setVisible(false);
        if (worker != null) {
            worker.cancel(true);
        }
    }

    /**
     * By default contents don't need special scroll
     * 
     * @return
     */
    protected boolean isScrollNeeded() {
        return false;
    }

    /**
     * Given an audio object returns a map with necessary parameters to call
     * data source
     * 
     * @param audioObject
     * @return Map with parameters to call data source
     */
    protected abstract Map<String, ?> getDataSourceParameters(IAudioObject audioObject);

    /**
     * Given a map containing result from data source updates this content
     * 
     * @param result
     */
    protected abstract void updateContentWithDataSourceResult(Map<String, ?> result);

    /**
     * Returns the content name to be shown in context panel
     * 
     * @return
     */
    protected abstract String getContentName();

    /**
     * Method to return a Swing component with panel content
     */
    protected abstract Component getComponent();

    /**
     * Returns a list of components to be shown in a popup button If this method
     * returns <code>null</code> or empty list button will not be visible
     * (default behaviour)
     * 
     * @return
     */
    protected List<Component> getOptions() {
        return null;
    }

    /**
     * @param parentPanel
     *            the parentTaskPane to set
     */
    protected void setParentPanel(JPanel parentPanel) {
        this.parentPanel = parentPanel;
    }

    /**
     * @return the parentTaskPane
     */
    protected JPanel getParentPanel() {
        return parentPanel;
    }
    
    protected IState getState() {
		return state;
	}
    
    public void setState(IState state) {
		this.state = state;
	}
    
    protected IContextInformationSource getDataSource() {
		return dataSource;
	}
    
    public void setDataSource(IContextInformationSource dataSource) {
		this.dataSource = dataSource;
	}
    
    

}
