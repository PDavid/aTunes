package net.sourceforge.atunes.model;


/**
 * Responsible of showing controls or icons in system tray if available
 * @author alex
 *
 */
public interface ISystemTrayHandler extends IHandler {

	/**
	 * Sets the playing.
	 * 
	 * @param playing
	 *            the new playing
	 */
	public void setPlaying(boolean playing);

	/**
	 * Sets the tray tool tip.
	 * 
	 * @param msg
	 *            the new tray tool tip
	 */
	public void setTrayToolTip(String msg);

}