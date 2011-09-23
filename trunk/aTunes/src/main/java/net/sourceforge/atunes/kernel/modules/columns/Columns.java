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

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.model.IColumn;
import net.sourceforge.atunes.utils.Logger;

/**
 * This class defines all columns than can be used by application
 * 
 * @author fleax
 */
public final class Columns {

    private static List<Class<? extends AbstractColumn>> classes;
    
    private static List<Class<? extends AbstractColumn>> albumClasses;

    static {
        classes = new ArrayList<Class<? extends AbstractColumn>>();
        classes.add(PlayingColumn.class);
        classes.add(FavoriteColumn.class);
        classes.add(TypeColumn.class);
        classes.add(TrackColumn.class);
        classes.add(TitleColumn.class);
        classes.add(ArtistColumn.class);
        classes.add(AlbumColumn.class);
        classes.add(AlbumArtistColumn.class);
        classes.add(ComposerColumn.class);
        classes.add(GenreColumn.class);
        classes.add(YearColumn.class);
        classes.add(DateColumn.class);
        classes.add(LengthColumn.class);
        classes.add(ScoreColumn.class);
        classes.add(FileNameColumn.class);
        classes.add(PathColumn.class);
        classes.add(SizeColumn.class);
        classes.add(BitrateColumn.class);
        classes.add(FrequencyColumn.class);
        classes.add(TimesPlayedColumn.class);
        classes.add(DiscNumberColumn.class);
        
		albumClasses = new ArrayList<Class<? extends AbstractColumn>>();
		albumClasses.add(TypeColumn.class);
		albumClasses.add(AlbumColumn.class);
		albumClasses.add(ArtistColumn.class);
		albumClasses.add(AlbumArtistColumn.class);
		albumClasses.add(GenreColumn.class);
		albumClasses.add(YearColumn.class);
		albumClasses.add(DiscNumberColumn.class);
    }
    
    private Columns() {

    }

    /**
     * Returns all columns by default.
     * 
     * @param playListExclusive
     *            used to return only play list columns
     * 
     * @return the columns by default
     */
    public static List<IColumn> getColumns(boolean playListExclusive) {
        List<IColumn> result = new ArrayList<IColumn>();

        int order = 0;
        for (Class<? extends AbstractColumn> columnClass : classes) {
            AbstractColumn column = null;
            try {
                column = columnClass.newInstance();
                if (!playListExclusive && !column.isPlayListExclusive() || playListExclusive) {
                    column.setOrder(order);
                    order++;
                    result.add(column);
                }
            } catch (Exception e) {
                Logger.error(e);
            }
        }

        return result;
    }
    
    /**
         * Returns all columns by default.
         * 
         * @param playListExclusive
         *            used to return only play list columns
         * 
         * @return the columns by default
         */
        public static List<IColumn> getAlbumColumns() {
            List<IColumn> result = new ArrayList<IColumn>();
    
            int order = 0;
            for (Class<? extends AbstractColumn> columnClass : albumClasses) {
                AbstractColumn column = null;
                try {
                    column = columnClass.newInstance();
                    column.setOrder(order);
                    order++;
                    result.add(column);
                } catch (Exception e) {
                    Logger.error(e);
                }
            }
    
            return result;
        }
}
