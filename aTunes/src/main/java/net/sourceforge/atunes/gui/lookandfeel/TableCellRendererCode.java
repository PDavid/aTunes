package net.sourceforge.atunes.gui.lookandfeel;

import java.awt.Component;

import javax.swing.JTable;

public abstract class TableCellRendererCode {

	public abstract Component getComponent(Component superComponent, JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column);

}
