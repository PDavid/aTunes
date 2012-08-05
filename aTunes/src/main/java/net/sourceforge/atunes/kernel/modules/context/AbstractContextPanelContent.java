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

package net.sourceforge.atunes.kernel.modules.context;

import java.awt.Component;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import javax.swing.JPanel;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IContextInformationSource;
import net.sourceforge.atunes.model.IContextPanelContent;
import net.sourceforge.atunes.model.IDesktop;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.ITaskService;

import org.commonjukebox.plugins.model.PluginApi;

/**
 * This class represents a little graphic component used in a context panel to
 * show information about an audio object Information shown is retrieved from a
 * context data source
 * 
 * @author alex
 * @param <T>
 */
@PluginApi
public abstract class AbstractContextPanelContent<T extends IContextInformationSource> implements IContextPanelContent<T> {

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
     * Future to access worker once submitted
     */
    private ScheduledFuture<?> future;

    /**
     * panel that handles this content
     */
    private JPanel parentPanel;
    
    private ILookAndFeelManager lookAndFeelManager;
    
    /**
     * Access to desktop
     */
    private IDesktop desktop;
    
    /**
     * Task Service
     */
    private ITaskService taskService;

    /**
     * @param taskService
     */
    public void setTaskService(ITaskService taskService) {
		this.taskService = taskService;
	}
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextPanelContent#updateContextPanelContent(net.sourceforge.atunes.model.IAudioObject)
	 */
    @Override
	public final void updateContextPanelContent(IAudioObject audioObject) {
        callDataSource(audioObject);
    }

    /**
     * Calls data source to get context information
     * 
     * @param audioObject
     */
    private void callDataSource(IAudioObject audioObject) {
    	cancelWorker();

        // Create a new worker and call it
        worker = new ContextInformationSwingWorker(this, this.dataSource, audioObject);
        future = taskService.submitNow(this.getClass().getName(), worker);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextPanelContent#clearContextPanelContent()
	 */
    @Override
	public void clearContextPanelContent() {
        parentPanel.setEnabled(false);
        parentPanel.setVisible(false);
        cancelWorker();
    }
    
    private void cancelWorker() {
    	if (future != null) {
    		future.cancel(true);
    	}
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
	public void setLookAndFeelManager(ILookAndFeelManager iLookAndFeelManager) {
		this.lookAndFeelManager = iLookAndFeelManager;
	}
    
    protected ILookAndFeelManager getLookAndFeelManager() {
		return lookAndFeelManager;
	}
    
    /**
     * @param desktop
     */
    public void setDesktop(IDesktop desktop) {
		this.desktop = desktop;
	}
    
    /**
     * @return access to desktop
     */
    protected IDesktop getDesktop() {
		return desktop;
	}
}
