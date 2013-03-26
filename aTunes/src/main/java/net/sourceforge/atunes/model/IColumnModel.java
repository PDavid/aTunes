package net.sourceforge.atunes.model;

/**
 * Custom column model
 * 
 * @author alex
 * 
 */
public interface IColumnModel {

	/**
	 * @return column set of model
	 */
	IColumnSet getColumnSet();

	/**
	 * Arrange columns.
	 * 
	 * @param reapplyFilter
	 *            the reapply filter
	 */
	void arrangeColumns(boolean reapplyFilter);
}
