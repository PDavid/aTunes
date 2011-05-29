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
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import net.sourceforge.atunes.gui.images.ColorMutableImageIcon;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;

import org.commonjukebox.plugins.model.PluginApi;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

/**
 * This class represents a context panel shown in a context tab. Context panel
 * shows information related to the current audio object active in the
 * application
 * 
 * @author alex
 */
@PluginApi
public abstract class AbstractContextPanel {

    private static final long serialVersionUID = 7870512266932745272L;

    /**
     * Last AudioObject used to update context panel
     */
    private AudioObject audioObject;

    private Component component;
    
    // BEGIN OF METHODS TO BE IMPLEMENTED BY CONCRETE CONTEXT PANELS

    /**
     * Name of the context panel. This is an internal ID
     * 
     * @return The name of the context panel
     */
    public abstract String getContextPanelName();

    /**
     * Title of the context panel as it will appear in context tab
     * 
     * @param audioObject
     *            The current audio object in context panels or
     *            <code>null</code> if no current audio object is selected
     * 
     * @return The title of the context panel
     */
    protected abstract String getContextPanelTitle(AudioObject audioObject);

    /**
     * Icon of the context panel. This icon is used in context tab
     * 
     * @param audioObject
     *            The current audio object in context panels or
     *            <code>null</code> if no current audio object is selected
     * @return The icon of the context panel
     */
    protected abstract ColorMutableImageIcon getContextPanelIcon(AudioObject audioObject);

    /**
     * List of contents shown in the context panel. Contents are shown in order
     * in context tab
     * 
     * @return List of contents of the context panel
     */
    protected abstract List<AbstractContextPanelContent> getContents();

    /**
     * Indicates if panel must be visible for the given audio object
     * 
     * @param audioObject
     *            The current audio object in context panels or
     *            <code>null</code> if no current audio object is selected
     * @return
     */
    protected abstract boolean isPanelVisibleForAudioObject(AudioObject audioObject);

    // END OF METHODS TO BE IMPLEMENTED BY CONCRETE CONTEXT PANELS

    /**
     * Updates the context panel with information related to the given audio
     * object This method must be called every time the current audio object of
     * the application changes and the panel is visible (the context tab showing
     * this panel is selected)
     * 
     * @param audioObject
     */
    protected final void updateContextPanel(final AudioObject audioObject, final boolean forceUpdate) {
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

    private void updateContextPanelEDT(AudioObject audioObject, boolean forceUpdate) {
        // If the AudioObject is the same as used before to update panel then do nothing if forceUpdate is false
        if (!forceUpdate && this.audioObject != null && this.audioObject.equals(audioObject)) {
            return;
        }

        Logger.debug(LogCategories.CONTEXT, "Updating panel: ", getContextPanelName());
        for (AbstractContextPanelContent content : getContents()) {
            content.clearContextPanelContent();
            content.updateContextPanelContent(audioObject);
        }

        this.audioObject = audioObject;
    }

    /**
     * Removes all content from this context panel This method must be called
     * when the user selected a different context tab, so if user returns to the
     * tab showing this panel method updateContextPanel must be called again
     */
    public final void clearContextPanel() {
        Logger.debug(LogCategories.CONTEXT, "Clearing panel: ", getContextPanelName());
        for (AbstractContextPanelContent content : getContents()) {
            content.clearContextPanelContent();
        }
        audioObject = null;
    }

    /**
     * Returns a graphical component with all contents of the context panel
     * 
     * @return
     */
    public final Component getUIComponent() {
    	if (component == null) {
    		JXTaskPaneContainer container = new JXTaskPaneContainer();
    		container.setOpaque(false);
    		for (AbstractContextPanelContent content : getContents()) {
    			JXTaskPane taskPane = new CustomJXTaskPane();
    			content.setParentTaskPane(taskPane);
    			taskPane.setTitle(content.getContentName());

    			Component componentToAdd = content.getComponent();
    			if (componentToAdd instanceof JComponent) {
    				((JComponent) componentToAdd).setOpaque(false);
    			}
    			if (content.isScrollNeeded()) {
    				JScrollPane scroll = new JScrollPane(componentToAdd);
    				scroll.setBorder(null);
    				scroll.getVerticalScrollBar().setUnitIncrement(50);
    				componentToAdd = scroll;
    			}
    			taskPane.add(componentToAdd);
    			taskPane.setCollapsed(true);
    			container.add(taskPane);
    		}
    		JScrollPane scrollPane = new JScrollPane(container);
    		scrollPane.getVerticalScrollBar().setUnitIncrement(50);
    		component = scrollPane;
    	}
    	return component;
    }

    /**
     * Returns title to be used in tab for the current audio object
     * 
     * @return
     */
    public final String getTitle() {
        return getContextPanelTitle(ContextHandler.getInstance().getCurrentAudioObject());
    }

    /**
     * Returns icon to be used in tab for the current audio object
     * 
     * @return
     */
    public final ColorMutableImageIcon getIcon() {
        return getContextPanelIcon(ContextHandler.getInstance().getCurrentAudioObject());
    }

    /**
     * Returns <code>true</code> if tab is enabled (can be used) for the current
     * audio object
     * 
     * @return
     */
    public final boolean isEnabled() {
        if (ContextHandler.getInstance().getCurrentAudioObject() == null) {
            return false;
        }
        return true;
    }
    
    /**
     * Returns <code>true</code> if tab is visible for the current audio object
     * @return
     */
    public final boolean isVisible() {
        if (ContextHandler.getInstance().getCurrentAudioObject() == null) {
            return true;
        }
        return isPanelVisibleForAudioObject(ContextHandler.getInstance().getCurrentAudioObject());
    }

    private static class CustomJXTaskPane extends JXTaskPane {
        private static final long serialVersionUID = 1569831509432974799L;

        @Override
        public void setCollapsed(boolean collapsed) {
            // TODO: Currently JXTaskPane collapses or expands when user presses mouse button
            // So we override setCollapsed method to avoid collapsing or expanding task pane if it's disabled
            if (isEnabled()) {
                super.setCollapsed(collapsed);
            }
        }

    }

	/**
	 * Return components to show in options
	 * @return
	 */
	public List<Component> getOptions() {
		List<Component> components = new ArrayList<Component>();
		for (AbstractContextPanelContent content : getContents()) {
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
		AbstractContextPanel other = (AbstractContextPanel) obj;
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
}
