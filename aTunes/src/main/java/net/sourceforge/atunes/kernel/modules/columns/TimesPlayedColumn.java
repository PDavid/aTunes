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

package net.sourceforge.atunes.kernel.modules.columns;

import javax.swing.SwingConstants;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectStatistics;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPodcastFeedEntry;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IStatisticsHandler;

/**
 * Column to show how many times audio object has been played
 * @author alex
 *
 */
public class TimesPlayedColumn extends AbstractColumn<String> {

	private static final long serialVersionUID = 7879150472122090859L;

	private transient IStatisticsHandler statisticsHandler;

	/**
	 * @param statisticsHandler
	 */
	public void setStatisticsHandler(final IStatisticsHandler statisticsHandler) {
		this.statisticsHandler = statisticsHandler;
	}

	/**
	 * Default constructor
	 */
	public TimesPlayedColumn() {
		super("TIMES_PLAYED");
		setWidth(100);
		setVisible(false);
		setAlignment(SwingConstants.CENTER);
	}

	@Override
	protected int ascendingCompare(final IAudioObject ao1, final IAudioObject ao2) {
		int times1 = 0;
		int times2 = 0;
		if (ao1 instanceof ILocalAudioObject) {
			IAudioObjectStatistics stats1 = statisticsHandler.getAudioObjectStatistics(ao1);
			times1 = stats1 != null ? stats1.getTimesPlayed() : 0;
		}
		if (ao2 instanceof ILocalAudioObject) {
			IAudioObjectStatistics stats2 = statisticsHandler.getAudioObjectStatistics(ao2);
			times2 = stats2 != null ? stats2.getTimesPlayed() : 0;
		}
		return ((Integer) times1).compareTo(times2);
	}

	@Override
	protected int descendingCompare(final IAudioObject ao1, final IAudioObject ao2) {
		return - ascendingCompare(ao1, ao2);
	}

	@Override
	public String getValueFor(final IAudioObject audioObject, final int row) {
		if (audioObject instanceof IRadio) {
			return "";
		}
		if (audioObject instanceof IPodcastFeedEntry) {
			return "";
		}
		// Return times played
		IAudioObjectStatistics stats = statisticsHandler.getAudioObjectStatistics(audioObject);
		if (stats != null && stats.getTimesPlayed() > 0) {
			return Integer.toString(stats.getTimesPlayed());
		}
		return "";
	}
}
