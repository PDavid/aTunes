package net.sourceforge.atunes.gui.views.dialogs;

import javax.swing.JOptionPane;

import net.sourceforge.atunes.model.IConfirmationDialog;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.utils.I18nUtils;

public class ConfirmationDialog implements IConfirmationDialog {
	
	private IFrame frame;
	
	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IConfirmationDialog#setFrame(net.sourceforge.atunes.model.IFrame)
	 */
	@Override
	public void setFrame(IFrame frame) {
		this.frame = frame;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.dialogs.IConfirmationDialog#showDialog(java.lang.String)
	 */
	@Override
	public boolean showDialog(String message) {
		return JOptionPane.showConfirmDialog(frame.getFrame(), message, I18nUtils.getString("CONFIRMATION"), JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION;
	}

}
