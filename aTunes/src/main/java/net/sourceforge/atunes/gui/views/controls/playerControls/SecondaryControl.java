package net.sourceforge.atunes.gui.views.controls.playerControls;

import javax.swing.Action;
import javax.swing.JToggleButton;

import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.gui.views.panels.PlayerControlsPanel;

public abstract class SecondaryControl extends JToggleButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = -124604413114002586L;
	
	public SecondaryControl(Action a) {
		super(a);
        setText(null);
        setPreferredSize(PlayerControlsPanel.OTHER_BUTTONS_SIZE);
        setFocusable(false);
        LookAndFeelSelector.getInstance().getCurrentLookAndFeel().putClientProperties(this);        
	}

}
