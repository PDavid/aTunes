package net.sourceforge.atunes.gui.renderers;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.gui.lookandfeel.TableCellRendererCode;

public class IntegerTableCellRendererCode extends TableCellRendererCode {

    @Override
    public Component getComponent(Component superComponent, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = superComponent;
        Integer intValue = (Integer) value;
        String stringValue;
        if (intValue <= 0) {
            stringValue = "";
        } else {
            stringValue = String.valueOf(intValue);
        }
        ((JLabel) c).setText(stringValue);
        ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
        return c;
    }

}
