package net.sourceforge.atunes.gui.lookandfeel;

import java.awt.Component;

import javax.swing.JList;

public abstract class ListCellRendererCode {

	public abstract Component getComponent(Component superComponent, JList list, Object value, int index, boolean isSelected, boolean cellHasFocus);

}
