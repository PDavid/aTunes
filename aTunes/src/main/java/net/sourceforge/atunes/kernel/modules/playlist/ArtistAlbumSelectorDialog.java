/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import net.sourceforge.atunes.gui.AlbumTableColumnModel;
import net.sourceforge.atunes.gui.AlbumTableModel;
import net.sourceforge.atunes.gui.ColumnRenderers;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IArtistAlbumSelectorDialog;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IColumnSet;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * This dialog show albums of an artist and add selected album to the end of the
 * current play list
 * 
 * @author encestre
 * 
 */
public final class ArtistAlbumSelectorDialog extends AbstractCustomDialog
		implements IArtistAlbumSelectorDialog {

	private static final long serialVersionUID = 8991547440913162267L;

	private IArtist artist;

	private IAlbum album;

	private IColumnSet albumColumnSet;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Instantiates a new dialog.
	 * 
	 * @param frame
	 * @param controlsBuilder
	 */
	public ArtistAlbumSelectorDialog(final IFrame frame,
			final IControlsBuilder controlsBuilder) {
		super(frame, 600, 500, controlsBuilder);
	}

	@Override
	public void initialize() {
		setResizable(false);
	}

	/**
	 * @param albumColumnSet
	 */
	public void setAlbumColumnSet(final IColumnSet albumColumnSet) {
		this.albumColumnSet = albumColumnSet;
	}

	/**
	 * Gets the content.
	 * 
	 * @return the content
	 */
	private JPanel getContent() {
		JPanel panel = new JPanel(new BorderLayout());

		final JTable albumTable = getLookAndFeel().getTable();
		albumTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// Disable autoresize, as we will control it
		albumTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		panel.add(getControlsBuilder().createScrollPane(albumTable),
				BorderLayout.CENTER);

		List<IAlbum> albumList = new ArrayList<IAlbum>(this.artist.getAlbums()
				.values());

		final AlbumTableModel model = new AlbumTableModel();
		albumTable.setModel(model);

		// Set column model
		AlbumTableColumnModel columnModel = this.beanFactory
				.getBean(AlbumTableColumnModel.class);
		columnModel.setTable(albumTable);
		columnModel.enableColumnChange(true);
		albumTable.setColumnModel(columnModel);

		// why ??? don't work without
		model.setColumnSet(this.albumColumnSet);
		columnModel.setColumnSet(this.albumColumnSet);
		// ???

		// Set renderers
		ColumnRenderers.addRenderers(albumTable, columnModel, getLookAndFeel());

		// Bind column set popup menu to select columns to display
		getControlsBuilder().createColumnSetPopupMenu(albumTable, columnModel,
				model);

		model.setAlbums(albumList);

		albumTable.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				int row = albumTable.getSelectedRow();
				if (row != -1) {
					ArtistAlbumSelectorDialog.this.album = model
							.getAlbumAt(row);
					setVisible(false);
				}
			}
		});

		return panel;
	}

	@Override
	public void setArtist(final IArtist artist) {
		this.artist = artist;
	}

	@Override
	public void showDialog() {
		String text = I18nUtils.getString("ADD_ARTIST_DIALOG_TITLE");
		text = text.replace("(%ARTIST%)", this.artist.getName());
		setTitle(text);
		add(getContent());
		setVisible(true);
	}

	@Override
	public IAlbum getAlbum() {
		return this.album;
	}

	@Override
	public void hideDialog() {
		setVisible(false);
	}
}
