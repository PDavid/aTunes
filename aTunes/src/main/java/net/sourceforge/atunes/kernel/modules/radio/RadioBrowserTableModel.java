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

package net.sourceforge.atunes.kernel.modules.radio;

import javax.swing.table.DefaultTableModel;

import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.utils.I18nUtils;

import com.google.common.collect.ListMultimap;

class RadioBrowserTableModel extends DefaultTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6337473995274751050L;

	private String selectedLabel;

	private final ListMultimap<String, IRadio> radios;

	RadioBrowserTableModel(final ListMultimap<String, IRadio> radios) {
		super();
		this.radios = radios;
	}

	@Override
	public Class<?> getColumnClass(final int columnIndex) {
		return String.class;
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public String getColumnName(final int columnIndex) {
		return columnIndex == 0 ? I18nUtils.getString("NAME") : I18nUtils
				.getString("URL");
	}

	@Override
	public int getRowCount() {
		if (this.radios != null && this.selectedLabel != null) {
			return this.radios.get(this.selectedLabel).size();
		}
		return 0;
	}

	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		IRadio radio = getRadioAt(rowIndex);
		if (radio != null) {
			if (columnIndex == 0) {
				return radio.getName();
			} else {
				return radio.getUrl();
			}
		}
		return null;
	}

	/**
	 * @param selectedLabel
	 */
	public void setSelectedLabel(final String selectedLabel) {
		this.selectedLabel = selectedLabel;
		fireTableDataChanged();
	}

	protected IRadio getRadioAt(final int rowIndex) {
		if (this.radios != null && this.selectedLabel != null
				&& this.radios.containsKey(this.selectedLabel)) {
			return this.radios.get(this.selectedLabel).get(rowIndex);
		}
		return null;
	}

	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		return false;
	}
}
