package net.sourceforge.atunes.gui.lookandfeel.nimbus;

import java.util.List;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.sourceforge.atunes.gui.lookandfeel.LookAndFeel;

public class NimbusLookAndFeel extends LookAndFeel {
	
	@Override
	public String getName() {
		return "Nimbus";
	}
	
	@Override
	public String getDescription() {
		return "Nimbus Look And Feel";
	}
	
	@Override
	public List<String> getSkins() {
		return null;
	}
	
	@Override
	public void initializeLookAndFeel() {
	}
	
	@Override
	public String getDefaultSkin() {
		return null;
	}
	
	@Override
	public void setLookAndFeel(String skin) {
        try {
        	UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException e) {
            getLogger().internalError(e);
        } catch (InstantiationException e) {
        	getLogger().internalError(e);
        } catch (IllegalAccessException e) {
        	getLogger().internalError(e);
        } catch (UnsupportedLookAndFeelException e) {
        	getLogger().internalError(e);
        }
	}	
}
