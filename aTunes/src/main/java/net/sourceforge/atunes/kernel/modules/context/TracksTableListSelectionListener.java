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

package net.sourceforge.atunes.kernel.modules.context;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.atunes.model.ITrackInfo;

final class TracksTableListSelectionListener implements ListSelectionListener {
	
	private final ITracksTableListener listener;
	private final JTable tracksTable;

	/**
	 * @param listener
	 * @param tracksTable
	 */
	TracksTableListSelectionListener(ITracksTableListener listener, JTable tracksTable) {
		this.listener = listener;
		this.tracksTable = tracksTable;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
	    if (!e.getValueIsAdjusting()) {
	        int selectedTrack = tracksTable.getSelectedRow();
	        if (selectedTrack != -1) {
	            ITrackInfo track = ((ITrackTableModel) tracksTable.getModel()).getTrack(selectedTrack);
	            listener.trackSelected(track);
	        }
	    }
	}
}