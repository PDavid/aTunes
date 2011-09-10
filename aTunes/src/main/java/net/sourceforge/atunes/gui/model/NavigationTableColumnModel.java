/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
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

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableColumn;

import net.sourceforge.atunes.gui.lookandfeel.AbstractTableCellRendererCode;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.gui.lookandfeel.substance.SubstanceLookAndFeel;
import net.sourceforge.atunes.kernel.modules.navigator.NavigationHandler;
import net.sourceforge.atunes.kernel.modules.tags.IncompleteTagsChecker;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IState;

public final class NavigationTableColumnModel extends AbstractCommonColumnModel {

    private static final long serialVersionUID = 1071222881574684439L;

    private IState state;
    
    public NavigationTableColumnModel(JTable table, IState state) {
        super(table);
        this.state = state;
        enableColumnChange(true);
    }

    @Override
    public void addColumn(TableColumn aColumn) {
        updateColumnSettings(aColumn);
        super.addColumn(aColumn);
    }

    @Override
    protected void reapplyFilter() {
        NavigationHandler.getInstance().updateTableContent(NavigationHandler.getInstance().getCurrentView().getTree());
    }

    @Override
    public AbstractTableCellRendererCode getRendererCodeFor(Class<?> clazz) {
        AbstractTableCellRendererCode renderer = super.getRendererCodeFor(clazz);
        return new NavigationTableCellRendererCode(renderer, state);
    }

    private static class NavigationTableCellRendererCode extends AbstractTableCellRendererCode {

        private AbstractTableCellRendererCode renderer;

        private IState state;
        
        public NavigationTableCellRendererCode(AbstractTableCellRendererCode renderer, IState state) {
            this.renderer = renderer;
            this.state = state;
        }

        @Override
        public JComponent getComponent(JComponent superComponent, JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            // Get result from super renderer
        	JComponent c = renderer.getComponent(superComponent, t, value, isSelected, hasFocus, row, column);
            // Check incomplete tags if necessary
            if (state.isHighlightIncompleteTagElements()) {
                IAudioObject audioObject = NavigationHandler.getInstance().getAudioObjectInNavigationTable(row);
                if (IncompleteTagsChecker.hasIncompleteTags(audioObject, state)) {
                    ((JLabel) c).setForeground(Color.red);
                } else {
                	// Only Substance doesn't need this workaround
                	if (!LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getClass().equals(SubstanceLookAndFeel.class)) {
                		((JLabel) c).setForeground(c.getForeground());
                		if( isSelected ) {
                			((JLabel) c).setForeground(UIManager.getColor("List.selectionForeground"));
                		}
                		else {
                			((JLabel) c).setForeground(UIManager.getColor("List.foreground"));
                		}
                	}
                }    
            }
            return c;
        }
    }

}
