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

package net.sourceforge.atunes.gui.views.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomModalFrame;
import net.sourceforge.atunes.gui.views.controls.CustomTextArea;
import net.sourceforge.atunes.gui.views.controls.CustomTextField;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The dialog for tag editing
 */
public final class EditTagDialog extends AbstractCustomModalFrame {

    private static final long serialVersionUID = 3395292301087643037L;

    private JCheckBox titleCheckBox;
    private JTextField titleTextField;
    private JCheckBox albumCheckBox;
    private JComboBox albumTextField;
    private JCheckBox artistCheckBox;
    private JComboBox artistTextField;
    private JCheckBox yearCheckBox;
    private JTextField yearTextField;
    private JCheckBox genreCheckBox;
    private JComboBox genreComboBox;
    private JCheckBox commentCheckBox;
    private JTextArea commentTextArea;
    private JCheckBox trackNumberCheckBox;
    private JTextField trackNumberTextField;
    private JCheckBox discNumberCheckBox;
    private JTextField discNumberTextField;
    private JCheckBox coverCheckBox;
    private JLabel cover;
    private JButton coverButton;
    private JButton removeCoverButton;
    private JCheckBox lyricsCheckBox;
    private JTextArea lyricsTextArea;
    private JCheckBox composerCheckBox;
    private JTextField composerTextField;
    private JCheckBox albumArtistCheckBox;
    private JTextField albumArtistTextField;
    private JButton okButton;
    private JButton cancelButton;
    private JButton nextButton;
    private JButton prevButton;
    private JTabbedPane tabbedPane = new JTabbedPane();

    /**
     * Instantiates a new edits the tag dialog.
     * 
     * @param owner
     *            the owner
     * @param arePrevNextButtonsShown
     */
    public EditTagDialog(JFrame owner, boolean arePrevNextButtonsShown) {
        super(owner, 500, 600);
        setTitle(I18nUtils.getString("EDIT_TAG"));
        setResizable(true);

        setLayout(new BorderLayout());

        add(tabbedPane, BorderLayout.CENTER);
        tabbedPane.addTab(I18nUtils.getString("TAGS"), getTagEditTab());
        tabbedPane.addTab(I18nUtils.getString("COVER"), getCoverTab());

        add(getOKAndCancelButtonPanel(arePrevNextButtonsShown), BorderLayout.SOUTH);

        GuiUtils.applyComponentOrientation(this);
        enableCloseActionWithEscapeKey();
    }

