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

import java.util.Iterator;
import java.util.List;

import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.datatype.Artwork;

/**
 * A tag that throws UnsupportedOperationException when trying to access a field
 * @author alex
 *
 */
final class WrongTag implements Tag {
	
	@Override
	public void setField(FieldKey genericKey, String value) throws KeyNotFoundException, FieldDataInvalidException {
	}

	@Override
	public void setField(TagField field) throws FieldDataInvalidException {
	}

	@Override
	public void setField(Artwork artwork) throws FieldDataInvalidException {
	}

	@Override
	public boolean setEncoding(String enc) throws FieldDataInvalidException {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean hasField(String id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasCommonFields() {
		return false;
	}

	@Override
	public String getValue(FieldKey id, int n) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getSubValue(FieldKey id, int n, int m) {
		throw new UnsupportedOperationException();
	}

	@Override
	public TagField getFirstField(FieldKey id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public TagField getFirstField(String id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Artwork getFirstArtwork() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getFirst(FieldKey id) throws KeyNotFoundException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getFirst(String id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<TagField> getFields(FieldKey id) throws KeyNotFoundException {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<TagField> getFields(String id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<TagField> getFields() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getFieldCountIncludingSubValues() {
		return 0;
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public List<Artwork> getArtworkList() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteField(String key) throws KeyNotFoundException {
	}

	@Override
	public void deleteField(FieldKey fieldKey) throws KeyNotFoundException {
	}

	@Override
	public void deleteArtworkField() throws KeyNotFoundException {
	}

	@Override
	public TagField createField(FieldKey genericKey, String value)
			throws KeyNotFoundException, FieldDataInvalidException {
		return null;
	}

	@Override
	public TagField createField(Artwork artwork)
			throws FieldDataInvalidException {
		return null;
	}

	@Override
	public void addField(FieldKey genericKey, String value)
			throws KeyNotFoundException, FieldDataInvalidException {
	}

	@Override
	public void addField(TagField field) throws FieldDataInvalidException {
	}

	@Override
	public void addField(Artwork artwork) throws FieldDataInvalidException {
	}
}