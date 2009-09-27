/*
 * aTunes 2.0.0
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
package net.sourceforge.atunes.kernel.controllers.editTagDialog;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.autocomplete.AutoCompleteDecorator;
import net.sourceforge.atunes.gui.views.dialogs.EditTagDialog;
import net.sourceforge.atunes.kernel.controllers.model.DialogController;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.model.Album;
import net.sourceforge.atunes.kernel.modules.repository.model.Artist;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.EditTagInfo;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.Tag;
import net.sourceforge.atunes.kernel.modules.repository.tags.writer.EditTagsProcess;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.misc.SystemProperties.OperatingSystem;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.AudioFilePictureUtils;

import org.jdesktop.swingx.combobox.ListComboBoxModel;

/**
 * The Class EditTagDialogController.
 */
public class EditTagDialogController extends DialogController<EditTagDialog> {

    Logger logger = new Logger();

    /** The audio files editing. */
    List<AudioFile> audioFilesEditing;
    private byte[] newCover;
    private boolean coverEdited;

    /**
     * Instantiates a new edits the tag dialog controller.
     * 
     * @param dialog
     *            the dialog
     */
    public EditTagDialogController(EditTagDialog dialog) {
        super(dialog);
        addBindings();
        addStateBindings();
    }

    @Override
    protected void addBindings() {
        // Add genres combo box items
        List<String> genresSorted = Arrays.asList(Tag.genres);
        Collections.sort(genresSorted);
        getDialogControlled().getGenreComboBox().setModel(new ListComboBoxModel<String>(genresSorted));
        // Add autocompletion
        AutoCompleteDecorator.decorate(getDialogControlled().getGenreComboBox());

        EditTagDialogActionListener actionListener = new EditTagDialogActionListener(this, getDialogControlled());
        getDialogControlled().getOkButton().addActionListener(actionListener);
        getDialogControlled().getCancelButton().addActionListener(actionListener);

        getDialogControlled().getCoverButton().addActionListener(actionListener);
        getDialogControlled().getRemoveCoverButton().addActionListener(actionListener);
    }

    @Override
    protected void addStateBindings() {
        // Nothing to do
    }

