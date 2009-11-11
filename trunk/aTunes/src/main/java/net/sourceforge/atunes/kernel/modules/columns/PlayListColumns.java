/*
 * aTunes 2.0.0-SNAPSHOT
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.gui.model.PlayListColumnModel;
import net.sourceforge.atunes.gui.views.controls.playList.Column;
import net.sourceforge.atunes.gui.views.controls.playList.ColumnBean;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;

import org.commonjukebox.plugins.Plugin;
import org.commonjukebox.plugins.PluginInfo;
import org.commonjukebox.plugins.PluginListener;
import org.commonjukebox.plugins.PluginSystemException;

/**
 * This class defines all columns than can be viewed in Play List.
 * 
 * @author fleax
 */
public class PlayListColumns implements PluginListener {

    /** The current visible columns. */
    private static List<Class<? extends Column>> currentColumns;

    /** The available columns. */
    private static List<Column> availableColumns;

    /**
     * Singleton instance, used to handle plugin listener
     */
    private static PlayListColumns instance;

    /**
     * Logger
     */
    private Logger logger = new Logger();

    /**
     * Returns singleton instance
     * 
     * @return
     */
    public static PlayListColumns getInstance() {
        if (instance == null) {
            instance = new PlayListColumns();
        }
        return instance;
    }

    /**
     * Gets the available columns.
     * 
     * @return the available columns
     */
    private static List<Column> getAvailableColumns() {
        if (availableColumns == null) {
            // Try to get configuration saved
            Map<String, ColumnBean> columnsBeans = ApplicationState.getInstance().getColumns();

            availableColumns = getColumns();

            // Apply configuration
            if (columnsBeans != null) {
                for (Column column : availableColumns) {
                    ColumnBean bean = columnsBeans.get(column.getClass().getName());
                    if (bean != null) {
                        column.applyColumnBean(bean);
                    }
                }
            } else {
                storeCurrentColumnSettings();
            }
        }
        return availableColumns;
    }

    /**
     * Returns all columns by default.
     * 
     * @return the columns by default
     */
    public static List<Column> getColumns() {
        List<Column> result = new ArrayList<Column>();
        result.add(new PlayingColumn());
        result.add(new FavoriteColumn());
        result.add(new TypeColumn());
        result.add(new TrackColumn());
        result.add(new TitleColumn());
        result.add(new ArtistColumn());
        result.add(new AlbumColumn());
        result.add(new AlbumArtistColumn());
        result.add(new ComposerColumn());
        result.add(new GenreColumn());
        result.add(new YearColumn());
        result.add(new LengthColumn());
        result.add(new ScoreColumn());
        result.add(new FileNameColumn());
        result.add(new PathColumn());
        result.add(new SizeColumn());
        result.add(new BitrateColumn());
        result.add(new FrequencyColumn());
        result.add(new TimesPlayedColumn());
        result.add(new DiscNumberColumn());
        return result;
    }

    /**
     * Returns Column ID given a visible column number
     * 
     * @param colIndex
     *            the col index
     * 
     * @return the column id
     */
    public static Class<? extends Column> getColumnId(int colIndex) {
        if (currentColumns == null) {
            setCurrentColumns();
        }
        return currentColumns.get(colIndex);
    }

    /**
     * Sets column order.
     */
    public static void setCurrentColumns() {
        int columnNumber = getVisibleColumnCount();
        if (columnNumber == 0) {
            return;
        }

        currentColumns = new ArrayList<Class<? extends Column>>();

        for (Column c : getColumnsOrdered()) {
            if (c.isVisible()) {
                currentColumns.add(c.getClass());
            }
        }
    }

    public static int getVisibleColumnCount() {
        int visibleColumns = 0;
        for (Column c : getAvailableColumns()) {
            if (c.isVisible()) {
                visibleColumns++;
            }
        }

        return visibleColumns;
    }

    /**
     * Returns columns for selection
     * 
     * @return the columns for selection
     */
    public static List<Column> getColumnsForSelection() {
        return new ArrayList<Column>(getAvailableColumns());
    }

    /**
     * Returns columns in order.
     * 
     * @return the columns ordered
     */
    public static List<Column> getColumnsOrdered() {
        List<Column> result = new ArrayList<Column>(getAvailableColumns());
        Collections.sort(result);
        return result;
    }

    /**
     * Store current column settings.
     */
    public static void storeCurrentColumnSettings() {
        // Get ColumnsBean from default columns and store it
        HashMap<String, ColumnBean> newColumnsBeans = new HashMap<String, ColumnBean>();
        for (Column column : getAvailableColumns()) {
            newColumnsBeans.put(column.getClass().getName(), column.getColumnBean());
        }
        ApplicationState.getInstance().setColumns(newColumnsBeans);
    }

    /**
     * Checks if is album visible.
     * 
     * @return true, if is album visible
     */
    public static boolean isAlbumVisible() {
        return getColumn(AlbumColumn.class) != null ? getColumn(AlbumColumn.class).isVisible() : false;
    }

    /**
     * Checks if is artist visible.
     * 
     * @return true, if is artist visible
     */
    public static boolean isArtistVisible() {
        return getColumn(ArtistColumn.class) != null ? getColumn(ArtistColumn.class).isVisible() : false;
    }

    /**
     * Returns a column object given its class name
     * 
     * @param columnClass
     * @return
     */
    public static Column getColumn(Class<? extends Column> columnClass) {
        for (Column column : getAvailableColumns()) {
            if (column.getClass().equals(columnClass)) {
                return column;
            }
        }
        return null;
    }

    @Override
    public void pluginActivated(PluginInfo plugin) {
        try {
            Column pluginColumn = (Column) plugin.getInstance();
            getAvailableColumns().add(pluginColumn);

            // Apply configuration if column has been previously used
            boolean needRefresh = false;
            Map<String, ColumnBean> columnsBeans = ApplicationState.getInstance().getColumns();
            if (columnsBeans != null) {
                ColumnBean bean = columnsBeans.get(pluginColumn.getClass().getName());
                if (bean != null) {
                    pluginColumn.applyColumnBean(bean);
                    needRefresh = true;
                }
            }

            // Refresh columns if necessary
            if (needRefresh) {
                refreshColumns();
            }

        } catch (PluginSystemException e) {
            logger.error(LogCategories.PLAYLIST, e);
        }
    }

    @Override
    public void pluginDeactivated(PluginInfo plugin, Collection<Plugin> createdInstances) {
        for (Plugin instancedColumn : createdInstances) {
            getAvailableColumns().remove(instancedColumn);
        }
        refreshColumns();
    }

    /**
     * Internal method called to refresh columns when a plugin is activated or
     * deactivated
     */
    private void refreshColumns() {
        ((PlayListColumnModel) GuiHandler.getInstance().getPlayListTable().getColumnModel()).arrangeColumns(false);
    }

}
