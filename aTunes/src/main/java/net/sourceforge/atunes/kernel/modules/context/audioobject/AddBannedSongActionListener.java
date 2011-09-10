package net.sourceforge.atunes.kernel.modules.context.audioobject;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.sourceforge.atunes.kernel.modules.context.ContextHandler;
import net.sourceforge.atunes.model.IWebServicesHandler;

class AddBannedSongActionListener implements ActionListener {
	
	private IWebServicesHandler webServicesHandler;
	
    public AddBannedSongActionListener(IWebServicesHandler webServicesHandler2) {
    	this.webServicesHandler = webServicesHandler2;
	}

	@Override
    public void actionPerformed(ActionEvent e) {
    	webServicesHandler.addBannedSong(ContextHandler.getInstance().getCurrentAudioObject());
    }    
}