package net.sourceforge.atunes.plugins.instantmessaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.kernel.modules.pattern.AbstractPattern;
import net.sourceforge.atunes.kernel.modules.player.PlaybackState;
import net.sourceforge.atunes.kernel.modules.player.PlaybackStateListener;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.StringUtils;

import org.commonjukebox.plugins.InvalidPluginConfigurationException;
import org.commonjukebox.plugins.Plugin;
import org.commonjukebox.plugins.PluginConfiguration;
import org.commonjukebox.plugins.PluginInfo;
import org.commonjukebox.plugins.PluginProperty;

public abstract class UpdateMessagePlugin implements PlaybackStateListener, Plugin {
    
    private String resourceDir;
    
    private PluginConfiguration configuration;

    private PlaybackState lastState;
    
    private AudioObject lastAudioObject;
    
    private String name;
    
    private String pythonName;
    
    protected UpdateMessagePlugin(String name, String pythonName) {
        this.name = name;
        this.pythonName = pythonName;
    }
    
    @Override
    public void playbackStateChanged(PlaybackState state, AudioObject currentAudioObject) {
        this.lastState = state;
        this.lastAudioObject = currentAudioObject;
        if (state == PlaybackState.PLAYING || state == PlaybackState.RESUMING) {            
            if (currentAudioObject instanceof AudioFile) {
                setMessage(AbstractPattern.applyPatternTransformations((String)configuration.getProperty("TEXT").getValue(), (AudioFile)currentAudioObject));
            } else {
                setMessage(currentAudioObject.getTitle());
            }
        } else {
            setMessage(null);
        }
    }
    
    private void setMessage(String message) {
        try {
            Runtime.getRuntime().exec(new String[] {"python", StringUtils.getString(resourceDir, "/", pythonName, ".py"), message != null ? message : ""});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void setPluginInfo(PluginInfo info) {
        this.resourceDir = info.getPluginLocation();
    }
    
    @Override
    public PluginConfiguration getDefaultConfiguration() {
        PluginProperty<String> textProperty = new PluginProperty<String>();
        textProperty.setName("TEXT");
        textProperty.setDescription(StringUtils.getString("Text to show in ", name, " state"));
        textProperty.setValue("Listening with aTunes: %T - %A");
        List<PluginProperty<?>> properties = new ArrayList<PluginProperty<?>>();
        properties.add(textProperty);
        PluginConfiguration defaultConfiguration = new PluginConfiguration(properties);
        return defaultConfiguration;
    }
    
    @Override
    public void configurationChanged(PluginConfiguration newConfiguration) {
        this.configuration = newConfiguration;
        playbackStateChanged(lastState, lastAudioObject);
    }
    
    @Override
    public void setConfiguration(PluginConfiguration configuration) {
        this.configuration = configuration;
    }    
    
	@Override
	public void validateConfiguration(PluginConfiguration configuration) throws InvalidPluginConfigurationException {
	}

}
