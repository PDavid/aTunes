/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.modules.context.album;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanelContent;
import net.sourceforge.atunes.kernel.modules.context.AlbumInfo;
import net.sourceforge.atunes.kernel.modules.context.TrackInfo;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.DesktopUtils;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Tracks of an album
 * 
 * @author alex
 * 
 */
public class AlbumTracksContent extends AbstractContextPanelContent {

    private static class TracksDefaultTableColumnModel extends DefaultTableColumnModel {
        private static final long serialVersionUID = 1338172152164826400L;

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

    public AlbumTracksContent() {
        super(new AlbumInfoDataSource());
    }

    @Override
    protected String getContentName() {
        return I18nUtils.getString("SONGS");
    }

    @Override
    protected Map<String, ?> getDataSourceParameters(AudioObject audioObject) {
        Map<String, AudioObject> parameters = new HashMap<String, AudioObject>();
        parameters.put(AlbumInfoDataSource.INPUT_AUDIO_OBJECT, audioObject);
        return parameters;
    }

    @Override
    protected void updateContentWithDataSourceResult(Map<String, ?> result) {
        if (result.containsKey(AlbumInfoDataSource.OUTPUT_ALBUM)) {
            tracksTable.setModel(new ContextTracksTableModel((AlbumInfo) result.get(AlbumInfoDataSource.OUTPUT_ALBUM)));
        }
    }

    @Override
    protected void clearContextPanelContent() {
        super.clearContextPanelContent();
        tracksTable.setModel(new ContextTracksTableModel(null));
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
                        TrackInfo track = ((ContextTracksTableModel) tracksTable.getModel()).getTrack(selectedTrack);
                        DesktopUtils.openURL(track.getUrl());
                    }
                }
            }
        });

        return tracksTable;
    }
}
