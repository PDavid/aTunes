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
import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanelContent;
import net.sourceforge.atunes.kernel.modules.context.ContextInformationTableFactory;
import net.sourceforge.atunes.kernel.modules.context.ITracksTableListener;
import net.sourceforge.atunes.utils.CollectionUtils;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Tracks of an album
 * 
 * @author alex
 * 
 */
public class AlbumTracksContent extends AbstractContextPanelContent<AlbumInfoDataSource> {

    private static final long serialVersionUID = -5538266144953409867L;

    private JTable tracksTable;
    
    private JScrollPane scrollPane;
    
    private ContextInformationTableFactory contextInformationTableFactory;
    
    private ITracksTableListener contextTableURLOpener;
    
    /**
     * @param contextTableURLOpener
     */
    public void setContextTableURLOpener(ITracksTableListener contextTableURLOpener) {
		this.contextTableURLOpener = contextTableURLOpener;
	}
    
    @Override
    public String getContentName() {
        return I18nUtils.getString("SONGS");
    }
    
    /**
     * @param contextInformationTableFactory
     */
    public void setContextInformationTableFactory(ContextInformationTableFactory contextInformationTableFactory) {
		this.contextInformationTableFactory = contextInformationTableFactory;
	}
    
    @Override
    public void updateContentFromDataSource(AlbumInfoDataSource source) {
   		((ContextTracksTableModel)tracksTable.getModel()).setAlbum(source.getAlbumInfo());
   		scrollPane.setVisible(source.getAlbumInfo() != null && !CollectionUtils.isEmpty(source.getAlbumInfo().getTracks()));
    }
    
    @Override
    public void clearContextPanelContent() {
        super.clearContextPanelContent();
   		((ContextTracksTableModel)tracksTable.getModel()).setAlbum(null);
        scrollPane.setVisible(false);
    }

    @Override
    public Component getComponent() {
        // Create components
    	tracksTable = contextInformationTableFactory.getNewTracksTable(contextTableURLOpener);
        tracksTable.setModel(new ContextTracksTableModel());
    	scrollPane = getLookAndFeelManager().getCurrentLookAndFeel().getTableScrollPane(tracksTable);
        scrollPane.setVisible(false);
    	scrollPane.setPreferredSize(new Dimension(100, 250));
    	return scrollPane;
    }
}
