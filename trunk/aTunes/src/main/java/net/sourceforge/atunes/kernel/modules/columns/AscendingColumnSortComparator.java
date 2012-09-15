package net.sourceforge.atunes.kernel.modules.columns;

import java.util.Comparator;

import net.sourceforge.atunes.model.IAudioObject;

/**
 * Sorts ascending depending on column
 * @author alex
 *
 */
class AscendingColumnSortComparator implements Comparator<IAudioObject> {

	private AbstractColumn<?> column;
	
	/**
	 * @param column
	 */
	public AscendingColumnSortComparator(AbstractColumn<?> column) {
		this.column = column;
	}
	
	@Override
	public int compare(IAudioObject o1, IAudioObject o2) {
		return column.ascendingCompare(o1, o2);
	}

}
