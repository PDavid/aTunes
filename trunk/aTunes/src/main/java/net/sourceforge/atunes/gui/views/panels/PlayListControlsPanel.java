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

package net.sourceforge.atunes.gui.views.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.sourceforge.atunes.gui.views.controls.CustomButton;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable;
import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.AutoScrollPlayListAction;
import net.sourceforge.atunes.kernel.actions.ClearPlayListAction;
import net.sourceforge.atunes.kernel.actions.CreatePlayListWithSelectedAlbumsAction;
import net.sourceforge.atunes.kernel.actions.CreatePlayListWithSelectedArtistsAction;
import net.sourceforge.atunes.kernel.actions.LoadPlayListAction;
import net.sourceforge.atunes.kernel.actions.MoveDownAction;
import net.sourceforge.atunes.kernel.actions.MoveToBottomAction;
import net.sourceforge.atunes.kernel.actions.MoveToTopAction;
import net.sourceforge.atunes.kernel.actions.MoveUpAction;
import net.sourceforge.atunes.kernel.actions.RemoveFromPlayListAction;
import net.sourceforge.atunes.kernel.actions.SavePlayListAction;
import net.sourceforge.atunes.kernel.actions.SetPlayListSelectionAsFavoriteAlbumAction;
import net.sourceforge.atunes.kernel.actions.SetPlayListSelectionAsFavoriteArtistAction;
import net.sourceforge.atunes.kernel.actions.SetPlayListSelectionAsFavoriteSongAction;
import net.sourceforge.atunes.kernel.actions.ShowPlayListItemInfoAction;
import net.sourceforge.atunes.kernel.actions.ShufflePlayListAction;

public final class PlayListControlsPanel extends JPanel {

    private static final long serialVersionUID = -1966827894270243002L;

    /** The save play list button. */
    private JButton savePlaylistButton;

    /** The load play list button. */
    private JButton loadPlaylistButton;

    /** The scroll play list to current song button. */
    private JButton scrollPlaylistToCurrentSongButton;

    /** The artist button. */
    private JButton artistButton;

    /** The album button. */
    private JButton albumButton;

    /** The top button. */
    private JButton topButton;

    /** The up button. */
    private JButton upButton;

    /** The delete button. */
    private JButton deleteButton;

    /** The down button. */
    private JButton downButton;

    /** The bottom button. */
    private JButton bottomButton;

    /** The info button. */
    private JButton infoButton;

    /** The clear button. */
    private JButton clearButton;

    /** The shuffle playlist. */
    private JButton shufflePlaylist;

    /** The favorite song. */
    private JButton favoriteSongButton;

    /** The favorite album. */
    private JButton favoriteAlbumButton;

    /** The favorite artist. */
    private JButton favoriteArtistButton;

    /** The playlist table */
    private PlayListTable playListTable;

    /**
     * Instantiates a new play list controls panel.
     */
    public PlayListControlsPanel(PlayListTable playListTable) {
        super(new GridBagLayout());
        this.playListTable = playListTable;
        addContent();
    }

