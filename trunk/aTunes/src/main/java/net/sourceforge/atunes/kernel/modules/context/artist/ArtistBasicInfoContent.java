/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.StyleConstants;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.Fonts;
import net.sourceforge.atunes.gui.views.controls.CustomTextPane;
import net.sourceforge.atunes.gui.views.controls.UrlLabel;
import net.sourceforge.atunes.kernel.modules.context.ContextPanelContent;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.I18nUtils;

import org.jdesktop.swingx.border.DropShadowBorder;

/**
 * Basic information about an artist
 * 
 * @author alex
 * 
 */
public class ArtistBasicInfoContent extends ContextPanelContent {

    private static final long serialVersionUID = -5538266144953409867L;

    private JLabel artistImageLabel;
    private UrlLabel artistNameLabel;
    private CustomTextPane artistWikiAbstract;
    private UrlLabel artistWikiReadMore;

    public ArtistBasicInfoContent() {
        super(new ArtistInfoDataSource());
    }

    @Override
    protected String getContentName() {
        return I18nUtils.getString("INFO");
    }

    @Override
    protected Map<String, ?> getDataSourceParameters(AudioObject audioObject) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(ArtistInfoDataSource.INPUT_AUDIO_OBJECT, audioObject);
        // Want image too 
        parameters.put(ArtistInfoDataSource.INPUT_BOOLEAN_IMAGE, true);
        return parameters;
    }

    @Override
    protected void updateContentWithDataSourceResult(Map<String, ?> result) {
        Image artistImage = (Image) result.get(ArtistInfoDataSource.OUTPUT_IMAGE);
        if (artistImage != null) {
            artistImageLabel.setIcon(ImageUtils.scaleImageBicubic(artistImage, Constants.ARTIST_IMAGE_SIZE, Constants.ARTIST_IMAGE_SIZE));
            artistImageLabel.setBorder(new DropShadowBorder());
        }
        String artistName = (String) result.get(ArtistInfoDataSource.OUTPUT_ARTIST_NAME);
        String artistUrl = (String) result.get(ArtistInfoDataSource.OUTPUT_ARTIST_URL);
        if (artistName != null && artistUrl != null) {
            artistNameLabel.setText(artistName, artistUrl);
        }
        String wikiText = (String) result.get(ArtistInfoDataSource.OUTPUT_WIKI_TEXT);
        if (wikiText != null) {
            artistWikiAbstract.setText(wikiText);
            artistWikiAbstract.setCaretPosition(0);
        }
        String wikiUrl = (String) result.get(ArtistInfoDataSource.OUTPUT_WIKI_URL);
        if (wikiUrl != null) {
            artistWikiReadMore.setText(I18nUtils.getString("READ_MORE"), wikiUrl);
        }
    }

    @Override
    protected void clearContextPanelContent() {
        super.clearContextPanelContent();
        artistImageLabel.setIcon(null);
        artistImageLabel.setBorder(null);
        artistNameLabel.setText(null, null);
        artistWikiAbstract.setText(null);
        artistWikiReadMore.setText(null, null);
    }

    @Override
    protected Component getComponent() {
        // Create components
        artistImageLabel = new JLabel();
        artistNameLabel = new UrlLabel();
        artistNameLabel.setFont(Fonts.CONTEXT_INFORMATION_BIG_FONT);
        artistWikiAbstract = new CustomTextPane(StyleConstants.ALIGN_JUSTIFIED);
        artistWikiAbstract.setEditable(false);
        artistWikiAbstract.setBorder(BorderFactory.createEmptyBorder());
        artistWikiAbstract.setOpaque(false);
        artistWikiReadMore = new UrlLabel();

        // Add components
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(15, 5, 0, 5);
        panel.add(artistImageLabel, c);
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(artistNameLabel, c);
        c.gridy = 2;
        c.weightx = 1;
        c.weighty = 0.3;
        c.fill = GridBagConstraints.BOTH;
        panel.add(artistWikiAbstract, c);
        c.gridy = 3;
        c.weighty = 0;
        panel.add(artistWikiReadMore, c);

        return panel;
    }

}
