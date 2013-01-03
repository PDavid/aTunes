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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.table.TableCellEditor;

import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ITag;

/**
 * Keeps information about changes made in an attribute when importing
 * 
 * @author fleax
 */
abstract class AbstractTagAttributeReviewed {

    /**
     * Name of this tag attribute
     */
    private String name;

    /**
     * Keeps changes made in files of a given folder for this tag attribute
     */
    private Map<File, String> changesMade = new HashMap<File, String>();

    AbstractTagAttributeReviewed(String name) {
        this.name = name;
    }

    /**
     * @return the name
     */
    String getName() {
        return name;
    }

    /**
     * @return the changesMade
     */
    Map<File, String> getChangesMade() {
        return changesMade;
    }

    /**
     * Returns TableCellEditor to be used to edit this tag attribute
     * 
     * @return
     */
    TableCellEditor getCellEditor() {
        return null;
    }

    /**
     * Returns a value of this tag attribute for the given AudioFile
     * 
     * @param audioFile
     * @return
     */
    abstract String getValue(ILocalAudioObject audioFile);

    /**
     * Modifies a Tag with the given value
     * 
     * @param tag
     * @param value
     * @return
     */
    abstract ITag changeTag(ITag tag, String value);

}
