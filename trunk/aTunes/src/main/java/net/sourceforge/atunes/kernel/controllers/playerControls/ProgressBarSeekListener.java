package net.sourceforge.atunes.kernel.controllers.playerControls;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JSlider;

import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;

public class ProgressBarSeekListener extends MouseAdapter {

	private JSlider progressBar;
	
	public ProgressBarSeekListener(JSlider progressBar) {
		super();
		this.progressBar = progressBar;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
        if (progressBar.isEnabled()) {
        	// Progress bar width is greater than real slider width so calculate value assuming 5 pixels in both left and right of track 
        	int value = (progressBar.getMaximum() * (e.getX() - 5)) / (progressBar.getWidth() - 10);
        	// Force new value to avoid jump to next major tick
        	progressBar.setValue(value);
        	PlayerHandler.getInstance().seekCurrentAudioObject(value);
        }
	}
}
