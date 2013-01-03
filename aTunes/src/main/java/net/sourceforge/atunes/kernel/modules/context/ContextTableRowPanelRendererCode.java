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

package net.sourceforge.atunes.kernel.modules.context;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JTable;

import net.sourceforge.atunes.gui.AbstractTableCellRendererCode;
import net.sourceforge.atunes.model.IControlsBuilder;

/**
 * Renderer for context table rows
 * 
 * @author alex
 * 
 * @param <T>
 */
public abstract class ContextTableRowPanelRendererCode<T> extends
		AbstractTableCellRendererCode<JComponent, T> {

	private final Class<?> clazz;

	private ContextTable table;

	private final ContextTableRowPanelFactory<T> factory;

	private String cacheKeyControl;

	private final Map<T, ContextTableRowPanel<T>> cachedPanels = new HashMap<T, ContextTableRowPanel<T>>();

	private IControlsBuilder controlsBuilder;

	/**
	 * @param controlsBuilder
	 */
	public void setControlsBuilder(final IControlsBuilder controlsBuilder) {
		this.controlsBuilder = controlsBuilder;
	}

	/**
	 * @param lookAndFeel
	 */
	@SuppressWarnings("unchecked")
	public ContextTableRowPanelRendererCode() {
		this.clazz = (Class<T>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
		this.factory = new ContextTableRowPanelFactory<T>();
	}

	@Override
	public JComponent getComponent(final JComponent superComponent,
			final JTable t, final T value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {
		if (value != null) {
			if (this.cacheKeyControl != null
					&& !this.cacheKeyControl.equals(getCacheKeyControl(value))) {
				// Clean cached panels when similar artist changes
				this.cachedPanels.clear();
			}

			// Create if necessary
			if (!this.cachedPanels.containsKey(value)) {
				this.cachedPanels
						.put(value, createPanel(superComponent, value));
			}

			// Get panel
			ContextTableRowPanel<T> panel = this.cachedPanels.get(value);

			// Update panel
			panel.setColors(superComponent.getBackground(),
					superComponent.getForeground());
			panel.setFocus(hasFocus);

			return panel;
		}
		return superComponent;
	}

	/**
	 * Binds as renderer and editor to table
	 * 
	 * @param table
	 */
	public void bind(final ContextTable table) {
		this.table = table;
		this.table.setDefaultRenderer(this.clazz, getLookAndFeel()
				.getTableCellRenderer(this));
	}

	/**
	 * @return
	 */
	protected ContextTable getTable() {
		return this.table;
	}

	protected final ContextTableRowPanel<T> getPanelForTableRenderer(
			final ImageIcon image, final String text, final int imageMaxWidth) {

		return this.factory.getPanelForTableRenderer(this.table, image, text,
				imageMaxWidth, this.controlsBuilder);
	}

	/**
	 * @param object
	 * @return cache key control value of this object
	 */
	public abstract String getCacheKeyControl(T object);

	/**
	 * @param superComponent
	 * @param value
	 * @return
	 */
	public abstract ContextTableRowPanel<T> createPanel(
			final JComponent superComponent, final T value);

}
