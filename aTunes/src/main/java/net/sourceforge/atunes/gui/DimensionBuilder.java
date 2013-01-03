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

package net.sourceforge.atunes.gui;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import net.sourceforge.atunes.utils.Logger;

/**
 * Creates Dimension objects according to screen size and proportions
 * 
 * @author alex
 * 
 */
public final class DimensionBuilder {

    private static Rectangle screenSize;

    private DimensionBuilder() {
    }

    static {
	retrieveScreenSize();
    }

    /**
     * Retrieves screen resolution
     */
    private static void retrieveScreenSize() {
	if (!GraphicsEnvironment.isHeadless()) {
	    screenSize = GraphicsEnvironment.getLocalGraphicsEnvironment()
		    .getDefaultScreenDevice().getDefaultConfiguration()
		    .getBounds();
	    if (screenSize != null) {
		Logger.debug("Screen size: ", screenSize.width, "x",
			screenSize.height);
	    } else {
		Logger.error("Can't retrieve screen size");
	    }
	} else {
	    Logger.error("This graphic environment is headless!!");
	}

	if (screenSize == null) {
	    // Set a default screen size if not retrieved
	    screenSize = new Rectangle(0, 0);
	}
    }

    /**
     * Returns a dimension with variable width and height according to screen
     * resolution
     * 
     * @param screenWidthFactor
     * @param screenHeightFactor
     * @return
     */
    public static Dimension getVariableDimension(final float screenWidthFactor,
	    final float screenHeightFactor) {
	return new Dimension(getWidthForResolution(screenWidthFactor),
		getHeightForResolution(screenHeightFactor));
    }

    /**
     * Returns a dimension with variable width and height according to screen
     * resolution
     * 
     * @param screenWidthFactor
     * @param minimumWidth
     * @param screenHeightFactor
     * @param minimumHeight
     * @return
     */
    public static Dimension getVariableDimension(final float screenWidthFactor,
	    final int minimumWidth, final float screenHeightFactor,
	    final int minimumHeight) {
	return new Dimension(getWidthForResolution(screenWidthFactor,
		minimumWidth), getHeightForResolution(screenHeightFactor,
		minimumHeight));
    }

    /**
     * Returns a dimension with variable width according to screen resolution
     * 
     * @param screenWidthFactor
     * @param minimumWidth
     * @param height
     * @return
     */
    public static Dimension getVariableWidthDimension(
	    final float screenWidthFactor, final int minimumWidth,
	    final int height) {
	return new Dimension(getWidthForResolution(screenWidthFactor,
		minimumWidth), height);
    }

    /**
     * Returns a dimension with variable width according to screen resolution
     * 
     * @param screenWidthFactor
     * @param height
     * @return
     */
    public static Dimension getVariableWidthDimension(
	    final float screenWidthFactor, final int height) {
	return new Dimension(getWidthForResolution(screenWidthFactor), height);
    }

    /**
     * Returns a dimension with variable height according to screen resolution
     * 
     * @param width
     * @param screenHeightFactor
     * @param minimumHeight
     * @return
     */
    public static Dimension getVariableHeightDimension(final int width,
	    final float screenHeightFactor, final int minimumHeight) {
	return new Dimension(width, getHeightForResolution(screenHeightFactor,
		minimumHeight));
    }

    /**
     * Returns a proportional width according to given factor for the current
     * screen resolution or the given minimum width if calculated value is lower
     * 
     * @param screenWidthFactor
     * @param minimumWidth
     * @return
     */
    private static int getWidthForResolution(final float screenWidthFactor,
	    final int minimumWidth) {
	return Math.max(getWidthForResolution(screenWidthFactor), minimumWidth);
    }

    /**
     * Returns a proportional width according to given factor for the current
     * screen resolution.
     * 
     * @param screenWidthFactor
     * @return the component width for resolution
     */
    private static int getWidthForResolution(final float screenWidthFactor) {
	return (int) (screenSize.width * screenWidthFactor);
    }

    /**
     * Returns a proportional height according to given screen height factor for
     * the current screen resolution.
     * 
     * @param screenHeight
     *            the screen height
     * @param desiredHeight
     *            the desired height
     * 
     * @return the component height for resolution
     */
    private static int getHeightForResolution(final float screenHeightFactor) {
	return (int) (screenSize.height * screenHeightFactor);
    }

    /**
     * Returns a proportional height according to given screen height factor for
     * the current screen resolution or the given minimum height if calculated
     * value is lower
     * 
     * @param screenHeightFactor
     * @param minimumHeight
     * @return
     */
    private static int getHeightForResolution(final float screenHeightFactor,
	    final int minimumHeight) {
	return Math.max(getHeightForResolution(screenHeightFactor),
		minimumHeight);
    }
}
