package net.sourceforge.atunes.kernel.modules.playlist;

import javax.swing.ImageIcon;

/**
 * Object of play list combo
 * 
 * @author alex
 * 
 */
class PlayListComboModelObject {

	private final String name;

	private final ImageIcon icon;

	/**
	 * @param name
	 * @param icon
	 */
	PlayListComboModelObject(final String name, final ImageIcon icon) {
		this.name = name;
		this.icon = icon;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the icon
	 */
	public ImageIcon getIcon() {
		return this.icon;
	}
}