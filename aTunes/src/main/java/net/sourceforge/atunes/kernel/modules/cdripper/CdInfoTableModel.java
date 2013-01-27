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

package net.sourceforge.atunes.kernel.modules.cdripper;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The model for cd info
 */
class CdInfoTableModel extends AbstractTableModel {

	private static final String TRACK = "TRACK";

	private static final long serialVersionUID = -7577681531593039707L;

	private transient CDInfo cdInfo;
	private List<String> trackNames;
	private List<String> artistNames = new ArrayList<String>();
	private List<String> composerNames = new ArrayList<String>();
	private List<Boolean> tracksSelected;

	@Override
	public Class<?> getColumnClass(final int columnIndex) {
		return columnIndex == 0 ? Boolean.class : String.class;
	}

	@Override
	public int getColumnCount() {
		return 5;
	}

	@Override
	public String getColumnName(final int column) {
		if (column == 0) {
			return "";
		} else if (column == 1) {
			return I18nUtils.getString("TITLE");
		} else if (column == 2) {
			return I18nUtils.getString("ARTIST");
		} else if (column == 3) {
			return I18nUtils.getString("COMPOSER");
		} else {
			return "";
		}
	}

	@Override
	public int getRowCount() {
		return this.cdInfo != null ? this.cdInfo.getTracks() : 0;
	}

	/**
	 * Gets the tracks selected.
	 * 
	 * @return the tracks selected
	 */
	public List<Boolean> getTracksSelected() {
		return this.tracksSelected;
	}

	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		if (columnIndex == 0) {
			return this.tracksSelected.get(rowIndex);
		} else if (columnIndex == 1) {
			return this.trackNames.get(rowIndex);
		} else if (columnIndex == 2) {
			return getArtistNameValue(rowIndex);
		} else if (columnIndex == 4) {
			return getDurationValue(rowIndex);
		} else {
			return getComposerValue(rowIndex);
		}
	}

	private Object getArtistNameValue(final int rowIndex) {
		if (rowIndex > this.artistNames.size() - 1) {
			// TODO if cdda2wav is modified for detecting song artist modify
			// here
			if (this.cdInfo.getArtist() != null) {
				return this.cdInfo.getArtist();
			}
			return "";
		}
		return this.artistNames.get(rowIndex);
	}

	private Object getDurationValue(final int rowIndex) {
		if (rowIndex > this.cdInfo.getDurations().size() - 1) {
			return "";
		}
		return this.cdInfo.getDurations().get(rowIndex);
	}

	private Object getComposerValue(final int rowIndex) {
		if (rowIndex > this.composerNames.size() - 1) {
			this.composerNames.add(rowIndex, "");
			return "";
		}
		return this.composerNames.get(rowIndex);
	}

	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		return columnIndex != 4;
	}

	/**
	 * Sets the artist names.
	 * 
	 * @param artistNames
	 *            the new artist names
	 */
	public void setArtistNames(final List<String> artistNames) {
		this.artistNames = artistNames;
	}

	/**
	 * Sets the cd info.
	 * 
	 * @param cdInfo
	 *            the new cd info
	 */
	public void setCDInfo(final CDInfo cdInfo) {
		if (cdInfo != null) {
			this.cdInfo = cdInfo;
			if (this.tracksSelected == null) {
				this.tracksSelected = new ArrayList<Boolean>();
			}
			this.tracksSelected.clear();
			for (int i = 0; i < cdInfo.getTracks(); i++) {
				this.tracksSelected.add(true);
			}

			fillTrackNames(this.cdInfo.getTracks());
		}
	}

	private void fillTrackNames(final int tracks) {
		if (this.trackNames == null) {
			this.trackNames = new ArrayList<String>();
		}
		this.trackNames.clear();
		for (int i = 0; i < tracks; i++) {
			this.trackNames.add(StringUtils.getString(
					I18nUtils.getString(TRACK), " ", (i + 1)));
		}
	}

	/**
	 * Sets the composer names.
	 * 
	 * @param composerNames
	 *            the new composer names
	 */
	public void setComposerNames(final List<String> composerNames) {
		this.composerNames = composerNames;
	}

	/**
	 * Sets the track names.
	 * 
	 * @param trackNames
	 *            the new track names
	 */
	public void setTrackNames(final List<String> trackNames) {
		List<String> newTrackNames = trackNames;
		if (trackNames.size() < this.trackNames.size()) {
			newTrackNames = new ArrayList<String>();
			// Use titles from argument list, and remaining titles use default
			// title
			for (int i = 0; i < this.trackNames.size(); i++) {
				if (i < trackNames.size()) {
					newTrackNames.add(trackNames.get(i));
				} else {
					newTrackNames.add(StringUtils.getString(
							I18nUtils.getString(TRACK), " ", (i + 1)));
				}
			}
		}
		this.trackNames = newTrackNames;
	}

	@Override
	public void setValueAt(final Object aValue, final int rowIndex,
			final int columnIndex) {
		if (columnIndex == 0) {
			this.tracksSelected.remove(rowIndex);
			this.tracksSelected.add(rowIndex, (Boolean) aValue);
		} else if (columnIndex == 1) {
			this.trackNames.set(rowIndex, (String) aValue);
		} else if (columnIndex == 2) {
			this.artistNames.set(rowIndex, (String) aValue);
		} else if (columnIndex == 3) {
			this.composerNames.set(rowIndex, (String) aValue);
		}
		fireTableCellUpdated(rowIndex, columnIndex);
	}

	/**
	 * @return
	 */
	public List<String> getTrackNames() {
		return this.trackNames;
	}

	/**
	 * @return
	 */
	public List<String> getComposerNames() {
		return this.composerNames;
	}

	/**
	 * @return
	 */
	public List<String> getArtistNames() {
		return this.artistNames;
	}
}