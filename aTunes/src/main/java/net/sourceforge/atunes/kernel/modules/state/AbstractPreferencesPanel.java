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

package net.sourceforge.atunes.kernel.modules.state;

import java.awt.GridBagLayout;
import java.awt.Window;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.sourceforge.atunes.model.IState;

abstract class AbstractPreferencesPanel extends JPanel {

    private static final long serialVersionUID = -6163144955354757264L;

    /**
     * Title of this panel
     */
    private String title;
    
    /**
     * True if panel has been shown to user (so it could have been modified)
     */
    private boolean dirty;
    
    /**
     * Access state of application
     */
    private IState state;

    /**
     * Instantiates a new preferences panel.
     */
    public AbstractPreferencesPanel(String title) {
        super(new GridBagLayout());
        this.title = title;
    }
    
    public Window getPreferenceDialog(){
    	return (Window) SwingUtilities.getWindowAncestor(this);
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Icon of panel
     * @return
     */
    public ImageIcon getIcon() {
    	return null;
    }

    /**
     * Returns state of application
     * @return
     */
    protected final IState getState() {
    	return state;
    }
    
    /**
     * Sets state of application
     * @param state
     */
    public final void setState(IState state) {
    	this.state = state;
    }
    
    /**
     * Called to update preferences values
     * 
     * @param state
     *            : current preferences
     */
    public abstract void updatePanel(IState state);

    /**
     * Validates data of this panel
     * @throws PreferencesValidationException
     */
    public abstract void validatePanel() throws PreferencesValidationException;

    /**
     * Called to apply preferences selected by user to an ApplicationState
     * object
     * 
     * @param state
     * @return <code>true</code>if it's necessary to restart application to
     *         apply the change
     */
    public abstract boolean applyPreferences(IState state);

    /**
     * Called if user cancels preference dialog. This method should reset
     * changes that were made immediately without waiting if user cancels dialog
     * or not.
     * 
     * @param state
     */
    public abstract void resetImmediateChanges(IState state);

    /**
     * Called when preferences dialog is shown or hidden Useful to execute code
     * to initialize or disable settings when dialog is shown / hidden
     * 
     * @param visible
     */
    public abstract void dialogVisibilityChanged(boolean visible);
    
	public final boolean isDirty() {
		return dirty;
	}

	public final void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
}