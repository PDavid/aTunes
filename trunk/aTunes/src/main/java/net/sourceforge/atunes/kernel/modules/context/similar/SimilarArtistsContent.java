/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.substance.SubstanceContextImageJTable;
import net.sourceforge.atunes.kernel.modules.context.ArtistInfo;
import net.sourceforge.atunes.kernel.modules.context.ContextPanelContent;
import net.sourceforge.atunes.kernel.modules.context.SimilarArtistsInfo;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.DesktopUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.jvnet.substance.api.renderers.SubstanceDefaultTableCellRenderer;

public class SimilarArtistsContent extends ContextPanelContent {

    private static final long serialVersionUID = 5041098100868186051L;
    private SubstanceContextImageJTable similarArtistsTable;

    public SimilarArtistsContent() {
        super(new SimilarArtistsDataSource());
    }

    @Override
    protected String getContentName() {
        return I18nUtils.getString("SIMILAR");
    }

    @Override
    protected Map<String, ?> getDataSourceParameters(AudioObject audioObject) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(SimilarArtistsDataSource.INPUT_AUDIO_OBJECT, audioObject);
        return parameters;
    }

    @Override
    protected void updateContentWithDataSourceResult(Map<String, ?> result) {
        if (result.containsKey(SimilarArtistsDataSource.OUTPUT_ARTISTS)) {
            similarArtistsTable.setModel(new SimilarArtistsTableModel(((SimilarArtistsInfo) result.get(SimilarArtistsDataSource.OUTPUT_ARTISTS)).getArtists()));
        }
    }

    @Override
    protected void clearContextPanelContent() {
        // TODO Auto-generated method stub
        super.clearContextPanelContent();
        similarArtistsTable.setModel(new SimilarArtistsTableModel(null));
    }

    @Override
    protected Component getComponent() {
        // Create components
        similarArtistsTable = new SubstanceContextImageJTable();
        similarArtistsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        similarArtistsTable.setShowGrid(false);
        similarArtistsTable.setDefaultRenderer(ArtistInfo.class, new SubstanceDefaultTableCellRenderer() {
            private static final long serialVersionUID = 0L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean arg2, boolean arg3, int arg4, int arg5) {
                Color backgroundColor = super.getTableCellRendererComponent(table, value, arg2, arg3, arg4, arg5).getBackground();
                return getPanelForTableRenderer(((ArtistInfo) value).getImage(), StringUtils.getString("<html><br>", ((ArtistInfo) value).getName(), "<br>", ((ArtistInfo) value)
                        .getMatch(), "%<br>", ((ArtistInfo) value).isAvailable() ? I18nUtils.getString("AVAILABLE_IN_REPOSITORY") : "", "</html>"), backgroundColor,
                        Constants.CONTEXT_IMAGE_WIDTH, Constants.CONTEXT_IMAGE_HEIGHT);
            }
        });
        similarArtistsTable.setColumnSelectionAllowed(false);
        similarArtistsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        similarArtistsTable.getTableHeader().setReorderingAllowed(false);

        similarArtistsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedArtist = similarArtistsTable.getSelectedRow();
                    if (selectedArtist != -1) {
                        ArtistInfo artist = ((SimilarArtistsTableModel) similarArtistsTable.getModel()).getArtist(selectedArtist);
                        DesktopUtils.openURL(artist.getUrl());
                    }
                }
            }
        });

        return similarArtistsTable;
    }

}
