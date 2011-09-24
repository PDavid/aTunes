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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.table.TableCellEditor;

import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.model.Artist;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ITag;
import net.sourceforge.atunes.model.ITagAttributesReviewed;

import org.jdesktop.swingx.combobox.ListComboBoxModel;

/**
 * Keeps information about a set of changes to be made on tags when importing a
 * set of files
 * 
 * @author fleax
 * 
 */
public class TagAttributesReviewed implements ITagAttributesReviewed {

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

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.tags.ITagAttributesReviewed#getTagAttributesCount()
	 */
    @Override
	public int getTagAttributesCount() {
        return getTagAttributes().size();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.tags.ITagAttributesReviewed#getTagAttributeName(int)
	 */
    @Override
	public String getTagAttributeName(int index) {
        if (getTagAttributes().size() <= index) {
            return null;
        }
        return getTagAttributes().get(index).getName();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.tags.ITagAttributesReviewed#getValueForTagAttribute(int, net.sourceforge.atunes.model.ILocalAudioObject)
	 */
    @Override
	public String getValueForTagAttribute(int index, ILocalAudioObject audioFile) {
        if (getTagAttributes().size() <= index) {
            return null;
        }
        return getTagAttributes().get(index).getValue(audioFile);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.tags.ITagAttributesReviewed#getChangeForAttributeAndFolder(int, java.io.File)
	 */
    @Override
	public String getChangeForAttributeAndFolder(int index, File folder) {
        if (getTagAttributes().size() <= index) {
            return null;
        }
        if (getTagAttributes().get(index).getChangesMade().containsKey(folder)) {
            return getTagAttributes().get(index).getChangesMade().get(folder);
        }
        return null;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.tags.ITagAttributesReviewed#setTagAttributeForFolder(int, java.io.File, java.lang.String)
	 */
    @Override
	public void setTagAttributeForFolder(int index, File folder, String value) {
        if (getTagAttributes().size() <= index) {
            return;
        }
        getTagAttributes().get(index).getChangesMade().put(folder, value);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.tags.ITagAttributesReviewed#getTagForAudioFile(net.sourceforge.atunes.model.ILocalAudioObject)
	 */
    @Override
	public ITag getTagForAudioFile(ILocalAudioObject file) {
        File parentFolder = file.getFile().getParentFile();
        ITag tag = null;
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

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.tags.ITagAttributesReviewed#getCellEditorForTagAttribute(int)
	 */
    @Override
	public TableCellEditor getCellEditorForTagAttribute(int index) {
        if (getTagAttributes().size() <= index) {
            return null;
        }
        return getTagAttributes().get(index).getCellEditor();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.kernel.modules.tags.ITagAttributesReviewed#getTagAttributeIndex(java.lang.String)
	 */
    @Override
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
        String getValue(ILocalAudioObject audioFile) {
            return audioFile.getDiscNumber() > 0 ? String.valueOf(audioFile.getDiscNumber()) : "";
        }

        @Override
        ITag changeTag(ITag tag, String value) {
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
        String getValue(ILocalAudioObject audioFile) {
            return audioFile.getYear();
        }

        @Override
        ITag changeTag(ITag tag, String value) {
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
        String getValue(ILocalAudioObject audioFile) {
            // we use getTag().getGenre() to avoid returning unknown genre
            return audioFile.getTag() != null ? audioFile.getTag().getGenre() : null;
        }

        @Override
        ITag changeTag(ITag tag, String value) {
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
        String getValue(ILocalAudioObject audioFile) {
            // we use getTag().getAlbum() to avoid returning unknown album
            return audioFile.getTag() != null ? audioFile.getTag().getAlbum() : null;
        }

        @Override
        ITag changeTag(ITag tag, String value) {
            tag.setAlbum(value);
            return tag;
        }
    }

    private static final class ComposerTagAttributeReviewed extends AbstractTagAttributeReviewed {
        private ComposerTagAttributeReviewed(String name) {
            super(name);
        }

        @Override
        String getValue(ILocalAudioObject audioFile) {
            return audioFile.getComposer();
        }

        @Override
        ITag changeTag(ITag tag, String value) {
            tag.setComposer(value);
            return tag;
        }
    }

    private static final class AlbumArtistTagAttributeReviewed extends AbstractTagAttributeReviewed {
        private AlbumArtistTagAttributeReviewed(String name) {
            super(name);
        }

        @Override
        String getValue(ILocalAudioObject audioFile) {
            return audioFile.getAlbumArtist();
        }

        @Override
        ITag changeTag(ITag tag, String value) {
            tag.setAlbumArtist(value);
            return tag;
        }
    }

    private static final class ArtistTagAttributeReviewed extends AbstractTagAttributeReviewed {
        private ArtistTagAttributeReviewed(String name) {
            super(name);
        }

        @Override
        String getValue(ILocalAudioObject audioFile) {
            // we use getTag().getArtist() to avoid returning unknown artist
            return audioFile.getTag() != null ? audioFile.getTag().getArtist() : null;
        }

        @Override
        ITag changeTag(ITag tag, String value) {
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
