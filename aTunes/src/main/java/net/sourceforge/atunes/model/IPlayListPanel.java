package net.sourceforge.atunes.model;

import java.awt.Dimension;

import javax.swing.JComponent;

import net.sourceforge.atunes.kernel.modules.draganddrop.PlayListTableTransferHandler;

/**
 * A panel containing play list
 * @author alex
 *
 */
public interface IPlayListPanel {

	/**
	 * Gets the play list table.
	 * 
	 * @return the play list table
	 */
	public IPlayListTable getPlayListTable();

	/**
	 * Gets the play list tab panel.
	 * 
	 * @return the playListTabPanel
	 */
	public IPlayListSelectorPanel getPlayListTabPanel();

	/**
	 * @param playListSelectorPanel
	 */
	public void setPlayListSelectorPanel(
			IPlayListSelectorPanel playListSelectorPanel);

	/**
	 * Prepares play list for drag and drop operations
	 * @param playListTableTransferHandler
	 */
	public void enableDragAndDrop(
			PlayListTableTransferHandler playListTableTransferHandler);

	/**
	 * @param playListPanelMinimumSize
	 */
	public void setMinimumSize(Dimension playListPanelMinimumSize);

	/**
	 * @param playListPanelPreferredSize
	 */
	public void setPreferredSize(Dimension playListPanelPreferredSize);

	/**
	 * @param playListPanelMaximumSize
	 */
	public void setMaximumSize(Dimension playListPanelMaximumSize);

	/**
	 * Returns the underlying Swing component
	 * @return
	 */
	public JComponent getSwingComponent();

}