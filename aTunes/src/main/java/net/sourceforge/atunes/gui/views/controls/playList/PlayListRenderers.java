/*
 * aTunes 1.14.0
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

package net.sourceforge.atunes.gui.views.controls.playList;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;

import net.sourceforge.atunes.gui.Fonts;
import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.model.PlayListColumnModel;
import net.sourceforge.atunes.gui.views.controls.playList.PlayListTable.PlayState;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.repository.AudioFileStats;
import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.DateUtils;
import net.sourceforge.atunes.utils.LanguageTool;

import org.jvnet.substance.api.renderers.SubstanceDefaultTableCellRenderer;

/**
 * The Class PlayListRenderers.
 */
public class PlayListRenderers {

    /**
     * Add renderers to PlayList.
     * 
     * @param playlist
     *            the playlist
     */
    static void addRenderers(final PlayListTable playlist) {
        // INTEGER renderer
        playlist.setDefaultRenderer(Integer.class, new SubstanceDefaultTableCellRenderer() {
            private static final long serialVersionUID = 4027676693367876748L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
                if (PlayListHandler.getInstance().isCurrentVisibleRowPlaying(row)) {
                    ((JLabel) c).setIcon(PlayState.getPlayStateIcon(playlist.getPlayState()));
                } else {
                    ((JLabel) c).setIcon(ImageLoader.getImage(ImageLoader.EMPTY));
                }

                // Get alignment from model
                ((JLabel) c).setHorizontalAlignment(((PlayListColumnModel) table.getColumnModel()).getColumnObject(column).getAlignment());
                return c;

            }
        });

        // ImageIcon renderer
        playlist.setDefaultRenderer(ImageIcon.class, new SubstanceDefaultTableCellRenderer() {
            private static final long serialVersionUID = 62797739155426672L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
                ((JLabel) c).setIcon((ImageIcon) value);

                // Get alignment from model
                ((JLabel) c).setHorizontalAlignment(((PlayListColumnModel) table.getColumnModel()).getColumnObject(column).getAlignment());
                return c;

            }
        });

        // AUDIOBJECT renderer
        playlist.setDefaultRenderer(AudioObject.class, new SubstanceDefaultTableCellRenderer() {
            private static final long serialVersionUID = 7305230546936745766L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, ((AudioObject) value).getTitleOrFileName(), isSelected, hasFocus, row, column);
                setFontForRow((JLabel) c, row);
                //TODO: Extend navigator tooltips to play list
                //((JLabel) c).setToolTipText(getToolTipForAudioObject((AudioObject) value));

                // Get alignment from model
                ((JLabel) c).setHorizontalAlignment(((PlayListColumnModel) table.getColumnModel()).getColumnObject(column).getAlignment());
                return c;
            }
        });

        // STRING renderer
        playlist.setDefaultRenderer(String.class, new SubstanceDefaultTableCellRenderer() {
            private static final long serialVersionUID = 7305230546936745766L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFontForRow((JLabel) c, row);

                // Get alignment from model
                ((JLabel) c).setHorizontalAlignment(((PlayListColumnModel) table.getColumnModel()).getColumnObject(column).getAlignment());
                return c;
            }
        });

        // JLabel renderer
        playlist.setDefaultRenderer(JLabel.class, new SubstanceDefaultTableCellRenderer() {
            private static final long serialVersionUID = 0L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFontForRow((JLabel) c, row);
                ((JLabel) c).setText(((JLabel) value).getText());
                ((JLabel) c).setIcon(((JLabel) value).getIcon());
                ((JLabel) c).setHorizontalAlignment(((JLabel) value).getHorizontalAlignment());
                // Get alignment from model
                ((JLabel) c).setHorizontalAlignment(((PlayListColumnModel) table.getColumnModel()).getColumnObject(column).getAlignment());

                return c;
            }
        });
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
            AudioFileStats stats = RepositoryHandler.getInstance().getAudioFileStatistics((AudioFile) audioObject);

            // Build string
            StringBuilder sb = new StringBuilder();
            sb.append(audioObject.getTitleOrFileName()).append(" - ");
            sb.append(audioObject.getArtist()).append(" - ");

            // If stats is null -> never played
            if (stats == null) {
                sb.append(LanguageTool.getString("SONG_NEVER_PLAYED"));
            } else {
                sb.append(LanguageTool.getString("LAST_DATE_PLAYED"));
                sb.append(": ");
                sb.append(DateUtils.toString(stats.getLastPlayed()));
                sb.append(" - ");
                sb.append(LanguageTool.getString("TIMES_PLAYED"));
                sb.append(": ");
                sb.append(stats.getTimesPlayed());
            }
            return sb.toString();
        }
        return audioObject.getTitleOrFileName();

    }

}
