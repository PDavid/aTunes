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

package net.sourceforge.atunes.gui.renderers;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;

import net.sourceforge.atunes.gui.Fonts;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.gui.model.AbstractCommonColumnModel;
import net.sourceforge.atunes.gui.model.NavigationTableModel.Property;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.statistics.AudioFileStats;
import net.sourceforge.atunes.kernel.modules.statistics.StatisticsHandler;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.DateUtils;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Common renderers for columns
 * 
 * @author fleax
 * 
 */
public final class ColumnRenderers {

    private ColumnRenderers() {

    }

    /**
     * Add renderers to table
     * 
     * @param jtable
     *            the playlist
     * 
     * @param model
     */
    public static void addRenderers(final JTable jtable, final AbstractCommonColumnModel model) {

        // Integer renderer
        jtable.setDefaultRenderer(Integer.class, LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getTableCellRenderer(model.getRendererCodeFor(Integer.class)));

        // ImageIcon renderer
        jtable.setDefaultRenderer(ImageIcon.class, LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getTableCellRenderer(model.getRendererCodeFor(ImageIcon.class)));

        // STRING renderer
        jtable.setDefaultRenderer(String.class, LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getTableCellRenderer(model.getRendererCodeFor(String.class)));

        // JLabel renderer
        jtable.setDefaultRenderer(JLabel.class, LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getTableCellRenderer(model.getRendererCodeFor(JLabel.class)));

        // Property renderer
        jtable.setDefaultRenderer(Property.class, LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getTableCellRenderer(model.getRendererCodeFor(Property.class)));

    }

    /**
     * Sets font for given label and row
     * 
     * @param label
     * @param row
     */
    static void setFontForRow(JLabel label, int row) {
        label.setFont(PlayListHandler.getInstance().isCurrentVisibleRowPlaying(row) ? Fonts.getPlayListSelectedItemFont() : Fonts.getPlayListFont());
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
