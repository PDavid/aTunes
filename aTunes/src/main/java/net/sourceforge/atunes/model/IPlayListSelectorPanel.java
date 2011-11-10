package net.sourceforge.atunes.model;

import java.awt.Component;

import javax.swing.JComboBox;

import net.sourceforge.atunes.gui.views.controls.PopUpButton;

/**
 * A panel to select play lists
 * @author alex
 *
 */
public interface IPlayListSelectorPanel {

	/**
	 * @return the playListCombo
	 */
	public JComboBox getPlayListCombo();

	/**
	 * @return the options
	 */
	public PopUpButton getOptions();

	/**
	 * Returns Swing component
	 * @return
	 */
	public Component getSwingComponent();

}