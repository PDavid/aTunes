package net.sourceforge.atunes.model;

import net.sourceforge.atunes.kernel.ApplicationLifeCycleListener;
import net.sourceforge.atunes.kernel.DeviceListener;
import net.sourceforge.atunes.kernel.FavoritesListener;
import net.sourceforge.atunes.kernel.PlayListEventListener;
import net.sourceforge.atunes.kernel.PlaybackStateListener;
import net.sourceforge.atunes.kernel.modules.state.ApplicationStateChangeListener;

public interface IHandler extends ApplicationLifeCycleListener, 
								  ApplicationStateChangeListener,
								  PlayListEventListener,
								  FavoritesListener,
								  DeviceListener,
								  PlaybackStateListener {
	
	/**
	 * Sets state
	 * @param state
	 */
	public void setState(IState state);

}
