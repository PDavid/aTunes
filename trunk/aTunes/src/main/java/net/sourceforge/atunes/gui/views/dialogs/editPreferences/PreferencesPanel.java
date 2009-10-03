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
package net.sourceforge.atunes.gui.views.dialogs.editPreferences;

import java.awt.GridBagLayout;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import net.sourceforge.atunes.kernel.modules.state.ApplicationState;

/**
 * The Class PreferencesPanel.
 */
public abstract class PreferencesPanel extends JPanel {

    private static final long serialVersionUID = -6163144955354757264L;

    /**
     * Title of this panel
     */
    private String title;

    /**
     * Instantiates a new preferences panel.
     */
    public PreferencesPanel(String title) {
        super(new GridBagLayout());
        this.title = title;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    public abstract ImageIcon getIcon();

    /**
     * Called to update preferences values
     * 
     * @param state
     *            : current preferences
     */
    public abstract void updatePanel(ApplicationState state);

    /**
     * Validates data of this panel
     * 
     * @return <code>true</code> if data is valid
     */
    public abstract boolean validatePanel();

    /**
     * Called to apply preferences selected by user to an ApplicationState
     * object
     * 
     * @param state
     * @return <code>true</code>if it's necessary to restart application to
     *         apply the change
     */
    public abstract boolean applyPreferences(ApplicationState state);

    /**
     * Called when preferences dialog is shown or hidden Useful to execute code
     * to initialize or disable settings when dialog is shown / hidden
     * 
     * @param visible
     */
    public abstract void dialogVisibilityChanged(boolean visible);
}
