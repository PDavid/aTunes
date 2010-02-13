package net.sourceforge.atunes.gui.renderers;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.lookandfeel.TableCellRendererCode;
import net.sourceforge.atunes.gui.model.NavigationTableModel.Property;

public class PropertyTableCellRendererCode extends TableCellRendererCode {

	public PropertyTableCellRendererCode() {
	}
	
    @Override
    public Component getComponent(Component superComponent, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component comp = superComponent;
        ImageIcon icon = Images.getImage(Images.EMPTY);
        Property val = (Property) value;
        if (val == Property.FAVORITE) {
            icon = Images.getImage(Images.FAVORITE);
        } else if (val == Property.NOT_LISTENED_ENTRY) {
            icon = Images.getImage(Images.NEW_PODCAST_ENTRY);
        } else if (val == Property.DOWNLOADED_ENTRY) {
            icon = Images.getImage(Images.DOWNLOAD_PODCAST);
        } else if (val == Property.OLD_ENTRY) {
            icon = Images.getImage(Images.REMOVE);
        }
        ((JLabel) comp).setIcon(icon);
        ((JLabel) comp).setText(null);
        return comp;
    }

}
