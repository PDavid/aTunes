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

package net.sourceforge.atunes.kernel.modules.context.audioobject;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanelContent;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IStateContext;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Basic information about an audio object
 * @author alex
 *
 */
public class AudioObjectBasicInfoContent extends AbstractContextPanelContent<AudioObjectBasicInfoDataSource> {

    private static final long serialVersionUID = 996227362636450601L;

    /**
     * Image for Audio Object
     */
    private JLabel audioObjectImage;

    /**
     * Title of audio object
     */
    private JLabel audioObjectTitle;

    /**
     * Artist of audio object
     */
    private JLabel audioObjectArtist;

    /**
     * Last date played this audio object
     */
    private JLabel audioObjectLastPlayDate;
    
    private IStateContext stateContext;
    
    private AbstractAction addLovedSongInLastFMAction;
    
    private AbstractAction addBannedSongInLastFMAction;
    
    /**
     * @param addBannedSongInLastFMAction
     */
    public void setAddBannedSongInLastFMAction(AbstractAction addBannedSongInLastFMAction) {
		this.addBannedSongInLastFMAction = addBannedSongInLastFMAction;
	}
    
    
    /**
     * @param addLovedSongInLastFMAction
     */
    public void setAddLovedSongInLastFMAction(AbstractAction addLovedSongInLastFMAction) {
		this.addLovedSongInLastFMAction = addLovedSongInLastFMAction;
	}
    
    /**
     * @param stateContext
     */
    public void setStateContext(IStateContext stateContext) {
		this.stateContext = stateContext;
	}

    @Override
    public void clearContextPanelContent() {
        super.clearContextPanelContent();
        audioObjectImage.setIcon(null);
        audioObjectImage.setBorder(null);
        audioObjectTitle.setText(null);
        audioObjectArtist.setText(null);
        audioObjectLastPlayDate.setText(null);
        addLovedSongInLastFMAction.setEnabled(false);
        addBannedSongInLastFMAction.setEnabled(false);
    }

    @Override
    public void updateContentFromDataSource(AudioObjectBasicInfoDataSource source) {
        ImageIcon image = source.getImage();
        if (image != null) {
            audioObjectImage.setIcon(image);
        }
        
        audioObjectTitle.setText(source.getTitle());
        audioObjectArtist.setText(source.getArtist());
        audioObjectLastPlayDate.setText(source.getLastPlayDate());

        // TODO: Allow these options for radios where song information is available
        addLovedSongInLastFMAction.setEnabled(stateContext.isLastFmEnabled() && source.getAudioObject() instanceof ILocalAudioObject);
        addBannedSongInLastFMAction.setEnabled(stateContext.isLastFmEnabled() && source.getAudioObject() instanceof ILocalAudioObject);
    }

    @Override
    public String getContentName() {
        return I18nUtils.getString("INFO");
    }

    @Override
    public Component getComponent() {
        // Create components
        audioObjectImage = new JLabel();
        audioObjectTitle = new JLabel();
        audioObjectTitle.setHorizontalAlignment(SwingConstants.CENTER);
        audioObjectTitle.setFont(getLookAndFeelManager().getCurrentLookAndFeel().getContextInformationBigFont());
        audioObjectArtist = new JLabel();
        audioObjectArtist.setHorizontalAlignment(SwingConstants.CENTER);
        audioObjectLastPlayDate = new JLabel();
        audioObjectLastPlayDate.setHorizontalAlignment(SwingConstants.CENTER);

        // Add components
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(15, 0, 0, 0);
        panel.add(audioObjectImage, c);
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 10, 0, 10);
        panel.add(audioObjectTitle, c);
        c.gridy = 2;
        c.insets = new Insets(5, 10, 10, 10);
        panel.add(audioObjectArtist, c);
        c.gridy = 3;
        panel.add(audioObjectLastPlayDate, c);

        return panel;
    }

    @Override
    public List<Component> getOptions() {
        List<Component> options = new ArrayList<Component>();
        options.add(new JMenuItem(addLovedSongInLastFMAction));
        options.add(new JMenuItem(addBannedSongInLastFMAction));
        return options;
    }
}
