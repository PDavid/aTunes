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
package net.sourceforge.atunes.kernel.modules.columns;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.lookandfeel.ListCellRendererCode;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.gui.lookandfeel.TableCellRendererCode;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.model.AudioObject;

public class ScoreColumn extends Column {

    /**
     * 
     */
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

        comboBox.setRenderer(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getListCellRenderer(new ListCellRendererCode() {

            @Override
            public Component getComponent(Component superComponent, JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                setLabel((JLabel) superComponent, (Integer) value);
                return superComponent;
            }
        }));

        return new DefaultCellEditor(comboBox);
    }

    @Override
    public TableCellRenderer getCellRenderer() {
        return LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getTableCellRenderer(new TableCellRendererCode() {

            @Override
            public Component getComponent(Component superComponent, JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                setLabel((JLabel) superComponent, (Integer) value);
                return superComponent;
            }
        });
    }

    @Override
    protected int ascendingCompare(AudioObject ao1, AudioObject ao2) {
        return -((Integer) ao1.getStars()).compareTo(ao2.getStars());
    }

    @Override
    public Object getValueFor(AudioObject audioObject) {
        return audioObject.getStars();
    }

    @Override
    public void setValueFor(AudioObject audioObject, Object value) {
        audioObject.setStars((Integer) value);

        // After setting score of an AudioFile, refresh playlist, as the same song can be duplicated
        ControllerProxy.getInstance().getPlayListController().refreshPlayList();
    }

    /**
     * Sets proper icon and text
     * 
     * @param label
     * @param score
     */
    private void setLabel(JLabel label, int score) {
        label.setText(null);
        switch (score) {
        case 0:
            label.setIcon(Images.getImage(Images.EMPTY));
            break;
        case 1:
            label.setIcon(Images.getImage(Images.ONE_STAR));
            break;
        case 2:
            label.setIcon(Images.getImage(Images.TWO_STAR));
            break;
        case 3:
            label.setIcon(Images.getImage(Images.THREE_STAR));
            break;
        case 4:
            label.setIcon(Images.getImage(Images.FOUR_STAR));
            break;
        case 5:
            label.setIcon(Images.getImage(Images.FIVE_STAR));
            break;
        }
    }

}
