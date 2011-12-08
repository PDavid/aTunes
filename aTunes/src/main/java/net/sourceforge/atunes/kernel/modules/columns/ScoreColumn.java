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

package net.sourceforge.atunes.kernel.modules.columns;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.sourceforge.atunes.gui.AbstractListCellRendererCode;
import net.sourceforge.atunes.gui.AbstractTableCellRendererCode;
import net.sourceforge.atunes.model.CachedIconFactory;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILookAndFeelManager;

public class ScoreColumn extends AbstractColumn {

    private static final long serialVersionUID = -2673502888298485650L;
    
    private transient ILookAndFeelManager lookAndFeelManager;
    
    private CachedIconFactory star1Icon;
    private CachedIconFactory star2Icon;
    private CachedIconFactory star3Icon;
    private CachedIconFactory star4Icon;
    private CachedIconFactory star5Icon;
    
    public void setStar1Icon(CachedIconFactory star1Icon) {
		this.star1Icon = star1Icon;
	}
    
    public void setStar2Icon(CachedIconFactory star2Icon) {
		this.star2Icon = star2Icon;
	}
    
    public void setStar3Icon(CachedIconFactory star3Icon) {
		this.star3Icon = star3Icon;
	}
    
    public void setStar4Icon(CachedIconFactory star4Icon) {
		this.star4Icon = star4Icon;
	}
    
    public void setStar5Icon(CachedIconFactory star5Icon) {
		this.star5Icon = star5Icon;
	}
    
    /**
     * @param lookAndFeelManager
     */
    public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

    public ScoreColumn() {
        super("SCORE", Integer.class);
        setWidth(100);
        setVisible(true);
        setEditable(true);
    }

    @Override
    public TableCellEditor getCellEditor() {
        JComboBox comboBox = new JComboBox(new Object[] { 0, 1, 2, 3, 4, 5 });

        comboBox.setRenderer(lookAndFeelManager.getCurrentLookAndFeel().getListCellRenderer(new AbstractListCellRendererCode() {

            @Override
            public JComponent getComponent(JComponent superComponent, JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                setLabel((JLabel) superComponent, value);
                return superComponent;
            }
        }));

        return new DefaultCellEditor(comboBox);
    }

    @Override
    public TableCellRenderer getCellRenderer() {
        return lookAndFeelManager.getCurrentLookAndFeel().getTableCellRenderer(new AbstractTableCellRendererCode(lookAndFeelManager.getCurrentLookAndFeel()) {

            @Override
            public JComponent getComponent(JComponent superComponent, JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                setLabel((JLabel) superComponent, value);
                return superComponent;
            }
        });
    }

    @Override
    protected int ascendingCompare(IAudioObject ao1, IAudioObject ao2) {
        return -((Integer) ao1.getStars()).compareTo(ao2.getStars());
    }

    @Override
    public Object getValueFor(IAudioObject audioObject) {
        return audioObject.getStars();
    }

    @Override
    public void setValueFor(IAudioObject audioObject, Object value) {
        audioObject.setStars((Integer) value);
    }

    /**
     * Sets proper icon and text
     * 
     * @param label
     * @param score
     */
    private void setLabel(JLabel label, Object score) {
        label.setText(null);
        CachedIconFactory icon = score != null ? getIcon((Integer)score) : null;
    	// TODO: ICONOS Sacar a un renderer
        label.setIcon(icon != null ? icon.getIcon(lookAndFeelManager.getCurrentLookAndFeel().getPaintForColorMutableIcon(label, false)) : null);
    }
    
    /**
     * Returns icon for score
     * @param score
     * @return
     */
    private CachedIconFactory getIcon(int score) {
    	switch (score) {
			case 1: return star1Icon;
			case 2: return star2Icon;
			case 3: return star3Icon;
			case 4: return star4Icon;
			case 5: return star5Icon;
		}
    	return null;
    }
}
