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
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.IContextHandler;
import net.sourceforge.atunes.model.IContextPanel;
import net.sourceforge.atunes.model.IContextPanelContent;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.Logger;

import org.commonjukebox.plugins.model.PluginApi;

/**
 * This class represents a context panel shown in a context tab. Context panel
 * shows information related to the current audio object active in the
 * application
 * 
 * @author fleax
 */
@PluginApi
public abstract class AbstractContextPanel implements IContextPanel {

    private static final long serialVersionUID = 7870512266932745272L;

    /**
     * Last AudioObject used to update context panel
     */
    private IAudioObject audioObject;

    private Component component;
    
    private IState state;
    
    private List<IContextPanelContent> contents;
    
    private IContextHandler contextHandler;
    
    private ILookAndFeel lookAndFeel;
    
    
    // BEGIN OF METHODS TO BE IMPLEMENTED BY CONCRETE CONTEXT PANELS

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextPanel#getContextPanelName()
	 */
    @Override
	public abstract String getContextPanelName();

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextPanel#getContextPanelTitle(net.sourceforge.atunes.model.IAudioObject)
	 */
    @Override
	public abstract String getContextPanelTitle(IAudioObject audioObject);

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextPanel#getContextPanelIcon(net.sourceforge.atunes.model.IAudioObject)
	 */
    @Override
	public abstract IColorMutableImageIcon getContextPanelIcon(IAudioObject audioObject);

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextPanel#isPanelVisibleForAudioObject(net.sourceforge.atunes.model.IAudioObject)
	 */
    @Override
	public abstract boolean isPanelVisibleForAudioObject(IAudioObject audioObject);

