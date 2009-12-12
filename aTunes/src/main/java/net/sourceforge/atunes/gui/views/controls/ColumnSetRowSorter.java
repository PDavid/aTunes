package net.sourceforge.atunes.gui.views.controls;

import javax.swing.table.TableRowSorter;

import net.sourceforge.atunes.gui.model.CommonColumnModel;
import net.sourceforge.atunes.gui.model.CommonTableModel;

public class ColumnSetRowSorter extends TableRowSorter<CommonTableModel> {
	
	private CommonColumnModel columnModel;
	
	public ColumnSetRowSorter(CommonTableModel model, CommonColumnModel columnModel) {
		super(model);
		this.columnModel = columnModel;
	}
	
	@Override
	public boolean isSortable(int column) {
		return this.columnModel.getColumnObject(column).isSortable();
	}		
}
