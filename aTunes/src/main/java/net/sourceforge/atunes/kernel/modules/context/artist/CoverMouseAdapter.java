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

package net.sourceforge.atunes.kernel.modules.context.artist;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.model.IDesktop;

final class CoverMouseAdapter extends MouseAdapter {
    private final IAlbumInfo album;
    private final JLabel coverLabel;
    private final IDesktop desktop;

    CoverMouseAdapter(IAlbumInfo album, JLabel coverLabel, IDesktop desktop) {
        this.album = album;
        this.coverLabel = coverLabel;
        this.desktop = desktop;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        coverLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        coverLabel.setCursor(Cursor.getDefaultCursor());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    	desktop.openURL(album.getUrl());
    }
}