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

package net.sourceforge.atunes.gui.renderers;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.images.ColorMutableImageIcon;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.gui.model.AbstractCommonColumnModel;
import net.sourceforge.atunes.gui.model.NavigationTableModel.Property;
import net.sourceforge.atunes.kernel.modules.columns.TextAndIcon;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectStatistics;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.model.IStatisticsHandler;
import net.sourceforge.atunes.utils.I18nUtils;

import org.joda.time.format.DateTimeFormat;

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

        // TextAndIcon renderer
        jtable.setDefaultRenderer(TextAndIcon.class, LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getTableCellRenderer(model.getRendererCodeFor(TextAndIcon.class)));

        // Property renderer
        jtable.setDefaultRenderer(Property.class, LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getTableCellRenderer(model.getRendererCodeFor(Property.class)));
        
        // ColorMutableImageIcon
        jtable.setDefaultRenderer(ColorMutableImageIcon.class, LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getTableCellRenderer(model.getRendererCodeFor(ColorMutableImageIcon.class)));

    }

    /**
     * Sets font for given label and row
     * 
     * @param playListHandler
     * @param label
     * @param row
     */
    static void setFontForRow(IPlayListHandler playListHandler, JLabel label, int row) {
    	if (playListHandler.isCurrentVisibleRowPlaying(row)) {
    		if (LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getPlayListSelectedItemFont() != null) {
    			label.setFont(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getPlayListSelectedItemFont());
    		} else if (LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getPlayListFont() != null) {
                label.setFont(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getPlayListFont());
    		}
    	}
    }

    /**
     * Builds a String to use as Tool Tip for an AudioObject.
     * 
     * @param audioObject
     *            the audio object
     * 
     * @return the tool tip for audio object
     */
    static String getToolTipForAudioObject(IAudioObject audioObject) {
        if (audioObject instanceof AudioFile) {
            // Get information
            IAudioObjectStatistics stats = Context.getBean(IStatisticsHandler.class).getAudioObjectStatistics(audioObject);

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
                sb.append(DateTimeFormat.shortDateTime().print(stats.getLastPlayed()));
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
