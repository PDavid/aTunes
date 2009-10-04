/*
 * aTunes 2.0.0-SNAPSHOT
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
import net.sourceforge.atunes.kernel.controllers.model.SimpleController;
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

public class EditTagDialogController extends SimpleController<EditTagDialog> {

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
        getComponentControlled().getGenreComboBox().setModel(new ListComboBoxModel<String>(genresSorted));
        // Add autocompletion
        AutoCompleteDecorator.decorate(getComponentControlled().getGenreComboBox());

        EditTagDialogActionListener actionListener = new EditTagDialogActionListener(this, getComponentControlled());
        getComponentControlled().getOkButton().addActionListener(actionListener);
        getComponentControlled().getCancelButton().addActionListener(actionListener);

        getComponentControlled().getCoverButton().addActionListener(actionListener);
        getComponentControlled().getRemoveCoverButton().addActionListener(actionListener);
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

        getComponentControlled().getCover().setIcon(null);
        getComponentControlled().getCoverButton().setEnabled(false);
        getComponentControlled().getRemoveCoverButton().setEnabled(false);
        newCover = null;
        coverEdited = false;

        // Load artists into combo box
        List<Artist> artistList = RepositoryHandler.getInstance().getArtists();
        List<String> artistNames = new ArrayList<String>();
        for (Artist a : artistList) {
            artistNames.add(a.getName());
        }
        getComponentControlled().getArtistTextField().setModel(new ListComboBoxModel<String>(artistNames));
        // Activate autocompletion of artists
        AutoCompleteDecorator.decorate(getComponentControlled().getArtistTextField());

        // Load albums into combo box
        List<Album> albumList = RepositoryHandler.getInstance().getAlbums();
        List<String> albumNames = new ArrayList<String>();
        for (Album alb : albumList) {
            // Because of artists and album artists there can be more than one album with the same name
            if (!albumNames.contains(alb.getName())) {
                albumNames.add(alb.getName());
            }
        }
        getComponentControlled().getAlbumTextField().setModel(new ListComboBoxModel<String>(albumNames));
        // Active autocompletion of albums
        AutoCompleteDecorator.decorate(getComponentControlled().getAlbumTextField());

        getComponentControlled().setTitleSelected(false);
        getComponentControlled().setCoverSelected(false);
        getComponentControlled().setAlbumArtistSelected(false);
        getComponentControlled().setArtistSelected(false);
        getComponentControlled().setTrackNumberSelected(false);
        getComponentControlled().setDiscNumberSelected(false);
        getComponentControlled().setYearSelected(false);
        getComponentControlled().setGenreSelected(false);
        getComponentControlled().setCommentSelected(false);
        getComponentControlled().setLyricsSelected(false);
        getComponentControlled().setAlbumSelected(false);
        getComponentControlled().setComposerSelected(false);

        // Check if at least one audio file supports internal pictures
        boolean supportsInternalPicture = false;
        for (AudioFile af : audioFilesEditing) {
            if (af.supportsInternalPicture()) {
                supportsInternalPicture = true;
                break;
            }
        }

        if (audioFiles.size() == 1) {
            getComponentControlled().getTitleCheckBox().setEnabled(false);
            getComponentControlled().getAlbumArtistCheckBox().setEnabled(false);
            getComponentControlled().getArtistCheckBox().setEnabled(false);
            getComponentControlled().getTrackNumberCheckBox().setEnabled(false);
            getComponentControlled().getYearCheckBox().setEnabled(false);
            getComponentControlled().getDiscNumberCheckBox().setEnabled(false);
            getComponentControlled().getGenreCheckBox().setEnabled(false);
            getComponentControlled().getCommentCheckBox().setEnabled(false);
            getComponentControlled().getLyricsCheckBox().setEnabled(false);
            getComponentControlled().getAlbumCheckBox().setEnabled(false);
            getComponentControlled().getComposerCheckBox().setEnabled(false);
            getComponentControlled().getCoverCheckBox().setEnabled(false);
            getComponentControlled().getCoverCheckBox().setEnabled(false);

            if (supportsInternalPicture) {
                getEditTagDialog().getOkButton().setEnabled(false);
                getComponentControlled().getCoverCheckBox().setSelected(true);

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
            getComponentControlled().getTitleCheckBox().setEnabled(true);
            getComponentControlled().getAlbumArtistCheckBox().setEnabled(true);
            getComponentControlled().getArtistCheckBox().setEnabled(true);
            getComponentControlled().getTrackNumberCheckBox().setEnabled(true);
            getComponentControlled().getYearCheckBox().setEnabled(true);
            getComponentControlled().getDiscNumberCheckBox().setEnabled(true);
            getComponentControlled().getGenreCheckBox().setEnabled(true);
            getComponentControlled().getCommentCheckBox().setEnabled(true);
            getComponentControlled().getLyricsCheckBox().setEnabled(true);
            getComponentControlled().getAlbumCheckBox().setEnabled(true);
            getComponentControlled().getComposerCheckBox().setEnabled(true);
            getComponentControlled().getCoverCheckBox().setEnabled(true);
            getComponentControlled().getCoverCheckBox().setEnabled(supportsInternalPicture);
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
            getComponentControlled().getTitleTextField().setText(titles.iterator().next());
            getComponentControlled().setTitleSelected(true);
        } else {
            getComponentControlled().getTitleTextField().setText("");
            getComponentControlled().setTitleSelected(audioFiles.size() == 1);
        }

        if (trackNumbers.size() == 1 && !trackNumbers.contains(0)) {
            getComponentControlled().getTrackNumberTextField().setText(trackNumbers.iterator().next().toString());
            getComponentControlled().setTrackNumberSelected(true);
        } else {
            getComponentControlled().getTrackNumberTextField().setText("");
            getComponentControlled().setTrackNumberSelected(audioFiles.size() == 1);
        }

        if (discNumbers.size() == 1 && !discNumbers.contains(0)) {
            getComponentControlled().getDiscNumberTextField().setText(discNumbers.iterator().next().toString());
            getComponentControlled().setDiscNumberSelected(true);
        } else {
            getComponentControlled().getDiscNumberTextField().setText("");
            getComponentControlled().setDiscNumberSelected(audioFiles.size() == 1);
        }

        if (artists.size() == 1 && !artists.contains("")) {
            getComponentControlled().getArtistTextField().getEditor().setItem(artists.iterator().next());
            getComponentControlled().setArtistSelected(true);
        } else {
            getComponentControlled().getArtistTextField().getEditor().setItem("");
            getComponentControlled().setArtistSelected(audioFiles.size() == 1);
        }

        if (albums.size() == 1 && !albums.contains("")) {
            getComponentControlled().getAlbumTextField().getEditor().setItem(albums.iterator().next());
            getComponentControlled().setAlbumSelected(true);
        } else {
            getComponentControlled().getAlbumTextField().getEditor().setItem("");
            getComponentControlled().setAlbumSelected(audioFiles.size() == 1);
        }

        if (years.size() == 1 && !years.contains(0)) {
            getComponentControlled().getYearTextField().setText(String.valueOf(years.iterator().next()));
            getComponentControlled().setYearSelected(true);
        } else {
            getComponentControlled().getYearTextField().setText("");
            getComponentControlled().setYearSelected(audioFiles.size() == 1);
        }

        if (comments.size() == 1 && !comments.contains("")) {
            getComponentControlled().getCommentTextArea().setText(comments.iterator().next());
            getComponentControlled().getCommentTextArea().setCaretPosition(0);
            getComponentControlled().setCommentSelected(true);
        } else {
            getComponentControlled().getCommentTextArea().setText("");
            getComponentControlled().setCommentSelected(audioFiles.size() == 1);
        }

        if (genres.size() == 1 && !genres.contains("")) {
            getComponentControlled().getGenreComboBox().getEditor().setItem(genres.iterator().next());
            getComponentControlled().setGenreSelected(true);
        } else {
            getComponentControlled().getGenreComboBox().getEditor().setItem("");
            getComponentControlled().setGenreSelected(audioFiles.size() == 1);
        }

        if (lyrics.size() == 1 && !lyrics.contains("")) {
            getComponentControlled().getLyricsTextArea().setText(lyrics.iterator().next());
            getComponentControlled().getLyricsTextArea().setCaretPosition(0);
            getComponentControlled().setLyricsSelected(true);
        } else {
            getComponentControlled().getLyricsTextArea().setText("");
            getComponentControlled().setLyricsSelected(audioFiles.size() == 1);
        }

        if (composers.size() == 1 && !composers.contains("")) {
            getComponentControlled().getComposerTextField().setText(composers.iterator().next());
            getComponentControlled().setComposerSelected(true);
        } else {
            getComponentControlled().getComposerTextField().setText("");
            getComponentControlled().setComposerSelected(audioFiles.size() == 1);
        }

        if (albumArtists.size() == 1 && !albumArtists.contains("")) {
            getComponentControlled().getAlbumArtistTextField().setText(albumArtists.iterator().next());
            getComponentControlled().setAlbumArtistSelected(true);
        } else {
            getComponentControlled().getAlbumArtistTextField().setText("");
            getComponentControlled().setAlbumArtistSelected(audioFiles.size() == 1);
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

        getComponentControlled().setVisible(true);

    }

    EditTagDialog getDialog() {
        return getComponentControlled();
    }

    /**
     * Returns edit tag dialog. Used for inner classes without increasing
     * visibility of getDialogControlled
     * 
     * @return
     */
    EditTagDialog getEditTagDialog() {
        return getComponentControlled();
    }

    /**
     * Edits the tag.
     */
    protected void editTag() {
        getLogger().debug(LogCategories.CONTROLLER);

        getComponentControlled().setVisible(false);

        // Build editor props
        EditTagInfo editTagInfo = new EditTagInfo();

        if (!getComponentControlled().getTitleCheckBox().isEnabled() || getComponentControlled().getTitleCheckBox().isSelected()) {
            editTagInfo.put("TITLE", getComponentControlled().getTitleTextField().getText());
        }
        if (!getComponentControlled().getArtistCheckBox().isEnabled() || getComponentControlled().getArtistCheckBox().isSelected()) {
            editTagInfo.put("ARTIST", getComponentControlled().getArtistTextField().getSelectedItem());
        }
        if (!getComponentControlled().getAlbumCheckBox().isEnabled() || getComponentControlled().getAlbumCheckBox().isSelected()) {
            editTagInfo.put("ALBUM", getComponentControlled().getAlbumTextField().getSelectedItem());
        }
        if (!getComponentControlled().getYearCheckBox().isEnabled() || getComponentControlled().getYearCheckBox().isSelected()) {
            editTagInfo.put("YEAR", getComponentControlled().getYearTextField().getText());
        }
        if (!getComponentControlled().getCommentCheckBox().isEnabled() || getComponentControlled().getCommentCheckBox().isSelected()) {
            editTagInfo.put("COMMENT", getComponentControlled().getCommentTextArea().getText());
        }
        if (!getComponentControlled().getGenreCheckBox().isEnabled() || getComponentControlled().getGenreCheckBox().isSelected()) {
            editTagInfo.put("GENRE", getComponentControlled().getGenreComboBox().getSelectedItem());
        }
        if (!getComponentControlled().getLyricsCheckBox().isEnabled() || getComponentControlled().getLyricsCheckBox().isSelected()) {
            // Text area line breaks are \n so in some OS (Windows) is not a correct line break -> Replace with OS line terminator
            String lyrics = getComponentControlled().getLyricsTextArea().getText();
            if (SystemProperties.OS.equals(OperatingSystem.WINDOWS)) {
                lyrics = lyrics.replaceAll("\n", "\r\n");
            }
            editTagInfo.put("LYRICS", lyrics);
        }
        if (!getComponentControlled().getComposerCheckBox().isEnabled() || getComponentControlled().getComposerCheckBox().isSelected()) {
            editTagInfo.put("COMPOSER", getComponentControlled().getComposerTextField().getText());
        }
        if (!getComponentControlled().getAlbumArtistCheckBox().isEnabled() || getComponentControlled().getAlbumArtistCheckBox().isSelected()) {
            editTagInfo.put("ALBUM_ARTIST", getComponentControlled().getAlbumArtistTextField().getText());
        }
        if (!getComponentControlled().getTrackNumberCheckBox().isEnabled() || getComponentControlled().getTrackNumberCheckBox().isSelected()) {
            editTagInfo.put("TRACK", getComponentControlled().getTrackNumberTextField().getText());
        }
        if (!getComponentControlled().getDiscNumberCheckBox().isEnabled() || getComponentControlled().getDiscNumberCheckBox().isSelected()) {
            editTagInfo.put("DISC_NUMBER", getComponentControlled().getDiscNumberTextField().getText());
        }
        if ((!getComponentControlled().getCoverCheckBox().isEnabled() || getComponentControlled().getCoverCheckBox().isSelected()) && coverEdited) {
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
