package net.sourceforge.atunes.kernel.modules.draganddrop;

import java.util.Comparator;

final class PlayListDragableRowComparator implements Comparator<PlayListDragableRow> {
	
	private final boolean needReverseRows;

	PlayListDragableRowComparator(boolean needReverseRows) {
		this.needReverseRows = needReverseRows;
	}

	@Override
	public int compare(PlayListDragableRow o1, PlayListDragableRow o2) {
	    return (needReverseRows ? -1 : 1) * Integer.valueOf(o1.getRowPosition()).compareTo(Integer.valueOf(o2.getRowPosition()));
	}
}