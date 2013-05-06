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

package net.sourceforge.atunes.kernel.modules.context.artist;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JTable;

import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanelContent;
import net.sourceforge.atunes.kernel.modules.context.ContextInformationTableFactory;
import net.sourceforge.atunes.kernel.modules.context.ITracksTableListener;
import net.sourceforge.atunes.model.IArtistTopTracks;
import net.sourceforge.atunes.utils.CollectionUtils;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Popular tracks of an artist
 * 
 * @author alex
 * 
 */
public class ArtistTopTracksContent extends
		AbstractContextPanelContent<ArtistPopularTracksDataSource> {

	private static final long serialVersionUID = -5538266144953409867L;

	private JTable tracksTable;

	private ContextInformationTableFactory contextInformationTableFactory;

	private ITracksTableListener contextTableURLOpener;

	private JMenuItem createPlayList;

	private CreatePlaylistWithPopularTracksActionListener createPlaylistWithPopularTracksActionListener;

	/**
	 * @param createPlaylistWithPopularTracksActionListener
	 */
	public void setCreatePlaylistWithPopularTracksActionListener(
			final CreatePlaylistWithPopularTracksActionListener createPlaylistWithPopularTracksActionListener) {
		this.createPlaylistWithPopularTracksActionListener = createPlaylistWithPopularTracksActionListener;
	}

	/**
	 * @param contextTableURLOpener
	 */
	public void setContextTableURLOpener(
			final ITracksTableListener contextTableURLOpener) {
		this.contextTableURLOpener = contextTableURLOpener;
	}

	/**
	 * @param contextInformationTableFactory
	 */
	public void setContextInformationTableFactory(
			final ContextInformationTableFactory contextInformationTableFactory) {
		this.contextInformationTableFactory = contextInformationTableFactory;
	}

	@Override
	public String getContentName() {
		return I18nUtils.getString("SONGS");
	}

	@Override
	public void updateContentFromDataSource(
			final ArtistPopularTracksDataSource source) {
		IArtistTopTracks lastTopTracks = source.getTopTracks();
		((ContextArtistTracksTableModel) this.tracksTable.getModel())
				.setTopTracks(lastTopTracks);
		this.createPlayList.setEnabled(lastTopTracks != null
				&& !CollectionUtils.isEmpty(lastTopTracks.getTracks()));
		this.createPlaylistWithPopularTracksActionListener
				.setLastTopTracks(lastTopTracks);
	}

	@Override
	public void clearContextPanelContent() {
		super.clearContextPanelContent();
		((ContextArtistTracksTableModel) this.tracksTable.getModel())
				.setTopTracks(null);
	}

	@Override
	public Component getComponent() {
		// Create components
		this.tracksTable = this.contextInformationTableFactory
				.getNewTracksTable(this.contextTableURLOpener);
		this.tracksTable.setModel(getBeanFactory().getBean(
				ContextArtistTracksTableModel.class));
		return this.tracksTable;
	}

	@Override
	public List<Component> getOptions() {
		List<Component> options = new ArrayList<Component>();
		this.createPlayList = new JMenuItem(
				I18nUtils.getString("CREATE_PLAYLIST_WITH_TOP_TRACKS"));
		this.createPlayList
				.addActionListener(this.createPlaylistWithPopularTracksActionListener);
		options.add(this.createPlayList);
		return options;
	}
}