    /**
     * Edits the files.
     * 
     * @param audioFiles
     *            the files
     */
    public void editFiles(final List<AudioFile> audioFiles) {
        if (audioFiles == null || audioFiles.isEmpty()) {
            return;
        }

        audioFilesEditing = audioFiles;

        getDialogControlled().getCover().setIcon(null);
        getDialogControlled().getCoverButton().setEnabled(false);
        getDialogControlled().getRemoveCoverButton().setEnabled(false);
        newCover = null;
        coverEdited = false;

        // Load artists into combo box
        List<Artist> artistList = RepositoryHandler.getInstance().getArtists();
        List<String> artistNames = new ArrayList<String>();
        for (Artist a : artistList) {
            artistNames.add(a.getName());
        }
        getDialogControlled().getArtistTextField().setModel(new ListComboBoxModel<String>(artistNames));
        // Activate autocompletion of artists
        AutoCompleteDecorator.decorate(getDialogControlled().getArtistTextField());

        // Load albums into combo box
        List<Album> albumList = RepositoryHandler.getInstance().getAlbums();
        List<String> albumNames = new ArrayList<String>();
        for (Album alb : albumList) {
            // Because of artists and album artists there can be more than one album with the same name
            if (!albumNames.contains(alb.getName())) {
                albumNames.add(alb.getName());
            }
        }
        getDialogControlled().getAlbumTextField().setModel(new ListComboBoxModel<String>(albumNames));
        // Active autocompletion of albums
        AutoCompleteDecorator.decorate(getDialogControlled().getAlbumTextField());

        getDialogControlled().setTitleSelected(false);
        getDialogControlled().setCoverSelected(false);
        getDialogControlled().setAlbumArtistSelected(false);
        getDialogControlled().setArtistSelected(false);
        getDialogControlled().setTrackNumberSelected(false);
        getDialogControlled().setDiscNumberSelected(false);
        getDialogControlled().setYearSelected(false);
        getDialogControlled().setGenreSelected(false);
        getDialogControlled().setCommentSelected(false);
        getDialogControlled().setLyricsSelected(false);
        getDialogControlled().setAlbumSelected(false);
        getDialogControlled().setComposerSelected(false);

        // Check if at least one audio file supports internal pictures
        boolean supportsInternalPicture = false;
        for (AudioFile af : audioFilesEditing) {
            if (af.supportsInternalPicture()) {
                supportsInternalPicture = true;
                break;
            }
        }

        if (audioFiles.size() == 1) {
            getDialogControlled().getTitleCheckBox().setEnabled(false);
            getDialogControlled().getAlbumArtistCheckBox().setEnabled(false);
            getDialogControlled().getArtistCheckBox().setEnabled(false);
            getDialogControlled().getTrackNumberCheckBox().setEnabled(false);
            getDialogControlled().getYearCheckBox().setEnabled(false);
            getDialogControlled().getDiscNumberCheckBox().setEnabled(false);
            getDialogControlled().getGenreCheckBox().setEnabled(false);
            getDialogControlled().getCommentCheckBox().setEnabled(false);
            getDialogControlled().getLyricsCheckBox().setEnabled(false);
            getDialogControlled().getAlbumCheckBox().setEnabled(false);
            getDialogControlled().getComposerCheckBox().setEnabled(false);
            getDialogControlled().getCoverCheckBox().setEnabled(false);
            getDialogControlled().getCoverCheckBox().setEnabled(false);

            if (supportsInternalPicture) {
                getEditTagDialog().getOkButton().setEnabled(false);
                getDialogControlled().getCoverCheckBox().setSelected(true);

                new SwingWorker<ImageIcon, Void>() {
                    @Override
                    protected ImageIcon doInBackground() throws Exception {
                        return AudioFilePictureUtils.getInsidePicture(audioFiles.get(0), Constants.DIALOG_LARGE_IMAGE_WIDTH, Constants.DIALOG_LARGE_IMAGE_HEIGHT);
                    }

                    @Override
                    protected void done() {
                        try {
                            // Check if it's the right dialog
                            if (audioFilesEditing.equals(audioFiles)) {
                                ImageIcon cover = get();
                                getEditTagDialog().getCover().setIcon(cover);
                                getEditTagDialog().getCoverButton().setEnabled(true);
                                getEditTagDialog().getRemoveCoverButton().setEnabled(true);
                                getEditTagDialog().getOkButton().setEnabled(true);
                            }
                        } catch (InterruptedException e) {
                            logger.error(LogCategories.IMAGE, e);
                        } catch (ExecutionException e) {
                            logger.error(LogCategories.IMAGE, e);
                        }
                    }
                }.execute();
            }

        } else {
            getDialogControlled().getTitleCheckBox().setEnabled(true);
            getDialogControlled().getAlbumArtistCheckBox().setEnabled(true);
            getDialogControlled().getArtistCheckBox().setEnabled(true);
            getDialogControlled().getTrackNumberCheckBox().setEnabled(true);
            getDialogControlled().getYearCheckBox().setEnabled(true);
            getDialogControlled().getDiscNumberCheckBox().setEnabled(true);
            getDialogControlled().getGenreCheckBox().setEnabled(true);
            getDialogControlled().getCommentCheckBox().setEnabled(true);
            getDialogControlled().getLyricsCheckBox().setEnabled(true);
            getDialogControlled().getAlbumCheckBox().setEnabled(true);
            getDialogControlled().getComposerCheckBox().setEnabled(true);
            getDialogControlled().getCoverCheckBox().setEnabled(true);
            getDialogControlled().getCoverCheckBox().setEnabled(supportsInternalPicture);
        }

        Set<String> titles = new HashSet<String>();
        Set<Integer> trackNumbers = new HashSet<Integer>();
        Set<Integer> discNumbers = new HashSet<Integer>();
        Set<String> artists = new HashSet<String>();
        Set<String> albums = new HashSet<String>();
        Set<Integer> years = new HashSet<Integer>();
        Set<String> comments = new HashSet<String>();
        Set<String> genres = new HashSet<String>();
        Set<String> composers = new HashSet<String>();
        Set<String> lyrics = new HashSet<String>();
        Set<String> albumArtists = new HashSet<String>();
        for (AudioFile audioFile : audioFiles) {
            Tag tag = audioFile.getTag();
            if (tag != null) {
                titles.add(tag.getTitle());
                trackNumbers.add(tag.getTrackNumber());
                discNumbers.add(tag.getDiscNumber());
                artists.add(tag.getArtist());
                albums.add(tag.getAlbum());
                years.add(tag.getYear());
                comments.add(tag.getComment());
                genres.add(tag.getGenre());
                composers.add(tag.getComposer());
                lyrics.add(tag.getLyrics());
                albumArtists.add(tag.getAlbumArtist());
            }
        }

        if (titles.size() == 1 && !titles.contains("")) {
            getDialogControlled().getTitleTextField().setText(titles.iterator().next());
            getDialogControlled().setTitleSelected(true);
        } else {
            getDialogControlled().getTitleTextField().setText("");
            getDialogControlled().setTitleSelected(audioFiles.size() == 1);
        }

        if (trackNumbers.size() == 1 && !trackNumbers.contains(0)) {
            getDialogControlled().getTrackNumberTextField().setText(trackNumbers.iterator().next().toString());
            getDialogControlled().setTrackNumberSelected(true);
        } else {
            getDialogControlled().getTrackNumberTextField().setText("");
            getDialogControlled().setTrackNumberSelected(audioFiles.size() == 1);
        }

        if (discNumbers.size() == 1 && !discNumbers.contains(0)) {
            getDialogControlled().getDiscNumberTextField().setText(discNumbers.iterator().next().toString());
            getDialogControlled().setDiscNumberSelected(true);
        } else {
            getDialogControlled().getDiscNumberTextField().setText("");
            getDialogControlled().setDiscNumberSelected(audioFiles.size() == 1);
        }

        if (artists.size() == 1 && !artists.contains("")) {
            getDialogControlled().getArtistTextField().getEditor().setItem(artists.iterator().next());
            getDialogControlled().setArtistSelected(true);
        } else {
            getDialogControlled().getArtistTextField().getEditor().setItem("");
            getDialogControlled().setArtistSelected(audioFiles.size() == 1);
        }

        if (albums.size() == 1 && !albums.contains("")) {
            getDialogControlled().getAlbumTextField().getEditor().setItem(albums.iterator().next());
            getDialogControlled().setAlbumSelected(true);
        } else {
            getDialogControlled().getAlbumTextField().getEditor().setItem("");
            getDialogControlled().setAlbumSelected(audioFiles.size() == 1);
        }

        if (years.size() == 1 && !years.contains(0)) {
            getDialogControlled().getYearTextField().setText(String.valueOf(years.iterator().next()));
            getDialogControlled().setYearSelected(true);
        } else {
            getDialogControlled().getYearTextField().setText("");
            getDialogControlled().setYearSelected(audioFiles.size() == 1);
        }

        if (comments.size() == 1 && !comments.contains("")) {
            getDialogControlled().getCommentTextArea().setText(comments.iterator().next());
            getDialogControlled().getCommentTextArea().setCaretPosition(0);
            getDialogControlled().setCommentSelected(true);
        } else {
            getDialogControlled().getCommentTextArea().setText("");
            getDialogControlled().setCommentSelected(audioFiles.size() == 1);
        }

        if (genres.size() == 1 && !genres.contains("")) {
            getDialogControlled().getGenreComboBox().getEditor().setItem(genres.iterator().next());
            getDialogControlled().setGenreSelected(true);
        } else {
            getDialogControlled().getGenreComboBox().getEditor().setItem("");
            getDialogControlled().setGenreSelected(audioFiles.size() == 1);
        }

        if (lyrics.size() == 1 && !lyrics.contains("")) {
            getDialogControlled().getLyricsTextArea().setText(lyrics.iterator().next());
            getDialogControlled().getLyricsTextArea().setCaretPosition(0);
            getDialogControlled().setLyricsSelected(true);
        } else {
            getDialogControlled().getLyricsTextArea().setText("");
            getDialogControlled().setLyricsSelected(audioFiles.size() == 1);
        }

        if (composers.size() == 1 && !composers.contains("")) {
            getDialogControlled().getComposerTextField().setText(composers.iterator().next());
            getDialogControlled().setComposerSelected(true);
        } else {
            getDialogControlled().getComposerTextField().setText("");
            getDialogControlled().setComposerSelected(audioFiles.size() == 1);
        }

        if (albumArtists.size() == 1 && !albumArtists.contains("")) {
            getDialogControlled().getAlbumArtistTextField().setText(albumArtists.iterator().next());
            getDialogControlled().setAlbumArtistSelected(true);
        } else {
            getDialogControlled().getAlbumArtistTextField().setText("");
            getDialogControlled().setAlbumArtistSelected(audioFiles.size() == 1);
        }

        // If there is only one file add a help to complete title from file name
        if (audioFiles.size() == 1) {
            final String fileName = audioFiles.get(0).getNameWithoutExtension();
            final JTextField textField = getDialog().getTitleTextField();
            textField.addKeyListener(new KeyAdapter() {

                int lenght = 0;

                @Override
                public void keyTyped(KeyEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            String text = textField.getText();

                            // User added a char
                            if (text.length() > lenght) {
                                if (text.length() >= 3) {
                                    int index = fileName.indexOf(text);
                                    if (index != -1) {
                                        textField.setText(fileName.substring(index));
                                        textField.setSelectionStart(text.length());
                                        textField.setSelectionEnd(textField.getText().length());
                                    }
                                }
                            }
                            lenght = text.length();
                        }
                    });
                }
            });
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // If title is enabled set focus
                if (getDialog().getTitleTextField().isEnabled()) {
                    getDialog().getTitleTextField().setCaretPosition(0);
                    getDialog().getTitleTextField().requestFocus();
                }
            }
        });

        getDialogControlled().setVisible(true);

    }

    EditTagDialog getDialog() {
        return getDialogControlled();
    }

    /**
     * Returns edit tag dialog. Used for inner classes without increasing
     * visibility of getDialogControlled
     * 
     * @return
     */
    EditTagDialog getEditTagDialog() {
        return getDialogControlled();
    }

    /**
     * Edits the tag.
     */
    protected void editTag() {
        getLogger().debug(LogCategories.CONTROLLER);

        getDialogControlled().setVisible(false);

        // Build editor props
        EditTagInfo editTagInfo = new EditTagInfo();

        if (!getDialogControlled().getTitleCheckBox().isEnabled() || getDialogControlled().getTitleCheckBox().isSelected()) {
            editTagInfo.put("TITLE", getDialogControlled().getTitleTextField().getText());
        }
        if (!getDialogControlled().getArtistCheckBox().isEnabled() || getDialogControlled().getArtistCheckBox().isSelected()) {
            editTagInfo.put("ARTIST", getDialogControlled().getArtistTextField().getSelectedItem());
        }
        if (!getDialogControlled().getAlbumCheckBox().isEnabled() || getDialogControlled().getAlbumCheckBox().isSelected()) {
            editTagInfo.put("ALBUM", getDialogControlled().getAlbumTextField().getSelectedItem());
        }
        if (!getDialogControlled().getYearCheckBox().isEnabled() || getDialogControlled().getYearCheckBox().isSelected()) {
            editTagInfo.put("YEAR", getDialogControlled().getYearTextField().getText());
        }
        if (!getDialogControlled().getCommentCheckBox().isEnabled() || getDialogControlled().getCommentCheckBox().isSelected()) {
            editTagInfo.put("COMMENT", getDialogControlled().getCommentTextArea().getText());
        }
        if (!getDialogControlled().getGenreCheckBox().isEnabled() || getDialogControlled().getGenreCheckBox().isSelected()) {
            editTagInfo.put("GENRE", getDialogControlled().getGenreComboBox().getSelectedItem());
        }
        if (!getDialogControlled().getLyricsCheckBox().isEnabled() || getDialogControlled().getLyricsCheckBox().isSelected()) {
            // Text area line breaks are \n so in some OS (Windows) is not a correct line break -> Replace with OS line terminator
            String lyrics = getDialogControlled().getLyricsTextArea().getText();
            if (SystemProperties.OS.equals(OperatingSystem.WINDOWS)) {
                lyrics = lyrics.replaceAll("\n", "\r\n");
            }
            editTagInfo.put("LYRICS", lyrics);
        }
        if (!getDialogControlled().getComposerCheckBox().isEnabled() || getDialogControlled().getComposerCheckBox().isSelected()) {
            editTagInfo.put("COMPOSER", getDialogControlled().getComposerTextField().getText());
        }
        if (!getDialogControlled().getAlbumArtistCheckBox().isEnabled() || getDialogControlled().getAlbumArtistCheckBox().isSelected()) {
            editTagInfo.put("ALBUM_ARTIST", getDialogControlled().getAlbumArtistTextField().getText());
        }
        if (!getDialogControlled().getTrackNumberCheckBox().isEnabled() || getDialogControlled().getTrackNumberCheckBox().isSelected()) {
            editTagInfo.put("TRACK", getDialogControlled().getTrackNumberTextField().getText());
        }
        if (!getDialogControlled().getDiscNumberCheckBox().isEnabled() || getDialogControlled().getDiscNumberCheckBox().isSelected()) {
            editTagInfo.put("DISC_NUMBER", getDialogControlled().getDiscNumberTextField().getText());
        }
        if ((!getDialogControlled().getCoverCheckBox().isEnabled() || getDialogControlled().getCoverCheckBox().isSelected()) && coverEdited) {
            editTagInfo.put("COVER", newCover);
        }

        EditTagsProcess process = new EditTagsProcess(new ArrayList<AudioFile>(audioFilesEditing), editTagInfo);
        process.execute();
    }

    public void setNewCover(byte[] newCover) {
        this.newCover = newCover;
    }

    public void setCoverEdited(boolean coverEdited) {
        this.coverEdited = coverEdited;
    }

    public List<AudioFile> getAudioFilesEditing() {
        return new ArrayList<AudioFile>(audioFilesEditing);
    }

    public void clear() {
        audioFilesEditing = Collections.emptyList();
        newCover = null;
        coverEdited = false;
    }

    @Override
    protected void notifyReload() {
        // Nothing to do
    }
}
