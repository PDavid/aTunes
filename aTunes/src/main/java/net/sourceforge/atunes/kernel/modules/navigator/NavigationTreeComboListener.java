package net.sourceforge.atunes.kernel.modules.navigator;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import net.sourceforge.atunes.model.INavigationHandler;

final class NavigationTreeComboListener implements ItemListener {

	private INavigationHandler navigationHandler;
	
	/**
	 * @param navigationHandler
	 */
	NavigationTreeComboListener(INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			this.navigationHandler.setNavigationView(e.getItem().getClass().getName());
		}
	}
}