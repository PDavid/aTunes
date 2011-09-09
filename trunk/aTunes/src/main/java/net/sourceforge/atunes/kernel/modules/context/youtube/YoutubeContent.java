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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuItem;

import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanelContent;
import net.sourceforge.atunes.kernel.modules.context.ContextHandler;
import net.sourceforge.atunes.kernel.modules.context.ContextTable;
import net.sourceforge.atunes.kernel.modules.internetsearch.SearchFactory;
import net.sourceforge.atunes.kernel.modules.webservices.youtube.YoutubeResultEntry;
import net.sourceforge.atunes.kernel.modules.webservices.youtube.YoutubeService;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.DesktopUtils;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Content to show videos from Youtube
 * 
 * @author alex
 * 
 */
public class YoutubeContent extends AbstractContextPanelContent {

    private static final long serialVersionUID = 5041098100868186051L;

    private ContextTable youtubeResultTable;
    
    private JMenuItem moreResults;
    
    private JMenuItem openYoutube;
    
    private YoutubeService youtubeService;

    public YoutubeContent() {
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
        youtubeResultTable = new ContextTable();
        youtubeResultTable.addContextRowPanel(new YoutubeResultsTableCellRendererCode(getState(), youtubeService));
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
        String searchString = youtubeService.getSearchForAudioObject(ContextHandler.getInstance().getCurrentAudioObject());
        if (searchString.length() > 0) {
            final List<YoutubeResultEntry> result = youtubeService.searchInYoutube(searchString, youtubeResultTable.getRowCount() + 1);
            ((YoutubeResultTableModel) youtubeResultTable.getModel()).addEntries(result);
        }
    }

    /**
     * Opens a web browser to show youtube results
     */
    protected void openYoutube() {
        DesktopUtils.openSearch(SearchFactory.getSearchForName("YouTube"), youtubeService.getSearchForAudioObject(
                ContextHandler.getInstance().getCurrentAudioObject()));
    }
    
    public void setYoutubeService(YoutubeService youtubeService) {
		this.youtubeService = youtubeService;
	}

}
