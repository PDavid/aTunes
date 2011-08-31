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

package net.sourceforge.atunes.kernel.modules.context.similar;

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
import net.sourceforge.atunes.kernel.modules.context.ArtistInfo;
import net.sourceforge.atunes.kernel.modules.context.ContextImageJTable;
import net.sourceforge.atunes.kernel.modules.context.ContextTableRowPanel;
import net.sourceforge.atunes.kernel.modules.context.SimilarArtistsInfo;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.DesktopUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

public class SimilarArtistsContent extends AbstractContextPanelContent {

    private static final long serialVersionUID = 5041098100868186051L;
    private ContextImageJTable similarArtistsTable;

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
        super.clearContextPanelContent();
        similarArtistsTable.setModel(new SimilarArtistsTableModel(null));
    }

    @Override
    protected Component getComponent() {
        // Create components
        similarArtistsTable = new ContextImageJTable();
        similarArtistsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        SimilarArtistTableCellRendererCode code = new SimilarArtistTableCellRendererCode(ArtistInfo.class, similarArtistsTable);
        code.bind();
        
        similarArtistsTable.setColumnSelectionAllowed(false);
        similarArtistsTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        similarArtistsTable.getTableHeader().setReorderingAllowed(false);
        
        return similarArtistsTable;
    }

    /**
     * Special renderer for table
     * 
     * @author fleax
     * 
     */
    private static class SimilarArtistTableCellRendererCode extends ContextTableRowPanel {

        public SimilarArtistTableCellRendererCode(Class<?> clazz, ContextImageJTable table) {
			super(clazz, table);
		}

		@Override
        public JComponent getComponent(JComponent superComponent, JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return getPanelForTableRenderer(((ArtistInfo) value).getImage(), 
            							    StringUtils.getString("<html><br>", ((ArtistInfo) value).getName(), "<br>", ((ArtistInfo) value).getMatch(), "%<br>", ((ArtistInfo) value).isAvailable() ? I18nUtils.getString("AVAILABLE_IN_REPOSITORY") : "", "</html>"), 
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
                    int selectedArtist = table.getSelectedRow();
                    if (selectedArtist != -1) {
                        ArtistInfo artist = ((SimilarArtistsTableModel) table.getModel()).getArtist(selectedArtist);
                        DesktopUtils.openURL(artist.getUrl());
                    }

				}
			});
			return actions;
		}
    }    
}
