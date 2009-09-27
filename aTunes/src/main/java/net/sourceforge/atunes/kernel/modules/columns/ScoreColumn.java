/*
 * aTunes 2.0.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.views.controls.playList.Column;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.model.AudioObject;

import org.jvnet.substance.api.renderers.SubstanceDefaultComboBoxRenderer;
import org.jvnet.substance.api.renderers.SubstanceDefaultTableCellRenderer;

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
        comboBox.setRenderer(new SubstanceDefaultComboBoxRenderer(comboBox) {
            private static final long serialVersionUID = 0L;

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setText("");

                switch ((Integer) value) {
                case 0:
                    label.setIcon(ImageLoader.getImage(ImageLoader.EMPTY));
                    break;
                case 1:
                    label.setIcon(ImageLoader.getImage(ImageLoader.ONE_STAR));
                    break;
                case 2:
                    label.setIcon(ImageLoader.getImage(ImageLoader.TWO_STAR));
                    break;
                case 3:
                    label.setIcon(ImageLoader.getImage(ImageLoader.THREE_STAR));
                    break;
                case 4:
                    label.setIcon(ImageLoader.getImage(ImageLoader.FOUR_STAR));
                    break;
                case 5:
                    label.setIcon(ImageLoader.getImage(ImageLoader.FIVE_STAR));
                    break;
                }

                return label;
            }
        });

        return new DefaultCellEditor(comboBox);
    }

    @Override
    public TableCellRenderer getCellRenderer() {
        return new SubstanceDefaultTableCellRenderer() {
            private static final long serialVersionUID = 0L;

            @Override
            public Component getTableCellRendererComponent(JTable arg0, Object value, boolean arg2, boolean arg3, int arg4, int arg5) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(arg0, "", arg2, arg3, arg4, arg5);
                switch ((Integer) value) {
                case 0:
                    label.setIcon(ImageLoader.getImage(ImageLoader.EMPTY));
                    break;
                case 1:
                    label.setIcon(ImageLoader.getImage(ImageLoader.ONE_STAR));
                    break;
                case 2:
                    label.setIcon(ImageLoader.getImage(ImageLoader.TWO_STAR));
                    break;
                case 3:
                    label.setIcon(ImageLoader.getImage(ImageLoader.THREE_STAR));
                    break;
                case 4:
                    label.setIcon(ImageLoader.getImage(ImageLoader.FOUR_STAR));
                    break;
                case 5:
                    label.setIcon(ImageLoader.getImage(ImageLoader.FIVE_STAR));
                    break;
                }
                return label;
            }
        };
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

}
