/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.kernel.modules.repository.tags;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.table.TableCellEditor;

import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.data.Artist;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.DefaultTag;
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.AbstractTag;

import org.jdesktop.swingx.combobox.ListComboBoxModel;

/**
 * Keeps information about a set of changes to be made on tags when importing a
 * set of files
 * 
 * @author fleax
 * 
 */
public class TagAttributesReviewed {

    /**
     * List of tag attributes to be reviewed
     */
    private List<AbstractTagAttributeReviewed> tagAttributes;

    /**
     * Returns all TagAttributeReviewed objects to be used
     * 
     * @return
     */
    private List<AbstractTagAttributeReviewed> getTagAttributes() {
        if (tagAttributes == null) {
            tagAttributes = new ArrayList<AbstractTagAttributeReviewed>();

            tagAttributes.add(new ArtistTagAttributeReviewed("ARTIST"));
            tagAttributes.add(new AlbumArtistTagAttributeReviewed("ALBUM_ARTIST"));
            tagAttributes.add(new ComposerTagAttributeReviewed("COMPOSER"));
            tagAttributes.add(new AlbumTagAttributeReviewed("ALBUM"));
            tagAttributes.add(new GenreTagAttributeReviewed("GENRE"));
            tagAttributes.add(new YearTagAttributeReviewed("YEAR"));
            tagAttributes.add(new DiscNumberTagAttributeReviewed("DISC_NUMBER"));
        }
        return tagAttributes;
    }

    /**
     * Returns the number of tags attributes used
     * 
     * @return
     */
    public int getTagAttributesCount() {
        return getTagAttributes().size();
    }

    /**
     * Returns the name of the tag attribute at given index
     * 
     * @param index
     * @return
     */
    public String getTagAttributeName(int index) {
        if (getTagAttributes().size() <= index) {
            return null;
        }
        return getTagAttributes().get(index).getName();
    }

    /**
     * Returns the value of the tag attribute at given index
     * 
     * @param index
     * @param audioFile
     * @return
     */
    public String getValueForTagAttribute(int index, AudioFile audioFile) {
        if (getTagAttributes().size() <= index) {
            return null;
        }
        return getTagAttributes().get(index).getValue(audioFile);
    }

    /**
     * Returns the value of the tag attribute at given index for the given
     * folder if it has been changed or null
     * 
     * @param index
     * @param folder
     * @return
     */
    public String getChangeForAttributeAndFolder(int index, File folder) {
        if (getTagAttributes().size() <= index) {
            return null;
        }
        if (getTagAttributes().get(index).getChangesMade().containsKey(folder)) {
            return getTagAttributes().get(index).getChangesMade().get(folder);
        }
        return null;
    }

    /**
     * Stores a tag attribute change at given index and folder
     * 
     * @param index
     * @param folder
     * @param value
     */
    public void setTagAttributeForFolder(int index, File folder, String value) {
        if (getTagAttributes().size() <= index) {
            return;
        }
        getTagAttributes().get(index).getChangesMade().put(folder, value);
    }

    /**
     * Returns a tag for given AudioFile with tag attributes changed according
     * to information stored in this object
     * 
     * @param file
     * @return
     */
    public AbstractTag getTagForAudioFile(AudioFile file) {
        File parentFolder = file.getFile().getParentFile();
        AbstractTag tag = null;
        for (AbstractTagAttributeReviewed tagAttribute : getTagAttributes()) {
            if (tagAttribute.getChangesMade().containsKey(parentFolder)) {
                if (tag == null) {
                    tag = file.getTag() != null ? file.getTag() : new DefaultTag();
                }
                tag = tagAttribute.changeTag(tag, tagAttribute.getChangesMade().get(parentFolder));
            }
        }
        return tag;
    }

    public TableCellEditor getCellEditorForTagAttribute(int index) {
        if (getTagAttributes().size() <= index) {
            return null;
        }
        return getTagAttributes().get(index).getCellEditor();
    }

    /**
     * Returns index of given tag attribute
     * 
     * @param tagAttributeName
     * @return
     */
    public int getTagAttributeIndex(String tagAttributeName) {
        for (int i = 0; i < tagAttributes.size(); i++) {
            if (tagAttributes.get(i).getName().equals(tagAttributeName)) {
                return i;
            }
        }
        return -1;
    }

    private static final class DiscNumberTagAttributeReviewed extends AbstractTagAttributeReviewed {
        private DiscNumberTagAttributeReviewed(String name) {
            super(name);
        }

