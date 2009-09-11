package net.sourceforge.atunes.kernel.modules.context.youtube;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.kernel.modules.context.ContextPanel;
import net.sourceforge.atunes.kernel.modules.context.ContextPanelContent;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.model.AudioObject;

/**
 * Panel to show youtube videos
 * @author alex
 *
 */
public class YoutubeContextPanel extends ContextPanel {
	
	private List<ContextPanelContent> contents;
	
	@Override
	protected ImageIcon getContextPanelIcon(AudioObject audioObject) {
		return ImageLoader.getImage(ImageLoader.YOUTUBE);
	}
	
	@Override
	public String getContextPanelName() {
		return "YOUTUBE";
	}
	
	@Override
	protected String getContextPanelTitle(AudioObject audioObject) {
		return "YouTube";
	}

	@Override
	protected List<ContextPanelContent> getContents() {
		if (contents == null) {
			contents = new ArrayList<ContextPanelContent>();
			contents.add(new YoutubeContent());
		}
		return contents;
	}
	
	@Override
	protected boolean isPanelEnabledForAudioObject(AudioObject audioObject) {
		return (audioObject instanceof AudioFile) || (audioObject instanceof Radio && ((Radio)audioObject).isSongInfoAvailable());
	}
	

}
