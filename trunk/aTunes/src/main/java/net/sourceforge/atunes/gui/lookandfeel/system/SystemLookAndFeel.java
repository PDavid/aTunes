package net.sourceforge.atunes.gui.lookandfeel.system;

import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.sourceforge.atunes.gui.lookandfeel.LookAndFeel;

public class SystemLookAndFeel extends LookAndFeel {
	
	@Override
	public String getName() {
		return "System";
	}
	
	@Override
	public String getDescription() {
		return "System Look And Feel";
	}
	
	@Override
	public List<String> getSkins() {
		return null;
	}
	
	@Override
	public void initializeLookAndFeel() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
	}
	
	@Override
	public String getDefaultSkin() {
		return null;
	}
	
	@Override
	public void setLookAndFeel(String skin) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
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
