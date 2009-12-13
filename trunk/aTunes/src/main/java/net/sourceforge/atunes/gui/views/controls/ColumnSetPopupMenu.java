package net.sourceforge.atunes.gui.views.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import net.sourceforge.atunes.gui.model.CommonColumnModel;
import net.sourceforge.atunes.gui.views.dialogs.ColumnSetSelectorDialog;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.utils.I18nUtils;

public class ColumnSetPopupMenu {

	private JMenuItem arrangeColumns;
	
	/**
	 * Adds a right-button popup menu to column set tables
	 * @param table
	 * @param model
	 */
	public ColumnSetPopupMenu(final JTable table, final CommonColumnModel model) {
        final JPopupMenu rightMenu = new JPopupMenu();
        arrangeColumns = new JMenuItem(I18nUtils.getString("ARRANGE_COLUMNS"));
        rightMenu.add(arrangeColumns);
        arrangeColumns.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				selectColumns(model);
			}
		});
        
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Use right button to arrange columns
                if (e.getButton() == MouseEvent.BUTTON3) {
                	rightMenu.show(table.getTableHeader(), e.getX(), e.getY());
                }
            }
        });
	}
	
	/**
	 * Opens selection dialog and updates model
	 * @param model
	 */
	public static void selectColumns(CommonColumnModel model) {
		// Show column selector
		ColumnSetSelectorDialog selector = GuiHandler.getInstance().getColumnSelector();
		selector.setColumnSet(model.getColumnSet());
		selector.setVisible(true);

		// Apply changes
		model.arrangeColumns(true);
	}
	
	public void enableArrangeColumns(boolean enable) {
		arrangeColumns.setEnabled(enable);
	}
	
	
}
