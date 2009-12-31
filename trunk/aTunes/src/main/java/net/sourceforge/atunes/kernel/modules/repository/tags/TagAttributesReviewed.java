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
import net.sourceforge.atunes.kernel.modules.repository.tags.tag.Tag;

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
    private List<TagAttributeReviewed> tagAttributes;

    /**
     * Returns all TagAttributeReviewed objects to be used
     * 
     * @return
     */
    private List<TagAttributeReviewed> getTagAttributes() {
        if (tagAttributes == null) {
            tagAttributes = new ArrayList<TagAttributeReviewed>();

            tagAttributes.add(new TagAttributeReviewed("ARTIST") {
                @Override
                String getValue(AudioFile audioFile) {
                    // we use getTag().getArtist() to avoid returning unknown artist
                    return audioFile.getTag() != null ? audioFile.getTag().getArtist() : null;
                }

                @Override
                Tag changeTag(Tag tag, String value) {
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
            });
            tagAttributes.add(new TagAttributeReviewed("ALBUM_ARTIST") {
                @Override
                String getValue(AudioFile audioFile) {
                    return audioFile.getAlbumArtist();
                }

                @Override
                Tag changeTag(Tag tag, String value) {
                    tag.setAlbumArtist(value);
                    return tag;
                }
            });
            tagAttributes.add(new TagAttributeReviewed("COMPOSER") {
                @Override
                String getValue(AudioFile audioFile) {
                    return audioFile.getComposer();
                }

                @Override
                Tag changeTag(Tag tag, String value) {
                    tag.setComposer(value);
                    return tag;
                }
            });
            tagAttributes.add(new TagAttributeReviewed("ALBUM") {
                @Override
                String getValue(AudioFile audioFile) {
                    // we use getTag().getAlbum() to avoid returning unknown album
                    return audioFile.getTag() != null ? audioFile.getTag().getAlbum() : null;
                }

                @Override
                Tag changeTag(Tag tag, String value) {
                    tag.setAlbum(value);
                    return tag;
                }
            });
            tagAttributes.add(new TagAttributeReviewed("GENRE") {
                @Override
                String getValue(AudioFile audioFile) {
                    // we use getTag().getGenre() to avoid returning unknown genre
                    return audioFile.getTag() != null ? audioFile.getTag().getGenre() : null;
                }

                @Override
                Tag changeTag(Tag tag, String value) {
                    tag.setGenre(value);
                    return tag;
                }

                @Override
                TableCellEditor getCellEditor() {
                    // Add genres combo box items
                    List<String> genresSorted = Arrays.asList(Tag.genres);
                    Collections.sort(genresSorted);
                    JComboBox genreComboBox = new JComboBox(new ListComboBoxModel<String>(genresSorted));
                    genreComboBox.setEditable(true);
                    // Activate auto completion of genres
                    // Automcomplete seems to work incorrectly when using it in a cell editor
                    //AutoCompleteDecorator.decorate(genreComboBox);
                    return new DefaultCellEditor(genreComboBox);
                }
            });
            tagAttributes.add(new TagAttributeReviewed("YEAR") {
                @Override
                String getValue(AudioFile audioFile) {
                    return audioFile.getYear();
                }

                @Override
                Tag changeTag(Tag tag, String value) {
                    try {
                        tag.setYear(Integer.parseInt(value));
                    } catch (NumberFormatException e) {
                        tag.setYear(0);
                    }
                    return tag;
                }
            });
            tagAttributes.add(new TagAttributeReviewed("DISC_NUMBER") {
                @Override
                String getValue(AudioFile audioFile) {
                    return audioFile.getDiscNumber() > 0 ? String.valueOf(audioFile.getDiscNumber()) : "";
                }

                @Override
                Tag changeTag(Tag tag, String value) {
                    try {
                        tag.setDiscNumber(Integer.parseInt(value));
                    } catch (NumberFormatException e) {
                        tag.setDiscNumber(0);
                    }
                    return tag;
                }
            });
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
    public Tag getTagForAudioFile(AudioFile file) {
        File parentFolder = file.getFile().getParentFile();
        Tag tag = null;
        for (TagAttributeReviewed tagAttribute : getTagAttributes()) {
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
}
