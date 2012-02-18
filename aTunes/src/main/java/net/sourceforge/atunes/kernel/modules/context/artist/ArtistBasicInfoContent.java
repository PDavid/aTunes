/*
 * aTunes 2.2.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.modules.context.artist;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.StyleConstants;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.views.controls.CustomTextPane;
import net.sourceforge.atunes.gui.views.controls.UrlLabel;
import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanelContent;
import net.sourceforge.atunes.utils.I18nUtils;

import org.jdesktop.swingx.border.DropShadowBorder;

/**
 * Basic information about an artist
 * 
 * @author fleax
 * 
 */
public class ArtistBasicInfoContent extends AbstractContextPanelContent<ArtistInfoDataSource> {

    private static final long serialVersionUID = -5538266144953409867L;

    private JLabel artistImageLabel;
    private UrlLabel artistNameLabel;
    private CustomTextPane artistWikiAbstract;
    private UrlLabel artistWikiReadMore;

    @Override
    public String getContentName() {
        return I18nUtils.getString("INFO");
    }

    @Override
    public void updateContentFromDataSource(ArtistInfoDataSource source) {
        ImageIcon artistImage = source.getArtistImage();
        if (artistImage != null) {
            artistImageLabel.setIcon(artistImage);
            artistImageLabel.setBorder(Context.getBean(DropShadowBorder.class));
        }
        String artistName = source.getArtistName();
        String artistUrl = source.getArtistUrl();
        if (artistName != null && artistUrl != null) {
            artistNameLabel.setText(artistName, artistUrl);
        }
        String wikiText = source.getWikiText();
        if (wikiText != null) {
            artistWikiAbstract.setText(wikiText);
            artistWikiAbstract.setCaretPosition(0);
        }
        String wikiUrl = source.getWikiUrl();
        if (wikiUrl != null) {
            artistWikiReadMore.setText(I18nUtils.getString("READ_MORE"), wikiUrl);
        }
    }

    @Override
    public void clearContextPanelContent() {
        super.clearContextPanelContent();
        artistImageLabel.setIcon(null);
        artistImageLabel.setBorder(null);
        artistNameLabel.setText(null, null);
        artistWikiAbstract.setText(null);
        artistWikiReadMore.setText(null, null);
    }

    @Override
    public Component getComponent() {
        // Create components
        artistImageLabel = new JLabel();
        artistNameLabel = new UrlLabel(getDesktop());
        artistNameLabel.setFont(getLookAndFeelManager().getCurrentLookAndFeel().getContextInformationBigFont());
        artistWikiAbstract = new CustomTextPane(StyleConstants.ALIGN_JUSTIFIED, getLookAndFeelManager());
        artistWikiAbstract.setEditable(false);
        artistWikiAbstract.setBorder(BorderFactory.createEmptyBorder());
        artistWikiAbstract.setOpaque(false);
        artistWikiReadMore = new UrlLabel(getDesktop());

        // Add components
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(15, 5, 0, 5);
        panel.add(artistImageLabel, c);
        c.gridy = 1;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(artistNameLabel, c);
        c.gridy = 2;
        c.weightx = 1;
        c.weighty = 1;        
        c.fill = GridBagConstraints.BOTH;
        panel.add(artistWikiAbstract, c);
        c.gridy = 3;
        c.weighty = 0;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.EAST;
        panel.add(artistWikiReadMore, c);

        return panel;
    }

}
