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

package net.sourceforge.atunes.kernel.modules.tags;

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
import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.kernel.modules.repository.IRepositoryHandler;
import net.sourceforge.atunes.model.Album;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.ITag;
import net.sourceforge.atunes.utils.AudioFilePictureUtils;
import net.sourceforge.atunes.utils.Logger;

import org.jdesktop.swingx.combobox.ListComboBoxModel;

public final class EditTagDialogController extends AbstractSimpleController<EditTagDialog> {

    private final class GetInsidePictureSwingWorker extends SwingWorker<ImageIcon, Void> {
        private final List<ILocalAudioObject> audioFiles;

        private GetInsidePictureSwingWorker(List<ILocalAudioObject> audioFiles) {
            this.audioFiles = audioFiles;
        }

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
                Logger.error(e);
            } catch (ExecutionException e) {
                Logger.error(e);
            }
        }
    }

    private static final class TitleTextFieldKeyAdapter extends KeyAdapter {
        private final JTextField textField;
        private final String fileName;
        private int lenght = 0;

        private TitleTextFieldKeyAdapter(JTextField textField, String fileName) {
            this.textField = textField;
            this.fileName = fileName;
        }

        @Override
        public void keyTyped(KeyEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    String text = textField.getText();

                    // User added a char
                    if (text.length() > lenght && text.length() >= 3) {
                        int index = fileName.indexOf(text);
                        if (index != -1) {
                            textField.setText(fileName.substring(index));
                            textField.setSelectionStart(text.length());
                            textField.setSelectionEnd(textField.getText().length());
                        }
                    }
                    lenght = text.length();
                }
            });
        }
    }

    /** The audio files editing. */
    private List<ILocalAudioObject> audioFilesEditing;
    private byte[] newCover;
    private boolean coverEdited;
    
    private IOSManager osManager;
    
    private IPlayListHandler playListHandler;
    
    private IRepositoryHandler repositoryHandler;

    /**
     * Instantiates a new edits the tag dialog controller.
     * @param dialog
     * @param state
     * @param osManager
     * @param playListHandler
     * @param repositoryHandler
     */
    public EditTagDialogController(EditTagDialog dialog, IState state, IOSManager osManager, IPlayListHandler playListHandler, IRepositoryHandler repositoryHandler) {
        super(dialog, state);
        this.osManager = osManager;
        this.playListHandler = playListHandler;
        this.repositoryHandler = repositoryHandler;
        addBindings();
        addStateBindings();
    }

    @Override
	public void addBindings() {
        // Add genres combo box items
        List<String> genresSorted = Arrays.asList(AbstractTag.genres);
        Collections.sort(genresSorted);
        getComponentControlled().getGenreComboBox().setModel(new ListComboBoxModel<String>(genresSorted));
        // Add autocompletion
        AutoCompleteDecorator.decorate(getComponentControlled().getGenreComboBox());

        EditTagDialogActionListener actionListener = new EditTagDialogActionListener(this, getComponentControlled(), playListHandler);
        getComponentControlled().getOkButton().addActionListener(actionListener);
        getComponentControlled().getCancelButton().addActionListener(actionListener);

        getComponentControlled().getNextButton().addActionListener(actionListener);
        getComponentControlled().getPrevButton().addActionListener(actionListener);

        getComponentControlled().getCoverButton().addActionListener(actionListener);
        getComponentControlled().getRemoveCoverButton().addActionListener(actionListener);
    }

    /**
     * Edits the files.
     * 
     * @param audioFiles
     *            the files
     */
    public void editFiles(final List<ILocalAudioObject> audioFiles) {
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
        List<Artist> artistList = repositoryHandler.getArtists();
        List<String> artistNames = new ArrayList<String>();
        for (Artist a : artistList) {
            artistNames.add(a.getName());
        }
        getComponentControlled().getArtistTextField().setModel(new ListComboBoxModel<String>(artistNames));
        // Activate autocompletion of artists
        AutoCompleteDecorator.decorate(getComponentControlled().getArtistTextField());

        // Load albums into combo box
        List<Album> albumList = repositoryHandler.getAlbums();
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
        for (ILocalAudioObject af : audioFilesEditing) {
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

                new GetInsidePictureSwingWorker(audioFiles).execute();
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
        for (ILocalAudioObject audioFile : audioFiles) {
            ITag tag = audioFile.getTag();
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
            textField.addKeyListener(new TitleTextFieldKeyAdapter(textField, fileName));
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
        getComponentControlled().setVisible(true);

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
            if (osManager.isWindows()) {
                lyrics = lyrics.replaceAll("^[\r]\n", "\r\n");
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

        EditTagsProcess process = new EditTagsProcess(new ArrayList<ILocalAudioObject>(audioFilesEditing), editTagInfo, getState(), playListHandler, repositoryHandler);
        process.execute();
    }

    public void setNewCover(byte[] newCover) {
        this.newCover = newCover;
    }

    public void setCoverEdited(boolean coverEdited) {
        this.coverEdited = coverEdited;
    }

    public List<ILocalAudioObject> getAudioFilesEditing() {
        return new ArrayList<ILocalAudioObject>(audioFilesEditing);
    }

    public void clear() {
        audioFilesEditing = Collections.emptyList();
        newCover = null;
        coverEdited = false;
    }

}
