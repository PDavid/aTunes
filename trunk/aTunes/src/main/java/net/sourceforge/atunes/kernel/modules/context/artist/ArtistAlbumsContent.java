/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.gui.lookandfeel.TableCellRendererCode;
import net.sourceforge.atunes.kernel.modules.context.AlbumInfo;
import net.sourceforge.atunes.kernel.modules.context.ContextImageJTable;
import net.sourceforge.atunes.kernel.modules.context.ContextPanelContent;
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
public class ArtistAlbumsContent extends ContextPanelContent {

    private static class AlbumsTableCellRendererCode extends
			TableCellRendererCode {
		@Override
		public Component getComponent(Component superComponent, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		    return getPanelForTableRenderer(((AlbumInfo) value).getCover(), StringUtils.getString("<html>", ((AlbumInfo) value).getTitle(), "</html>"), superComponent
		            .getBackground(), Constants.CONTEXT_IMAGE_WIDTH, Constants.CONTEXT_IMAGE_HEIGHT);
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
        albumsTable.setShowGrid(false);
        albumsTable.getTableHeader().setReorderingAllowed(false);
        albumsTable.getTableHeader().setResizingAllowed(false);
        albumsTable.setDefaultRenderer(AlbumInfo.class, LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getTableCellRenderer(new AlbumsTableCellRendererCode()));

        albumsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedAlbum = albumsTable.getSelectedRow();
                    if (selectedAlbum != -1) {
                        AlbumInfo album = ((ContextAlbumsTableModel) albumsTable.getModel()).getAlbum(selectedAlbum);
                        DesktopUtils.openURL(album.getUrl());
                    }
                }
            }
        });

        return albumsTable;
    }

}
