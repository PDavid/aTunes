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

package net.sourceforge.atunes.kernel.modules.cdripper;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The model for cd info
 */
class CdInfoTableModel extends AbstractTableModel {

    private static final String TRACK = "TRACK";

	private static final long serialVersionUID = -7577681531593039707L;

    private transient CDInfo cdInfo;
    private List<String> trackNames = new ArrayList<String>();
    private List<String> artistNames = new ArrayList<String>();
    private List<String> composerNames = new ArrayList<String>();
    private List<Boolean> tracksSelected;

	private IUnknownObjectChecker unknownObjectChecker;
	
    /**
     * Instantiates a new cd info table model.
     * @param unknownObjectChecker
     */
    public CdInfoTableModel(IUnknownObjectChecker unknownObjectChecker) {
        this.unknownObjectChecker = unknownObjectChecker;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnIndex == 0 ? Boolean.class : String.class;
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public String getColumnName(int column) {
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
        return cdInfo != null ? cdInfo.getTracks() : 0;
    }

    /**
     * Gets the tracks selected.
     * 
     * @return the tracks selected
     */
    public List<Boolean> getTracksSelected() {
        return tracksSelected;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return tracksSelected.get(rowIndex);
        } else if (columnIndex == 1) {
            if (rowIndex > trackNames.size() - 1) {
                trackNames.add(rowIndex, StringUtils.getString(I18nUtils.getString(TRACK), " ", (rowIndex + 1)));
                return StringUtils.getString(I18nUtils.getString(TRACK), " ", (rowIndex + 1));
            }
            if (rowIndex < trackNames.size()) {
                return trackNames.get(rowIndex);
            }

            trackNames.add(rowIndex, StringUtils.getString(I18nUtils.getString(TRACK), " ", (rowIndex + 1)));
            return StringUtils.getString(I18nUtils.getString(TRACK), " ", (rowIndex + 1));

        } else if (columnIndex == 2) {
            if (rowIndex > artistNames.size() - 1) {
                // TODO if cdda2wav is modified for detecting song artist modify here
                if (cdInfo.getArtist() != null) {
                    return cdInfo.getArtist();
                }
                return unknownObjectChecker.getUnknownArtist();
            }
            return artistNames.get(rowIndex);
        } else if (columnIndex == 4) {
        	if (rowIndex > cdInfo.getDurations().size() - 1) {
        		return "";
        	}
            return cdInfo.getDurations().get(rowIndex);
        } else {
            if (rowIndex > composerNames.size() - 1) {
                composerNames.add(rowIndex, "");
                return "";
            }
            return composerNames.get(rowIndex);
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex != 4;
    }

    /**
     * Sets the artist names.
     * 
     * @param artistNames
     *            the new artist names
     */
    public void setArtistNames(List<String> artistNames) {
        this.artistNames = artistNames;
    }

    /**
     * Sets the cd info.
     * 
     * @param cdInfo
     *            the new cd info
     */
    public void setCDInfo(CDInfo cdInfo) {
        if (cdInfo != null) {
            this.cdInfo = cdInfo;
            if (tracksSelected == null) {
                tracksSelected = new ArrayList<Boolean>();
            }
            tracksSelected.clear();
            for (int i = 0; i < cdInfo.getTracks(); i++) {
                tracksSelected.add(true);
            }
        }
    }

    /**
     * Sets the composer names.
     * 
     * @param composerNames
     *            the new composer names
     */
    public void setComposerNames(List<String> composerNames) {
        this.composerNames = composerNames;
    }

    /**
     * Sets the track names.
     * 
     * @param trackNames
     *            the new track names
     */
    public void setTrackNames(List<String> trackNames) {
        this.trackNames = trackNames;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            tracksSelected.remove(rowIndex);
            tracksSelected.add(rowIndex, (Boolean) aValue);
        } else if (columnIndex == 1) {
            trackNames.set(rowIndex, (String) aValue);
        } else if (columnIndex == 2) {
            artistNames.set(rowIndex, (String) aValue);
        } else if (columnIndex == 3) {
            composerNames.set(rowIndex, (String) aValue);
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }
    
    /**
     * @return
     */
    public List<String> getTrackNames() {
		return trackNames;
	}
    
    /**
     * @return
     */
    public List<String> getComposerNames() {
		return composerNames;
	}
    
    /**
     * @return
     */
    public List<String> getArtistNames() {
		return artistNames;
	}
}