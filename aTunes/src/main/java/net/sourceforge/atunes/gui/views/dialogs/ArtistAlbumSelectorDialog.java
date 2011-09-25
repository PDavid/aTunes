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

package net.sourceforge.atunes.gui.views.dialogs;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.model.AlbumTableColumnModel;
import net.sourceforge.atunes.gui.model.AlbumTableModel;
import net.sourceforge.atunes.gui.renderers.ColumnRenderers;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.gui.views.controls.ColumnSetPopupMenu;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.IArtistAlbumSelectorDialog;
import net.sourceforge.atunes.model.IColumnSet;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * This dialog show albums of an artist and add selected album to the end of the current playlist
 * @author encestre
 *
 */
public final class ArtistAlbumSelectorDialog extends AbstractCustomDialog implements IArtistAlbumSelectorDialog {

    private static final long serialVersionUID = 8991547440913162267L;

	private Artist artist;
   
	private Album album;
	
	private ILookAndFeel lookAndFeel;

    /**
     * Instantiates a new  dialog.
     * @param frame
     * @param lookAndFeelManager
     */
    public ArtistAlbumSelectorDialog(IFrame frame, ILookAndFeelManager lookAndFeelManager) {
        super(frame, 600, 500, true, CloseAction.DISPOSE, lookAndFeelManager.getCurrentLookAndFeel());
        this.lookAndFeel = lookAndFeelManager.getCurrentLookAndFeel();
        setResizable(false);
    }
    
    /**
     * Gets the content.
     * 
     * @return the content
     */
    private JPanel getContent() {
        JPanel panel = new JPanel(new BorderLayout());
        
        final JTable albumTable = lookAndFeel.getTable();
        albumTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);        
        // Disable autoresize, as we will control it
        albumTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        panel.add(lookAndFeel.getTableScrollPane(albumTable), BorderLayout.CENTER);
        
        ArrayList<Album> albumList = new ArrayList<Album>(artist.getAlbums().values());
        
        final AlbumTableModel model = new AlbumTableModel(lookAndFeel);
        albumTable.setModel(model);
        
        // Set column model
        AlbumTableColumnModel columnModel = new AlbumTableColumnModel(albumTable, lookAndFeel);
        albumTable.setColumnModel(columnModel);
        
        // why ??? don't work without
        IColumnSet columnSet = (IColumnSet) Context.getBean("albumColumnSet");
        model.setColumnSet(columnSet);
        columnModel.setColumnSet(columnSet);        
        // ???
        
        // 	Set renderers
        ColumnRenderers.addRenderers(albumTable, columnModel, lookAndFeel);
        
        // Bind column set popup menu to select columns to display
		new ColumnSetPopupMenu(albumTable, columnModel);
        
        model.setAlbums(albumList);
        
        albumTable.addMouseListener(new MouseAdapter(){

			@Override
			public void mouseClicked(MouseEvent e) {
				int row = albumTable.getSelectedRow();
				album = (Album) model.getAlbumAt(row);
				setVisible(false);
			}
		});
        
        return panel;
    }
    
    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IArtistAlbumSelectorDialog#showDialog(net.sourceforge.atunes.model.Artist)
	 */
    @Override
	public Album showDialog(Artist artist) {
        this.artist = artist;
      	String text = I18nUtils.getString("ADD_ARTIST_DIALOG_TITLE");
        text = text.replace("(%ARTIST%)", artist.getName());
        setTitle(text);
        add(getContent());
    	setVisible(true);
    	return album;
    }

}
