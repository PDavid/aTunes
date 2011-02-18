package net.sourceforge.atunes.kernel.modules.playlist;

import java.awt.event.MouseEvent;

import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;

class TabReorderer extends MouseInputAdapter {

	private PlayListTabController controller;
	
	private JTabbedPane tabPane;
	private int draggedTabIndex;

	public TabReorderer(PlayListTabController controller, JTabbedPane pane) {
		this.controller = controller;
		this.tabPane = pane;
		draggedTabIndex = -1;
	}

	public void enableReordering() {
		tabPane.addMouseListener(this);
		tabPane.addMouseMotionListener(this);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		draggedTabIndex = tabPane.getUI().tabForCoordinate(tabPane, e.getX(), e.getY());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (draggedTabIndex == -1) {
			return;
		}

		int targetTabIndex = tabPane.getUI().tabForCoordinate(tabPane, e.getX(), e.getY());
		if (targetTabIndex != -1 && targetTabIndex != draggedTabIndex) {

			controller.switchPlayListTabs(draggedTabIndex, targetTabIndex);
			PlayListHandler.getInstance().movePlaylistToPosition(draggedTabIndex, targetTabIndex);

			((PlayListTableModel) GuiHandler.getInstance().getPlayListTable().getModel()).setVisiblePlayList(PlayListHandler.getInstance().getCurrentPlayList(true));
			controller.forceSwitchTo(targetTabIndex);

			draggedTabIndex = -1;

			SwingUtilities.invokeLater(new SwitchPlayList(targetTabIndex));
		}
	}
	
    private static class SwitchPlayList implements Runnable {

        private int targetTabIndex;

        public SwitchPlayList(int targetTabIndex) {
            this.targetTabIndex = targetTabIndex;
        }

        @Override
        public void run() {
            PlayListHandler.getInstance().switchToPlaylist(targetTabIndex);
        }
    }
}

