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

import javax.swing.ImageIcon;
import javax.swing.JTable;

import net.sourceforge.atunes.model.AudioObjectProperty;
import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.PlaybackState;

/**
 * Common renderers for columns
 * 
 * @author fleax
 * 
 */
public final class ColumnRenderers {

	private ColumnRenderers() {

	}

	/**
	 * Add renderers to table
	 * 
	 * USE ColumnDecorator
	 * 
	 * @param jtable
	 * @param model
	 * @param lookAndFeel
	 */
	@Deprecated
	public static void addRenderers(final JTable jtable,
			final AbstractCommonColumnModel model,
			final ILookAndFeel lookAndFeel) {

		// Integer renderer
		jtable.setDefaultRenderer(Integer.class, lookAndFeel
				.getTableCellRenderer(model.getRendererCodeFor(Integer.class)));

		// ImageIcon renderer
		jtable.setDefaultRenderer(ImageIcon.class,
				lookAndFeel.getTableCellRenderer(model
						.getRendererCodeFor(ImageIcon.class)));

		// STRING renderer
		jtable.setDefaultRenderer(String.class, lookAndFeel
				.getTableCellRenderer(model.getRendererCodeFor(String.class)));

		// TextAndIcon renderer
		jtable.setDefaultRenderer(TextAndIcon.class, lookAndFeel
				.getTableCellRenderer(model
						.getRendererCodeFor(TextAndIcon.class)));

		// Property renderer
		jtable.setDefaultRenderer(AudioObjectProperty.class, lookAndFeel
				.getTableCellRenderer(model
						.getRendererCodeFor(AudioObjectProperty.class)));

		// ColorMutableImageIcon
		jtable.setDefaultRenderer(IColorMutableImageIcon.class, lookAndFeel
				.getTableCellRenderer(model
						.getRendererCodeFor(IColorMutableImageIcon.class)));

		// PlayState renderer
		jtable.setDefaultRenderer(PlaybackState.class, lookAndFeel
				.getTableCellRenderer(model
						.getRendererCodeFor(PlaybackState.class)));

		// Set header renderer
		jtable.getTableHeader().setDefaultRenderer(
				lookAndFeel.getTableHeaderCellRenderer(model));
	}
}
