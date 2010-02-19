/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.gui.model;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import net.sourceforge.atunes.gui.lookandfeel.TableCellRendererCode;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.repository.tags.IncompleteTagsChecker;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.model.AudioObject;

public final class NavigationTableColumnModel extends CommonColumnModel {

    private static final long serialVersionUID = 1071222881574684439L;

    public NavigationTableColumnModel(JTable table) {
        super(table);
        enableColumnChange(true);
    }

    @Override
    public void addColumn(TableColumn aColumn) {
        updateColumnSettings(aColumn);
        updateColumnHeader(aColumn);
        super.addColumn(aColumn);
    }

    @Override
    protected void reapplyFilter() {
        ControllerProxy.getInstance().getNavigationController().updateTableContent(NavigationHandler.getInstance().getCurrentView().getTree());
    }

	@Override
	public TableCellRendererCode getRendererCodeFor(Class<?> clazz) {
		TableCellRendererCode renderer = super.getRendererCodeFor(clazz);
		return new NavigationTableCellRendererCode(renderer);
	}
	
	private static class NavigationTableCellRendererCode extends TableCellRendererCode {
		
		private TableCellRendererCode renderer;
		
		public NavigationTableCellRendererCode(TableCellRendererCode renderer) {
			this.renderer = renderer;
		}
		
		@Override
		public Component getComponent(Component superComponent, JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			// Get result from super renderer
			Component c = renderer.getComponent(superComponent, t, value, isSelected, hasFocus, row, column);
			
			// Check incomplete tags if necessary
			if (ApplicationState.getInstance().isHighlightIncompleteTagElements()) {
				AudioObject audioObject = ControllerProxy.getInstance().getNavigationController().getAudioObjectInNavigationTable(row);
				if (IncompleteTagsChecker.hasIncompleteTags(audioObject)) {
					((JLabel)c).setForeground(Color.red);
				}
			}
			
			return c;
		}
	}
	
    
}
