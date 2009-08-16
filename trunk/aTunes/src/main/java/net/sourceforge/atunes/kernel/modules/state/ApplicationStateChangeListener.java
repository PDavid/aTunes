package net.sourceforge.atunes.kernel.modules.state;

public interface ApplicationStateChangeListener {
	
	/**
	 * Called when any application configuration is changed
	 * @param newState
	 */
	public void applicationStateChanged(ApplicationState newState);

}
