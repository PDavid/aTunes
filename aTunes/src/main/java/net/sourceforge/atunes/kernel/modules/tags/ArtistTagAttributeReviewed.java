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
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.table.TableCellEditor;

import net.sourceforge.atunes.model.IArtist;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.model.ITag;

import org.jdesktop.swingx.combobox.ListComboBoxModel;

final class ArtistTagAttributeReviewed extends AbstractTagAttributeReviewed {

    private final IRepositoryHandler repositoryHandler;

    ArtistTagAttributeReviewed(final String name,
	    final IRepositoryHandler repositoryHandler) {
	super(name);
	this.repositoryHandler = repositoryHandler;
    }

    @Override
    String getValue(final ILocalAudioObject audioFile) {
	// Don't want to return unknown artist
	return audioFile.getArtist(null);
    }

    @Override
    ITag changeTag(final ITag tag, final String value) {
	tag.setArtist(value);
	return tag;
    }

    @Override
    TableCellEditor getCellEditor() {
	List<IArtist> artistList = repositoryHandler.getArtists();
	List<String> artistNames = new ArrayList<String>();
	for (IArtist a : artistList) {
	    artistNames.add(a.getName());
	}
	JComboBox artistsCombo = new JComboBox(new ListComboBoxModel<String>(
		artistNames));
	artistsCombo.setEditable(true);
	// Automcomplete seems to work incorrectly when using it in a cell
	// editor
	// AutoCompleteDecorator.decorate(artistsCombo);
	return new DefaultCellEditor(artistsCombo);
    }
}