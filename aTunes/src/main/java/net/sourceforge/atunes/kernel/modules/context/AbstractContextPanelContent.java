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
import net.sourceforge.atunes.model.IContextPanelContent;
import net.sourceforge.atunes.model.ILookAndFeelManager;
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
public abstract class AbstractContextPanelContent implements IContextPanelContent {

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
    
    private ILookAndFeelManager lookAndFeelManager;

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextPanelContent#updateContextPanelContent(net.sourceforge.atunes.model.IAudioObject)
	 */
    @Override
	public final void updateContextPanelContent(IAudioObject audioObject) {
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

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextPanelContent#clearContextPanelContent()
	 */
    @Override
	public void clearContextPanelContent() {
        parentPanel.setEnabled(false);
        parentPanel.setVisible(false);
        if (worker != null) {
            worker.cancel(true);
        }
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextPanelContent#isScrollNeeded()
	 */
    @Override
	public boolean isScrollNeeded() {
        return false;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextPanelContent#getDataSourceParameters(net.sourceforge.atunes.model.IAudioObject)
	 */
    @Override
	public abstract Map<String, ?> getDataSourceParameters(IAudioObject audioObject);

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextPanelContent#updateContentWithDataSourceResult(java.util.Map)
	 */
    @Override
	public abstract void updateContentWithDataSourceResult(Map<String, ?> result);

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextPanelContent#getContentName()
	 */
    @Override
	public abstract String getContentName();

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextPanelContent#getComponent()
	 */
    @Override
	public abstract Component getComponent();

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextPanelContent#getOptions()
	 */
    @Override
	public List<Component> getOptions() {
        return null;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextPanelContent#setParentPanel(javax.swing.JPanel)
	 */
    @Override
	public void setParentPanel(JPanel parentPanel) {
        this.parentPanel = parentPanel;
    }

    @Override
    public JPanel getParentPanel() {
        return parentPanel;
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextPanelContent#getState()
	 */
    @Override
	public IState getState() {
		return state;
	}
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextPanelContent#setState(net.sourceforge.atunes.model.IState)
	 */
    @Override
	public void setState(IState state) {
		this.state = state;
	}
    
    protected IContextInformationSource getDataSource() {
		return dataSource;
	}
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextPanelContent#setDataSource(net.sourceforge.atunes.model.IContextInformationSource)
	 */
    @Override
	public void setDataSource(IContextInformationSource dataSource) {
		this.dataSource = dataSource;
	}
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextPanelContent#setLookAndFeelManager(net.sourceforge.atunes.model.ILookAndFeelManager)
	 */
    @Override
	public void setLookAndFeelManager(ILookAndFeelManager ILookAndFeelManager) {
		this.lookAndFeelManager = ILookAndFeelManager;
	}
    
    protected ILookAndFeelManager getLookAndFeelManager() {
		return lookAndFeelManager;
	}
}
