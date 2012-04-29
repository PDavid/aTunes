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

import net.sourceforge.atunes.model.IStateRadio;

/**
 * This class represents the application settings that are stored at application
 * shutdown and loaded at application startup.
 * <p>
 * <b>NOTE: All classes that are used as properties must be Java Beans!</b>
 * </p>
 */
public class ApplicationStateRadio implements IStateRadio {

	/**
     * Component responsible of store state
     */
    private IStateStore stateStore;
    
    /**
     * Sets state store
     * @param store
     */
    public void setStateStore(IStateStore store) {
		this.stateStore = store;
	}

    @Override
	public boolean isShowAllRadioStations() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.SHOW_ALL_RADIO_STATIONS, true);
    }

    @Override
	public void setShowAllRadioStations(boolean showAllRadioStations) {
        this.stateStore.storePreference(Preferences.SHOW_ALL_RADIO_STATIONS, showAllRadioStations);
    }

    @Override
	public boolean isReadInfoFromRadioStream() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.READ_INFO_FROM_RADIO_STREAM, true);
    }

    @Override
	public void setReadInfoFromRadioStream(boolean readInfoFromRadioStream) {
    	this.stateStore.storePreference(Preferences.READ_INFO_FROM_RADIO_STREAM, readInfoFromRadioStream);
    }	
}
