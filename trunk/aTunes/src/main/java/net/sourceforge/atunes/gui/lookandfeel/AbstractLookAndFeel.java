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

package net.sourceforge.atunes.gui.lookandfeel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import net.sourceforge.atunes.gui.views.controls.CustomStatusBar;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IColumnModel;
import net.sourceforge.atunes.model.IListCellRendererCode;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.ITableCellRendererCode;
import net.sourceforge.atunes.model.ITreeCellRendererCode;

/**
 * Defines look and feel
 * 
 * @author alex
 * 
 */

public abstract class AbstractLookAndFeel implements ILookAndFeel {

	/**
	 * Base font
	 */
	private Font baseFont;

	private IOSManager osManager;

	private IBeanFactory beanFactory;

	/**
	 * @return
	 */
	public IBeanFactory getBeanFactory() {
		return this.beanFactory;
	}

	/**
	 * @return
	 */
	public IOSManager getOsManager() {
		return this.osManager;
	}

	@Override
	public abstract String getName();

	@Override
	public abstract String getDescription();

	@Override
	public abstract void setLookAndFeel(String skin);

	@Override
	public abstract String getDefaultSkin();

	@Override
	public abstract List<String> getSkins();

	@Override
	public boolean isDialogUndecorated() {
		return false;
	}

	@Override
	public TreeCellRenderer getTreeCellRenderer(
			final ITreeCellRendererCode<?, ?> code) {
		return new LookAndFeelTreeCellRenderer(code);
	}

	@Override
	public TableCellRenderer getTableCellRenderer(
			final ITableCellRendererCode<?, ?> code) {
		return new LookAndFeelTableCellRenderer(code);
	}

	@Override
	public TableCellRenderer getTableHeaderCellRenderer(final IColumnModel model) {
		LookAndFeelTableHeaderCellRenderer renderer = this.beanFactory
				.getBean(LookAndFeelTableHeaderCellRenderer.class);
		renderer.bindToModel(model);
		return renderer;
	}

	@Override
	public ListCellRenderer getListCellRenderer(
			final IListCellRendererCode<?, ?> code) {
		return new LookAndFeelListCellRenderer(code);
	}

	@Override
	public boolean customComboBoxRenderersSupported() {
		return true;
	}

	@Override
	public void putClientProperties(final JComponent c) {
		// No properties by default
	}

	@Override
	public boolean isCustomPlayerControlsSupported() {
		return false;
	}

	@Override
	public Border getShadowBorder() {
		return null;
	}

	@Override
	public void initializeFonts(final Font baseFont) {
		this.baseFont = baseFont;
		setUIFont(new FontUIResource(baseFont));
	}

	/**
	 * Changes all components' font to a given one
	 * 
	 * @param f
	 */
	protected void setUIFont(final FontUIResource f) {
		Enumeration<Object> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof FontUIResource) {
				UIManager.put(key, f);
			}
		}
	}

	@Override
	public boolean allowsSkins() {
		return getSkins() != null && !getSkins().isEmpty();
	}

	@Override
	public Color getPaintForSpecialControls() {
		return UIManager.getColor("Label.foreground");
	}

	@Override
	public Color getPaintForDisabledSpecialControls() {
		return UIManager.getColor("Label.foreground");
	}

	@Override
	public Color getPaintForColorMutableIcon(final Component c,
			final boolean isSelected) {
		return isSelected ? UIManager.getColor("Tree.selectionForeground")
				: UIManager.getColor("Label.foreground");
	}

	@Override
	public Dimension getPopUpButtonSize() {
		return new Dimension(20, 20);
	}

	/********************************************************* FONTS *******************************************************/

	@Override
	public Font getDefaultFont() {
		return this.baseFont;
	}

	@Override
	public abstract boolean supportsCustomFontSettings();

	@Override
	public final Font getAboutBigFont() {
		return getDefaultFont().deriveFont(getDefaultFont().getSize() + 8f);
	}

	@Override
	public Font getPlayListFont() {
		return getDefaultFont();
	}

	@Override
	public Font getPlayListSelectedItemFont() {
		if (getPlayListFont() != null) {
			return getPlayListFont().deriveFont(Font.BOLD);
		}
		return getDefaultFont();
	}

	@Override
	public final Font getContextInformationBigFont() {
		return getDefaultFont().deriveFont(getDefaultFont().getSize() + 8f);
	}

	@Override
	public final Font getPropertiesDialogBigFont() {
		return getDefaultFont().deriveFont(getDefaultFont().getSize() + 4f);
	}

	@Override
	public final Font getOsdLine1Font() {
		return getDefaultFont().deriveFont(Font.BOLD,
				getDefaultFont().getSize() + 4f);
	}

	@Override
	public final Font getOsdLine2Font() {
		return getDefaultFont().deriveFont(getDefaultFont().getSize() + 2f);
	}

	@Override
	public final Font getOsdLine3Font() {
		return getOsdLine2Font();
	}

	@Override
	public final Font getFullScreenLine1Font() {
		return getDefaultFont().deriveFont(getDefaultFont().getSize() + 35f);
	}

	@Override
	public final Font getFullScreenLine2Font() {
		return getDefaultFont().deriveFont(getDefaultFont().getSize() + 20f);
	}

	@Override
	public JTable getTable() {
		JTable table = new JTable();
		table.setShowGrid(false);
		return table;
	}

	@Override
	public void decorateTable(final JTable table) {
		table.setShowGrid(false);
	}

	@Override
	public JScrollPane getTableScrollPane(final JTable table) {
		return new JScrollPane(table);
	}

	@Override
	public JScrollPane getTreeScrollPane(final JTree tree) {
		return new JScrollPane(tree);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public JList getList() {
		return new JList();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public JScrollPane getListScrollPane(final JList list) {
		return new JScrollPane(list);
	}

	@Override
	public JScrollPane getScrollPane(final Component component) {
		return new JScrollPane(component);
	}

	/**
	 * @param osManager
	 *            the osManager to set
	 */
	@Override
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	@Override
	public void setBaseFont(final Font f) {
		this.baseFont = f;
	}

	@Override
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	@Override
	public void customizeSplitPane(final JSplitPane splitPane) {
	}

	@Override
	public void customizeStatusBar(final CustomStatusBar customStatusBar) {
	}

	@Override
	public void hideNodeIcons(JTree tree) {
		((BasicTreeUI) tree.getUI()).setCollapsedIcon(null);
		((BasicTreeUI) tree.getUI()).setExpandedIcon(null);
	}

}
