package net.sourceforge.atunes.kernel.modules.context.audioobject;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.kernel.modules.context.ContextPanel;
import net.sourceforge.atunes.kernel.modules.context.ContextPanelContent;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.LanguageTool;

/**
 * Context panel to show song information
 * @author alex
 *
 */
public class AudioObjectContextPanel extends ContextPanel {
	
	private static final long serialVersionUID = -7910261492394049289L;

	private List<ContextPanelContent> contents;
	
	@Override
	protected ImageIcon getContextPanelIcon(AudioObject audioObject) {
		if (audioObject instanceof AudioFile || (audioObject instanceof Radio && ((Radio)audioObject).isSongInfoAvailable())) {
			return ImageLoader.getImage(ImageLoader.AUDIO_FILE_LITTLE);
		} else if (audioObject instanceof Radio) {
			return ImageLoader.getImage(ImageLoader.RADIO_LITTLE);
		} else if (audioObject instanceof PodcastFeedEntry) {
			return ImageLoader.getImage(ImageLoader.RSS_LITTLE);
		}
		
		return null;
	}
	
	@Override
	public String getContextPanelName() {
		return "AUDIOOBJECT";
	}
	
	@Override
	protected String getContextPanelTitle(AudioObject audioObject) {
		if (audioObject instanceof AudioFile || (audioObject instanceof Radio && ((Radio)audioObject).isSongInfoAvailable())) {
			return LanguageTool.getString("SONG");
		} else if (audioObject instanceof Radio) {
			return LanguageTool.getString("RADIO");
		} else if (audioObject instanceof PodcastFeedEntry) {
			return LanguageTool.getString("PODCAST_FEED");
		}
		
		return null;
	}

	@Override
	protected List<ContextPanelContent> getContents() {
		if (contents == null) {
			contents = new ArrayList<ContextPanelContent>();
			contents.add(new AudioObjectBasicInfoContent());
			contents.add(new LyricsContent());
		}
		return contents;
	}
	
	@Override
	protected boolean isPanelEnabledForAudioObject(AudioObject audioObject) {
		return true;
	}
	

}
