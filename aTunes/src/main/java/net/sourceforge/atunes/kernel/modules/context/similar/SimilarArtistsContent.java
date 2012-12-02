/*
 * aTunes 3.0.0
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

package net.sourceforge.atunes.kernel.modules.context.similar;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanelContent;
import net.sourceforge.atunes.kernel.modules.context.ContextTable;
import net.sourceforge.atunes.kernel.modules.context.ContextTableAction;
import net.sourceforge.atunes.model.IArtistInfo;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * @author alex
 * 
 */
public class SimilarArtistsContent extends
		AbstractContextPanelContent<SimilarArtistsDataSource> {

	private static final long serialVersionUID = 5041098100868186051L;
	private ContextTable similarArtistsTable;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	@Override
	public String getContentName() {
		return I18nUtils.getString("SIMILAR");
	}

	@Override
	public void updateContentFromDataSource(
			final SimilarArtistsDataSource source) {
		if (source.getSimilarArtistsInfo() != null) {
			((SimilarArtistsTableModel) this.similarArtistsTable.getModel())
					.setArtists(source.getSimilarArtistsInfo().getArtists());
		} else {
			((SimilarArtistsTableModel) this.similarArtistsTable.getModel())
					.setArtists(null);
		}
	}

	@Override
	public void clearContextPanelContent() {
		super.clearContextPanelContent();
		((SimilarArtistsTableModel) this.similarArtistsTable.getModel())
				.setArtists(null);
	}

	@Override
	public Component getComponent() {
		// Create components
		this.similarArtistsTable = this.beanFactory.getBean(ContextTable.class);
		new SimilarArtistsDragAndDrop(this.similarArtistsTable);
		this.similarArtistsTable.addContextRowPanel(this.beanFactory
				.getBean(SimilarArtistTableCellRendererCode.class));
		this.similarArtistsTable.setModel(new SimilarArtistsTableModel());

		List<ContextTableAction<?>> actions = new ArrayList<ContextTableAction<?>>();
		ContextTableAction<IArtistInfo> readMore = this.beanFactory
				.getBean(ReadMoreContextTableAction.class);
		ContextTableAction<IArtistInfo> search = this.beanFactory
				.getBean(SearchArtistContextTableAction.class);
		ContextTableAction<IArtistInfo> addAlbum = this.beanFactory
				.getBean(AddAlbumArtistToPlayListContextTableAction.class);
		actions.add(readMore);
		actions.add(search);
		actions.add(addAlbum);
		this.similarArtistsTable.setRowActions(actions);
		return this.similarArtistsTable;
	}
}
