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
import java.util.Collections;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanelContent;
import net.sourceforge.atunes.kernel.modules.context.ArtistTopTracks;
import net.sourceforge.atunes.kernel.modules.context.TrackInfo;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.DesktopUtils;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Popular tracks of an artist
 * 
 * @author alex
 * 
 */
public class ArtistPopularTracksContent extends AbstractContextPanelContent {

    private static class TracksDefaultTableColumnModel extends DefaultTableColumnModel {

        /**
		 * 
		 */
		private static final long serialVersionUID = -3430934135011974702L;

		@Override
        public void addColumn(TableColumn column) {
            super.addColumn(column);
            if (column.getModelIndex() == 0) {
                column.setMaxWidth(25);
            }
        }
    }

    private static final long serialVersionUID = -5538266144953409867L;

    private JTable tracksTable;

    public ArtistPopularTracksContent() {
        super(new ArtistPopularTracksDataSource());
    }

    @Override
    protected String getContentName() {
        return I18nUtils.getString("SONGS");
    }

    @Override
    protected Map<String, ?> getDataSourceParameters(AudioObject audioObject) {
    	return Collections.singletonMap(ArtistPopularTracksDataSource.INPUT_AUDIO_OBJECT, audioObject);
    }

    @Override
    protected void updateContentWithDataSourceResult(Map<String, ?> result) {
        if (result.containsKey(ArtistPopularTracksDataSource.OUTPUT_TRACKS)) {
            tracksTable.setModel(new ContextArtistTracksTableModel((ArtistTopTracks) result.get(ArtistPopularTracksDataSource.OUTPUT_TRACKS)));
        }
    }

    @Override
    protected void clearContextPanelContent() {
        super.clearContextPanelContent();
        tracksTable.setModel(new ContextArtistTracksTableModel(null));
    }

    @Override
    protected Component getComponent() {
        // Create components
        tracksTable = new JTable();
        tracksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tracksTable.setShowGrid(false);
        tracksTable.setDefaultRenderer(String.class, LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getTableCellRenderer(
                GuiUtils.getComponentOrientationTableCellRendererCode()));

        tracksTable.setDefaultRenderer(Integer.class, LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getTableCellRenderer(
                GuiUtils.getComponentOrientationTableCellRendererCode()));

        tracksTable.getTableHeader().setReorderingAllowed(true);
        tracksTable.getTableHeader().setResizingAllowed(false);
        tracksTable.setColumnModel(new TracksDefaultTableColumnModel());

        tracksTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedTrack = tracksTable.getSelectedRow();
                    if (selectedTrack != -1) {
                        TrackInfo track = ((ContextArtistTracksTableModel) tracksTable.getModel()).getTrack(selectedTrack);
                        DesktopUtils.openURL(track.getUrl());
                    }
                }
            }
        });

        return tracksTable;
    }
    
    @Override
    protected boolean isScrollNeeded() {
    	return true;
    }
}
