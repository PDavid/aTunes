/*
 * aTunes 2.2.0-SNAPSHOT
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

package net.sourceforge.atunes.gui;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;

import net.sourceforge.atunes.kernel.modules.tags.IncompleteTagsChecker;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IState;

class NavigationTableCellRendererCode extends AbstractTableCellRendererCode<JComponent, Object> {

    private AbstractTableCellRendererCode renderer;

    private IState state;
    
    private INavigationHandler navigationHandler;
    
    private boolean isSubstance;
    
    public NavigationTableCellRendererCode(AbstractTableCellRendererCode<?, ?> renderer, IState state, ILookAndFeel lookAndFeel, INavigationHandler navigationHandler) {
    	super(lookAndFeel);
        this.renderer = renderer;
        this.state = state;
        this.navigationHandler = navigationHandler;
        this.isSubstance = lookAndFeel.getName().equalsIgnoreCase("Substance");
    }

    @Override
    public JComponent getComponent(JComponent superComponent, JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        // Get result from super renderer
    	JComponent c = renderer.getComponent(superComponent, t, value, isSelected, hasFocus, row, column);
        // Check incomplete tags if necessary
        if (state.isHighlightIncompleteTagElements()) {
            IAudioObject audioObject = navigationHandler.getAudioObjectInNavigationTable(row);
            if (IncompleteTagsChecker.hasIncompleteTags(audioObject, state.getHighlightIncompleteTagFoldersAttributes())) {
                ((JLabel) c).setForeground(Color.red);
            } else {
            	// Only Substance doesn't need this workaround
            	if (!isSubstance) {
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