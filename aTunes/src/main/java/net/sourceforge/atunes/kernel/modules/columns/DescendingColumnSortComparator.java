package net.sourceforge.atunes.kernel.modules.columns;

import java.util.Comparator;

import net.sourceforge.atunes.model.IAudioObject;

/**
 * Sorts descending depending on column
 * @author alex
 *
 */
class DescendingColumnSortComparator implements Comparator<IAudioObject> {

	private AbstractColumn<?> column;
	
	/**
	 * @param column
	 */
	public DescendingColumnSortComparator(AbstractColumn<?> column) {
		this.column = column;
	}
	
	@Override
	public int compare(IAudioObject o1, IAudioObject o2) {
		return column.descendingCompare(o1, o2);
	}

}
