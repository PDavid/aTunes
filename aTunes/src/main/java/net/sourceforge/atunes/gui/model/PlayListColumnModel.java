/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard and contributors
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

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import net.sourceforge.atunes.gui.Fonts;
import net.sourceforge.atunes.gui.lookandfeel.AbstractTableCellRendererCode;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.gui.renderers.StringTableCellRendererCode;
import net.sourceforge.atunes.gui.renderers.TextAndIconTableCellRendererCode;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable.PlayState;
import net.sourceforge.atunes.kernel.modules.columns.PlayListColumnSet;
import net.sourceforge.atunes.kernel.modules.columns.TextAndIcon;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;

/**
 * The Class PlayListColumnModel.
 */
public final class PlayListColumnModel extends AbstractCommonColumnModel {

    private static final class PlayListTextAndIconTableCellRendererCode extends TextAndIconTableCellRendererCode {
        private PlayListTextAndIconTableCellRendererCode(AbstractCommonColumnModel model) {
            super(model);
        }

        @Override
        public Component getComponent(Component superComponent, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getComponent(superComponent, table, value, isSelected, hasFocus, row, column);
            ((JLabel) c).setFont(PlayListHandler.getInstance().isCurrentVisibleRowPlaying(row) ? Fonts.getPlayListSelectedItemFont() : Fonts.getPlayListFont());
            return c;
        }
    }

    private static final class PlayListStringTableCellRendererCode extends StringTableCellRendererCode {
        private PlayListStringTableCellRendererCode(AbstractCommonColumnModel model) {
            super(model);
        }

        @Override
        public Component getComponent(Component superComponent, JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getComponent(superComponent, t, value, isSelected, hasFocus, row, column);
            ((JLabel) c).setFont(PlayListHandler.getInstance().isCurrentVisibleRowPlaying(row) ? Fonts.getPlayListSelectedItemFont() : Fonts.getPlayListFont());
            return c;
        }
    }

    private static final long serialVersionUID = -2211160302611944001L;

    /**
     * Instantiates a new play list column model.
     * 
     * @param playList
     *            the play list
     */
    public PlayListColumnModel(PlayListTable playList) {
        super(playList, PlayListColumnSet.getInstance());
        enableColumnChange(true);
    }

    @Override
    protected void reapplyFilter() {
        PlayListHandler.getInstance().reapplyFilter();
    }

    @Override
    public void addColumn(TableColumn aColumn) {
        super.addColumn(aColumn);
        updateColumnSettings(aColumn);

        // No header renderer is added to play list since user can change order of table manually by adding, removing or moving rows
        // so keep ordering has no sense
    }

    @Override
    public AbstractTableCellRendererCode getRendererCodeFor(Class<?> clazz) {
        if (clazz.equals(Integer.class)) {
            return new AbstractTableCellRendererCode() {

                @Override
                public Component getComponent(Component superComponent, JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = superComponent;
                    ((JLabel) c).setText(null);
                    if (PlayListHandler.getInstance().isCurrentVisibleRowPlaying(row)) {
                        ((JLabel) c).setIcon(PlayState.getPlayStateIcon(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getPaintForColorMutableIcon(c), 
                        		((PlayListTable) getTable()).getPlayState()));
                    } else {
                        ((JLabel) c).setIcon(null); // was using Images.getImage(Images.EMPTY) previously
                    }

                    // Get alignment from model
                    ((JLabel) c).setHorizontalAlignment(getColumnAlignment(column));
                    return c;
                }
            };
        } else if (clazz.equals(String.class)) {
            return new PlayListStringTableCellRendererCode(this);
        } else if (clazz.equals(TextAndIcon.class)) {
            return new PlayListTextAndIconTableCellRendererCode(this);
        } else {
            return super.getRendererCodeFor(clazz);
        }
    }

}
