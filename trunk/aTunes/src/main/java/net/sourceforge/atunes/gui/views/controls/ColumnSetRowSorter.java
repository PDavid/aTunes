package net.sourceforge.atunes.gui.views.controls;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;

import net.sourceforge.atunes.gui.model.ColumnSetTableModel;
import net.sourceforge.atunes.gui.model.CommonColumnModel;
import net.sourceforge.atunes.kernel.modules.columns.Column;
import net.sourceforge.atunes.model.AudioObject;

public class ColumnSetRowSorter {

	private JTable table;
	private ColumnSetTableModel model;
	private CommonColumnModel columnModel;
	
	public ColumnSetRowSorter(JTable table, ColumnSetTableModel model, CommonColumnModel columnModel) {
		this.table = table;
		this.model = model;
		this.columnModel = columnModel;
		setListeners();
	}
	
	private void setListeners() {
		table.getTableHeader().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					int columnClickedIndex = table.getTableHeader().getColumnModel().getColumnIndexAtX(e.getX());
					if (columnClickedIndex != -1) {
						// Get column
						Column columnClicked = ColumnSetRowSorter.this.columnModel.getColumnObject(columnClickedIndex);
						if (columnClicked.isSortable()) {
							sort(columnClicked.getComparator());
						}
					}
				}
			}
		});
	}	

	/**
	 * Method to sort a column set. It must sort the underlying data
	 * @param comparator
	 */
	protected void sort(Comparator<AudioObject> comparator) {
		// Sort model
		this.model.sort(comparator);
		
        // Refresh model
        model.refresh(TableModelEvent.UPDATE);        
	}
}
