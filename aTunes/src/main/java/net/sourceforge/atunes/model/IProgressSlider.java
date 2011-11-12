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

package net.sourceforge.atunes.model;




/**
 * Component to show a progress slider that user can move
 * @author alex
 *
 */
public interface IProgressSlider extends IPanel {

	/**
	 * @param state
	 */
	public void setState(IState state);

	/**
	 * Sets played time
	 * @param time in milliseconds
	 */
	public void setProgress(long time, long remainingTime);

	/**
	 * Sets layout
	 */
	public void setLayout();

	/**
	 * Shows or hides ticks and labels in progress bar
	 * @param showTicks
	 */
	public void setShowTicksAndLabels(boolean showTicks);

	/**
	 * Delegate method
	 * @param value
	 */
	public void setValue(int value);

	/**
	 * Delegate method
	 * @return
	 */
	public int getMaximum();

	/**
	 * Delegate method
	 * @return
	 */
	public int getProgressBarWidth();

	/**
	 * Delegate method
	 * @param length
	 */
	public void setMaximum(int length);

	/**
	 * Delegate method
	 * @return
	 */
	public int getValue();

	/**
	 * Setup ticks spacing
	 * 
	 * @param length
	 */
	public void setupProgressTicks(long length);

}