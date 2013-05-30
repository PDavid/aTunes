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

import java.awt.Component;
import java.util.List;

import javax.swing.JPanel;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IBackgroundWorkerCallback;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IContextInformationSource;
import net.sourceforge.atunes.model.IContextPanelContent;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IDesktop;
import net.sourceforge.atunes.model.ILookAndFeelManager;

/**
 * This class represents a little graphic component used in a context panel to
 * show information about an audio object Information shown is retrieved from a
 * context data source
 * 
 * @author alex
 * @param <T>
 */

public abstract class AbstractContextPanelContent<T extends IContextInformationSource>
		implements IContextPanelContent<T> {

	private static final long serialVersionUID = 7059398864514654378L;

	/**
	 * Data Source used by this content to retrieve context information
	 */
	private IContextInformationSource dataSource;

	/**
	 * Worker used to retrieve data
	 */
	private ContextInformationBackgroundWorker worker;

	/**
	 * panel that handles this content
	 */
	private JPanel parentPanel;

	private ILookAndFeelManager lookAndFeelManager;

	/**
	 * Access to desktop
	 */
	private IDesktop desktop;

	private IControlsBuilder controlsBuilder;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @return bean factory
	 */
	protected final IBeanFactory getBeanFactory() {
		return this.beanFactory;
	}

	/**
	 * @param controlsBuilder
	 */
	public void setControlsBuilder(final IControlsBuilder controlsBuilder) {
		this.controlsBuilder = controlsBuilder;
	}

	/**
	 * @return
	 */
	protected IControlsBuilder getControlsBuilder() {
		return this.controlsBuilder;
	}

	@Override
	public final void updateContextPanelContent(final IAudioObject audioObject,
			final IBackgroundWorkerCallback<Void> updateCallback) {
		callDataSource(audioObject, updateCallback);
	}

	/**
	 * Calls data source to get context information
	 * 
	 * @param audioObject
	 * @param updateCallback
	 */
	@SuppressWarnings("unchecked")
	private void callDataSource(final IAudioObject audioObject,
			final IBackgroundWorkerCallback<Void> updateCallback) {
		// Create a new worker and call it
		this.worker = this.beanFactory
				.getBean(ContextInformationBackgroundWorker.class);
		this.worker.setAudioObject(audioObject);
		this.worker.setDataSource(this.dataSource);
		this.worker
				.setContent((IContextPanelContent<IContextInformationSource>) this);
		this.worker.execute(updateCallback);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextPanelContent#
	 * clearContextPanelContent()
	 */
	@Override
	public void clearContextPanelContent() {
		if (this.parentPanel != null) {
			this.parentPanel.setEnabled(false);
			this.parentPanel.setVisible(false);
		}
		cancelWorker();
	}

	private void cancelWorker() {
		if (this.worker != null) {
			this.worker.cancel();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextPanelContent#
	 * isScrollNeeded()
	 */
	@Override
	public boolean isScrollNeeded() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sourceforge.atunes.kernel.modules.context.IContextPanelContent#getOptions
	 * ()
	 */
	@Override
	public List<Component> getOptions() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextPanelContent#
	 * setParentPanel(javax.swing.JPanel)
	 */
	@Override
	public void setParentPanel(final JPanel parentPanel) {
		this.parentPanel = parentPanel;
	}

	@Override
	public JPanel getParentPanel() {
		return this.parentPanel;
	}

	protected IContextInformationSource getDataSource() {
		return this.dataSource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextPanelContent#
	 * setDataSource(net.sourceforge.atunes.model.IContextInformationSource)
	 */
	@Override
	public void setDataSource(final IContextInformationSource dataSource) {
		this.dataSource = dataSource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.atunes.kernel.modules.context.IContextPanelContent#
	 * setLookAndFeelManager(net.sourceforge.atunes.model.ILookAndFeelManager)
	 */
	@Override
	public void setLookAndFeelManager(
			final ILookAndFeelManager iLookAndFeelManager) {
		this.lookAndFeelManager = iLookAndFeelManager;
	}

	protected ILookAndFeelManager getLookAndFeelManager() {
		return this.lookAndFeelManager;
	}

	/**
	 * @param desktop
	 */
	public void setDesktop(final IDesktop desktop) {
		this.desktop = desktop;
	}

	/**
	 * @return access to desktop
	 */
	protected IDesktop getDesktop() {
		return this.desktop;
	}
}
