package net.sourceforge.atunes.model;

import java.util.List;


/**
 * Responsible of managing look and feels
 * @author alex
 *
 */
public interface ILookAndFeelManager {

	/**
	 * Sets the look and feel.
	 * 
	 * @param lookAndFeelBean
	 * @param state
	 */
	public void setLookAndFeel(LookAndFeelBean lookAndFeelBean, IState state, IOSManager osManager);

	/**
	 * Returns available look and feels
	 */
	public List<String> getAvailableLookAndFeels();

	/**
	 * Returns available skins for given look and feel
	 * 
	 * @param lookAndFeelName
	 * @return
	 */
	public List<String> getAvailableSkins(String lookAndFeelName);

	/**
	 * Returns the name of the current look and feel
	 * 
	 * @return
	 */
	public String getCurrentLookAndFeelName();

	/**
	 * Updates the user interface to use a new skin
	 * 
	 * @param selectedSkin
	 * @param state
	 * @param osManager
	 */
	public void applySkin(String selectedSkin, IState state, IOSManager osManager);

	/**
	 * @return the currentLookAndFeel
	 */
	public ILookAndFeel getCurrentLookAndFeel();

	/**
	 * Returns default skin for a given look and feel
	 * 
	 * @param lookAndFeelName
	 * @return
	 */
	public String getDefaultSkin(String lookAndFeelName);

	/**
	 * @return the defaultLookAndFeel
	 */
	public ILookAndFeel getDefaultLookAndFeel();

	/**
	 * Adds a new look and feel change listener
	 * @param listener
	 */
	public void addLookAndFeelChangeListener(ILookAndFeelChangeListener listener);

	/**
	 * Removes a look and feel change listener
	 * @param listener
	 */
	public void removeLookAndFeelChangeListener(ILookAndFeelChangeListener listener);

}