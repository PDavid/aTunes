package net.sourceforge.atunes.model;

import net.sourceforge.atunes.kernel.modules.updates.ApplicationVersion;

public interface IUpdateHandler extends IHandler {

	/**
	 * Returns last version of application
	 * @return
	 */
	public ApplicationVersion getLastVersion();
	
    /**
     * Used to check for new version.
     * @param alwaysInDialog
     * @param showNoNewVersion
     */
    public void checkUpdates(boolean alwaysInDialog, boolean showNoNewVersion);

}
