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

package net.sourceforge.atunes.kernel.modules.context.youtube;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JTable;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.modules.context.ContextTableAction;
import net.sourceforge.atunes.kernel.modules.context.ContextTableRowPanel;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.kernel.modules.webservices.youtube.YoutubeResultEntry;
import net.sourceforge.atunes.kernel.modules.webservices.youtube.YoutubeService;
import net.sourceforge.atunes.kernel.modules.webservices.youtube.YoutubeVideoDownloader;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.DesktopUtils;
import net.sourceforge.atunes.utils.FileNameUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.jfree.ui.ExtensionFileFilter;

public class YoutubeResultsTableCellRendererCode extends ContextTableRowPanel<YoutubeResultEntry> {
	
	private IState state;
	
	private YoutubeService youtubeService;
	
	public YoutubeResultsTableCellRendererCode(IState state, YoutubeService youtubeService) {
		this.state = state;
		this.youtubeService = youtubeService;
	}
	
	@Override
    public JComponent getComponent(JComponent superComponent, JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return getPanelForTableRenderer(((YoutubeResultEntry) value).getImage(), 
        								StringUtils.getString("<html>", ((YoutubeResultEntry) value).getName(), "<br>(", ((YoutubeResultEntry) value).getDuration(), ")</html>"), 
        								superComponent.getBackground(), 
        								superComponent.getForeground(), 
        								Constants.CONTEXT_IMAGE_WIDTH, 
        								Constants.CONTEXT_IMAGE_HEIGHT, 
        								hasFocus);
    }
	
	@Override
	public List<ContextTableAction<YoutubeResultEntry>> getActions() {
		List<ContextTableAction<YoutubeResultEntry>> actions = new ArrayList<ContextTableAction<YoutubeResultEntry>>();
		actions.add(new ContextTableAction<YoutubeResultEntry>(I18nUtils.getString("PLAY_VIDEO_AT_YOUTUBE"), table) {

			/**
			 * 
			 */
			private static final long serialVersionUID = -7758596564970276630L;

			@Override
			protected void execute(YoutubeResultEntry entry) {
			     //open youtube url
                DesktopUtils.openURL(entry.getUrl());
                // When playing a video in web browser automatically pause current song
                if (PlayerHandler.getInstance().isEnginePlaying()) {
                    PlayerHandler.getInstance().playCurrentAudioObject(true);
                }
			}
			
			@Override
			protected YoutubeResultEntry getSelectedObject(int row) {
				return ((YoutubeResultTableModel) table.getModel()).getEntry(row);
			}
			
			@Override
			protected boolean isEnabledForObject(YoutubeResultEntry object) {
				return true;
			}
		});
		
		actions.add(new ContextTableAction<YoutubeResultEntry>(I18nUtils.getString("DOWNLOAD_VIDEO"), table) {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = -4803916885071056150L;

			@Override
			protected void execute(YoutubeResultEntry entry) {
				// Open save dialog to select file
	            JFileChooser dialog = new JFileChooser();
	            dialog.setDialogType(JFileChooser.SAVE_DIALOG);
	            dialog.setDialogTitle(I18nUtils.getString("SAVE_YOUTUBE_VIDEO"));
	            dialog.setFileFilter(new ExtensionFileFilter("MP4", "MP4"));
	            // Set default file name
	            // for some reason dialog fails with files with [ or ] chars
	            File defaultFileName = new File(FileNameUtils.getValidFileName(entry.getName().replace("\\", "\\\\").replace("$", "\\$").replace('[', ' ').replace(']', ' ')));
	            dialog.setSelectedFile(defaultFileName);
	            int returnValue = dialog.showSaveDialog(GuiHandler.getInstance().getFrame().getFrame());
	            File selectedFile = dialog.getSelectedFile();
	            if (selectedFile != null && JFileChooser.APPROVE_OPTION == returnValue) {
	                final YoutubeVideoDownloader downloader = new YoutubeVideoDownloader(entry, selectedFile, state.getProxy(), youtubeService);
	                downloader.execute();
	            }
			}
			
			@Override
			protected YoutubeResultEntry getSelectedObject(int row) {
				return ((YoutubeResultTableModel) table.getModel()).getEntry(row);
			}
			
			@Override
			protected boolean isEnabledForObject(YoutubeResultEntry object) {
				return true;
			}
		});
		return actions;
	}
}
