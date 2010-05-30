package net.sourceforge.atunes.plugins.columns;

import net.sourceforge.atunes.kernel.modules.columns.AbstractColumn;
import net.sourceforge.atunes.model.AudioObject;

import org.commonjukebox.plugins.exceptions.InvalidPluginConfigurationException;
import org.commonjukebox.plugins.model.Plugin;
import org.commonjukebox.plugins.model.PluginConfiguration;
import org.commonjukebox.plugins.model.PluginInfo;

public class CommentColumn extends AbstractColumn implements Plugin {

    private static final long serialVersionUID = 409180231407332024L;

    public CommentColumn() {
        super("COMMENT", String.class);
    }

    @Override
    protected int ascendingCompare(AudioObject ao1, AudioObject ao2) {
        return ao1.getComment().compareTo(ao2.getComment());
    }

    @Override
    public Object getValueFor(AudioObject audioObject) {
        return audioObject.getComment();
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
