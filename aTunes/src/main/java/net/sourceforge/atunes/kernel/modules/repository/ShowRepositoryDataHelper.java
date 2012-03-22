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

package net.sourceforge.atunes.kernel.modules.repository;

import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

public class ShowRepositoryDataHelper {

	private IFrame frame;

	/**
	 * @param frame
	 */
	public void setFrame(IFrame frame) {
		this.frame = frame;
	}
	
    //TODO RTL component orientation
    /**
     * Show repository song number.
     * 
     * @param size
     *            the size
     * @param sizeInBytes
     *            the size in bytes
     * @param duration
     *            the duration
     */
    void showRepositoryAudioFileNumber(long size, long sizeInBytes, long duration) {
    	frame.setCenterStatusBarText(getText(size), 
    							     getToolTip(size, sizeInBytes, duration));
    }
    
    /**
     * Returns text with repository numbers
     * @param size
     */
    private String getText(long size) {
        // Check if differenciation is required (needed by some slavic languages)
    	return StringUtils.getString(I18nUtils.getString("REPOSITORY"), ": ", size, " ", 
    			(I18nUtils.getString("SONGS_IN_REPOSITORY").isEmpty() ? I18nUtils.getString("SONGS") : I18nUtils.getString("SONGS_IN_REPOSITORY")));
    }
    
    /**
     * Returns tooltip for repository numbers
     * @param size
     * @param sizeInBytes
     * @param duration
     * @return
     */
    private String getToolTip(long size, long sizeInBytes, long duration) {
        return StringUtils.getString(getText(size), " - ", 
        		StringUtils.fromByteToMegaOrGiga(sizeInBytes), " - ", StringUtils.fromSecondsToHoursAndDays(duration));
    }
}
