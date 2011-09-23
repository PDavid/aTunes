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

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableColumn;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.lookandfeel.AbstractTableCellRendererCode;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.gui.renderers.StringTableCellRendererCode;
import net.sourceforge.atunes.gui.renderers.TextAndIconTableCellRendererCode;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable;
import net.sourceforge.atunes.kernel.modules.columns.TextAndIcon;
import net.sourceforge.atunes.model.IColumnSet;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.ITaskService;
import net.sourceforge.atunes.model.PlayState;

/**
 * The Class PlayListColumnModel.
 */
public final class PlayListColumnModel extends AbstractCommonColumnModel {

    private final class PlayListTextAndIconTableCellRendererCode extends TextAndIconTableCellRendererCode {

        private PlayListTextAndIconTableCellRendererCode(AbstractCommonColumnModel model) {
            super(model);
        }

        @Override
        public JComponent getComponent(JComponent superComponent, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        	JComponent c = super.getComponent(superComponent, table, value, isSelected, hasFocus, row, column);
        	if (playListHandler.isCurrentVisibleRowPlaying(row)) {
        		if (LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getPlayListSelectedItemFont() != null) {
        			 ((JLabel) c).setFont(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getPlayListSelectedItemFont());
        		} else if (LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getPlayListFont() != null) {
                    ((JLabel) c).setFont(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getPlayListFont());
        		}
        	}
            return c;
        }
    }

    private final class PlayListStringTableCellRendererCode extends StringTableCellRendererCode {

        private PlayListStringTableCellRendererCode(AbstractCommonColumnModel model) {
            super(model);
        }

        @Override
        public JComponent getComponent(JComponent superComponent, JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        	JComponent c = super.getComponent(superComponent, t, value, isSelected, hasFocus, row, column);
        	if (playListHandler.isCurrentVisibleRowPlaying(row)) {
        		if (LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getPlayListSelectedItemFont() != null) {
        			 ((JLabel) c).setFont(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getPlayListSelectedItemFont());
        		} else if (LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getPlayListFont() != null) {
                    ((JLabel) c).setFont(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getPlayListFont());
        		}
        	}
            return c;
        }
    }
    private static final long serialVersionUID = -2211160302611944001L;

    private IPlayListHandler playListHandler;
    
    /**
     * Instantiates a new play list column model.
     * 
     * @param playList
     *            the play list
     */
    public PlayListColumnModel(PlayListTable playList, IPlayListHandler playListHandler) {
        super(playList, (IColumnSet) Context.getBean("playlistColumnSet"), Context.getBean(ITaskService.class));
        this.playListHandler = playListHandler;
        enableColumnChange(true);
    }

    @Override
    protected void reapplyFilter() {
    	playListHandler.reapplyFilter();
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
                public JComponent getComponent(JComponent c, JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    String name = t.getColumnName(column);
                    //Display Integer values if the column is nameless
                    if (!"".equals(name)) {
                    	if (playListHandler.isCurrentVisibleRowPlaying(row)) {
                    		if (LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getPlayListSelectedItemFont() != null) {
                    			 ((JLabel) c).setFont(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getPlayListSelectedItemFont());
                    		} else if (LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getPlayListFont() != null) {
                                ((JLabel) c).setFont(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getPlayListFont());
                    		}
                    	}
                        ((JLabel) c).setIcon(null);
                        ((JLabel) c).setText(value == null ? null : value.toString());
                        ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                        return c;
                    }

                    //Display an icon if playing and cell is in a "special" column
                    ((JLabel) c).setText(null);
                    if (playListHandler.isCurrentVisibleRowPlaying(row)) {
                        ((JLabel) c).setIcon(PlayState.getPlayStateIcon(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getPaintForColorMutableIcon(c, isSelected), 
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