        @Override
        String getValue(AudioFile audioFile) {
            return audioFile.getDiscNumber() > 0 ? String.valueOf(audioFile.getDiscNumber()) : "";
        }

        @Override
        AbstractTag changeTag(AbstractTag tag, String value) {
            try {
                tag.setDiscNumber(Integer.parseInt(value));
            } catch (NumberFormatException e) {
                tag.setDiscNumber(0);
            }
            return tag;
        }
    }

    private static final class YearTagAttributeReviewed extends AbstractTagAttributeReviewed {
        private YearTagAttributeReviewed(String name) {
            super(name);
        }

        @Override
        String getValue(AudioFile audioFile) {
            return audioFile.getYear();
        }

        @Override
        AbstractTag changeTag(AbstractTag tag, String value) {
            try {
                tag.setYear(Integer.parseInt(value));
            } catch (NumberFormatException e) {
                tag.setYear(0);
            }
            return tag;
        }
    }

    private static final class GenreTagAttributeReviewed extends AbstractTagAttributeReviewed {
        private GenreTagAttributeReviewed(String name) {
            super(name);
        }

        @Override
        String getValue(AudioFile audioFile) {
            // we use getTag().getGenre() to avoid returning unknown genre
            return audioFile.getTag() != null ? audioFile.getTag().getGenre() : null;
        }

        @Override
        AbstractTag changeTag(AbstractTag tag, String value) {
            tag.setGenre(value);
            return tag;
        }

        @Override
        TableCellEditor getCellEditor() {
            // Add genres combo box items
            List<String> genresSorted = Arrays.asList(AbstractTag.genres);
            Collections.sort(genresSorted);
            JComboBox genreComboBox = new JComboBox(new ListComboBoxModel<String>(genresSorted));
            genreComboBox.setEditable(true);
            // Activate auto completion of genres
            // Automcomplete seems to work incorrectly when using it in a cell editor
            //AutoCompleteDecorator.decorate(genreComboBox);
            return new DefaultCellEditor(genreComboBox);
        }
    }

    private static final class AlbumTagAttributeReviewed extends AbstractTagAttributeReviewed {
        private AlbumTagAttributeReviewed(String name) {
            super(name);
        }

        @Override
        String getValue(AudioFile audioFile) {
            // we use getTag().getAlbum() to avoid returning unknown album
            return audioFile.getTag() != null ? audioFile.getTag().getAlbum() : null;
        }

        @Override
        AbstractTag changeTag(AbstractTag tag, String value) {
            tag.setAlbum(value);
            return tag;
        }
    }

    private static final class ComposerTagAttributeReviewed extends AbstractTagAttributeReviewed {
        private ComposerTagAttributeReviewed(String name) {
            super(name);
        }

        @Override
        String getValue(AudioFile audioFile) {
            return audioFile.getComposer();
        }

        @Override
        AbstractTag changeTag(AbstractTag tag, String value) {
            tag.setComposer(value);
            return tag;
        }
    }

    private static final class AlbumArtistTagAttributeReviewed extends AbstractTagAttributeReviewed {
        private AlbumArtistTagAttributeReviewed(String name) {
            super(name);
        }

        @Override
        String getValue(AudioFile audioFile) {
            return audioFile.getAlbumArtist();
        }

        @Override
        AbstractTag changeTag(AbstractTag tag, String value) {
            tag.setAlbumArtist(value);
            return tag;
        }
    }

    private static final class ArtistTagAttributeReviewed extends AbstractTagAttributeReviewed {
        private ArtistTagAttributeReviewed(String name) {
            super(name);
        }

        @Override
        String getValue(AudioFile audioFile) {
            // we use getTag().getArtist() to avoid returning unknown artist
            return audioFile.getTag() != null ? audioFile.getTag().getArtist() : null;
        }

        @Override
        AbstractTag changeTag(AbstractTag tag, String value) {
            tag.setArtist(value);
            return tag;
        }

        @Override
        TableCellEditor getCellEditor() {
            List<Artist> artistList = RepositoryHandler.getInstance().getArtists();
            List<String> artistNames = new ArrayList<String>();
            for (Artist a : artistList) {
                artistNames.add(a.getName());
            }
            JComboBox artistsCombo = new JComboBox(new ListComboBoxModel<String>(artistNames));
            artistsCombo.setEditable(true);
            // Automcomplete seems to work incorrectly when using it in a cell editor
            // AutoCompleteDecorator.decorate(artistsCombo);
            return new DefaultCellEditor(artistsCombo);
        }
    }

}
