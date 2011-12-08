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

package net.sourceforge.atunes.kernel.modules.context.similar;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.kernel.modules.context.ContextTable;
import net.sourceforge.atunes.kernel.modules.context.ContextTableAction;
import net.sourceforge.atunes.kernel.modules.context.ContextTableRowPanel;
import net.sourceforge.atunes.model.IArtistInfo;
import net.sourceforge.atunes.model.IDesktop;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

class SimilarArtistTableCellRendererCode extends ContextTableRowPanel<IArtistInfo> {

	private IDesktop desktop;
	
	public SimilarArtistTableCellRendererCode(ILookAndFeel lookAndFeel, IDesktop desktop) {
		super(lookAndFeel);
		this.desktop = desktop;
	}

	@Override
    public JComponent getComponent(JLabel superComponent, JTable t, IArtistInfo value, boolean isSelected, boolean hasFocus, int row, int column) {
        return getPanelForTableRenderer(value.getImage(), 
        							    StringUtils.getString("<html><br>", value.getName(), "<br>", value.getMatch(), "%<br>", value.isAvailable() ? I18nUtils.getString("AVAILABLE_IN_REPOSITORY") : "", "</html>"), 
        							    superComponent.getBackground(),
        							    superComponent.getForeground(),
        							    Constants.CONTEXT_IMAGE_WIDTH, 
        							    Constants.CONTEXT_IMAGE_HEIGHT,
        							    hasFocus);
    }

	@Override
	public List<ContextTableAction<IArtistInfo>> getActions() {
		List<ContextTableAction<IArtistInfo>> actions = new ArrayList<ContextTableAction<IArtistInfo>>();
		actions.add(new ContextTableAction<IArtistInfo>(I18nUtils.getString("READ_MORE"), (ContextTable) table, desktop) {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = -4964635263019533125L;

			@Override
			protected void execute(IArtistInfo object) {
				desktop.openURL(object.getUrl());
			}

			@Override
			protected IArtistInfo getSelectedObject(int row) {
				return ((SimilarArtistsTableModel) table.getModel()).getArtist(row);
			}
			
			@Override
			protected boolean isEnabledForObject(IArtistInfo object) {
				return true;
			}
			
		});
		actions.add(new ContextTableAction<IArtistInfo>(I18nUtils.getString("ADD_ALBUM_ARTIST_TO_PLAYLIST"), (ContextTable) table, desktop) {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = -3920095074089169426L;

			@Override
			protected void execute(IArtistInfo object) {
				Context.getBean(IPlayListHandler.class).showAddArtistDragDialog(Context.getBean(IRepositoryHandler.class).getArtist(object.getName()));
			}
			
			@Override
			protected IArtistInfo getSelectedObject(int row) {
				return ((SimilarArtistsTableModel) table.getModel()).getArtist(row);
			}
			
			@Override
			protected boolean isEnabledForObject(IArtistInfo object) {
				return Context.getBean(IRepositoryHandler.class).getArtist(object.getName()) != null;
			}				
		});
		
		return actions;
	}
}    
