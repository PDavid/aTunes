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

package net.sourceforge.atunes.kernel.modules.context.youtube;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;

import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanelContent;
import net.sourceforge.atunes.kernel.modules.context.ContextTable;
import net.sourceforge.atunes.kernel.modules.context.ContextTableAction;
import net.sourceforge.atunes.kernel.modules.internetsearch.SearchFactory;
import net.sourceforge.atunes.kernel.modules.webservices.youtube.YoutubeService;
import net.sourceforge.atunes.model.IContextHandler;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.model.IVideoEntry;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Content to show videos from Youtube
 * 
 * @author alex
 * 
 */
public class YoutubeContent extends
		AbstractContextPanelContent<YoutubeDataSource> {

	private static final long serialVersionUID = 5041098100868186051L;

	private ContextTable youtubeResultTable;

	private final JMenuItem moreResults;

	private final JMenuItem openYoutube;

	private YoutubeService youtubeService;

	private IContextHandler contextHandler;

	private IUnknownObjectChecker unknownObjectChecker;

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	/**
	 * Default constructor
	 */
	public YoutubeContent() {
		this.moreResults = new JMenuItem(
				I18nUtils.getString("SEE_MORE_RESULTS"));
		this.moreResults.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				searchMoreResultsInYoutube();
			}
		});
		this.openYoutube = new JMenuItem(I18nUtils.getString("GO_TO_YOUTUBE"));
		this.openYoutube.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				openYoutube();
			}
		});
	}

	@Override
	public String getContentName() {
		return I18nUtils.getString("YOUTUBE_VIDEOS");
	}

	@Override
	public void updateContentFromDataSource(final YoutubeDataSource source) {
		((YoutubeResultTableModel) this.youtubeResultTable.getModel())
				.setEntries(source.getVideos());
		this.moreResults.setEnabled(true);
		this.openYoutube.setEnabled(true);
	}

	@Override
	public void clearContextPanelContent() {
		super.clearContextPanelContent();
		((YoutubeResultTableModel) this.youtubeResultTable.getModel())
				.setEntries(null);
		this.moreResults.setEnabled(false);
		this.openYoutube.setEnabled(false);
	}

	@Override
	public Component getComponent() {
		// Create components
		this.youtubeResultTable = getBeanFactory().getBean(ContextTable.class);
		this.youtubeResultTable.setModel(new YoutubeResultTableModel());
		this.youtubeResultTable.addContextRowPanel(getBeanFactory().getBean(
				YoutubeResultsTableCellRendererCode.class));

		ContextTableAction<IVideoEntry> action = getBeanFactory().getBean(
				OpenYoutubeVideoAction.class);
		List<ContextTableAction<?>> list = new ArrayList<ContextTableAction<?>>();
		list.add(action);

		this.youtubeResultTable.setRowActions(list);
		return this.youtubeResultTable;
	}

	@Override
	public List<Component> getOptions() {
		List<Component> options = new ArrayList<Component>();
		options.add(this.moreResults);
		options.add(this.openYoutube);
		return options;
	}

	/**
	 * Searches for more results of the last search
	 * 
	 * @return
	 */
	private void searchMoreResultsInYoutube() {
		String searchString = this.youtubeService
				.getSearchForAudioObject(this.contextHandler
						.getCurrentAudioObject());
		if (searchString.length() > 0) {
			final List<IVideoEntry> result = this.youtubeService
					.searchInYoutube(
							this.contextHandler.getCurrentAudioObject()
									.getArtist(this.unknownObjectChecker),
							searchString,
							this.youtubeResultTable.getRowCount() + 1);
			((YoutubeResultTableModel) this.youtubeResultTable.getModel())
					.addEntries(result);
		}
	}

	/**
	 * Opens a web browser to show youtube results
	 */
	protected void openYoutube() {
		getDesktop().openSearch(
				SearchFactory.getSearchForName("YouTube"),
				this.youtubeService.getSearchForAudioObject(this.contextHandler
						.getCurrentAudioObject()));
	}

	/**
	 * @param youtubeService
	 */
	public void setYoutubeService(final YoutubeService youtubeService) {
		this.youtubeService = youtubeService;
	}

	/**
	 * @param contextHandler
	 */
	public void setContextHandler(final IContextHandler contextHandler) {
		this.contextHandler = contextHandler;
	}
}
