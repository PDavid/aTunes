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

package net.sourceforge.atunes.kernel.modules.cdripper;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IRipperProgressDialog;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.StringUtils;

public final class RipperProgressDialog extends AbstractCustomDialog implements IRipperProgressDialog {

    private static final long serialVersionUID = -3891515847607545757L;

    private JLabel cover;
    private JProgressBar totalProgressBar;
    private JLabel totalProgressValueLabel;
    private JProgressBar decodeProgressBar;
    private JLabel decodeProgressValueLabel;
    private JProgressBar encodeProgressBar;
    private JLabel encodeProgressValueLabel;
    private JButton cancelButton;
    
    private ILookAndFeelManager lookAndFeelManager;
    
    private IIconFactory albumBigIcon;
    
    /**
     * @param albumBigIcon
     */
    public void setAlbumBigIcon(IIconFactory albumBigIcon) {
		this.albumBigIcon = albumBigIcon;
	}
    
    /**
     * @param lookAndFeelManager
     */
    public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

    /**
     * Instantiates a new ripper progress dialog.
     * @param frame
     * @param lookAndFeelManager
     */
    public RipperProgressDialog(IFrame frame, ILookAndFeelManager lookAndFeelManager) {
        super(frame, 420, 200, true, CloseAction.NOTHING, lookAndFeelManager.getCurrentLookAndFeel());
        setTitle(I18nUtils.getString("RIPPING_CD"));
        setResizable(false);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IRipperProgressDialog#addCancelAction(java.awt.event.ActionListener)
	 */
    @Override
	public void addCancelAction(ActionListener action) {
        cancelButton.addActionListener(action);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IRipperProgressDialog#setArtistAndAlbum(java.lang.String, java.lang.String)
	 */
    @Override
	public void setArtistAndAlbum(String artist, String album) {
        setTitle(StringUtils.getString(I18nUtils.getString("RIPPING_CD"), " ", artist, " - ", album));
    }

    /**
     * Sets the content.
     */
    public void initialize() {
        JPanel panel = new JPanel(new GridBagLayout());

        cover = new JLabel(albumBigIcon.getIcon(lookAndFeelManager.getCurrentLookAndFeel().getPaintForSpecialControls()));

        JLabel totalProgressLabel = new JLabel(I18nUtils.getString("TOTAL_PROGRESS"));
        totalProgressBar = new JProgressBar();
        totalProgressBar.setPreferredSize(new Dimension(10, 12));
        totalProgressValueLabel = new JLabel();
        JLabel decodeProgressLabel = new JLabel(I18nUtils.getString("DECODING"));
        decodeProgressBar = new JProgressBar();
        decodeProgressBar.setPreferredSize(new Dimension(10, 12));
        decodeProgressValueLabel = new JLabel();
        JLabel encodeProgressLabel = new JLabel(I18nUtils.getString("ENCODING"));
        encodeProgressBar = new JProgressBar();
        encodeProgressBar.setPreferredSize(new Dimension(10, 12));
        encodeProgressLabel.setBorder(BorderFactory.createEmptyBorder());
        encodeProgressValueLabel = new JLabel();
        cancelButton = new JButton(I18nUtils.getString("CANCEL"));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        setDecodeProgressBarLimits(0, 100);
        setEncodeProgressBarLimits(0, 100);

        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 6;
        c.insets = new Insets(10, 20, 0, 20);
        panel.add(cover, c);

        c.gridx = 1;
        c.weightx = 1;
        c.gridy = 0;
        c.gridheight = 1;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(20, 0, 0, 20);
        panel.add(totalProgressLabel, c);
        c.gridx = 2;
        c.weightx = 0;
        c.anchor = GridBagConstraints.EAST;
        panel.add(totalProgressValueLabel, c);
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 0, 20);
        panel.add(totalProgressBar, c);

        c.gridy = 2;
        c.gridwidth = 1;
        c.weightx = 0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(3, 0, 0, 20);
        panel.add(decodeProgressLabel, c);
        c.gridx = 2;
        c.weightx = 0;
        c.anchor = GridBagConstraints.EAST;
        panel.add(decodeProgressValueLabel, c);
        c.gridx = 1;
        c.gridy = 3;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 0, 20);
        panel.add(decodeProgressBar, c);

        c.gridy = 4;
        c.gridwidth = 1;
        c.weightx = 0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(3, 0, 0, 20);
        panel.add(encodeProgressLabel, c);
        c.gridx = 2;
        c.weightx = 0;
        c.anchor = GridBagConstraints.EAST;
        panel.add(encodeProgressValueLabel, c);
        c.gridx = 1;
        c.gridy = 5;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 0, 20);
        panel.add(encodeProgressBar, c);

        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 3;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(10, 0, 5, 0);
        panel.add(cancelButton, c);

        add(panel);
    }

    @Override
	public void setCover(ImageIcon img) {
        cover.setIcon(ImageUtils.resize(img, 90, 90));
    }

    /**
     * Sets the decode progress bar limits.
     * 
     * @param min
     *            the min
     * @param max
     *            the max
     */
    private void setDecodeProgressBarLimits(int min, int max) {
        setLimits(decodeProgressBar, min, max);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IRipperProgressDialog#setDecodeProgressValue(int)
	 */
    @Override
	public void setDecodeProgressValue(int value) {
        if (value < 0) {
            decodeProgressBar.setIndeterminate(true);
        } else {
            decodeProgressBar.setIndeterminate(false);
            decodeProgressBar.setValue(value);
        }

    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IRipperProgressDialog#setDecodeProgressValue(java.lang.String)
	 */
    @Override
	public void setDecodeProgressValue(String value) {
        decodeProgressValueLabel.setText(value);
    }

    /**
     * Sets the encode progress bar limits.
     * 
     * @param min
     *            the min
     * @param max
     *            the max
     */
    private void setEncodeProgressBarLimits(int min, int max) {
        setLimits(encodeProgressBar, min, max);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IRipperProgressDialog#setEncodeProgressValue(int)
	 */
    @Override
	public void setEncodeProgressValue(int value) {
        if (value < 0) {
            encodeProgressBar.setIndeterminate(true);
        } else {
            encodeProgressBar.setIndeterminate(false);
            encodeProgressBar.setValue(value);
        }
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IRipperProgressDialog#setEncodeProgressValue(java.lang.String)
	 */
    @Override
	public void setEncodeProgressValue(String value) {
        encodeProgressValueLabel.setText(value);
    }

    /**
     * Sets the limits.
     * 
     * @param progressBar
     *            the progress bar
     * @param min
     *            the min
     * @param max
     *            the max
     */
    private void setLimits(JProgressBar progressBar, int min, int max) {
        progressBar.setMinimum(min);
        progressBar.setMaximum(max);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IRipperProgressDialog#setTotalProgressBarLimits(int, int)
	 */
    @Override
	public void setTotalProgressBarLimits(int min, int max) {
        setLimits(totalProgressBar, min, max);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IRipperProgressDialog#setTotalProgressValue(int)
	 */
    @Override
	public void setTotalProgressValue(int value) {
        totalProgressBar.setValue(value);
        totalProgressValueLabel.setText(StringUtils.getString(value, " / ", totalProgressBar.getMaximum()));
    }
    
    @Override
    public void showDialog() {
    	setVisible(true);
    }
    
    @Override
    public void hideDialog() {
    	setVisible(false);
    	dispose();
    }

}