    private JPanel getOKAndCancelButtonPanel(boolean arePrevNextButtonsShown) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 10, 2, 10);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.weighty = 0;
        panel.add(okButton, c);
        c.gridx = 0;
        c.gridy = 1;
        panel.add(cancelButton, c);
        if (arePrevNextButtonsShown) {
            panel.add(prevButton, c);
            panel.add(nextButton, c);
        }
        return panel;
    }

    public static void main(String[] args) {
        new EditTagDialog(null, true).setVisible(true);
    }

    /**
     * Gets the album artist text field.
     * 
     * @return the album artist text field
     */
    public JTextField getAlbumArtistTextField() {
        return albumArtistTextField;
    }

    /**
     * Gets the album text field.
     * 
     * @return the album text field
     */
    public JComboBox getAlbumTextField() {
        return albumTextField;
    }

    /**
     * Gets the artist text field.
     * 
     * @return the artist text field
     */
    public JComboBox getArtistTextField() {
        return artistTextField;
    }

    /**
     * Gets the cancel button.
     * 
     * @return the cancel button
     */
    public JButton getCancelButton() {
        return cancelButton;
    }

    /**
     * Gets the comment text area.
     * 
     * @return the comment text area
     */
    public JTextArea getCommentTextArea() {
        return commentTextArea;
    }

    /**
     * Gets the composer text field.
     * 
     * @return the composer text field
     */
    public JTextField getComposerTextField() {
        return composerTextField;
    }

    /**
     * Gets the cover tab
     * 
     * @return cover tab
     */
    private JPanel getCoverTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel coverPanel = new JPanel();
        FlowLayout fl = new FlowLayout();
        fl.setAlignment(FlowLayout.CENTER);
        coverPanel.setLayout(fl);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(fl);

        coverCheckBox = new JCheckBox();
        infoPanel.add(coverCheckBox);
        genreComboBox.setEditable(true);
        cover = new JLabel();
        cover.setPreferredSize(new Dimension(Constants.DIALOG_LARGE_IMAGE_WIDTH, Constants.DIALOG_LARGE_IMAGE_HEIGHT));
        cover.setMinimumSize(new Dimension(Constants.DIALOG_LARGE_IMAGE_WIDTH, Constants.DIALOG_LARGE_IMAGE_HEIGHT));
        cover.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        coverButton = new JButton(I18nUtils.getString("EDIT_COVER"));
        removeCoverButton = new JButton(I18nUtils.getString("REMOVE_COVER"));
        JPanel coverButtonsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        coverButtonsPanel.add(coverButton);
        coverButtonsPanel.add(removeCoverButton);
        coverCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setCoverSelected(coverCheckBox.isSelected());
            }

        });
        panel.add(infoPanel, BorderLayout.NORTH);
        coverPanel.add(cover);
        panel.add(coverPanel, BorderLayout.CENTER);
        panel.add(coverButtonsPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Gets the content.
     * 
     * @return the content
     */
    private JPanel getTagEditTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        titleCheckBox = new JCheckBox();
        titleCheckBox.setFocusable(false);
        JLabel titleLabel = new JLabel(I18nUtils.getString("TITLE"));
        titleTextField = new CustomTextField();
        titleCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setTitleSelected(titleCheckBox.isSelected());
            }
        });
        titleCheckBox.setSelected(true);
        albumCheckBox = new JCheckBox();
        JLabel albumLabel = new JLabel(I18nUtils.getString("ALBUM"));
        albumTextField = new JComboBox();
        albumTextField.setEditable(true);
        albumCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAlbumSelected(albumCheckBox.isSelected());
            }
        });
        albumCheckBox.setSelected(true);
        artistCheckBox = new JCheckBox();
        JLabel artistLabel = new JLabel(I18nUtils.getString("ARTIST"));
        artistTextField = new JComboBox();
        artistTextField.setEditable(true);
        artistCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setArtistSelected(artistCheckBox.isSelected());
            }
        });
        artistCheckBox.setSelected(true);
        yearCheckBox = new JCheckBox();
        JLabel yearLabel = new JLabel(I18nUtils.getString("YEAR"));
        yearTextField = new CustomTextField();
        yearCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setYearSelected(yearCheckBox.isSelected());
            }
        });
        yearCheckBox.setSelected(true);
        genreCheckBox = new JCheckBox();
        JLabel genreLabel = new JLabel(I18nUtils.getString("GENRE"));
        genreComboBox = new JComboBox();
        genreCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setGenreSelected(genreCheckBox.isSelected());
            }
        });
        genreCheckBox.setSelected(true);

        commentCheckBox = new JCheckBox();
        JLabel commentLabel = new JLabel(I18nUtils.getString("COMMENT"));
        commentTextArea = new CustomTextArea();
        commentTextArea.setBorder(BorderFactory.createEmptyBorder());
        commentCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setCommentSelected(commentCheckBox.isSelected());
            }
        });
        commentCheckBox.setSelected(true);
        JScrollPane scrollPane = LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getScrollPane(commentTextArea);
        lyricsCheckBox = new JCheckBox();
        JLabel lyricsLabel = new JLabel(I18nUtils.getString("LYRICS"));
        lyricsTextArea = new CustomTextArea();
        lyricsTextArea.setBorder(BorderFactory.createEmptyBorder());
        lyricsCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setLyricsSelected(lyricsCheckBox.isSelected());
            }
        });
        lyricsCheckBox.setSelected(true);
        JScrollPane scrollPane2 = LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getScrollPane(lyricsTextArea);
        trackNumberCheckBox = new JCheckBox();
        JLabel trackNumberLabel = new JLabel(I18nUtils.getString("TRACK"));
        trackNumberTextField = new CustomTextField();
        trackNumberCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setTrackNumberSelected(trackNumberCheckBox.isSelected());

            }
        });
        trackNumberCheckBox.setSelected(true);
        discNumberCheckBox = new JCheckBox();
        JLabel discNumberLabel = new JLabel(I18nUtils.getString("DISC_NUMBER"));
        discNumberTextField = new CustomTextField();
        discNumberCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDiscNumberSelected(discNumberCheckBox.isSelected());

            }
        });
        discNumberCheckBox.setSelected(true);
        composerCheckBox = new JCheckBox();
        JLabel composerLabel = new JLabel(I18nUtils.getString("COMPOSER"));
        composerTextField = new CustomTextField();
        composerCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setComposerSelected(composerCheckBox.isSelected());
            }
        });
        composerCheckBox.setSelected(true);
        albumArtistCheckBox = new JCheckBox();
        JLabel albumArtistLabel = new JLabel(I18nUtils.getString("ALBUM_ARTIST"));
        albumArtistTextField = new CustomTextField();
        albumArtistCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAlbumArtistSelected(albumArtistCheckBox.isSelected());
            }
        });
        albumArtistCheckBox.setSelected(true);

        okButton = new JButton(I18nUtils.getString("OK"));
        cancelButton = new JButton(I18nUtils.getString("CANCEL"));
        nextButton = new JButton(I18nUtils.getString("NEXT"));
        prevButton = new JButton(I18nUtils.getString("PREVIOUS"));

        GridBagConstraints c = new GridBagConstraints();

        //

        c.insets = new Insets(10, 10, 2, 2);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LINE_START;
        c.weightx = 0;
        panel.add(titleCheckBox, c);

        c.insets = new Insets(10, 2, 2, 10);
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LINE_START;
        c.weightx = 0;
        panel.add(titleLabel, c);

        c.insets = new Insets(10, 10, 2, 10);
        c.gridx = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridwidth = 2;
        panel.add(titleTextField, c);

        //

        c.insets = new Insets(2, 10, 2, 2);
        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridwidth = 1;
        panel.add(albumArtistCheckBox, c);

        c.insets = new Insets(2, 2, 2, 10);
        c.gridx = 1;
        c.gridy = 1;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridwidth = 1;
        panel.add(albumArtistLabel, c);

        c.insets = new Insets(2, 10, 2, 10);
        c.gridx = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridwidth = 2;
        panel.add(albumArtistTextField, c);

        //

        c.insets = new Insets(2, 10, 2, 2);
        c.gridx = 0;
        c.gridy = 2;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridwidth = 1;
        panel.add(artistCheckBox, c);

        c.insets = new Insets(2, 2, 2, 10);
        c.gridx = 1;
        c.gridy = 2;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridwidth = 1;
        panel.add(artistLabel, c);

        c.insets = new Insets(2, 10, 2, 10);
        c.gridx = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridwidth = 2;
        panel.add(artistTextField, c);

        //

        c.insets = new Insets(2, 10, 2, 2);
        c.gridx = 0;
        c.gridy = 3;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        panel.add(albumCheckBox, c);

        c.insets = new Insets(2, 2, 2, 10);
        c.gridx = 1;
        c.gridy = 3;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridheight = 1;
        c.gridwidth = 1;
        panel.add(albumLabel, c);

        c.insets = new Insets(2, 10, 2, 10);
        c.gridx = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridwidth = 2;
        panel.add(albumTextField, c);

        //

        c.insets = new Insets(2, 10, 2, 2);
        c.gridx = 0;
        c.gridy = 4;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridwidth = 1;
        panel.add(yearCheckBox, c);

        c.insets = new Insets(2, 2, 2, 10);
        c.gridx = 1;
        c.gridy = 4;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridwidth = 1;
        panel.add(yearLabel, c);

        c.insets = new Insets(2, 10, 2, 10);
        c.gridx = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridwidth = 2;
        panel.add(yearTextField, c);

        //

        c.insets = new Insets(2, 10, 2, 2);
        c.gridx = 0;
        c.gridy = 5;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridwidth = 1;
        panel.add(trackNumberCheckBox, c);

        c.insets = new Insets(2, 2, 2, 10);
        c.gridx = 1;
        c.gridy = 5;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridwidth = 1;
        panel.add(trackNumberLabel, c);

        c.insets = new Insets(2, 10, 2, 10);
        c.gridx = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridwidth = 2;
        panel.add(trackNumberTextField, c);

        //

        c.insets = new Insets(2, 10, 2, 2);
        c.gridx = 0;
        c.gridy = 6;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridwidth = 1;
        panel.add(discNumberCheckBox, c);

        c.insets = new Insets(2, 2, 2, 10);
        c.gridx = 1;
        c.gridy = 6;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridwidth = 1;
        panel.add(discNumberLabel, c);

        c.insets = new Insets(2, 10, 2, 10);
        c.gridx = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridwidth = 2;
        panel.add(discNumberTextField, c);

        //

        c.insets = new Insets(2, 10, 2, 2);
        c.gridx = 0;
        c.gridy = 7;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridwidth = 1;
        panel.add(genreCheckBox, c);

        c.insets = new Insets(2, 2, 2, 10);
        c.gridx = 1;
        c.gridy = 7;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridwidth = 1;
        panel.add(genreLabel, c);

        c.insets = new Insets(2, 10, 2, 10);
        c.gridx = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridwidth = 2;
        panel.add(genreComboBox, c);

        //

        c.insets = new Insets(2, 10, 2, 2);
        c.gridx = 0;
        c.gridy = 8;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridwidth = 1;
        panel.add(composerCheckBox, c);

        c.insets = new Insets(2, 2, 2, 10);
        c.gridx = 1;
        c.gridy = 8;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridwidth = 1;
        panel.add(composerLabel, c);

        c.insets = new Insets(2, 10, 2, 10);
        c.gridx = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridwidth = 2;
        panel.add(composerTextField, c);

        //

        c.insets = new Insets(2, 10, 2, 2);
        c.gridx = 0;
        c.gridy = 9;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridwidth = 1;
        panel.add(commentCheckBox, c);

        c.insets = new Insets(2, 2, 2, 10);
        c.gridx = 1;
        c.gridy = 9;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridwidth = 1;
        panel.add(commentLabel, c);

        c.insets = new Insets(2, 10, 2, 10);
        c.gridx = 2;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 2;
        panel.add(scrollPane, c);

        //

        c.insets = new Insets(2, 10, 2, 2);
        c.gridx = 0;
        c.gridy = 10;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridwidth = 1;
        panel.add(lyricsCheckBox, c);

        c.insets = new Insets(2, 2, 2, 10);
        c.gridx = 1;
        c.gridy = 10;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridwidth = 1;
        panel.add(lyricsLabel, c);

        c.insets = new Insets(2, 10, 2, 10);
        c.gridx = 2;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 2;
        panel.add(scrollPane2, c);

        return panel;
    }

    /**
     * Gets the genre combo box.
     * 
     * @return the genre combo box
     */
    public JComboBox getGenreComboBox() {
        return genreComboBox;
    }

    /**
     * Gets the lyrics text area.
     * 
     * @return the lyrics text area
     */
    public JTextArea getLyricsTextArea() {
        return lyricsTextArea;
    }

    /**
     * Gets the ok button.
     * 
     * @return the ok button
     */
    public JButton getOkButton() {
        return okButton;
    }

    /**
     * Gets the previous (song) button.
     * 
     * @return the previous (song) button
     */
    public JButton getPrevButton() {
        return prevButton;
    }

    /**
     * Gets the next (song) button.
     * 
     * @return the next (song) button
     */
    public JButton getNextButton() {
        return nextButton;
    }

    /**
     * Gets the title text field.
     * 
     * @return the title text field
     */
    public JTextField getTitleTextField() {
        return titleTextField;
    }

    /**
     * Gets the track number text field.
     * 
     * @return the track number text field
     */
    public JTextField getTrackNumberTextField() {
        return trackNumberTextField;
    }

    /**
     * Gets the year text field.
     * 
     * @return the year text field
     */
    public JTextField getYearTextField() {
        return yearTextField;
    }

    public JLabel getCover() {
        return cover;
    }

    public JButton getCoverButton() {
        return coverButton;
    }

    public JButton getRemoveCoverButton() {
        return removeCoverButton;
    }

    /**
     * @return the titleCheckBox
     */
    public JCheckBox getTitleCheckBox() {
        return titleCheckBox;
    }

    /**
     * @return the albumCheckBox
     */
    public JCheckBox getAlbumCheckBox() {
        return albumCheckBox;
    }

    /**
     * @return the artistCheckBox
     */
    public JCheckBox getArtistCheckBox() {
        return artistCheckBox;
    }

    /**
     * @return the yearCheckBox
     */
    public JCheckBox getYearCheckBox() {
        return yearCheckBox;
    }

    /**
     * @return the genreCheckBox
     */
    public JCheckBox getGenreCheckBox() {
        return genreCheckBox;
    }

    /**
     * @return the commentCheckBox
     */
    public JCheckBox getCommentCheckBox() {
        return commentCheckBox;
    }

    /**
     * @return the trackNumberCheckBox
     */
    public JCheckBox getTrackNumberCheckBox() {
        return trackNumberCheckBox;
    }

    /**
     * @return the coverCheckBox
     */
    public JCheckBox getCoverCheckBox() {
        return coverCheckBox;
    }

    /**
     * @return the lyricsCheckBox
     */
    public JCheckBox getLyricsCheckBox() {
        return lyricsCheckBox;
    }

    /**
     * @return the composerCheckBox
     */
    public JCheckBox getComposerCheckBox() {
        return composerCheckBox;
    }

    /**
     * @return the albumArtistCheckBox
     */
    public JCheckBox getAlbumArtistCheckBox() {
        return albumArtistCheckBox;
    }

    public void setTitleSelected(boolean b) {
        titleCheckBox.setSelected(b);
        titleTextField.setEnabled(b);
    }

    public void setAlbumArtistSelected(boolean b) {
        albumArtistCheckBox.setSelected(b);
        albumArtistTextField.setEnabled(b);
    }

    public void setArtistSelected(boolean b) {
        artistCheckBox.setSelected(b);
        artistTextField.setEnabled(b);
    }

    public void setYearSelected(boolean b) {
        yearCheckBox.setSelected(b);
        yearTextField.setEnabled(b);
    }

    public void setGenreSelected(boolean b) {
        genreCheckBox.setSelected(b);
        genreComboBox.setEnabled(b);
    }

    public void setTrackNumberSelected(boolean b) {
        trackNumberCheckBox.setSelected(b);
        trackNumberTextField.setEnabled(b);
    }

    public void setDiscNumberSelected(boolean b) {
        discNumberCheckBox.setSelected(b);
        discNumberTextField.setEnabled(b);
    }

    public void setCommentSelected(boolean b) {
        commentCheckBox.setSelected(b);
        commentTextArea.setEnabled(b);
    }

    public void setCoverSelected(boolean b) {
        coverCheckBox.setSelected(b);
        coverButton.setEnabled(b);
        removeCoverButton.setEnabled(b);
    }

    public void setLyricsSelected(boolean b) {
        lyricsCheckBox.setSelected(b);
        lyricsTextArea.setEnabled(b);
    }

    public void setComposerSelected(boolean b) {
        composerCheckBox.setSelected(b);
        composerTextField.setEnabled(b);
    }

    public void setAlbumSelected(boolean b) {
        albumCheckBox.setSelected(b);
        albumTextField.setEnabled(b);
    }

    public JCheckBox getDiscNumberCheckBox() {
        return discNumberCheckBox;
    }

    public JTextField getDiscNumberTextField() {
        return discNumberTextField;
    }

}
