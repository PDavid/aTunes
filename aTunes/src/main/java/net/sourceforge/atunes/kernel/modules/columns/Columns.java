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

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;

/**
 * This class defines all columns than can be used by application
 * 
 * @author fleax
 */
public class Columns {

    private static List<Class<? extends Column>> classes;

    private Columns() {
    	
    }
    
    private static List<Class<? extends Column>> getClasses() {
        if (classes == null) {
            classes = new ArrayList<Class<? extends Column>>();
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
        }
        return classes;
    }

    /**
     * Returns all columns by default.
     * 
     * @param playListExclusive
     *            used to return only play list columns
     * 
     * @return the columns by default
     */
    public static List<Column> getColumns(boolean playListExclusive) {
        List<Column> result = new ArrayList<Column>();

        int order = 0;
        for (Class<? extends Column> columnClass : getClasses()) {
            Column column = null;
            try {
                column = columnClass.newInstance();
                if (!playListExclusive && !column.isPlayListExclusive() || playListExclusive) {
                    column.setOrder(order);
                    order++;
                    result.add(column);
                }
            } catch (Exception e) {
                new Logger().error(LogCategories.COLUMNS, e);
            }
        }

        return result;
    }
}
