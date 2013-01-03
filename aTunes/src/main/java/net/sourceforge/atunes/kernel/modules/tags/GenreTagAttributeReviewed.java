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

import java.util.Collections;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.table.TableCellEditor;

import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ITag;

import org.jdesktop.swingx.combobox.ListComboBoxModel;

final class GenreTagAttributeReviewed extends AbstractTagAttributeReviewed {

	private final List<String> genresSorted;

	GenreTagAttributeReviewed(String name, List<String> genresSorted) {
		super(name);
		this.genresSorted = genresSorted;
	}

	@Override
	String getValue(ILocalAudioObject audioFile) {
		// we use getTag().getGenre() to avoid returning unknown genre
		return audioFile.getTag() != null ? audioFile.getTag().getGenre()
				: null;
	}

	@Override
	ITag changeTag(ITag tag, String value) {
		tag.setGenre(value);
		return tag;
	}

	@Override
	TableCellEditor getCellEditor() {
		// Add genres combo box items
		Collections.sort(genresSorted);
		JComboBox genreComboBox = new JComboBox(new ListComboBoxModel<String>(
				genresSorted));
		genreComboBox.setEditable(true);
		// Activate auto completion of genres
		// Automcomplete seems to work incorrectly when using it in a cell
		// editor
		// AutoCompleteDecorator.decorate(genreComboBox);
		return new DefaultCellEditor(genreComboBox);
	}
}