    // END OF METHODS TO BE IMPLEMENTED BY CONCRETE CONTEXT PANELS

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextPanel#updateContextPanel(net.sourceforge.atunes.model.IAudioObject, boolean)
	 */
    @Override
	public final void updateContextPanel(final IAudioObject audioObject, final boolean forceUpdate) {
        if (!EventQueue.isDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    updateContextPanelEDT(audioObject, forceUpdate);
                }
            });
        } else {
            updateContextPanelEDT(audioObject, forceUpdate);
        }
    }

    private void updateContextPanelEDT(IAudioObject audioObject, boolean forceUpdate) {
        // If the AudioObject is the same as used before to update panel then do nothing if forceUpdate is false
        if (!forceUpdate && this.audioObject != null && this.audioObject.equals(audioObject)) {
            return;
        }

        Logger.debug("Updating panel: ", getContextPanelName());
        for (IContextPanelContent content : getContents()) {
            content.clearContextPanelContent();
            content.updateContextPanelContent(audioObject);
        }

        this.audioObject = audioObject;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextPanel#clearContextPanel()
	 */
    @Override
	public final void clearContextPanel() {
        Logger.debug("Clearing panel: ", getContextPanelName());
        for (IContextPanelContent content : getContents()) {
            content.clearContextPanelContent();
        }
        audioObject = null;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextPanel#getUIComponent(net.sourceforge.atunes.model.ILookAndFeel)
	 */
    @Override
	public final Component getUIComponent(ILookAndFeel lookAndFeel) {
    	if (component == null) {
    		JPanel panel = new JPanel(new GridBagLayout()) {
    			/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
    			public Dimension getPreferredSize() {
    				Dimension d = super.getPreferredSize();
    				// Override horizontal preferred width to avoid excessive width of some components
    				d.width = 200;
    				return d;
    			}
    		};
    		panel.setOpaque(false);
    		GridBagConstraints c = new GridBagConstraints();
    		c.gridx = 0;
    		c.gridy = 0;
    		c.weightx = 1;
    		c.fill = GridBagConstraints.HORIZONTAL;
    		c.insets = new Insets(10, 10, 10, 10);
    		int numberOfContents = getContents().size();
    		for (IContextPanelContent content : getContents()) {
    			Component componentToAdd = content.getComponent();
    			if (componentToAdd instanceof JComponent) {
    				((JComponent) componentToAdd).setOpaque(false);
    			}
    			if (content.isScrollNeeded()) {
    				JScrollPane scroll = null;
    		    	if (componentToAdd instanceof JTable) {
    		    		scroll = lookAndFeel.getTableScrollPane((JTable)componentToAdd);
    		    	} else if (componentToAdd instanceof JTree) {
    		    		scroll = lookAndFeel.getTreeScrollPane((JTree)componentToAdd);
    		    	} else if (componentToAdd instanceof JList) {
    		    		scroll = lookAndFeel.getListScrollPane((JList)componentToAdd);
    		    	} else {
    		    		scroll = lookAndFeel.getScrollPane(componentToAdd);
    		    	}
    				// Set a minimum height
    				scroll.setMinimumSize(new Dimension(0, 200));
    				componentToAdd = scroll;
    			}
    			content.setParentPanel(panel);
    			if (c.gridy == numberOfContents - 1) {
    				// Last component will fill also vertically
    				c.weighty = 1;
    				c.fill = GridBagConstraints.BOTH;
    			}
    			panel.add(componentToAdd, c);
    			c.gridy++;
    		}
    		JScrollPane scrollPane = lookAndFeel.getScrollPane(panel);
    		scrollPane.getVerticalScrollBar().setUnitIncrement(50);
    		component = scrollPane;
    	}
    	return component;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextPanel#getTitle()
	 */
    @Override
	public final String getTitle() {
        return getContextPanelTitle(contextHandler.getCurrentAudioObject());
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextPanel#getIcon()
	 */
    @Override
	public final IColorMutableImageIcon getIcon() {
        return getContextPanelIcon(contextHandler.getCurrentAudioObject());
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextPanel#isEnabled()
	 */
    @Override
	public final boolean isEnabled() {
        if (contextHandler.getCurrentAudioObject() == null) {
            return false;
        }
        return true;
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextPanel#isVisible()
	 */
    @Override
	public final boolean isVisible() {
        if (contextHandler.getCurrentAudioObject() == null) {
            return true;
        }
        return isPanelVisibleForAudioObject(contextHandler.getCurrentAudioObject());
    }

	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextPanel#getOptions()
	 */
	@Override
	public List<Component> getOptions() {
		List<Component> components = new ArrayList<Component>();
		for (IContextPanelContent content : getContents()) {
			List<Component> options = content.getOptions();
			if (options != null && !options.isEmpty()) {
				components.addAll(options);
			}
		}
		return components;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result	+ ((getContextPanelName() == null) ? 0 : getContextPanelName().hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		IContextPanel other = (IContextPanel) obj;
		if (getContextPanelName() == null) {
			if (other.getContextPanelName() != null) {
				return false;
			}
		} else if (!getContextPanelName().equals(other.getContextPanelName())) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return getTitle();
	}
	
	protected IState getState() {
		return state;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextPanel#setState(net.sourceforge.atunes.model.IState)
	 */
	@Override
	public void setState(IState state) {
		this.state = state;
	}
	
    private final List<IContextPanelContent> getContents() {
    	return contents;
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextPanel#setContents(java.util.List)
	 */
    @Override
	public final void setContents(List<IContextPanelContent> contents) {
		this.contents = contents;
	}
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextPanel#setContextHandler(net.sourceforge.atunes.model.IContextHandler)
	 */
    @Override
	public void setContextHandler(IContextHandler contextHandler) {
		this.contextHandler = contextHandler;
	}
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextPanel#setLookAndFeel(net.sourceforge.atunes.model.ILookAndFeel)
	 */
    @Override
	public void setLookAndFeel(ILookAndFeel lookAndFeel) {
		this.lookAndFeel = lookAndFeel;
	}
    
    protected ILookAndFeel getLookAndFeel() {
		return lookAndFeel;
	}
}
