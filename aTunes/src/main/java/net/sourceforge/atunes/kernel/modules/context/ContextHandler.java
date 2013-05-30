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

package net.sourceforge.atunes.kernel.modules.context;

import java.util.List;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.kernel.AbstractHandler;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IContextHandler;
import net.sourceforge.atunes.model.IContextPanel;
import net.sourceforge.atunes.model.IContextPanelsContainer;
import net.sourceforge.atunes.model.IFileManager;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayListAudioObject;
import net.sourceforge.atunes.model.IPlayListEventListener;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IStateContext;
import net.sourceforge.atunes.model.IStatePlaylist;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.model.IWebServicesHandler;

/**
 * Responsible of showing context information
 * 
 * @author alex
 * 
 */
public final class ContextHandler extends AbstractHandler implements
		IContextHandler, IPlayListEventListener {

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

	private ITaskService taskService;

	private IWebServicesHandler webServicesHandler;

	private IStateContext stateContext;

	private IStatePlaylist statePlaylist;

	private IFileManager fileManager;

	/**
	 * @param fileManager
	 */
	public void setFileManager(final IFileManager fileManager) {
		this.fileManager = fileManager;
	}

	/**
	 * @param statePlaylist
	 */
	public void setStatePlaylist(final IStatePlaylist statePlaylist) {
		this.statePlaylist = statePlaylist;
	}

	/**
	 * @param stateContext
	 */
	public void setStateContext(final IStateContext stateContext) {
		this.stateContext = stateContext;
	}

	/**
	 * @param webServicesHandler
	 */
	public void setWebServicesHandler(
			final IWebServicesHandler webServicesHandler) {
		this.webServicesHandler = webServicesHandler;
	}

	/**
	 * @param taskService
	 */
	public void setTaskService(final ITaskService taskService) {
		this.taskService = taskService;
	}

	@Override
	public void applicationStarted() {
		addContextPanels(this.contextPanels);

		// Set previous selected tab
		setContextTab(this.stateContext.getSelectedContextTab());

		getFrame().showContextPanel(this.stateContext.isUseContext());
	}

	/**
	 * Called when user changes context tab
	 */
	@Override
	public void contextPanelChanged() {
		// Update selected tab
		this.stateContext
				.setSelectedContextTab(getContextTab() != null ? getContextTab()
						.getContextPanelName() : null);
		// Call to fill information: Don't force update since audio object can
		// be the same
		retrieveInfo(this.currentAudioObject, false);
	}

	/**
	 * Clears all context panels
	 * 
	 */
	private void clearContextPanels() {
		clearTabsContent();
		this.currentAudioObject = null;

		// Select first tab
		this.stateContext.setSelectedContextTab(null);
		setContextTab(null);
	}

	/**
	 * Clears tabs content
	 */
	private void clearTabsContent() {
		// Clear all context panels
		for (IContextPanel panel : this.contextPanels) {
			panel.clearContextPanel();
		}
	}

	@Override
	public void retrieveInfoAndShowInPanel(final IAudioObject ao) {
		boolean audioObjectModified = false;
		// Avoid retrieve information about the same audio object twice except
		// if is an LocalAudioObject and has been recently changed
		if (this.currentAudioObject != null
				&& this.currentAudioObject.equals(ao)) {
			if (ao instanceof ILocalAudioObject) {
				if (this.fileManager
						.getModificationTime((ILocalAudioObject) ao) != this.lastAudioObjectModificationTime) {
					audioObjectModified = true;
				} else {
					return;
				}
			} else if (!(ao instanceof IRadio)) {
				return;
			}
		}
		this.currentAudioObject = ao;

		// Update modification time if necessary
		updateModificationTimeOfLastAudioObject(ao);

		// Call to retrieve and show information
		if (this.stateContext.isUseContext()) {
			retrieveInfoAndShowInPanel(ao, audioObjectModified);
		}
	}

	/**
	 * @param ao
	 */
	private void updateModificationTimeOfLastAudioObject(final IAudioObject ao) {
		if (ao instanceof ILocalAudioObject) {
			this.lastAudioObjectModificationTime = this.fileManager
					.getModificationTime((ILocalAudioObject) ao);
		} else {
			this.lastAudioObjectModificationTime = 0;
		}
	}

	/**
	 * @param ao
	 * @param audioObjectModified
	 */
	private void retrieveInfoAndShowInPanel(final IAudioObject ao,
			final boolean audioObjectModified) {

		// Enable or disable tabs
		updateContextTabs();

		if (ao == null) {
			// Clear all tabs
			clearContextPanels();
		} else {
			if (audioObjectModified) {
				clearTabsContent();
			}
			// Retrieve data for audio object. Force Update since audio file has
			// been modified
			retrieveInfo(ao, audioObjectModified);
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
	private void retrieveInfo(final IAudioObject audioObject,
			final boolean forceUpdate) {
		if (audioObject == null) {
			return;
		}

		// Context panel can be removed so check index
		String selectedTab = this.stateContext.getSelectedContextTab();
		// Update current context panel
		for (IContextPanel panel : this.contextPanels) {
			if (panel.getContextPanelName().equals(selectedTab)) {
				panel.updateContextPanel(audioObject, forceUpdate);
				break;
			}
		}
	}

	@Override
	public void applicationStateChanged() {
		// Show or hide context panel
		showContextPanel(this.stateContext.isUseContext());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextHandler#
	 * getCurrentAudioObject()
	 */
	@Override
	public IAudioObject getCurrentAudioObject() {
		return this.currentAudioObject;
	}

	@Override
	public void selectedAudioObjectChanged(final IAudioObject audioObject) {
		if (this.stateContext.isUseContext()) {
			retrieveInfoAndShowInPanel(audioObject);
		}
	}

	@Override
	public void playListCleared() {
		if (this.stateContext.isUseContext()) {
			retrieveInfoAndShowInPanel(null);

			if (this.statePlaylist.isStopPlayerOnPlayListClear()) {
				clearContextPanels();
			}
		}
	}

	@Override
	public void audioObjectsAdded(
			final List<IPlayListAudioObject> playListAudioObjects) {
	}

	@Override
	public void audioObjectsRemoved(
			final List<IPlayListAudioObject> audioObjectList) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextHandler#
	 * showContextPanel(boolean)
	 */
	@Override
	public void showContextPanel(final boolean show) {
		this.stateContext.setUseContext(show);
		getFrame().showContextPanel(show);
		if (show) {
			retrieveInfoAndShowInPanel(this.playListHandler
					.getCurrentAudioObjectFromVisiblePlayList());
		}
	}

	/**
	 * Selects context tab
	 * 
	 * @param selectedContextTab
	 */
	@Override
	public void setContextTab(final String selectedContextTab) {
		this.contextPanelsContainer.setSelectedContextPanel(selectedContextTab);
		contextPanelChanged();
	}

	/**
	 * Returns context tab
	 * 
	 * @return
	 */
	private IContextPanel getContextTab() {
		return this.contextPanelsContainer.getSelectedContextPanel();
	}

	private void updateContextTabs() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ContextHandler.this.contextPanelsContainer
						.updateContextPanels();
			}
		});
	}

	private void removeContextPanel(final IContextPanel instance) {
		this.contextPanelsContainer.removeContextPanel(instance);
		this.contextPanelsContainer.updateContextPanels();
	}

	/**
	 * Adds context panels
	 * 
	 * @param contextPanels
	 */
	private void addContextPanels(final List<IContextPanel> contextPanels) {
		for (IContextPanel panel : contextPanels) {
			this.contextPanelsContainer.addContextPanel(panel);
		}
		this.contextPanelsContainer.updateContextPanels();
	}

	/**
	 * @param contextPanels
	 */
	public void setContextPanels(final List<IContextPanel> contextPanels) {
		this.contextPanels = contextPanels;
	}

	/**
	 * @param contextPanelsContainer
	 */
	public void setContextPanelsContainer(
			final IContextPanelsContainer contextPanelsContainer) {
		this.contextPanelsContainer = contextPanelsContainer;
	}

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	@Override
	public void finishedContextPanelUpdate() {
		this.taskService.submitNow("Consolidate Web Content", new Runnable() {
			@Override
			public void run() {
				ContextHandler.this.webServicesHandler.consolidateWebContent();
			}
		});
	}
}