    /**
     * Adds the content.
     */
    private void addContent() {
        savePlaylistButton = new CustomButton(Actions.getAction(SavePlayListAction.class));
        savePlaylistButton.setText(null);

        loadPlaylistButton = new CustomButton(Actions.getAction(LoadPlayListAction.class));
        loadPlaylistButton.setText(null);

        scrollPlaylistToCurrentSongButton = new CustomButton(Actions.getAction(AutoScrollPlayListAction.class));
        scrollPlaylistToCurrentSongButton.setText(null);

        artistButton = new CustomButton();
        artistButton.setAction(Actions.getActionAndBind(CreatePlayListWithSelectedArtistsAction.class, artistButton, playListTable));
        artistButton.setText(null);

        albumButton = new CustomButton();
        albumButton.setAction(Actions.getActionAndBind(CreatePlayListWithSelectedAlbumsAction.class, albumButton, playListTable));
        albumButton.setText(null);

        topButton = new CustomButton(Actions.getAction(MoveToTopAction.class));
        topButton.setText(null);

        upButton = new CustomButton(Actions.getAction(MoveUpAction.class));
        upButton.setText(null);

        deleteButton = new CustomButton(Actions.getAction(RemoveFromPlayListAction.class));
        deleteButton.setText(null);

        downButton = new CustomButton(Actions.getAction(MoveDownAction.class));
        downButton.setText(null);

        bottomButton = new CustomButton(Actions.getAction(MoveToBottomAction.class));
        bottomButton.setText(null);

        infoButton = new CustomButton(Actions.getAction(ShowPlayListItemInfoAction.class));
        infoButton.setText(null);

        clearButton = new CustomButton(Actions.getAction(ClearPlayListAction.class));
        clearButton.setText(null);

        shufflePlaylist = new CustomButton(Actions.getAction(ShufflePlayListAction.class));
        shufflePlaylist.setText(null);

        favoriteSongButton = new CustomButton(Actions.getAction(SetPlayListSelectionAsFavoriteSongAction.class));
        favoriteSongButton.setText(null);

        favoriteAlbumButton = new CustomButton(Actions.getAction(SetPlayListSelectionAsFavoriteAlbumAction.class));
        favoriteAlbumButton.setText(null);

        favoriteArtistButton = new CustomButton(Actions.getAction(SetPlayListSelectionAsFavoriteArtistAction.class));
        favoriteArtistButton.setText(null);

        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(1, 0, 0, 0);
        setButton(savePlaylistButton, c);

        c.gridx = 1;
        c.gridy = 0;
        setButton(loadPlaylistButton, c);

        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 1;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(1, 0, 0, 10);
        setButton(scrollPlaylistToCurrentSongButton, c);

        c.gridx = 3;
        c.gridy = 0;
        c.weightx = 0;
        c.insets = new Insets(1, 0, 0, 0);
        setButton(artistButton, c);

        c.gridx = 4;
        c.gridy = 0;
        c.weightx = 0;
        c.insets = new Insets(1, 0, 0, 10);
        setButton(albumButton, c);

        c.gridx = 5;
        c.gridy = 0;
        c.insets = new Insets(1, 0, 0, 10);
        setButton(infoButton, c);

        c.gridx = 6;
        c.gridy = 0;
        c.insets = new Insets(1, 0, 0, 0);
        setButton(deleteButton, c);

        c.gridx = 7;
        c.gridy = 0;
        setButton(clearButton, c);

        c.gridx = 8;
        c.gridy = 0;
        setButton(topButton, c);

        c.gridx = 9;
        c.gridy = 0;
        setButton(upButton, c);

        c.gridx = 10;
        c.gridy = 0;
        setButton(downButton, c);

        c.gridx = 11;
        c.gridy = 0;
        c.weighty = 1;
        c.insets = new Insets(1, 0, 0, 10);
        setButton(bottomButton, c);

        c.gridx = 12;
        c.gridy = 0;
        c.weighty = 1;
        c.insets = new Insets(1, 0, 0, 10);
        setButton(shufflePlaylist, c);

        c.gridx = 13;
        c.gridy = 0;
        c.insets = new Insets(1, 0, 0, 0);
        setButton(favoriteSongButton, c);

        c.gridx = 14;
        c.gridy = 0;
        c.insets = new Insets(1, 0, 0, 0);
        setButton(favoriteArtistButton, c);

        c.gridx = 15;
        c.gridy = 0;
        c.insets = new Insets(1, 0, 0, 0);
        setButton(favoriteAlbumButton, c);
    }

    /**
     * Sets the button.
     * 
     * @param button
     *            the button
     * @param c
     *            the c
     */
    private void setButton(JButton button, GridBagConstraints c) {
        button.setPreferredSize(new Dimension(20, 20));
        add(button, c);
    }
}
