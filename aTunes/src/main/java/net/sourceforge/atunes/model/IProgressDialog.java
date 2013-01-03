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

package net.sourceforge.atunes.model;

import java.awt.event.ActionListener;

import javax.swing.ImageIcon;

/**
 * A dialog to show progress of a task
 * 
 * @author alex
 * 
 */
public interface IProgressDialog extends IDialog {

    /**
     * Sets title of dialog
     * 
     * @param title
     */
    @Override
    void setTitle(String title);

    /**
     * Sets the info text.
     * 
     * @param s
     *            the new info text
     */
    void setInfoText(String s);

    /**
     * Sets the progress bar value
     * 
     * @param value
     *            the new progress
     */
    void setProgressBarValue(int value);

    /**
     * Sets the progress.
     * 
     * @param value
     */
    void setCurrentProgress(long value);

    /**
     * Sets the total.
     * 
     * @param value
     */
    void setTotalProgress(long value);

    /**
     * Adds the cancel button action listener.
     * 
     * @param a
     *            the a
     */
    void addCancelButtonActionListener(ActionListener a);

    /**
     * Disables cancel button
     */
    void disableCancelButton();

    /**
     * Overrides default icon
     * 
     * @param icon
     */
    void setIcon(ImageIcon icon);
}