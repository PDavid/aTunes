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
     * 				used to return only play list columns
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
