/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.modules.context.artist;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanelContent;
import net.sourceforge.atunes.kernel.modules.context.AlbumInfo;
import net.sourceforge.atunes.kernel.modules.context.ContextImageJTable;
import net.sourceforge.atunes.kernel.modules.context.ContextTableRowPanel;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.DesktopUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Albums of an artist
 * 
 * @author alex
 * 
 */
public class ArtistAlbumsContent extends AbstractContextPanelContent {

    private static class AlbumsTableCellRendererCode extends ContextTableRowPanel {
    	
        public AlbumsTableCellRendererCode(Class<?> clazz, ContextImageJTable table) {
			super(clazz, table);
		}

		@Override
        public JComponent getComponent(JComponent superComponent, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return getPanelForTableRenderer(((AlbumInfo) value).getCover(), 
            								StringUtils.getString("<html>", ((AlbumInfo) value).getTitle(), "</html>"), 
            								superComponent.getBackground(),
            								superComponent.getForeground(), 
            								Constants.CONTEXT_IMAGE_WIDTH, 
            								Constants.CONTEXT_IMAGE_HEIGHT,
            								hasFocus);
        }
		
		@Override
		public List<AbstractAction> getActions() {
			List<AbstractAction> actions = new ArrayList<AbstractAction>();
			actions.add(new AbstractAction(I18nUtils.getString("READ_MORE")) {
				
				/**
				 * 
				 */
				private static final long serialVersionUID = -7322221144744041599L;

				@Override
				public void actionPerformed(ActionEvent arg0) {
                    int selectedAlbum = table.getSelectedRow();
                    if (selectedAlbum != -1) {
                        AlbumInfo album = ((ContextAlbumsTableModel) table.getModel()).getAlbum(selectedAlbum);
                        DesktopUtils.openURL(album.getUrl());
                    }
				}
			});
			return actions;
		}
    }

    private static final long serialVersionUID = -5538266144953409867L;
    private ContextImageJTable albumsTable;

    public ArtistAlbumsContent() {
        super(new ArtistInfoDataSource());
    }

    @Override
    protected String getContentName() {
        return I18nUtils.getString("ALBUMS");
    }

    @Override
    protected Map<String, ?> getDataSourceParameters(AudioObject audioObject) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(ArtistInfoDataSource.INPUT_AUDIO_OBJECT, audioObject);
        parameters.put(ArtistInfoDataSource.INPUT_ALBUMS, true);
        return parameters;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void updateContentWithDataSourceResult(Map<String, ?> result) {
        if (result.containsKey(ArtistInfoDataSource.OUTPUT_ALBUMS)) {
            albumsTable.setModel(new ContextAlbumsTableModel((List<AlbumInfo>) result.get(ArtistInfoDataSource.OUTPUT_ALBUMS)));
        }
    }

    @Override
    protected void clearContextPanelContent() {
        super.clearContextPanelContent();
        albumsTable.setModel(new ContextAlbumsTableModel(null));
    }

    @Override
    protected Component getComponent() {
        // Create components
        albumsTable = new ContextImageJTable();
        albumsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        AlbumsTableCellRendererCode code = new AlbumsTableCellRendererCode(AlbumInfo.class, albumsTable);
        code.bind();

        albumsTable.setColumnSelectionAllowed(false);
        albumsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        albumsTable.getTableHeader().setReorderingAllowed(false);

        return albumsTable;
    }

}
