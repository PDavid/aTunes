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

package net.sourceforge.atunes.kernel.modules.tags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import net.sourceforge.atunes.gui.autocomplete.AutoCompleteDecorator;
import net.sourceforge.atunes.gui.views.dialogs.EditTagDialog;
import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.kernel.modules.process.EditTagsProcess;
import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IFileManager;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObjectImageHandler;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IProcessFactory;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.ITag;
import net.sourceforge.atunes.model.TextTagAttribute;

import org.jdesktop.swingx.combobox.ListComboBoxModel;

/**
 * Controller for edit tag dialog
 * 
 * @author alex
 * 
 */
public final class EditTagDialogController extends
		AbstractSimpleController<EditTagDialog> {

	/** The audio files editing. */
	List<ILocalAudioObject> audioFilesEditing;
	private byte[] newCover;
	private boolean coverEdited;

	private IOSManager osManager;

	private IRepositoryHandler repositoryHandler;

	private IProcessFactory processFactory;

	private Genres genresHelper;

	private IControlsBuilder controlsBuilder;

	private IFileManager fileManager;

	ILocalAudioObjectImageHandler localAudioObjectImageHandler;

	private IBeanFactory beanFactory;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param localAudioObjectImageHandler
	 */
	public void setLocalAudioObjectImageHandler(
			final ILocalAudioObjectImageHandler localAudioObjectImageHandler) {
		this.localAudioObjectImageHandler = localAudioObjectImageHandler;
	}

	/**
	 * @param fileManager
	 */
	public void setFileManager(final IFileManager fileManager) {
		this.fileManager = fileManager;
	}

	/**
	 * @param controlsBuilder
	 */
	public void setControlsBuilder(final IControlsBuilder controlsBuilder) {
		this.controlsBuilder = controlsBuilder;
	}

	/**
	 * Instantiates a new edits the tag dialog controller.
	 * 
	 * @param dialog
	 */
	public EditTagDialogController(final EditTagDialog dialog) {
		super(dialog);
	}

	/**
	 * Initializes controller
	 */
	public void initialize() {
		addBindings();
		addStateBindings();
	}

	/**
	 * @param genresHelper
	 */
	public void setGenresHelper(final Genres genresHelper) {
		this.genresHelper = genresHelper;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param repositoryHandler
	 */
	public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
		this.repositoryHandler = repositoryHandler;
	}

	/**
	 * @param processFactory
	 */
	public void setProcessFactory(final IProcessFactory processFactory) {
		this.processFactory = processFactory;
	}

	@Override
	public void addBindings() {
		// Add genres combo box items
		List<String> genresSorted = this.genresHelper.getGenres();
		Collections.sort(genresSorted);
		getComponentControlled().getComboBoxEditor(TextTagAttribute.GENRE)
				.setModel(new ListComboBoxModel<String>(genresSorted));
		// Add autocompletion
		AutoCompleteDecorator.decorate(getComponentControlled()
				.getComboBoxEditor(TextTagAttribute.GENRE));

		EditTagDialogActionListener actionListener = new EditTagDialogActionListener(
				this, getComponentControlled(), this.controlsBuilder,
				this.fileManager);
		getComponentControlled().getOkButton()
				.addActionListener(actionListener);
		getComponentControlled().getCancelButton().addActionListener(
				actionListener);

		getComponentControlled().getCoverButton().addActionListener(
				actionListener);
		getComponentControlled().getRemoveCoverButton().addActionListener(
				actionListener);
	}

	/**
	 * Checks if the tag of this audio file does support internal images
	 * 
	 * @param audioObject
	 * @return
	 */
	private final boolean supportsInternalPicture(
			final ILocalAudioObject audioObject) {

		// All formats support "internal" picture, even if it's really an
		// external picture
		return true;
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

		this.audioFilesEditing = audioFiles;

		getComponentControlled().getCover().setIcon(null);
		getComponentControlled().getCoverButton().setEnabled(false);
		getComponentControlled().getRemoveCoverButton().setEnabled(false);
		this.newCover = null;
		this.coverEdited = false;

		// Load artists into combo box
		getComponentControlled().getComboBoxEditor(TextTagAttribute.ARTIST)
				.setModel(new ListComboBoxModel<String>(getArtistsNames()));

		// Activate autocompletion of artists
		AutoCompleteDecorator.decorate(getComponentControlled()
				.getComboBoxEditor(TextTagAttribute.ARTIST));

		// Load albums into combo box
		getComponentControlled().getComboBoxEditor(TextTagAttribute.ALBUM)
				.setModel(new ListComboBoxModel<String>(getAlbumNames()));

		// Active autocompletion of albums
		AutoCompleteDecorator.decorate(getComponentControlled()
				.getComboBoxEditor(TextTagAttribute.ALBUM));

		setFieldsUnselected();

		// Check if at least one audio file supports internal pictures
		enableOrDisableCheckBoxes(audioFiles);

		prepareFields(audioFiles);

		// If there is only one file add a help to complete title from file name
		if (audioFiles.size() == 1) {
			final String fileName = audioFiles.get(0).getNameWithoutExtension();
			final JTextField textField = getDialog().getTextFieldEditor(
					TextTagAttribute.TITLE);
			textField.addKeyListener(new TitleTextFieldKeyAdapter(textField,
					fileName));
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// If title is enabled set focus
				if (getDialog().getTextFieldEditor(TextTagAttribute.TITLE)
						.isEnabled()) {
					getDialog().getTextFieldEditor(TextTagAttribute.TITLE)
							.setCaretPosition(0);
					getDialog().getTextFieldEditor(TextTagAttribute.TITLE)
							.requestFocus();
				}
			}
		});

		getComponentControlled().setVisible(true);
	}

	/**
	 * @return
	 */
	private List<String> getAlbumNames() {
		List<IAlbum> albumList = this.repositoryHandler.getAlbums();
		List<String> albumNames = new ArrayList<String>();
		for (IAlbum alb : albumList) {
			// Because of artists and album artists there can be more than one
			// album with the same name
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
		List<IArtist> artistList = this.repositoryHandler.getArtists();
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

		prepareStringControl(TextTagAttribute.TITLE, audioFiles, titles);
		prepareIntegerControl(TextTagAttribute.TRACK, audioFiles, trackNumbers);
		prepareIntegerControl(TextTagAttribute.DISC_NUMBER, audioFiles,
				discNumbers);
		prepareStringComboControl(TextTagAttribute.ARTIST, audioFiles, artists);
		prepareStringComboControl(TextTagAttribute.ALBUM, audioFiles, albums);
		prepareIntegerControl(TextTagAttribute.YEAR, audioFiles, years);
		prepareStringTextAreaControl(TextTagAttribute.COMMENT, audioFiles,
				comments);
		prepareStringComboControl(TextTagAttribute.GENRE, audioFiles, genres);
		prepareStringTextAreaControl(TextTagAttribute.LYRICS, audioFiles,
				lyrics);
		prepareStringControl(TextTagAttribute.COMPOSER, audioFiles, composers);
		prepareStringControl(TextTagAttribute.ALBUM_ARTIST, audioFiles,
				albumArtists);
	}

	/**
	 * @param audioFiles
	 */
	private void enableOrDisableCheckBoxes(
			final List<ILocalAudioObject> audioFiles) {
		boolean supportsInternalPicture = false;
		for (ILocalAudioObject af : this.audioFilesEditing) {
			if (supportsInternalPicture(af)) {
				supportsInternalPicture = true;
				break;
			}
		}

		boolean enable = audioFiles.size() > 1;

		for (TextTagAttribute attribute : TextTagAttribute.values()) {
			getComponentControlled().getCheckBox(attribute).setEnabled(enable);
		}

		getComponentControlled().getCoverCheckBox().setEnabled(
				enable && supportsInternalPicture);

		if (audioFiles.size() == 1 && supportsInternalPicture) {
			getEditTagDialog().getOkButton().setEnabled(false);
			getComponentControlled().getCoverCheckBox().setSelected(true);
			GetInsidePictureBackgroundWorker worker = this.beanFactory
					.getBean(GetInsidePictureBackgroundWorker.class);
			worker.setAudioFiles(audioFiles);
			worker.setController(this);
			worker.execute();
		}
	}

	/**
	 * @param audioFiles
	 * @param set
	 */
	private void prepareStringControl(final TextTagAttribute attribute,
			final List<ILocalAudioObject> audioFiles, final Set<String> set) {
		if (set.size() == 1 && !set.contains("")) {
			getComponentControlled().getTextFieldEditor(attribute).setText(
					set.iterator().next());
			getComponentControlled().setTagAttributeSelected(attribute, true);
		} else {
			getComponentControlled().getTextFieldEditor(attribute).setText("");
			getComponentControlled().setTagAttributeSelected(attribute,
					audioFiles.size() == 1);
		}
	}

	/**
	 * @param audioFiles
	 * @param set
	 */
	private void prepareStringComboControl(final TextTagAttribute attribute,
			final List<ILocalAudioObject> audioFiles, final Set<String> set) {
		if (set.size() == 1 && !set.contains("")) {
			getComponentControlled().getComboBoxEditor(attribute)
					.setSelectedItem(set.iterator().next());
			getComponentControlled().setTagAttributeSelected(attribute, true);
		} else {
			getComponentControlled().getComboBoxEditor(attribute)
					.setSelectedItem("");
			getComponentControlled().setTagAttributeSelected(attribute,
					audioFiles.size() == 1);
		}
	}

	/**
	 * @param audioFiles
	 * @param set
	 */
	private void prepareStringTextAreaControl(final TextTagAttribute attribute,
			final List<ILocalAudioObject> audioFiles, final Set<String> set) {
		if (set.size() == 1 && !set.contains("")) {
			getComponentControlled().getTextAreaEditor(attribute).setText(
					set.iterator().next());
			getComponentControlled().getTextAreaEditor(attribute)
					.setCaretPosition(0);
			getComponentControlled().setTagAttributeSelected(attribute, true);
		} else {
			getComponentControlled().getTextAreaEditor(attribute).setText("");
			getComponentControlled().setTagAttributeSelected(attribute,
					audioFiles.size() == 1);
		}
	}

	/**
	 * @param audioFiles
	 * @param set
	 */
	private void prepareIntegerControl(final TextTagAttribute attribute,
			final List<ILocalAudioObject> audioFiles, final Set<Integer> set) {
		if (set.size() == 1 && !set.contains(0)) {
			getComponentControlled().getTextFieldEditor(attribute).setText(
					set.iterator().next().toString());
			getComponentControlled().setTagAttributeSelected(attribute, true);
		} else {
			getComponentControlled().getTextFieldEditor(attribute).setText("");
			getComponentControlled().setTagAttributeSelected(attribute,
					audioFiles.size() == 1);
		}
	}

	/**
	 * 
	 */
	private void setFieldsUnselected() {
		getComponentControlled().setCoverSelected(false);
		for (TextTagAttribute attribute : TextTagAttribute.values()) {
			getComponentControlled().getCheckBox(attribute).setEnabled(false);
		}
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
	 * Inserts value of text field in map if checkbox is not enabled (only one
	 * file being edited) or selected
	 * 
	 * @param editTagInfo
	 * @param key
	 * @param checkBox
	 * @param textField
	 */
	private void setEditTagInfo(final Map<String, Object> editTagInfo,
			final TextTagAttribute attribute) {
		JCheckBox checkBox = getComponentControlled().getCheckBox(attribute);
		JComponent editor = getComponentControlled().getEditor(attribute);
		if (!checkBox.isEnabled() || checkBox.isSelected()) {
			if (editor instanceof JComboBox) {
				editTagInfo.put(attribute.toString(),
						((JComboBox) editor).getSelectedItem());
			} else if (editor instanceof JTextComponent) {
				String text = ((JTextComponent) editor).getText();
				// Text area line breaks are \n so in some OS (Windows) is not a
				// correct line break -> Replace with OS line terminator
				if (attribute.equals(TextTagAttribute.LYRICS)
						&& this.osManager.isWindows()) {
					text = text.replaceAll("^[\r]\n", "\r\n");
				}
				editTagInfo.put(attribute.toString(), text);
			}
		}
	}

	/**
	 * Edits the tag.
	 */
	protected void editTag() {
		getComponentControlled().setVisible(true);

		// Build editor props
		Map<String, Object> editTagInfo = new HashMap<String, Object>();

		for (TextTagAttribute attribute : TextTagAttribute.values()) {
			setEditTagInfo(editTagInfo, attribute);
		}

		if ((!getComponentControlled().getCoverCheckBox().isEnabled() || getComponentControlled()
				.getCoverCheckBox().isSelected()) && this.coverEdited) {
			editTagInfo.put("COVER", this.newCover);
		}

		EditTagsProcess process = (EditTagsProcess) this.processFactory
				.getProcessByName("editTagsProcess");
		process.setFilesToChange(new ArrayList<ILocalAudioObject>(
				this.audioFilesEditing));
		process.setEditTagInfo(editTagInfo);
		process.execute();
	}

	/**
	 * @param newCover
	 */
	public void setNewCover(final byte[] newCover) {
		if (newCover != null) {
			this.newCover = Arrays.copyOf(newCover, newCover.length);
		} else {
			this.newCover = null;
		}
	}

	/**
	 * @param coverEdited
	 */
	public void setCoverEdited(final boolean coverEdited) {
		this.coverEdited = coverEdited;
	}

	/**
	 * @return
	 */
	public List<ILocalAudioObject> getAudioFilesEditing() {
		return new ArrayList<ILocalAudioObject>(this.audioFilesEditing);
	}

	/**
	 * Clear dialog
	 */
	public void clear() {
		this.audioFilesEditing = Collections.emptyList();
		this.newCover = null;
		this.coverEdited = false;
	}
}
