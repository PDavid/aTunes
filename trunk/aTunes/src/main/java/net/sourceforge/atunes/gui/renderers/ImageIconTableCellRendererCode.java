package net.sourceforge.atunes.gui.renderers;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;

import net.sourceforge.atunes.gui.lookandfeel.TableCellRendererCode;
import net.sourceforge.atunes.gui.model.CommonColumnModel;

public class ImageIconTableCellRendererCode extends TableCellRendererCode {

    private CommonColumnModel model;

    public ImageIconTableCellRendererCode(CommonColumnModel model) {
        this.model = model;
    }

    @Override
    public Component getComponent(Component superComponent, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = superComponent;
        ((JLabel) c).setText(null);
        ((JLabel) c).setIcon((ImageIcon) value);

        // Get alignment from model
        ((JLabel) c).setHorizontalAlignment(model.getColumnAlignment(column));
        return c;
    }

}
