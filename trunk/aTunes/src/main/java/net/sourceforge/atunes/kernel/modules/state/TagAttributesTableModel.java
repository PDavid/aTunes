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

package net.sourceforge.atunes.kernel.modules.state;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import net.sourceforge.atunes.model.TagAttribute;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The Class TagAttributesTableModel.
 */
class TagAttributesTableModel implements TableModel {

	private static final long serialVersionUID = 5251001708812824836L;

	/** The tag attributes hash map. */
	private Map<TagAttribute, Boolean> tagAttributes;

	/** The listeners. */
	private List<TableModelListener> listeners = new ArrayList<TableModelListener>();

	/**
	 * Instantiates a new tag attributes table model
	 */
	TagAttributesTableModel() {
		// Nothing to do
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		listeners.add(l);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return columnIndex == 0 ? Boolean.class : String.class;
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public String getColumnName(int column) {
		return "";
	}

	@Override
	public int getRowCount() {
		if (this.tagAttributes != null) {
			return this.tagAttributes.size();
		}
		return 0;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return tagAttributes.get(TagAttribute.values()[rowIndex]);
		}
		return I18nUtils.getString(TagAttribute.values()[rowIndex].name());
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 0;
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		listeners.remove(l);
	}

	/**
	 * Sets the columns.
	 * 
	 * @param columns
	 *            the new columns
	 */
	 public void setTagAttributes(Map<TagAttribute, Boolean> attrs) {
		 this.tagAttributes = attrs;
	 }

	 @Override
	 public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		 if (columnIndex == 0) {
			 tagAttributes.put(TagAttribute.values()[rowIndex], (Boolean) aValue);
		 }
	 }

	 /**
	  * @return the tagAttributes selected by user
	  */
	 public List<TagAttribute> getSelectedTagAttributes() {
		 List<TagAttribute> result = new ArrayList<TagAttribute>();
		 for (TagAttribute attr : tagAttributes.keySet()) {
			 if (tagAttributes.get(attr)) {
				 result.add(attr);
			 }
		 }
		 return result;
	 }
}