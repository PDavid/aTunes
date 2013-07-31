package net.sourceforge.atunes.kernel.modules.playlist;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;

import net.sourceforge.atunes.model.IListCellRendererCode;

class PlayListComboRenderer implements
		IListCellRendererCode<JLabel, PlayListComboModelObject> {

	@Override
	public JComponent getComponent(final JLabel c, final JList list,
			final PlayListComboModelObject object, final int index,
			final boolean isSelected, final boolean cellHasFocus) {

		if (object != null) {
			c.setIcon(object.getIcon());
			c.setText(object.getName());
		}

		return c;
	}
}
