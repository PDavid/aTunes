package net.sourceforge.atunes.plugins.webinterface.actions;

import java.awt.Image;
import java.util.Map;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.api.PlayerApi;
import net.sourceforge.atunes.api.WebServicesApi;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.model.GenericImageSize;
import net.sourceforge.atunes.model.ImageSize;
import net.sourceforge.atunes.plugins.webinterface.ImageAction;

public class CurrentImageAction extends ImageAction {
	
	@Override
	public Image execute(Map<String, String> parameters) {
		AudioObject audioObject = PlayerApi.getCurrentAudioObject();
		Image image = null;
		if (audioObject != null) {
			ImageIcon imageIcon = audioObject.getImage(ImageSize.SIZE_MAX);
			if (imageIcon != null) {
				image = imageIcon.getImage();
			}
			if (image == null) {
				image = WebServicesApi.getAlbumImage(audioObject.getArtist(), audioObject.getAlbum());
			}
			if (image == null) {
				image = audioObject.getGenericImage(GenericImageSize.BIG).getImage();
			}
		}
		return image;
	}

}
