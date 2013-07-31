package net.sourceforge.atunes.kernel.modules.playlist;

import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.IPlayList;

/**
 * Selects icon for play list
 * 
 * @author alex
 * 
 */
public class PlayListIconSelector {

	private IIconFactory playListIcon;

	private IIconFactory dynamicPlayListIcon;

	/**
	 * @param playListIcon
	 */
	public void setPlayListIcon(final IIconFactory playListIcon) {
		this.playListIcon = playListIcon;
	}

	/**
	 * @param dynamicPlayListIcon
	 */
	public void setDynamicPlayListIcon(final IIconFactory dynamicPlayListIcon) {
		this.dynamicPlayListIcon = dynamicPlayListIcon;
	}

	IColorMutableImageIcon getIcon(final IPlayList playList) {
		if (playList instanceof DynamicPlayList) {
			return this.dynamicPlayListIcon.getColorMutableIcon();
		} else {
			return this.playListIcon.getColorMutableIcon();
		}
	}
}
