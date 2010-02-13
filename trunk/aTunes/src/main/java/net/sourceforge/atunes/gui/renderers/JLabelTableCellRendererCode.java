package net.sourceforge.atunes.gui.renderers;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;

import net.sourceforge.atunes.gui.lookandfeel.TableCellRendererCode;
import net.sourceforge.atunes.gui.model.CommonColumnModel;

public class JLabelTableCellRendererCode extends TableCellRendererCode {

	private CommonColumnModel model;
	
	public JLabelTableCellRendererCode(CommonColumnModel model) {
		this.model = model;
	}
	
    @Override
    public Component getComponent(Component superComponent, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = superComponent;
        ((JLabel) c).setText(((JLabel) value).getText());
        ((JLabel) c).setIcon(((JLabel) value).getIcon());
        ((JLabel) c).setHorizontalAlignment(((JLabel) value).getHorizontalAlignment());
        // Get alignment from model
        ((JLabel) c).setHorizontalAlignment(model.getColumnAlignment(column));
        return c;
    }

}
