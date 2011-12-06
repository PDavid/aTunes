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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.TableCellEditor;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ITag;
import net.sourceforge.atunes.model.ITagAttributesReviewed;
import net.sourceforge.atunes.model.ITagHandler;


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

    @Override
	public int getTagAttributesCount() {
        return getTagAttributes().size();
    }

    @Override
	public String getTagAttributeName(int index) {
        if (getTagAttributes().size() <= index) {
            return null;
        }
        return getTagAttributes().get(index).getName();
    }

    @Override
	public String getValueForTagAttribute(int index, ILocalAudioObject audioFile) {
        if (getTagAttributes().size() <= index) {
            return null;
        }
        return getTagAttributes().get(index).getValue(audioFile);
    }

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

    @Override
	public void setTagAttributeForFolder(int index, File folder, String value) {
        if (getTagAttributes().size() <= index) {
            return;
        }
        getTagAttributes().get(index).getChangesMade().put(folder, value);
    }

    @Override
	public ITag getTagForAudioFile(ILocalAudioObject file) {
        File parentFolder = file.getFile().getParentFile();
        ITag tag = null;
        for (AbstractTagAttributeReviewed tagAttribute : getTagAttributes()) {
            if (tagAttribute.getChangesMade().containsKey(parentFolder)) {
                if (tag == null) {
                    tag = file.getTag() != null ? file.getTag() : Context.getBean(ITagHandler.class).getNewTag();
                }
                tag = tagAttribute.changeTag(tag, tagAttribute.getChangesMade().get(parentFolder));
            }
        }
        return tag;
    }

    @Override
	public TableCellEditor getCellEditorForTagAttribute(int index) {
        if (getTagAttributes().size() <= index) {
            return null;
        }
        return getTagAttributes().get(index).getCellEditor();
    }

    @Override
	public int getTagAttributeIndex(String tagAttributeName) {
        for (int i = 0; i < tagAttributes.size(); i++) {
            if (tagAttributes.get(i).getName().equals(tagAttributeName)) {
                return i;
            }
        }
        return -1;
    }
}
