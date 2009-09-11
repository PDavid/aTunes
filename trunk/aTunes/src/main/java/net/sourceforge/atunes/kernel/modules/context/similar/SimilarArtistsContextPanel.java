package net.sourceforge.atunes.kernel.modules.context.similar;

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
 * Panel to show similar artists
 * @author alex
 *
 */
public class SimilarArtistsContextPanel extends ContextPanel {
	
	private List<ContextPanelContent> contents;
	
	@Override
	protected ImageIcon getContextPanelIcon(AudioObject audioObject) {
		return ImageLoader.getImage(ImageLoader.ARTIST_SIMILAR);
	}
	
	@Override
	public String getContextPanelName() {
		return "SIMILAR";
	}
	
	@Override
	protected String getContextPanelTitle(AudioObject audioObject) {
		return LanguageTool.getString("SIMILAR");
	}

	@Override
	protected List<ContextPanelContent> getContents() {
		if (contents == null) {
			contents = new ArrayList<ContextPanelContent>();
			contents.add(new SimilarArtistsContent());
		}
		return contents;
	}
	
	@Override
	protected boolean isPanelEnabledForAudioObject(AudioObject audioObject) {
		return (audioObject instanceof AudioFile) || (audioObject instanceof Radio && ((Radio)audioObject).isSongInfoAvailable());
	}

}
