package net.sourceforge.atunes.model;


/**
 * A dialog to ask confirmation
 * @author alex
 *
 */
public interface IConfirmationDialog {

	/**
	 * Sets parent frame
	 * @param frame
	 */
	public void setFrame(IFrame frame);

	/**
	 * Shows confirmation dialog
	 * @param message
	 * @return true if user pressed Yes, false if pressed No
	 */
	public boolean showDialog(String message);

}