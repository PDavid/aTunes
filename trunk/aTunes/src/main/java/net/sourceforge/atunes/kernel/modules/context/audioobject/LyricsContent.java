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

package net.sourceforge.atunes.kernel.modules.context.audioobject;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.text.StyleConstants;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.views.controls.CustomTextPane;
import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanelContent;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.model.EditTagSources;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILyrics;
import net.sourceforge.atunes.model.ILyricsService;
import net.sourceforge.atunes.model.ITagHandler;
import net.sourceforge.atunes.utils.ClipboardFacade;
import net.sourceforge.atunes.utils.DesktopUtils;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Lyrics information
 * 
 * @author alex
 * 
 */
public class LyricsContent extends AbstractContextPanelContent {

    private static final class OpenUrlActionListener implements ActionListener {
        private final Entry<String, String> entry;

        private OpenUrlActionListener(Entry<String, String> entry) {
            this.entry = entry;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            DesktopUtils.openURL(entry.getValue());
        }
    }

    private static final long serialVersionUID = 962229017133714396L;

    private CustomTextPane lyricsContainer;

    private JMenu addLyrics;

    private JMenuItem copyLyrics;

    private JMenuItem openLyrics;

    private String lyricsSourceUrl;

    private IAudioObject audioObject;
    
    private ILyricsService lyricsService;
    
    private ClipboardFacade clipboard;

    public LyricsContent() {
        copyLyrics = new JMenuItem(new AbstractAction(I18nUtils.getString("COPY_TO_CLIPBOARD")) {

            private static final long serialVersionUID = -851267486478098295L;

            @Override
            public void actionPerformed(ActionEvent e) {
                String sLyric = lyricsContainer.getText();
                if (sLyric == null) {
                    sLyric = "";
                }
                clipboard.copyToClipboard(sLyric);
            }
        });
        addLyrics = new JMenu(I18nUtils.getString("ADD_LYRICS"));
        openLyrics = new JMenuItem(new AbstractAction(I18nUtils.getString("OPEN_LYRICS_SOURCE")) {

            private static final long serialVersionUID = 9043861642969889713L;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (lyricsSourceUrl != null && !lyricsSourceUrl.trim().isEmpty()) {
                    DesktopUtils.openURL(lyricsSourceUrl);
                } else {
                    if (audioObject instanceof AudioFile) {
                    	Context.getBean(ITagHandler.class).editFiles(EditTagSources.NAVIGATOR, Arrays.asList((ILocalAudioObject) audioObject));                        
                    }
                }
            }
        });
    }

    @Override
    protected Map<String, ?> getDataSourceParameters(IAudioObject audioObject) {
        Map<String, IAudioObject> parameters = new HashMap<String, IAudioObject>();
        parameters.put(LyricsDataSource.INPUT_AUDIO_OBJECT, audioObject);
        this.audioObject = audioObject;
        return parameters;
    }

    @Override
    protected void updateContentWithDataSourceResult(Map<String, ?> result) {
        if (result.containsKey(LyricsDataSource.OUTPUT_LYRIC)) {
            ILyrics lyrics = (ILyrics) result.get(LyricsDataSource.OUTPUT_LYRIC);
            lyricsContainer.setText(lyrics != null ? lyrics.getLyrics() : null);
            lyricsContainer.setCaretPosition(0);

            boolean lyricsNotEmpty = lyrics != null && !lyrics.getLyrics().trim().isEmpty();
            copyLyrics.setEnabled(lyricsNotEmpty);
            addLyrics.setEnabled(!lyricsNotEmpty);
            lyricsSourceUrl = lyrics != null ? lyrics.getUrl() : null;
            openLyrics.setEnabled(lyricsNotEmpty);
            if (!lyricsNotEmpty) {
                addLyrics.removeAll();
                for (final Entry<String, String> entry : lyricsService.getUrlsForAddingNewLyrics(audioObject.getArtist(), audioObject.getTitle()).entrySet()) {
                    JMenuItem mi = new JMenuItem(entry.getKey());
                    mi.addActionListener(new OpenUrlActionListener(entry));
                    addLyrics.add(mi);
                }
                addLyrics.setEnabled(addLyrics.getMenuComponentCount() > 0);
            }

        }
    }

    @Override
    protected void clearContextPanelContent() {
        super.clearContextPanelContent();
        lyricsContainer.setText(null);
        copyLyrics.setEnabled(false);
        addLyrics.setEnabled(false);
        addLyrics.removeAll();
        openLyrics.setEnabled(false);
        lyricsSourceUrl = null;
        audioObject = null;
    }

    @Override
    protected String getContentName() {
        return I18nUtils.getString("LYRICS");
    }

    @Override
    protected Component getComponent() {
        lyricsContainer = new CustomTextPane(StyleConstants.ALIGN_CENTER, getLookAndFeelManager());
        lyricsContainer.setBorder(null);
        lyricsContainer.setEditable(false);
        lyricsContainer.setOpaque(false);
        return lyricsContainer;
    }

    @Override
    protected List<Component> getOptions() {
        List<Component> options = new ArrayList<Component>();
        options.add(copyLyrics);
        options.add(addLyrics);
        options.add(openLyrics);
        return options;
    }

    public void setLyricsService(ILyricsService lyricsService) {
		this.lyricsService = lyricsService;
	}
    
    public void setClipboard(ClipboardFacade clipboard) {
		this.clipboard = clipboard;
	}
}
