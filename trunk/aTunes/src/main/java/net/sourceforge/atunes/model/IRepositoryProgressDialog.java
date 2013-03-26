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

import java.awt.Image;

/**
 * Shows progress about repository read
 * 
 * @author alex
 * 
 */
public interface IRepositoryProgressDialog extends IDialog {

    /**
     * Sets current task being performed
     * 
     * @param task
     */
    public void setCurrentTask(String task);

    /**
     * Sets current folder being read
     * 
     * @param folder
     */
    public void setCurrentFolder(String folder);

    /**
     * Sets progress bar indeterminate
     * 
     * @param indeterminate
     */
    public void setProgressBarIndeterminate(boolean indeterminate);

    /**
     * Sets progress bar value
     * 
     * @param value
     */
    public void setProgressBarValue(int value);

    /**
     * Sets total number of files to load
     * 
     * @param max
     */
    public void setTotalFiles(int max);

    /**
     * Sets progress text
     * 
     * @param text
     */
    public void setProgressText(String text);

    /**
     * Sets remaining time
     * 
     * @param text
     */
    public void setRemainingTime(String text);

    /**
     * Enable buttons
     * 
     * @param enabled
     * 
     */
    public void setButtonsEnabled(boolean enabled);

    /**
     * Show buttons
     * 
     * @param visible
     * 
     */
    public void setButtonsVisible(boolean visible);

    /**
     * Sets image to show
     * 
     * @param image
     */
    public void setImage(Image image);

    /**
     * Returns if dialog is visible
     * 
     * @return
     */
    public boolean isVisible();
}