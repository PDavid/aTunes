package net.sourceforge.atunes.model;

/**
 * A dialog to enter information
 * @author alex
 *
 */
public interface IInputDialog {

	/**
	 * Sets title
	 * @param title
	 */
	public void setTitle(String title);
	
	/**
	 * Gets the result.
	 * 
	 * @return the result
	 */
	public String getResult();

	/**
	 * Show.
	 * 
	 * @param text
	 *            the text
	 */
	public void showDialog(String text);

}