package net.sourceforge.atunes.plugins.context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.commonjukebox.plugins.Plugin;
import org.commonjukebox.plugins.PluginConfiguration;
import org.commonjukebox.plugins.PluginInfo;

import net.sourceforge.atunes.kernel.modules.context.ContextPanel;
import net.sourceforge.atunes.kernel.modules.context.ContextPanelContent;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.kernel.modules.repository.data.Artist;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.model.AudioObject;

public class LastFmEventContextPanel extends ContextPanel implements Plugin {

    private List<ContextPanelContent> contents;

	@Override
	protected List<ContextPanelContent> getContents() {
		if (contents == null) {
			contents = new ArrayList<ContextPanelContent>();
			contents.add(new LastFmEventContent());
		}
		return contents;
	}

	@Override
	protected ImageIcon getContextPanelIcon(AudioObject audioObject) {
		try {
			return new ImageIcon(ImageIO.read(getClass().getResourceAsStream("calendar.png")));
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public String getContextPanelName() {
		return "EVENTS";
	}

	@Override
	protected String getContextPanelTitle(AudioObject audioObject) {
		return "Events";
	}

	@Override
	protected boolean isPanelEnabledForAudioObject(AudioObject audioObject) {
		return (audioObject instanceof AudioFile || audioObject instanceof Radio && ((Radio)audioObject).isSongInfoAvailable()) && 
		        !Artist.isUnknownArtist(audioObject.getArtist());
	}

	@Override
	public void configurationChanged(PluginConfiguration newConfiguration) {
	}
	
	@Override
	public PluginConfiguration getDefaultConfiguration() {
		return null;
	}
	
	@Override
	public void setConfiguration(PluginConfiguration configuration) {
	}
	
	@Override
	public void setPluginInfo(PluginInfo pluginInfo) {
	}
	
	@Override
	public void validateConfiguration(PluginConfiguration configuration) {
	}
}
