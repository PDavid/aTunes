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

package net.sourceforge.atunes.model;

import java.io.File;

import javax.swing.table.TableCellEditor;

/**
 * Keeps information about a set of changes to be made on tags when importing a
 * set of files
 * 
 * @author alex
 * 
 */
public interface ITagAttributesReviewed {

    /**
     * Returns the number of tags attributes used
     * 
     * @return
     */
    public int getTagAttributesCount();

    /**
     * Returns the name of the tag attribute at given index
     * 
     * @param index
     * @return
     */
    public String getTagAttributeName(int index);

    /**
     * Returns the value of the tag attribute at given index
     * 
     * @param index
     * @param audioFile
     * @return
     */
    public String getValueForTagAttribute(int index, ILocalAudioObject audioFile);

    /**
     * Returns the value of the tag attribute at given index for the given
     * folder if it has been changed or null
     * 
     * @param index
     * @param folder
     * @return
     */
    public String getChangeForAttributeAndFolder(int index, File folder);

    /**
     * Stores a tag attribute change at given index and folder
     * 
     * @param index
     * @param folder
     * @param value
     */
    public void setTagAttributeForFolder(int index, File folder, String value);

    /**
     * Returns a tag for given LocalAudioObject with tag attributes changed
     * according to information stored in this object
     * 
     * @param file
     * @return
     */
    public ITag getTagForAudioFile(ILocalAudioObject file);

    /**
     * Component to edit attribute given by index
     * 
     * @param index
     * @return
     */
    public TableCellEditor getCellEditorForTagAttribute(int index);

    /**
     * Returns index of given tag attribute
     * 
     * @param tagAttributeName
     * @return
     */
    public int getTagAttributeIndex(String tagAttributeName);

}