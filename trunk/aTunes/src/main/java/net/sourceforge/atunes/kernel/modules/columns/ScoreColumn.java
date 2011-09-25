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

package net.sourceforge.atunes.kernel.modules.columns;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.images.StarImageIcon;
import net.sourceforge.atunes.gui.lookandfeel.AbstractListCellRendererCode;
import net.sourceforge.atunes.gui.lookandfeel.AbstractTableCellRendererCode;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILookAndFeelManager;

public class ScoreColumn extends AbstractColumn {

   
    private static final long serialVersionUID = -2673502888298485650L;

    public ScoreColumn() {
        super("SCORE", Integer.class);
        setWidth(100);
        setVisible(true);
        setEditable(true);
    }

    @Override
    public TableCellEditor getCellEditor() {
        JComboBox comboBox = new JComboBox(new Object[] { 0, 1, 2, 3, 4, 5 });

        comboBox.setRenderer(Context.getBean(ILookAndFeelManager.class).getCurrentLookAndFeel().getListCellRenderer(new AbstractListCellRendererCode() {

            @Override
            public JComponent getComponent(JComponent superComponent, JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                setLabel((JLabel) superComponent, (Integer) value);
                return superComponent;
            }
        }));

        return new DefaultCellEditor(comboBox);
    }

    @Override
    public TableCellRenderer getCellRenderer() {
        return Context.getBean(ILookAndFeelManager.class).getCurrentLookAndFeel().getTableCellRenderer(new AbstractTableCellRendererCode(Context.getBean(ILookAndFeelManager.class).getCurrentLookAndFeel()) {

            @Override
            public JComponent getComponent(JComponent superComponent, JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                setLabel((JLabel) superComponent, (Integer) value);
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
    private void setLabel(JLabel label, int score) {
        label.setText(null);
        if (score == 0) {
            label.setIcon(null);
        } else {
        	// TODO: ICONOS Sacar a un renderer
            label.setIcon(StarImageIcon.getIcon(Context.getBean(ILookAndFeelManager.class).getCurrentLookAndFeel().getPaintForColorMutableIcon(label, false), score, Context.getBean(ILookAndFeelManager.class).getCurrentLookAndFeel()));
        }
    }

}
