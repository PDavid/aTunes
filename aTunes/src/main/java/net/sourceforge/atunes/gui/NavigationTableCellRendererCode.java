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

package net.sourceforge.atunes.gui;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IColumnSet;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IStateNavigation;
import net.sourceforge.atunes.model.ITableCellRendererCode;
import net.sourceforge.atunes.model.ITagHandler;

/**
 * Renderer for navigation table, includes highlight of incomplete tags
 * 
 * @author alex
 * 
 */
public class NavigationTableCellRendererCode extends
		AbstractTableCellRendererCode<JComponent, Object> {

	private ITableCellRendererCode<JComponent, Object> renderer;

	private INavigationHandler navigationHandler;

	private boolean isSubstance;

	private ITagHandler tagHandler;

	private IStateNavigation stateNavigation;

	private IColumnSet navigatorColumnSet;

	/**
	 * @param navigatorColumnSet
	 */
	public void setNavigatorColumnSet(final IColumnSet navigatorColumnSet) {
		this.navigatorColumnSet = navigatorColumnSet;
	}

	/**
	 * @param renderer
	 */
	public void setRenderer(
			final ITableCellRendererCode<JComponent, Object> renderer) {
		this.renderer = renderer;
	}

	/**
	 * @param navigationHandler
	 */
	public void setNavigationHandler(final INavigationHandler navigationHandler) {
		this.navigationHandler = navigationHandler;
	}

	/**
	 * @param tagHandler
	 */
	public void setTagHandler(final ITagHandler tagHandler) {
		this.tagHandler = tagHandler;
	}

	/**
	 * @param stateNavigation
	 */
	public void setStateNavigation(final IStateNavigation stateNavigation) {
		this.stateNavigation = stateNavigation;
	}

	/**
	 * Initialization needed
	 */
	public void initialize() {
		this.isSubstance = getLookAndFeel().getName().equalsIgnoreCase(
				"Substance");
	}

	@Override
	public JComponent getComponent(final JComponent superComponent,
			final JTable t, final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {
		// Get result from super renderer
		JComponent c = this.renderer.getComponent(superComponent, t, value,
				isSelected, hasFocus, row, column);

		// Apply component orientation
		IColumnSet columnSet = this.navigatorColumnSet;
		if (!this.navigationHandler.getCurrentView()
				.isUseDefaultNavigatorColumnSet()) {
			columnSet = this.navigationHandler.getCurrentView()
					.getCustomColumnSet();
		}

		((JLabel) c).setHorizontalAlignment(columnSet.getColumn(
				columnSet.getColumnId(column)).getAlignment());

		// Check incomplete tags if necessary
		if (this.stateNavigation.isHighlightIncompleteTagElements()) {
			IAudioObject audioObject = this.navigationHandler
					.getAudioObjectInNavigationTable(row);
			if (this.tagHandler.hasIncompleteTags(audioObject)) {
				((JLabel) c).setForeground(Color.red);
			} else {
				// Only Substance doesn't need this workaround
				if (!this.isSubstance) {
					((JLabel) c).setForeground(c.getForeground());
					if (isSelected) {
						((JLabel) c).setForeground(UIManager
								.getColor("List.selectionForeground"));
					} else {
						((JLabel) c).setForeground(UIManager
								.getColor("List.foreground"));
					}
				}
			}
		}
		return c;
	}
}