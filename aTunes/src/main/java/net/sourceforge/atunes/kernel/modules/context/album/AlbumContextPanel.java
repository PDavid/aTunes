package net.sourceforge.atunes.kernel.modules.context.album;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.kernel.modules.context.ContextPanel;
import net.sourceforge.atunes.kernel.modules.context.ContextPanelContent;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.LanguageTool;

/**
 * Context panel to show album information
 * @author alex
 *
 */
public class AlbumContextPanel extends ContextPanel {
	
	private static final long serialVersionUID = -7910261492394049289L;

	private List<ContextPanelContent> contents;
	
	@Override
	protected ImageIcon getContextPanelIcon(AudioObject audioObject) {
		return ImageLoader.getImage(ImageLoader.ALBUM);
	}
	
	@Override
	public String getContextPanelName() {
		return "ALBUM";
	}
	
	@Override
	protected String getContextPanelTitle(AudioObject audioObject) {
		return LanguageTool.getString("ALBUM");
	}
	
	@Override
	protected List<ContextPanelContent> getContents() {
		if (contents == null) {
			contents = new ArrayList<ContextPanelContent>();
			contents.add(new AlbumBasicInfoContent());
			contents.add(new AlbumTracksContent());
		}
		return contents;
	}
	
	@Override
	protected boolean isPanelEnabledForAudioObject(AudioObject audioObject) {
		return (audioObject instanceof AudioFile) || (audioObject instanceof Radio && ((Radio)audioObject).isSongInfoAvailable());
	}
	

}
