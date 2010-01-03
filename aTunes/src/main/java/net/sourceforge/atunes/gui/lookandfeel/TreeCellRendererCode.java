package net.sourceforge.atunes.gui.lookandfeel;

import java.awt.Component;

import javax.swing.JTree;

public abstract class TreeCellRendererCode {

	public abstract Component getComponent(Component superComponent, JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row, boolean isHasFocus);

}
