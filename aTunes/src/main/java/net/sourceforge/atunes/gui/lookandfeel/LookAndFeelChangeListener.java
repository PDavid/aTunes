package net.sourceforge.atunes.gui.lookandfeel;

/**
 * Interface implemented by components that need be aware of look and feel changes
 * @author fleax
 *
 */
public interface LookAndFeelChangeListener {

	/**
	 * Called every time application changes its look and feel
	 */
	public void lookAndFeelChanged();
}
