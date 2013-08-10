package net.sourceforge.atunes.kernel.modules.navigator;

import java.awt.Component;
import java.util.List;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import net.sourceforge.atunes.kernel.actions.CustomAbstractAction;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.ITreeNode;

/**
 * Enables or disables navigator actions given selection
 * 
 * @author alex
 * 
 */
public class NavigatorActionsStateController {

	private IPlayListHandler playListHandler;

	/**
	 * @param playListHandler
	 */
	public void setPlayListHandler(final IPlayListHandler playListHandler) {
		this.playListHandler = playListHandler;
	}

	/**
	 * Enables or disables tree popup actions
	 * 
	 * @param rootSelected
	 * @param components
	 * @param nodes
	 */
	void updateTreePopupMenuWithTreeSelection(final boolean rootSelected,
			final Component[] components, final List<ITreeNode> nodes) {
		for (Component c : components) {
			updateMenuComponent(rootSelected, nodes, c);
		}
	}

	/**
	 * Enables or disables table popup actions
	 * 
	 * @param rootSelected
	 * @param components
	 * @param selection
	 */
	void updateTablePopupMenuWithTableSelection(final boolean rootSelected,
			final Component[] components, final List<IAudioObject> selection) {
		for (Component c : components) {
			updateTableMenuComponent(rootSelected, selection, c);
		}
	}

	/**
	 * @param rootSelected
	 * @param selection
	 * @param c
	 */
	private void updateMenuComponent(final boolean rootSelected,
			final List<ITreeNode> selection, final Component c) {
		if (c != null) {
			if (c instanceof JMenu) {
				for (int i = 0; i < ((JMenu) c).getItemCount(); i++) {
					updateMenuComponent(rootSelected, selection,
							((JMenu) c).getItem(i));
				}
			} else if (c instanceof JMenuItem) {
				updateMenuItem(rootSelected, selection, (JMenuItem) c);
			}
		}
	}

	/**
	 * @param rootSelected
	 * @param selection
	 * @param c
	 */
	private void updateTableMenuComponent(final boolean rootSelected,
			final List<IAudioObject> selection, final Component c) {
		if (c != null) {
			if (c instanceof JMenu) {
				for (int i = 0; i < ((JMenu) c).getItemCount(); i++) {
					updateTableMenuComponent(rootSelected, selection,
							((JMenu) c).getItem(i));
				}
			} else if (c instanceof JMenuItem) {
				updateTableMenuItem(rootSelected, selection, (JMenuItem) c);
			}
		}
	}

	/**
	 * @param rootSelected
	 * @param selection
	 * @param menuItem
	 */
	private void updateMenuItem(final boolean rootSelected,
			final List<ITreeNode> selection, final JMenuItem menuItem) {
		Action a = menuItem.getAction();
		if (a instanceof CustomAbstractAction) {
			CustomAbstractAction customAction = (CustomAbstractAction) a;
			// Dynamic playlist and action can be enabled -> then check
			// selection, otherwise disable without more checks
			if (!this.playListHandler.getVisiblePlayList().canBeChangedByUser()
					&& !customAction.isEnabledForDynamicPlayList()) {
				customAction.setEnabled(false);
			} else {
				customAction.setEnabled(customAction
						.isEnabledForNavigationTreeSelection(rootSelected,
								selection));
			}
		}
	}

	/**
	 * @param rootSelected
	 * @param selection
	 * @param menuItem
	 */
	private void updateTableMenuItem(final boolean rootSelected,
			final List<IAudioObject> selection, final JMenuItem menuItem) {
		Action a = menuItem.getAction();
		if (a instanceof CustomAbstractAction) {
			CustomAbstractAction customAction = (CustomAbstractAction) a;
			// Dynamic playlist and action can be enabled -> then check
			// selection, otherwise disable without more checks
			if (!this.playListHandler.getVisiblePlayList().canBeChangedByUser()
					&& !customAction.isEnabledForDynamicPlayList()) {
				customAction.setEnabled(false);
			} else {
				customAction.setEnabled(customAction
						.isEnabledForNavigationTableSelection(selection));
			}
		}
	}

}
