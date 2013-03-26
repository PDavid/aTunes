package net.sourceforge.atunes.gui.javafx;

/**
 * Utility to control Swing dialogs from JavaFX
 * 
 * @author alex
 * 
 */
public abstract class JavaFXDialogController {

	private JavaFXDialog dialog;

	/**
	 * @param dialog
	 */
	public void setDialog(JavaFXDialog dialog) {
		this.dialog = dialog;
		this.dialog.setCloseDialogCallback(this);
	}

	/**
	 * @return
	 */
	public JavaFXDialog getDialog() {
		return dialog;
	}

	/**
	 * Called when dialog closed
	 */
	protected abstract void dialogClosed();
}
