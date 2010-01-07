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

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.gui.Fonts;
import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.gui.lookandfeel.TableCellRendererCode;
import net.sourceforge.atunes.gui.model.CommonColumnModel;
import net.sourceforge.atunes.gui.model.NavigationTableModel.Property;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable.PlayState;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.statistics.AudioFileStats;
import net.sourceforge.atunes.kernel.modules.repository.statistics.StatisticsHandler;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.DateUtils;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Common renderers for columns
 * @author fleax
 *
 */
public final class ColumnRenderers {

    /**
     * Add renderers to table
     * 
     * @param jtable
     *            the playlist
     * 
     * @param model
     */
    public static void addRenderers(final JTable jtable, final CommonColumnModel model) {
        // INTEGER renderer
        jtable.setDefaultRenderer(Integer.class, LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getTableCellRenderer(new TableCellRendererCode() {

        	@Override
        	public Component getComponent(Component superComponent, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        		Component c = superComponent;
            	if (table instanceof PlayListTable) {
            		((JLabel) c).setText(null);
            		if (PlayListHandler.getInstance().isCurrentVisibleRowPlaying(row)) {
            			((JLabel) c).setIcon(PlayState.getPlayStateIcon(((PlayListTable)jtable).getPlayState()));
            		} else {
            			((JLabel) c).setIcon(Images.getImage(Images.EMPTY));
            		}

            		// Get alignment from model
            		((JLabel) c).setHorizontalAlignment(model.getColumnAlignment(column));
            	} else {
            		Integer intValue = (Integer) value;
            		String stringValue;
            		if (intValue <= 0) {
            			stringValue = "";
            		} else {
            			stringValue = String.valueOf(intValue);
            		}
            		((JLabel) c).setText(stringValue);
            		((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
            	}
        		return c;
            }
        }));

        // ImageIcon renderer
        jtable.setDefaultRenderer(ImageIcon.class, LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getTableCellRenderer(new TableCellRendererCode() {

        	@Override
        	public Component getComponent(Component superComponent, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        		Component c = superComponent;
        		((JLabel) c).setText(null);
                ((JLabel) c).setIcon((ImageIcon) value);

                // Get alignment from model
                ((JLabel) c).setHorizontalAlignment(model.getColumnAlignment(column));
                return c;

            }
        }));

        // STRING renderer
        jtable.setDefaultRenderer(String.class, LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getTableCellRenderer(new TableCellRendererCode() {

        	@Override
        	public Component getComponent(Component superComponent, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        		Component c = superComponent;
                if (table instanceof PlayListTable) {
                	setFontForRow((JLabel) c, row);
                }

                // Get alignment from model
                ((JLabel) c).setHorizontalAlignment(model.getColumnAlignment(column));
                return c;
            }
        }));

        // JLabel renderer
        jtable.setDefaultRenderer(JLabel.class, LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getTableCellRenderer(new TableCellRendererCode() {

        	@Override
        	public Component getComponent(Component superComponent, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        		Component c = superComponent;
                if (table instanceof PlayListTable) {
                	setFontForRow((JLabel) c, row);
                }
                ((JLabel) c).setText(((JLabel) value).getText());
                ((JLabel) c).setIcon(((JLabel) value).getIcon());
                ((JLabel) c).setHorizontalAlignment(((JLabel) value).getHorizontalAlignment());
                // Get alignment from model
                ((JLabel) c).setHorizontalAlignment(model.getColumnAlignment(column));

                return c;
            }
        }));
        
        // Property renderer
        jtable.setDefaultRenderer(Property.class, LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getTableCellRenderer(new TableCellRendererCode() {

        	@Override
        	public Component getComponent(Component superComponent, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        		Component comp = superComponent;
        		ImageIcon icon = Images.getImage(Images.EMPTY);
        		Property val = (Property) value;
        		if (val == Property.FAVORITE) {
        			icon = Images.getImage(Images.FAVORITE);
        		} else if (val == Property.NOT_LISTENED_ENTRY) {
        			icon = Images.getImage(Images.NEW_PODCAST_ENTRY);
        		} else if (val == Property.DOWNLOADED_ENTRY) {
        			icon = Images.getImage(Images.DOWNLOAD_PODCAST);
        		} else if (val == Property.OLD_ENTRY) {
        			icon = Images.getImage(Images.REMOVE);
        		}
        		((JLabel) comp).setIcon(icon);
        		((JLabel) comp).setText(null);
        		return comp;

        	}
        }));

    }

    /**
     * Sets font for given label and row
     * 
     * @param label
     * @param row
     */
    static void setFontForRow(JLabel label, int row) {
        label.setFont(PlayListHandler.getInstance().isCurrentVisibleRowPlaying(row) ? Fonts.PLAY_LIST_FONT_SELECTED_ITEM : Fonts.PLAY_LIST_FONT);
    }

    /**
     * Builds a String to use as Tool Tip for an AudioObject.
     * 
     * @param audioObject
     *            the audio object
     * 
     * @return the tool tip for audio object
     */
    static String getToolTipForAudioObject(AudioObject audioObject) {
        if (audioObject instanceof AudioFile) {
            // Get information
            AudioFileStats stats = StatisticsHandler.getInstance().getAudioFileStatistics((AudioFile) audioObject);

            // Build string
            StringBuilder sb = new StringBuilder();
            sb.append(audioObject.getTitleOrFileName()).append(" - ");
            sb.append(audioObject.getArtist()).append(" - ");

            // If stats is null -> never played
            if (stats == null) {
                sb.append(I18nUtils.getString("SONG_NEVER_PLAYED"));
            } else {
                sb.append(I18nUtils.getString("LAST_DATE_PLAYED"));
                sb.append(": ");
                sb.append(DateUtils.toString(stats.getLastPlayed()));
                sb.append(" - ");
                sb.append(I18nUtils.getString("TIMES_PLAYED"));
                sb.append(": ");
                sb.append(stats.getTimesPlayed());
            }
            return sb.toString();
        }
        return audioObject.getTitleOrFileName();

    }

}
