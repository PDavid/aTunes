package net.sourceforge.atunes.gui.model;

import java.util.Comparator;

import net.sourceforge.atunes.kernel.modules.columns.Column;
import net.sourceforge.atunes.kernel.modules.columns.ColumnSet;
import net.sourceforge.atunes.kernel.modules.columns.Column.ColumnSort;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.I18nUtils;

public abstract class ColumnSetTableModel extends CommonTableModel {
	
	private ColumnSet columnSet;
	
	public ColumnSetTableModel(ColumnSet columnSet) {
		super();
		this.columnSet = columnSet;		
	}
	
    /**
     * Returns column data class.
     * 
     * @param colIndex
     *            the col index
     * 
     * @return the column class
     */
    @Override
    public Class<?> getColumnClass(int colIndex) {
        return getColumn(colIndex).getColumnClass();
    }
    
    /**
     * Return column count.
     * 
     * @return the column count
     */
    @Override
    public int getColumnCount() {
        return columnSet.getVisibleColumnCount();
    }

    /**
     * Return column name.
     * 
     * @param colIndex
     *            the col index
     * 
     * @return the column name
     */
    @Override
    public String getColumnName(int colIndex) {
    	ColumnSort columnSort = getColumn(colIndex).getColumnSort();
        return I18nUtils.getString(getColumn(colIndex).getHeaderText()) + (columnSort == null ? "" : (columnSort.equals(ColumnSort.ASCENDING) ? " \u22C0" : " \u22C1"));
    }

    /**
     * Returns column in given index
     * @param colIndex
     * @return
     */
    protected Column getColumn(int colIndex) {
    	return columnSet.getColumn(columnSet.getColumnId(colIndex));
    }

    /**
     * Abstract method to sort by the given comparator
     * @param comparator
     */
    public abstract void sort(Comparator<AudioObject> comparator);
	

}
