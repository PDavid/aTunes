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

package net.sourceforge.atunes.kernel.actions;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.Icon;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPlayList;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.utils.Logger;

/**
 * Abstract action common to all app actions
 * 
 * @author alex
 * 
 */

public abstract class CustomAbstractAction extends javax.swing.AbstractAction {

	private static final long serialVersionUID = 1648027023533465104L;

	/**
	 * Source of the action
	 */
	private Object source;

	/**
	 * Default constructor
	 */
	public CustomAbstractAction() {
		super();
	}

	/**
	 * Creates a new action with given name
	 * 
	 * @param name
	 */
	public CustomAbstractAction(final String name) {
		super(name);
	}

	/**
	 * Creates a new action with given name and icon
	 * 
	 * @param name
	 * @param icon
	 */
	public CustomAbstractAction(final String name, final Icon icon) {
		super(name, icon);
	}

	/**
	 * Indicates whether this action must be enabled or disabled when used in
	 * navigator tree with given selection
	 * 
	 * @param rootSelected
	 * @param selection
	 * @return
	 */
	public boolean isEnabledForNavigationTreeSelection(
			final boolean rootSelected, final List<ITreeNode> selection) {
		return false;
	}

	/**
	 * Indicates whether this action must be enabled or disabled when used in
	 * navigator table with given selection
	 * 
	 * @param selection
	 * @return
	 */
	public boolean isEnabledForNavigationTableSelection(
			final List<IAudioObject> selection) {
		return false;
	}

	/**
	 * Initializes action if needed All initialization needed retrieving values
	 * from <code>getState</code> must be done here
	 */
	protected void initialize() {
		// For toggle actions
		updateTooltip();
	}

	/**
	 * Indicates whether this action must be enabled or disabled when used in
	 * play list with given selection
	 * 
	 * @param selection
	 * @return
	 */
	public boolean isEnabledForPlayListSelection(
			final List<IAudioObject> selection) {
		return false;
	}

	/**
	 * Indicates whether this action must be enabled or disabled when used in a
	 * play list of the given type
	 * 
	 * @param playlist
	 * @return
	 */
	public boolean isEnabledForPlayList(final IPlayList playlist) {
		return true;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		Logger.debug("Executing action: ", this.getClass().getName());
		this.source = e != null ? e.getSource() : null;
		executeAction();

		// For toggle actions
		updateTooltip();
	}

	/**
	 * Source component that fired this action
	 * 
	 * @return
	 */
	protected final Object getSource() {
		return this.source;
	}

	/**
	 * Override this method to execute action
	 */
	protected abstract void executeAction();

	/**
	 * Override this method to update tooltip
	 */
	protected void updateTooltip() {
		// Does nothing by default
	}
}
