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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sourceforge.atunes.gui.views.controls.PopUpButton;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.commonjukebox.plugins.PluginApi;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

/**
 * This class represents a context panel shown in a context tab. Context panel
 * shows information related to the current audio object active in the
 * application
 * 
 * @author alex
 * 
 */
@PluginApi
public abstract class ContextPanel {

    private static final long serialVersionUID = 7870512266932745272L;

    /**
     * Logger shared by all context panels
     */
    private static Logger logger;

    /**
     * Last AudioObject used to update context panel
     */
    private AudioObject audioObject;
    
    /**
     * Time stamp when audio object was modified. Used to decide if context info
     * must be updated
     */
    private long lastAudioObjectModificationTime;

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
    protected abstract ImageIcon getContextPanelIcon(AudioObject audioObject);

    /**
     * List of contents shown in the context panel. Contents are shown in order
     * in context tab
     * 
     * @return List of contents of the context panel
     */
    protected abstract List<ContextPanelContent> getContents();

    /**
     * Indicates if panel must be enabled or disabled for the given audio object
     * 
     * @param audioObject
     *            The current audio object in context panels or
     *            <code>null</code> if no current audio object is selected
     * @return
     */
    protected abstract boolean isPanelEnabledForAudioObject(AudioObject audioObject);

    // END OF METHODS TO BE IMPLEMENTED BY CONCRETE CONTEXT PANELS

    /**
     * Updates the context panel with information related to the given audio
     * object This method must be called every time the current audio object of
     * the application changes and the panel is visible (the context tab showing
     * this panel is selected)
     * 
     * @param audioObject
     */
    protected final void updateContextPanel(AudioObject audioObject, boolean forceUpdate) {
        // If the AudioObject is the same as used before to update panel then do nothing if forceUpdate is false
        if (!forceUpdate && this.audioObject != null && this.audioObject.equals(audioObject)) {
            return;
        }

        getLogger().debug(LogCategories.CONTEXT, StringUtils.getString("Updating panel: ", getContextPanelName()));
        for (ContextPanelContent content : getContents()) {
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
        getLogger().debug(LogCategories.CONTEXT, StringUtils.getString("Clearing panel: ", getContextPanelName()));
        for (ContextPanelContent content : getContents()) {
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
        JXTaskPaneContainer container = new JXTaskPaneContainer();
        container.setOpaque(false);
        for (ContextPanelContent content : getContents()) {
            JXTaskPane taskPane = new JXTaskPane();
            content.setParentTaskPane(taskPane);
            taskPane.setTitle(content.getContentName());

            Component componentToAdd = content.getComponent();
            if (content.isScrollNeeded()) {
                JScrollPane scroll = new JScrollPane(componentToAdd);
                scroll.setBorder(null);
                scroll.getVerticalScrollBar().setUnitIncrement(50);
                componentToAdd = scroll;
            }

            List<Component> options = content.getOptions();
            if (options != null && !options.isEmpty()) {
                PopUpButton button = new PopUpButton(I18nUtils.getString("OPTIONS"), PopUpButton.TOP_RIGHT);
                for (Component option : options) {
                    button.add(option);
                }
                JPanel panel = new JPanel(new GridBagLayout());
                GridBagConstraints c = new GridBagConstraints();
                c.weightx = 1;
                c.weighty = 1;
                c.fill = GridBagConstraints.BOTH;
                panel.add(componentToAdd, c);
                c.gridy = 1;
                c.weightx = 0;
                c.weighty = 0;
                c.fill = GridBagConstraints.NONE;
                c.insets = new Insets(5, 0, 0, 0);
                c.anchor = GridBagConstraints.WEST;
                panel.add(button, c);
                GuiUtils.applyComponentOrientation(panel);
                componentToAdd = panel;
            }

            taskPane.add(componentToAdd);
            taskPane.setCollapsed(true);
            container.add(taskPane);
        }
        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(50);
        return scrollPane;
    }

    /**
     * Returns title to be used in tab for the current audio object
     * 
     * @return
     */
    public final String getTitle() {
        if (ApplicationState.getInstance().isShowContextTabsText()) {
            return getContextPanelTitle(ContextHandler.getInstance().getCurrentAudioObject());
        } else {
            return null;
        }
    }

    /**
     * Returns icon to be used in tab for the current audio object
     * 
     * @return
     */
    public final ImageIcon getIcon() {
        return getContextPanelIcon(ContextHandler.getInstance().getCurrentAudioObject());
    }

    /**
     * Returns <code>true</code> if tab is enabled (can be used) for the current
     * audio object
     * 
     * @return
     */
    public final boolean isEnabled() {
    	// If current audio object is null don't even ask panel
    	if (ContextHandler.getInstance().getCurrentAudioObject() == null) {
    		return false;
    	}
        return isPanelEnabledForAudioObject(ContextHandler.getInstance().getCurrentAudioObject());
    }

    /**
     * Getter of logger
     * 
     * @return
     */
    private Logger getLogger() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }
}
