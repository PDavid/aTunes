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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanelContent;
import net.sourceforge.atunes.kernel.modules.context.ContextHandler;
import net.sourceforge.atunes.kernel.modules.context.ContextImageJTable;
import net.sourceforge.atunes.kernel.modules.context.ContextTableRowPanel;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.internetsearch.SearchFactory;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.kernel.modules.webservices.youtube.YoutubeResultEntry;
import net.sourceforge.atunes.kernel.modules.webservices.youtube.YoutubeService;
import net.sourceforge.atunes.kernel.modules.webservices.youtube.YoutubeVideoDownloader;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.DesktopUtils;
import net.sourceforge.atunes.utils.FileNameUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.jfree.ui.ExtensionFileFilter;

/**
 * Content to show videos from Youtube
 * 
 * @author alex
 * 
 */
public class YoutubeContent extends AbstractContextPanelContent {

    private static final long serialVersionUID = 5041098100868186051L;

    private ContextImageJTable youtubeResultTable;
    
    private JMenuItem moreResults;
    
    private JMenuItem openYoutube;

    public YoutubeContent() {
        super(new YoutubeDataSource());
        moreResults = new JMenuItem(I18nUtils.getString("SEE_MORE_RESULTS"));
        moreResults.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                searchMoreResultsInYoutube();
            }
        });
        openYoutube = new JMenuItem(I18nUtils.getString("GO_TO_YOUTUBE"));
        openYoutube.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                openYoutube();
            }
        });
    }

    @Override
    protected String getContentName() {
        return I18nUtils.getString("YOUTUBE_VIDEOS");
    }

    @Override
    protected Map<String, ?> getDataSourceParameters(AudioObject audioObject) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(YoutubeDataSource.INPUT_AUDIO_OBJECT, audioObject);
        return parameters;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void updateContentWithDataSourceResult(Map<String, ?> result) {
        if (result.containsKey(YoutubeDataSource.OUTPUT_VIDEOS)) {
            youtubeResultTable.setModel(new YoutubeResultTableModel((List<YoutubeResultEntry>) result.get(YoutubeDataSource.OUTPUT_VIDEOS)));
            moreResults.setEnabled(true);
            openYoutube.setEnabled(true);
        }        
    }

    @Override
    protected void clearContextPanelContent() {
        super.clearContextPanelContent();
        youtubeResultTable.setModel(new YoutubeResultTableModel(null));
        moreResults.setEnabled(false);
        openYoutube.setEnabled(false);
    }

    @Override
    protected Component getComponent() {
        // Create components
        youtubeResultTable = new ContextImageJTable();
        youtubeResultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        youtubeResultTable.getTableHeader().setReorderingAllowed(false);
        
        YoutubeResultsTableCellRendererCode code = new YoutubeResultsTableCellRendererCode(YoutubeResultEntry.class, youtubeResultTable);
        code.bind();
        
        youtubeResultTable.setColumnSelectionAllowed(false);

        return youtubeResultTable;
    }

    @Override
    protected List<Component> getOptions() {
        List<Component> options = new ArrayList<Component>();
        options.add(moreResults);
        options.add(openYoutube);
        return options;
    }

    /**
     * Searches for more results of the last search
     * 
     * @return
     */
    protected void searchMoreResultsInYoutube() {
        String searchString = YoutubeService.getInstance().getSearchForAudioObject(ContextHandler.getInstance().getCurrentAudioObject());
        if (searchString.length() > 0) {
            final List<YoutubeResultEntry> result = YoutubeService.getInstance().searchInYoutube(searchString, youtubeResultTable.getRowCount() + 1);
            ((YoutubeResultTableModel) youtubeResultTable.getModel()).addEntries(result);
        }
    }

    /**
     * Opens a web browser to show youtube results
     */
    protected void openYoutube() {
        DesktopUtils.openSearch(SearchFactory.getSearchForName("YouTube"), YoutubeService.getInstance().getSearchForAudioObject(
                ContextHandler.getInstance().getCurrentAudioObject()));
    }

	private static class YoutubeResultsTableCellRendererCode extends ContextTableRowPanel {
		
        public YoutubeResultsTableCellRendererCode(Class<?> clazz, ContextImageJTable table) {
			super(clazz, table);
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
		public List<AbstractAction> getActions() {
			List<AbstractAction> actions = new ArrayList<AbstractAction>();
			actions.add(new AbstractAction(I18nUtils.getString("PLAY_VIDEO_AT_YOUTUBE")) {
				
				private static final long serialVersionUID = -7322221144744041599L;

				@Override
				public void actionPerformed(ActionEvent arg0) {
			        int selectedVideo = table.getSelectedRow();
			        if (selectedVideo != -1) {
			            // get entry
			            YoutubeResultEntry entry = ((YoutubeResultTableModel) table.getModel()).getEntry(selectedVideo);
			            if (entry.getUrl() != null) {
			                //open youtube url
			                DesktopUtils.openURL(entry.getUrl());
			                // When playing a video in web browser automatically pause current song
			                if (PlayerHandler.getInstance().isEnginePlaying()) {
			                    PlayerHandler.getInstance().playCurrentAudioObject(true);
			                }
			            }
			        }

				}
			});
			
			actions.add(new AbstractAction(I18nUtils.getString("DOWNLOAD_VIDEO")) {
				
				/**
				 * 
				 */
				private static final long serialVersionUID = 7103883762831086189L;

				@Override
				public void actionPerformed(ActionEvent e) {
			        int selectedVideo = table.getSelectedRow();
			        if (selectedVideo != -1) {
			            // get entry
			            YoutubeResultEntry entry = ((YoutubeResultTableModel) table.getModel()).getEntry(selectedVideo);

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
			                final YoutubeVideoDownloader downloader = new YoutubeVideoDownloader(entry, selectedFile);
			                downloader.execute();
			            }
			        }
				}
			});
			return actions;
		}
    }
}
