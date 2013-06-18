/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
 *
 * See http://www.atunes.org/wiki/index.php?title=Contributing for information about contributors
 *
 * http://www.atunes.org
 * http://sourceforge.net/projects/atunes
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package net.sourceforge.atunes.model;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;

import javax.swing.JFileChooser;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import net.sourceforge.atunes.gui.views.controls.RemoteImage;

/**
 * Interface to build UI components
 * 
 * @author alex
 * 
 */
public interface IControlsBuilder {

	/**
	 * @return table
	 */
	JTable createTable();

	/**
	 * @return text area
	 */
	JTextArea createTextArea();

	/**
	 * @return text field
	 */
	JTextField createTextField();

	/**
	 * @param alignJustified
	 * @return text pane
	 */
	JTextPane createTextPane(Integer alignJustified);

	/**
	 * @param text
	 * @return text pane with no edition capabilities
	 */
	JTextPane createReadOnlyTextPane(String text);

	/**
	 * @return play button
	 */
	IControlButton createPlayPauseButton();

	/**
	 * @return previous button
	 */
	IControlButton createPreviousButton();

	/**
	 * @return next button
	 */
	IControlButton createNextButton();

	/**
	 * @return stop button
	 */
	IControlButton createStopButton();

	/**
	 * Applies locale specific component orientation to containers.
	 * 
	 * @param containers
	 *            One or more containers
	 */
	void applyComponentOrientation(final Container... containers);

	/**
	 * Returns the component orientation.
	 * 
	 * @return The component orientation
	 */
	ComponentOrientation getComponentOrientation();

	/**
	 * @param menuPosition
	 * @return pop up button
	 */
	IPopUpButton createPopUpButton(int menuPosition);

	/**
	 * @param type
	 * @return split pane
	 */
	JSplitPane createSplitPane(int type);

	/**
	 * @return divider size of a split pane
	 */
	int getSplitPaneDividerSize();

	/**
	 * @return component orientation
	 */
	int getComponentOrientationAsTextStyleConstant();

	/**
	 * Creates a popup menu for column set of table
	 * 
	 * @param table
	 * @param columnModel
	 * @param tableModel
	 * @return
	 */
	IColumnSetPopupMenu createColumnSetPopupMenu(JTable table,
			IColumnModel columnModel, IColumnSetTableModel tableModel);

	/**
	 * @return component orientation as swing constant
	 */
	int getComponentOrientationAsSwingConstant();

	/**
	 * @return a new button panel
	 */
	IButtonPanel createButtonPanel();

	/**
	 * @return file chooser
	 */
	JFileChooser getFileChooser();

	/**
	 * @param path
	 * @param name
	 * @return file chooser
	 */
	JFileChooser getFileChooser(String path, String name);

	/**
	 * Returns scroll pane with special look and feel settings
	 * 
	 * @param component
	 * @return
	 */
	JScrollPane createScrollPane(Component component);

	/**
	 * @param text
	 * @param url
	 * @return new url label
	 */
	IUrlLabel getUrlLabel(String text, String url);

	/**
	 * @return new url label
	 */
	IUrlLabel getUrlLabel();

	/**
	 * Returns a new TreeCellRenderer executing given code (default
	 * implementation)
	 * 
	 * @param code
	 * @return
	 */
	TreeCellRenderer getTreeCellRenderer(final ITreeCellRendererCode<?, ?> code);

	/**
	 * @return a new remote image
	 */
	RemoteImage createRemoteImage();

	/**
	 * @return indeterminate progress bar
	 */
	JProgressBar createIndeterminateProgressBar();

	/**
	 * Creates tree with or without node icons
	 * 
	 * @param nodeIcons
	 * @return treee
	 */
	JTree createTree(boolean nodeIcons);
}
