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

package net.sourceforge.atunes.kernel.modules.context.album;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanelContent;
import net.sourceforge.atunes.kernel.modules.context.TracksTableFactory;
import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ITrackInfo;
import net.sourceforge.atunes.utils.DesktopUtils;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Tracks of an album
 * 
 * @author alex
 * 
 */
public class AlbumTracksContent extends AbstractContextPanelContent {

    private static final long serialVersionUID = -5538266144953409867L;

    private JTable tracksTable;

    @Override
    public String getContentName() {
        return I18nUtils.getString("SONGS");
    }

    @Override
    public Map<String, ?> getDataSourceParameters(IAudioObject audioObject) {
        Map<String, IAudioObject> parameters = new HashMap<String, IAudioObject>();
        parameters.put(AlbumInfoDataSource.INPUT_AUDIO_OBJECT, audioObject);
        return parameters;
    }

    @Override
    public void updateContentWithDataSourceResult(Map<String, ?> result) {
        if (result.containsKey(AlbumInfoDataSource.OUTPUT_ALBUM)) {
            tracksTable.setModel(new ContextTracksTableModel((IAlbumInfo) result.get(AlbumInfoDataSource.OUTPUT_ALBUM)));
        }
    }

    @Override
    public void clearContextPanelContent() {
        super.clearContextPanelContent();
        tracksTable.setModel(new ContextTracksTableModel(null));
    }

    @Override
    public Component getComponent() {
        // Create components
    	TracksTableFactory factory = new TracksTableFactory();
    	factory.setLookAndFeelManager(getLookAndFeelManager());
    	tracksTable = factory.getNewTracksTable(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedTrack = tracksTable.getSelectedRow();
                    if (selectedTrack != -1) {
                        ITrackInfo track = ((ContextTracksTableModel) tracksTable.getModel()).getTrack(selectedTrack);
                        DesktopUtils.openURL(track.getUrl());
                    }
                }
            }
        });
        return getLookAndFeelManager().getCurrentLookAndFeel().getTableScrollPane(tracksTable);
    }
}
