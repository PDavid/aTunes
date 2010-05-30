package net.sourceforge.atunes.plugins.columns;

import net.sourceforge.atunes.kernel.modules.columns.AbstractColumn;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.model.AudioObject;

import org.commonjukebox.plugins.exceptions.InvalidPluginConfigurationException;
import org.commonjukebox.plugins.model.Plugin;
import org.commonjukebox.plugins.model.PluginConfiguration;
import org.commonjukebox.plugins.model.PluginInfo;

public class LyricsColumn extends AbstractColumn implements Plugin {

    private static final long serialVersionUID = 409180231407332024L;

    public LyricsColumn() {
        super("LYRICS", String.class);
    }

    @Override
    protected int ascendingCompare(AudioObject ao1, AudioObject ao2) {
        if (!(ao1 instanceof AudioFile) || !(ao2 instanceof AudioFile)) {
            return 0;
        }
        return ((AudioFile) ao1).getLyrics().compareTo(((AudioFile) ao2).getLyrics());
    }

    @Override
    public Object getValueFor(AudioObject audioObject) {
        if (audioObject instanceof AudioFile) {
            return ((AudioFile) audioObject).getLyrics();
        }
        return "";
    }

    @Override
    public void configurationChanged(PluginConfiguration arg0) {
        // No configuration
    }

    @Override
    public PluginConfiguration getDefaultConfiguration() {
        // No configuration
        return null;
    }

    @Override
    public void setConfiguration(PluginConfiguration arg0) {
        // No configuration
    }

    @Override
    public void setPluginInfo(PluginInfo arg0) {
        // No plugin info needed
    }

    @Override
    public void validateConfiguration(PluginConfiguration configuration) throws InvalidPluginConfigurationException {
        // No configuration
    }
}
