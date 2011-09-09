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

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.gui.model.AlbumTableColumnModel;
import net.sourceforge.atunes.gui.model.AlbumTableModel;
import net.sourceforge.atunes.gui.renderers.ColumnRenderers;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.gui.views.controls.ColumnSetPopupMenu;
import net.sourceforge.atunes.kernel.modules.columns.AbstractColumnSet;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * This dialog show albums of an artist and add selected album to the end of the current playlist
 * @author encestre
 *
 */
public final class AddArtistDragDialog extends AbstractCustomDialog {

    private static final long serialVersionUID = 8991547440913162267L;

	private Artist artist;
   

    /**
     * Instantiates a new  dialog.
     * @param jFrame 
     */
    public AddArtistDragDialog(JFrame jFrame, final Artist artist) {
        super(jFrame, 600, 500, true, CloseAction.DISPOSE);
      	String text = I18nUtils.getString("ADD_ARTIST_DIALOG_TITLE");
        text = text.replace("(%ARTIST%)", artist.getName());
        setTitle(text);
        setResizable(false);
        this.artist = artist;
        add(getContent());
    }

    
    /**
     * Gets the content.
     * 
     * @return the content
     */
    private JPanel getContent() {
        JPanel panel = new JPanel(new BorderLayout());
        
        final JTable albumTable = LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getTable();
        albumTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);        
        // Disable autoresize, as we will control it
        albumTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        panel.add(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getTableScrollPane(albumTable), BorderLayout.CENTER);
        
        ArrayList<Album> albumList = new ArrayList<Album>(artist.getAlbums().values());
        
        final AlbumTableModel model = new AlbumTableModel();
        albumTable.setModel(model);
        
        // Set column model
        AlbumTableColumnModel columnModel = new AlbumTableColumnModel(albumTable);
        albumTable.setColumnModel(columnModel);
        
        // why ??? don't work without
        AbstractColumnSet columnSet = (AbstractColumnSet) Context.getBean("albumColumnSet");
        model.setColumnSet(columnSet);
        columnModel.setColumnSet(columnSet);        
        // ???
        
        // 	Set renderers
        ColumnRenderers.addRenderers(albumTable, columnModel);
        
        // Bind column set popup menu to select columns to display
        @SuppressWarnings("unused")
		ColumnSetPopupMenu columnSetPopupMenu = new ColumnSetPopupMenu(albumTable, columnModel);
        
        model.setAlbums(albumList);
        
        albumTable.addMouseListener(new MouseAdapter(){

			@Override
			public void mouseClicked(MouseEvent e) {
				int row = albumTable.getSelectedRow();
				Album album = (Album) model.getAlbumAt(row);
				PlayListHandler.getInstance().addToPlayList(album.getAudioObjects());
				setVisible(false);
			}
		});
        
        return panel;
    }

}
