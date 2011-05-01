/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

package net.sourceforge.atunes.kernel.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListIO;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This action saves current play list to file
 * 
 * @author fleax
 * 
 */
public class SavePlayListAction extends AbstractAction {

    private static final long serialVersionUID = -303252911138284095L;

    SavePlayListAction() {
        super(StringUtils.getString(I18nUtils.getString("SAVE"), "..."));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("SAVE_PLAYLIST_TOOLTIP"));
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser(ApplicationState.getInstance().getSavePlaylistPath());
        FileFilter filter = PlayListIO.getPlaylistFileFilter();
        // Open file chooser
        if (GuiHandler.getInstance().showSaveDialog(fileChooser, filter) == JFileChooser.APPROVE_OPTION) {

            // Get selected file
            File file = fileChooser.getSelectedFile();

            ApplicationState.getInstance().setSavePlaylistPath(file.getParentFile().getAbsolutePath());

            // If filename have incorrect extension, add it
            if (!file.getName().toUpperCase().endsWith(PlayListIO.M3U_FILE_EXTENSION.toUpperCase())) {
                file = new File(StringUtils.getString(file.getAbsolutePath(), ".", PlayListIO.M3U_FILE_EXTENSION));
            }

            // If file does not exist, or exist and overwrite is confirmed, then write file
            if (!file.exists()
                    || (file.exists() && GuiHandler.getInstance().showConfirmationDialog(I18nUtils.getString("OVERWRITE_FILE"), I18nUtils.getString("INFO")) == JOptionPane.OK_OPTION)) {
                PlayListIO.write(PlayListHandler.getInstance().getCurrentPlayList(true), file);
            }
        }
    }

    @Override
    public boolean isEnabledForPlayListSelection(List<AudioObject> selection) {
        return !PlayListHandler.getInstance().getCurrentPlayList(true).isEmpty();
    }

}
