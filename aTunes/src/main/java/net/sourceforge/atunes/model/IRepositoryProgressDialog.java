package net.sourceforge.atunes.model;

import java.awt.Image;

public interface IRepositoryProgressDialog {

	/**
	 * Sets current task being performed
	 * @param task
	 */
	public void setCurrentTask(String task);

	/**
	 * Sets current folder being read
	 * @param folder
	 */
	public void setCurrentFolder(String folder);

	/**
	 * Sets progress bar indeterminate
	 * @param indeterminate
	 */
	public void setProgressBarIndeterminate(boolean indeterminate);

	/**
	 * Sets progress bar value
	 * @param value
	 */
	public void setProgressBarValue(int value);

	/**
	 * Sets total number of files to load
	 * @param max
	 */
	public void setTotalFiles(int max);

	/**
	 * Sets progress text
	 * @param text
	 */
	public void setProgressText(String text);

	/**
	 * Sets remaining time
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
	 * Shows dialog
	 */
	public void showProgressDialog();

	/**
	 * Hides dialog
	 */
	public void hideProgressDialog();

	/**
	 * Sets image to show
	 * @param image
	 */
	public void setImage(Image image);
	
	/**
	 * Returns if dialog is visible
	 * @return
	 */
	public boolean isVisible();

}