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

package net.sourceforge.atunes.kernel.modules.tags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.text.JTextComponent;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.autocomplete.AutoCompleteDecorator;
import net.sourceforge.atunes.gui.views.dialogs.EditTagDialog;
import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.kernel.modules.process.EditTagsProcess;
import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectValidator;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IProcessFactory;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.ITag;
import net.sourceforge.atunes.model.LocalAudioObjectFormat;
import net.sourceforge.atunes.model.TextTagAttribute;
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
        protected ImageIcon doInBackground() {
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

    /** The audio files editing. */
    private List<ILocalAudioObject> audioFilesEditing;
    private byte[] newCover;
    private boolean coverEdited;
    
    private IOSManager osManager;
    
    private IPlayListHandler playListHandler;
    
    private IRepositoryHandler repositoryHandler;
    
    private ILocalAudioObjectValidator localAudioObjectValidator;
    
    private IProcessFactory processFactory;

    /**
     * Instantiates a new edits the tag dialog controller.
     * @param dialog
     * @param osManager
     * @param playListHandler
     * @param repositoryHandler
     * @param localAudioObjectValidator
     * @param processFactory
     */
    public EditTagDialogController(EditTagDialog dialog, IOSManager osManager, IPlayListHandler playListHandler, IRepositoryHandler repositoryHandler, ILocalAudioObjectValidator localAudioObjectValidator, IProcessFactory processFactory) {
        super(dialog);
        this.osManager = osManager;
        this.playListHandler = playListHandler;
        this.repositoryHandler = repositoryHandler;
        this.localAudioObjectValidator = localAudioObjectValidator;
        this.processFactory = processFactory;
        addBindings();
        addStateBindings();
    }

    @Override
	public void addBindings() {
        // Add genres combo box items
        List<String> genresSorted = Context.getBean(Genres.class).getGenres();
        Collections.sort(genresSorted);
        getComponentControlled().getComboBoxEditor(TextTagAttribute.GENRE).setModel(new ListComboBoxModel<String>(genresSorted));
        // Add autocompletion
        AutoCompleteDecorator.decorate(getComponentControlled().getComboBoxEditor(TextTagAttribute.GENRE));

        EditTagDialogActionListener actionListener = new EditTagDialogActionListener(this, getComponentControlled(), playListHandler, localAudioObjectValidator);
        getComponentControlled().getOkButton().addActionListener(actionListener);
        getComponentControlled().getCancelButton().addActionListener(actionListener);

        getComponentControlled().getNextButton().addActionListener(actionListener);
        getComponentControlled().getPrevButton().addActionListener(actionListener);

        getComponentControlled().getCoverButton().addActionListener(actionListener);
        getComponentControlled().getRemoveCoverButton().addActionListener(actionListener);
    }

    /**
     * Checks if the tag of this audio file does support internal images
     * @param audioObject
     * @return
     */
    private final boolean supportsInternalPicture(ILocalAudioObject audioObject) {
        return localAudioObjectValidator.isOneOfTheseFormats(audioObject.getUrl(), LocalAudioObjectFormat.FLAC, LocalAudioObjectFormat.MP3, LocalAudioObjectFormat.MP4_1, LocalAudioObjectFormat.MP4_2, LocalAudioObjectFormat.OGG, LocalAudioObjectFormat.WMA);
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
        getComponentControlled().getComboBoxEditor(TextTagAttribute.ARTIST).setModel(new ListComboBoxModel<String>(getArtistsNames()));
        
        // Activate autocompletion of artists
        AutoCompleteDecorator.decorate(getComponentControlled().getComboBoxEditor(TextTagAttribute.ARTIST));

        // Load albums into combo box
        getComponentControlled().getComboBoxEditor(TextTagAttribute.ALBUM).setModel(new ListComboBoxModel<String>(getAlbumNames()));
        
        // Active autocompletion of albums
        AutoCompleteDecorator.decorate(getComponentControlled().getComboBoxEditor(TextTagAttribute.ALBUM));

        setFieldsUnselected();

        // Check if at least one audio file supports internal pictures
        enableOrDisableCheckBoxes(audioFiles);

        prepareFields(audioFiles);

        // If there is only one file add a help to complete title from file name
        if (audioFiles.size() == 1) {
            final String fileName = audioFiles.get(0).getNameWithoutExtension();
            final JTextField textField = getDialog().getTextFieldEditor(TextTagAttribute.TITLE);
            textField.addKeyListener(new TitleTextFieldKeyAdapter(textField, fileName));
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // If title is enabled set focus
                if (getDialog().getTextFieldEditor(TextTagAttribute.TITLE).isEnabled()) {
                    getDialog().getTextFieldEditor(TextTagAttribute.TITLE).setCaretPosition(0);
                    getDialog().getTextFieldEditor(TextTagAttribute.TITLE).requestFocus();
                }
            }
        });

        getComponentControlled().setVisible(true);
    }

	/**
	 * @return
	 */
	private List<String> getAlbumNames() {
		List<IAlbum> albumList = repositoryHandler.getAlbums();
        List<String> albumNames = new ArrayList<String>();
        for (IAlbum alb : albumList) {
            // Because of artists and album artists there can be more than one album with the same name
            if (!albumNames.contains(alb.getName())) {
                albumNames.add(alb.getName());
            }
        }
		return albumNames;
	}

	/**
	 * @return
	 */
	private List<String> getArtistsNames() {
		List<IArtist> artistList = repositoryHandler.getArtists();
        List<String> artistNames = new ArrayList<String>();
        for (IArtist a : artistList) {
            artistNames.add(a.getName());
        }
		return artistNames;
	}

	/**
	 * @param audioFiles
	 */
	private void prepareFields(final List<ILocalAudioObject> audioFiles) {
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

        prepareTitle(audioFiles, titles);
        prepareTrackNumbers(audioFiles, trackNumbers);
        prepareDiscNumbers(audioFiles, discNumbers);
        prepareArtists(audioFiles, artists);
        prepareAlbums(audioFiles, albums);
        prepareYears(audioFiles, years);
        prepareComments(audioFiles, comments);
        prepareGenres(audioFiles, genres);
        prepareLyrics(audioFiles, lyrics);
        prepareComposers(audioFiles, composers);
        prepareAlbumArtists(audioFiles, albumArtists);
	}

	/**
	 * @param audioFiles
	 */
	private void enableOrDisableCheckBoxes(final List<ILocalAudioObject> audioFiles) {
        boolean supportsInternalPicture = false;
        for (ILocalAudioObject af : audioFilesEditing) {
            if (supportsInternalPicture(af)) {
                supportsInternalPicture = true;
                break;
            }
        }

		boolean enable = audioFiles.size() > 1; 
			
        getComponentControlled().getCheckBox(TextTagAttribute.TITLE).setEnabled(enable);
        getComponentControlled().getCheckBox(TextTagAttribute.ALBUM_ARTIST).setEnabled(enable);
        getComponentControlled().getCheckBox(TextTagAttribute.ARTIST).setEnabled(enable);
        getComponentControlled().getCheckBox(TextTagAttribute.TRACK).setEnabled(enable);
        getComponentControlled().getCheckBox(TextTagAttribute.YEAR).setEnabled(enable);
        getComponentControlled().getCheckBox(TextTagAttribute.DISC_NUMBER).setEnabled(enable);
        getComponentControlled().getCheckBox(TextTagAttribute.GENRE).setEnabled(enable);
        getComponentControlled().getCheckBox(TextTagAttribute.COMMENT).setEnabled(enable);
        getComponentControlled().getCheckBox(TextTagAttribute.LYRICS).setEnabled(enable);
        getComponentControlled().getCheckBox(TextTagAttribute.ALBUM).setEnabled(enable);
        getComponentControlled().getCheckBox(TextTagAttribute.COMPOSER).setEnabled(enable);
        
        getComponentControlled().getCoverCheckBox().setEnabled(enable && supportsInternalPicture);
        
        if (audioFiles.size() == 1 && supportsInternalPicture) {
        	getEditTagDialog().getOkButton().setEnabled(false);
        	getComponentControlled().getCoverCheckBox().setSelected(true);
        	new GetInsidePictureSwingWorker(audioFiles).execute();
        }
	}

	/**
	 * @param audioFiles
	 * @param albumArtists
	 */
	private void prepareAlbumArtists(final List<ILocalAudioObject> audioFiles,
			Set<String> albumArtists) {
		if (albumArtists.size() == 1 && !albumArtists.contains("")) {
            getComponentControlled().getTextFieldEditor(TextTagAttribute.ALBUM_ARTIST).setText(albumArtists.iterator().next());
            getComponentControlled().setTagAttributeSelected(TextTagAttribute.ALBUM_ARTIST, true);
        } else {
            getComponentControlled().getTextFieldEditor(TextTagAttribute.ALBUM_ARTIST).setText("");
            getComponentControlled().setTagAttributeSelected(TextTagAttribute.ALBUM_ARTIST, audioFiles.size() == 1);
        }
	}

	/**
	 * @param audioFiles
	 * @param composers
	 */
	private void prepareComposers(final List<ILocalAudioObject> audioFiles,
			Set<String> composers) {
		if (composers.size() == 1 && !composers.contains("")) {
            getComponentControlled().getTextFieldEditor(TextTagAttribute.COMPOSER).setText(composers.iterator().next());
            getComponentControlled().setTagAttributeSelected(TextTagAttribute.COMPOSER, true);
        } else {
            getComponentControlled().getTextFieldEditor(TextTagAttribute.COMPOSER).setText("");
            getComponentControlled().setTagAttributeSelected(TextTagAttribute.COMPOSER, audioFiles.size() == 1);
        }
	}

	/**
	 * @param audioFiles
	 * @param lyrics
	 */
	private void prepareLyrics(final List<ILocalAudioObject> audioFiles,
			Set<String> lyrics) {
		if (lyrics.size() == 1 && !lyrics.contains("")) {
            getComponentControlled().getTextAreaEditor(TextTagAttribute.LYRICS).setText(lyrics.iterator().next());
            getComponentControlled().getTextAreaEditor(TextTagAttribute.LYRICS).setCaretPosition(0);
            getComponentControlled().setTagAttributeSelected(TextTagAttribute.LYRICS, true);
        } else {
            getComponentControlled().getTextAreaEditor(TextTagAttribute.LYRICS).setText("");
            getComponentControlled().setTagAttributeSelected(TextTagAttribute.LYRICS, audioFiles.size() == 1);
        }
	}

	/**
	 * @param audioFiles
	 * @param genres
	 */
	private void prepareGenres(final List<ILocalAudioObject> audioFiles,
			Set<String> genres) {
		if (genres.size() == 1 && !genres.contains("")) {
            getComponentControlled().getComboBoxEditor(TextTagAttribute.GENRE).getEditor().setItem(genres.iterator().next());
            getComponentControlled().setTagAttributeSelected(TextTagAttribute.GENRE, true);
        } else {
            getComponentControlled().getComboBoxEditor(TextTagAttribute.GENRE).getEditor().setItem("");
            getComponentControlled().setTagAttributeSelected(TextTagAttribute.GENRE, audioFiles.size() == 1);
        }
	}

	/**
	 * @param audioFiles
	 * @param comments
	 */
	private void prepareComments(final List<ILocalAudioObject> audioFiles,
			Set<String> comments) {
		if (comments.size() == 1 && !comments.contains("")) {
            getComponentControlled().getTextAreaEditor(TextTagAttribute.COMMENT).setText(comments.iterator().next());
            getComponentControlled().getTextAreaEditor(TextTagAttribute.COMMENT).setCaretPosition(0);
            getComponentControlled().setTagAttributeSelected(TextTagAttribute.COMMENT, true);
        } else {
            getComponentControlled().getTextAreaEditor(TextTagAttribute.COMMENT).setText("");
            getComponentControlled().setTagAttributeSelected(TextTagAttribute.COMMENT, audioFiles.size() == 1);
        }
	}

	/**
	 * @param audioFiles
	 * @param years
	 */
	private void prepareYears(final List<ILocalAudioObject> audioFiles, Set<Integer> years) {
		if (years.size() == 1 && !years.contains(0)) {
            getComponentControlled().getTextFieldEditor(TextTagAttribute.YEAR).setText(String.valueOf(years.iterator().next()));
            getComponentControlled().setTagAttributeSelected(TextTagAttribute.YEAR, true);
        } else {
            getComponentControlled().getTextFieldEditor(TextTagAttribute.YEAR).setText("");
            getComponentControlled().setTagAttributeSelected(TextTagAttribute.YEAR, audioFiles.size() == 1);
        }
	}

	/**
	 * @param audioFiles
	 * @param albums
	 */
	private void prepareAlbums(final List<ILocalAudioObject> audioFiles, Set<String> albums) {
		if (albums.size() == 1 && !albums.contains("")) {
            getComponentControlled().getComboBoxEditor(TextTagAttribute.ALBUM).getEditor().setItem(albums.iterator().next());
            getComponentControlled().setTagAttributeSelected(TextTagAttribute.ALBUM, true);
        } else {
            getComponentControlled().getComboBoxEditor(TextTagAttribute.ALBUM).getEditor().setItem("");
            getComponentControlled().setTagAttributeSelected(TextTagAttribute.ALBUM, audioFiles.size() == 1);
        }
	}

	/**
	 * @param audioFiles
	 * @param artists
	 */
	private void prepareArtists(final List<ILocalAudioObject> audioFiles, Set<String> artists) {
		if (artists.size() == 1 && !artists.contains("")) {
            getComponentControlled().getComboBoxEditor(TextTagAttribute.ARTIST).getEditor().setItem(artists.iterator().next());
            getComponentControlled().setTagAttributeSelected(TextTagAttribute.ARTIST, true);
        } else {
            getComponentControlled().getComboBoxEditor(TextTagAttribute.ARTIST).getEditor().setItem("");
            getComponentControlled().setTagAttributeSelected(TextTagAttribute.ARTIST, audioFiles.size() == 1);
        }
	}

	/**
	 * @param audioFiles
	 * @param discNumbers
	 */
	private void prepareDiscNumbers(final List<ILocalAudioObject> audioFiles,
			Set<Integer> discNumbers) {
		if (discNumbers.size() == 1 && !discNumbers.contains(0)) {
            getComponentControlled().getTextFieldEditor(TextTagAttribute.DISC_NUMBER).setText(discNumbers.iterator().next().toString());
            getComponentControlled().setTagAttributeSelected(TextTagAttribute.DISC_NUMBER, true);
        } else {
            getComponentControlled().getTextFieldEditor(TextTagAttribute.DISC_NUMBER).setText("");
            getComponentControlled().setTagAttributeSelected(TextTagAttribute.DISC_NUMBER, audioFiles.size() == 1);
        }
	}

	/**
	 * @param audioFiles
	 * @param trackNumbers
	 */
	private void prepareTrackNumbers(final List<ILocalAudioObject> audioFiles,
			Set<Integer> trackNumbers) {
		if (trackNumbers.size() == 1 && !trackNumbers.contains(0)) {
            getComponentControlled().getTextFieldEditor(TextTagAttribute.TRACK).setText(trackNumbers.iterator().next().toString());
            getComponentControlled().setTagAttributeSelected(TextTagAttribute.TRACK, true);
        } else {
            getComponentControlled().getTextFieldEditor(TextTagAttribute.TRACK).setText("");
            getComponentControlled().setTagAttributeSelected(TextTagAttribute.TRACK, audioFiles.size() == 1);
        }
	}

	/**
	 * @param audioFiles
	 * @param titles
	 */
	private void prepareTitle(final List<ILocalAudioObject> audioFiles,
			Set<String> titles) {
		if (titles.size() == 1 && !titles.contains("")) {
            getComponentControlled().getTextFieldEditor(TextTagAttribute.TITLE).setText(titles.iterator().next());
            getComponentControlled().setTagAttributeSelected(TextTagAttribute.TITLE, true);
        } else {
            getComponentControlled().getTextFieldEditor(TextTagAttribute.TITLE).setText("");
            getComponentControlled().setTagAttributeSelected(TextTagAttribute.TITLE, audioFiles.size() == 1);
        }
	}

	/**
	 * 
	 */
	private void setFieldsUnselected() {
		getComponentControlled().setTagAttributeSelected(TextTagAttribute.TITLE,  false);
        getComponentControlled().setCoverSelected(false);
        getComponentControlled().setTagAttributeSelected(TextTagAttribute.ALBUM_ARTIST, false);
        getComponentControlled().setTagAttributeSelected(TextTagAttribute.ARTIST, false);
        getComponentControlled().setTagAttributeSelected(TextTagAttribute.TRACK, false);
        getComponentControlled().setTagAttributeSelected(TextTagAttribute.DISC_NUMBER, false);
        getComponentControlled().setTagAttributeSelected(TextTagAttribute.YEAR, false);
        getComponentControlled().setTagAttributeSelected(TextTagAttribute.GENRE, false);
        getComponentControlled().setTagAttributeSelected(TextTagAttribute.COMMENT, false);
        getComponentControlled().setTagAttributeSelected(TextTagAttribute.LYRICS, false);
        getComponentControlled().setTagAttributeSelected(TextTagAttribute.ALBUM, false);
        getComponentControlled().setTagAttributeSelected(TextTagAttribute.COMPOSER, false);
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
     * Inserts value of text field in map if checkbox is not enabled (only one file being edited) or selected
     * @param editTagInfo
     * @param key
     * @param checkBox
     * @param textField
     */
    private void setEditTagInfo(Map<String, Object> editTagInfo, String key, JCheckBox checkBox, JTextComponent textField) {
        if (!checkBox.isEnabled() || checkBox.isSelected()) {
            editTagInfo.put(key, textField.getText());
        }    	
    }

    /**
     * Inserts value of combo in map if checkbox is not enabled (only one file being edited) or selected
     * @param editTagInfo
     * @param key
     * @param checkBox
     * @param combo
     */
    private void setEditTagInfo(Map<String, Object> editTagInfo, String key, JCheckBox checkBox, JComboBox combo) {
        if (!checkBox.isEnabled() || checkBox.isSelected()) {
            editTagInfo.put(key, combo.getSelectedItem());
        }    	
    }

    /**
     * Edits the tag.
     */
    protected void editTag() {
        getComponentControlled().setVisible(true);

        // Build editor props
        Map<String, Object> editTagInfo = new HashMap<String, Object>();

        setEditTagInfo(editTagInfo, "TITLE", getComponentControlled().getCheckBox(TextTagAttribute.TITLE), getComponentControlled().getTextFieldEditor(TextTagAttribute.TITLE));
        setEditTagInfo(editTagInfo, "ARTIST", getComponentControlled().getCheckBox(TextTagAttribute.ARTIST), getComponentControlled().getComboBoxEditor(TextTagAttribute.ARTIST));
        setEditTagInfo(editTagInfo, "ALBUM", getComponentControlled().getCheckBox(TextTagAttribute.ALBUM), getComponentControlled().getComboBoxEditor(TextTagAttribute.ALBUM));
        setEditTagInfo(editTagInfo, "YEAR", getComponentControlled().getCheckBox(TextTagAttribute.YEAR), getComponentControlled().getTextFieldEditor(TextTagAttribute.YEAR));
        setEditTagInfo(editTagInfo, "COMMENT", getComponentControlled().getCheckBox(TextTagAttribute.COMMENT), getComponentControlled().getTextAreaEditor(TextTagAttribute.COMMENT));
        setEditTagInfo(editTagInfo, "GENRE", getComponentControlled().getCheckBox(TextTagAttribute.GENRE), getComponentControlled().getComboBoxEditor(TextTagAttribute.GENRE));
        
        if (!getComponentControlled().getCheckBox(TextTagAttribute.LYRICS).isEnabled() || getComponentControlled().getCheckBox(TextTagAttribute.LYRICS).isSelected()) {
            // Text area line breaks are \n so in some OS (Windows) is not a correct line break -> Replace with OS line terminator
            String lyrics = getComponentControlled().getTextAreaEditor(TextTagAttribute.LYRICS).getText();
            if (osManager.isWindows()) {
                lyrics = lyrics.replaceAll("^[\r]\n", "\r\n");
            }
            editTagInfo.put("LYRICS", lyrics);
        }
        
        setEditTagInfo(editTagInfo, "COMPOSER", getComponentControlled().getCheckBox(TextTagAttribute.COMPOSER), getComponentControlled().getTextFieldEditor(TextTagAttribute.COMPOSER));
        setEditTagInfo(editTagInfo, "ALBUM_ARTIST", getComponentControlled().getCheckBox(TextTagAttribute.ALBUM_ARTIST), getComponentControlled().getTextFieldEditor(TextTagAttribute.ALBUM_ARTIST));
        setEditTagInfo(editTagInfo, "TRACK", getComponentControlled().getCheckBox(TextTagAttribute.TRACK), getComponentControlled().getTextFieldEditor(TextTagAttribute.TRACK));
        setEditTagInfo(editTagInfo, "DISC_NUMBER", getComponentControlled().getCheckBox(TextTagAttribute.DISC_NUMBER), getComponentControlled().getTextFieldEditor(TextTagAttribute.DISC_NUMBER));
        
        if ((!getComponentControlled().getCoverCheckBox().isEnabled() || getComponentControlled().getCoverCheckBox().isSelected()) && coverEdited) {
            editTagInfo.put("COVER", newCover);
        }

        EditTagsProcess process = (EditTagsProcess) processFactory.getProcessByName("editTagsProcess");
        process.setFilesToChange(new ArrayList<ILocalAudioObject>(audioFilesEditing));
        process.setEditTagInfo(editTagInfo);
        process.execute();
    }

    /**
     * @param newCover
     */
    public void setNewCover(byte[] newCover) {
    	if (newCover != null) {
    		this.newCover = Arrays.copyOf(newCover, newCover.length);
    	} else {
    		this.newCover = null;
    	}
    }

    /**
     * @param coverEdited
     */
    public void setCoverEdited(boolean coverEdited) {
        this.coverEdited = coverEdited;
    }

    /**
     * @return
     */
    public List<ILocalAudioObject> getAudioFilesEditing() {
        return new ArrayList<ILocalAudioObject>(audioFilesEditing);
    }

    /**
     * Clear dialog
     */
    public void clear() {
        audioFilesEditing = Collections.emptyList();
        newCover = null;
        coverEdited = false;
    }
}
