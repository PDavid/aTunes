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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import net.sourceforge.atunes.gui.views.controls.CustomStatusBar;

/**
 * A look and feel for the application
 * 
 * @author alex
 * 
 */
public interface ILookAndFeel {

	/**
	 * Returns name of the look and feel
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Returns description to show to user
	 * 
	 * @return
	 */
	public String getDescription();

	/**
	 * Steps needed to initialize look and feel
	 * 
	 * @param beanFactory
	 */
	public void initializeLookAndFeel(IBeanFactory beanFactory);

	/**
	 * Steps needed to set up look and feel
	 * 
	 * @param skin
	 */
	public void setLookAndFeel(String skin);

	/**
	 * Returns default skin (if supported)
	 * 
	 * @return
	 */
	public String getDefaultSkin();

	/**
	 * Returns list of available skins for this look and feel (if supported)
	 * 
	 * @return
	 */
	public List<String> getSkins();

	/**
	 * Returns if dialogs must be undecorated
	 * 
	 * @return
	 */
	public boolean isDialogUndecorated();

	/**
	 * Returns a new TreeCellRenderer executing given code (default
	 * implementation)
	 * 
	 * @param code
	 * @return
	 */
	public TreeCellRenderer getTreeCellRenderer(
			final ITreeCellRendererCode<?, ?> code);

	/**
	 * Returns a new TableCellRenderer executing given code (default
	 * implementation)
	 * 
	 * @param code
	 * @return
	 */
	public TableCellRenderer getTableCellRenderer(
			ITableCellRendererCode<?, ?> code);

	/**
	 * Returns a new TableHeaderCellRenderer for column model
	 * 
	 * @param model
	 * @return
	 */
	public TableCellRenderer getTableHeaderCellRenderer(IColumnModel model);

	/**
	 * Returns a new ListCellRendeder executing given code (default
	 * implementation)
	 * 
	 * @param code
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public ListCellRenderer getListCellRenderer(
			final IListCellRendererCode<?, ?> code);

	/**
	 * Returns if custom combo box renderers are supported
	 * 
	 * @return
	 */
	public boolean customComboBoxRenderersSupported();

	/**
	 * Puts client properties in components
	 * 
	 * @param c
	 */
	public void putClientProperties(JComponent c);

	/**
	 * Returns if look and feel supports custom player controls (not by default)
	 * 
	 * @return
	 */
	public boolean isCustomPlayerControlsSupported();

	/**
	 * Returns shadow border used by look and feel (none by default)
	 * 
	 * @return
	 */
	public Border getShadowBorder();

	/**
	 * Initializes needed properties for fonts depending on given base font and
	 * look and feel (none by default)
	 * 
	 * @param baseFont
	 */
	public void initializeFonts(Font baseFont);

	/**
	 * @return <code>true</code> if this look and feel implements skins or
	 *         themes
	 */
	public boolean allowsSkins();

	/**
	 * Returns paint to be used with certain controls (player controls)
	 * 
	 * @return
	 */
	public Color getPaintForSpecialControls();

	/**
	 * Returns paint to be used with certain controls (player controls) when
	 * disabled
	 * 
	 * @return
	 */
	public Color getPaintForDisabledSpecialControls();

	/**
	 * Returns paint to be used to draw a color mutable icon in given component
	 * 
	 * @param c
	 * @param isSelected
	 * @return
	 */
	public Color getPaintForColorMutableIcon(Component c, boolean isSelected);

	/**
	 * Returns size to be used with PopUpButton
	 * 
	 * @return
	 */
	public Dimension getPopUpButtonSize();

	/**
	 * Returns default font
	 * 
	 * @return
	 */
	public Font getDefaultFont();

	/**
	 * Returns true if look and feel support custom font selection
	 * 
	 * @return
	 */
	public boolean supportsCustomFontSettings();

	/**
	 * @return the aboutBigFont
	 */
	public Font getAboutBigFont();

	/**
	 * @return the playListFont
	 */
	public Font getPlayListFont();

	/**
	 * @return the playListSelectedItemFont
	 */
	public Font getPlayListSelectedItemFont();

	/**
	 * @return the contextInformationBigFont
	 */
	public Font getContextInformationBigFont();

	/**
	 * @return the propertiesDialogBigFont
	 */
	public Font getPropertiesDialogBigFont();

	/**
	 * @return the osdLine1Font
	 */
	public Font getOsdLine1Font();

	/**
	 * @return the osdLine2Font
	 */
	public Font getOsdLine2Font();

	/**
	 * @return the osdLine3Font
	 */
	public Font getOsdLine3Font();

	/**
	 * @return the fullScreenLine1Font
	 */
	public Font getFullScreenLine1Font();

	/**
	 * @return font for second line of full scren
	 */
	public Font getFullScreenLine2Font();

	/**
	 * Returns instance of table with special look and feel settings
	 * 
	 * @return
	 */
	public JTable getTable();

	/**
	 * Adds special look and feel settings to an already created JTable
	 * 
	 * @param table
	 */
	public void decorateTable(JTable table);

	/**
	 * Returns scroll pane to show a table with special look and feel settings
	 * 
	 * Only call this method from ControlsBuilder
	 * 
	 * @param table
	 * @return
	 */
	public JScrollPane getTableScrollPane(JTable table);

	/**
	 * Returns scroll pane to show a tree with special look and feel settings
	 * 
	 * Only call this method from ControlsBuilder
	 * 
	 * @param tree
	 * @return
	 */
	public JScrollPane getTreeScrollPane(JTree tree);

	/**
	 * Returns scroll pane to show a list with special look and feel settings
	 * 
	 * Only call this method from ControlsBuilder
	 * 
	 * @param list
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public JScrollPane getListScrollPane(JList list);

	/**
	 * Returns scroll pane with special look and feel settings
	 * 
	 * Only call this method from ControlsBuilder
	 * 
	 * @param component
	 * @return
	 */
	public JScrollPane getScrollPane(Component component);

	/**
	 * Returns instance of list with special look and feel settings
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public JList getList();

	/**
	 * Sets base font
	 * 
	 * @param f
	 */
	public void setBaseFont(Font f);

	/**
	 * Sets os manager
	 * 
	 * @param osManager
	 */
	public void setOsManager(IOSManager osManager);

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(IBeanFactory beanFactory);

	/**
	 * Customize split pane
	 * 
	 * @param customSplitPane
	 */
	public void customizeSplitPane(JSplitPane customSplitPane);

	/**
	 * Customize status bar
	 * 
	 * @param customStatusBar
	 */
	public void customizeStatusBar(CustomStatusBar customStatusBar);

	/**
	 * @return font suggested by look and feel
	 */
	public Font getSuggestedFont();

	/**
	 * Hides node icons in a tree
	 * 
	 * @param tree
	 */
	public void hideNodeIcons(JTree tree);

}