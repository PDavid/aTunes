package net.sourceforge.atunes.plugins.webinterface;

import java.util.ArrayList;
import java.util.Collection;

import net.sourceforge.atunes.api.LoggerService;
import net.sourceforge.atunes.kernel.modules.plugins.AbstractGeneralPurposePlugin;
import net.sourceforge.atunes.utils.StringUtils;

import org.commonjukebox.plugins.exceptions.InvalidPluginConfigurationException;
import org.commonjukebox.plugins.model.Plugin;
import org.commonjukebox.plugins.model.PluginConfiguration;
import org.commonjukebox.plugins.model.PluginInfo;
import org.commonjukebox.plugins.model.PluginProperty;

public class WebInterfacePlugin extends AbstractGeneralPurposePlugin implements Plugin {
	
	/**
	 * Directory where plugin is installed
	 */
	private String pluginLocation;
	
	/**
	 * "pub" directory: static contents
	 */
	private String pubResourceDir;
	
	/**
	 * "template" directory: dynamic contents
	 */
	private String templateResourceDir;
	
	/**
	 * Port used to listen connections
	 */
	private int port;
	
	@Override
	public void activate() {
		try {
			WebServer.start(this.port, this.pubResourceDir, this.templateResourceDir, this.pluginLocation);
		} catch (Exception e) {
			new LoggerService().error(e);
		}
	}
	
	@Override
	public void deactivate() {
		WebServer.stop();
	}
	
	@Override
	public void configurationChanged(PluginConfiguration newConfiguration) {
		this.port = Integer.valueOf((String)newConfiguration.getProperty("PORT").getValue());
		deactivate();
		activate();
	}
	
	@Override
	public PluginConfiguration getDefaultConfiguration() {
		PluginProperty<String> property = new PluginProperty<String>();
		property.setName("PORT");
		property.setDescription("Listening port");
		property.setValue("8000");
		Collection<PluginProperty<?>> properties = new ArrayList<PluginProperty<?>>();
		properties.add(property);
		PluginConfiguration defaultConfiguration = new PluginConfiguration();
		defaultConfiguration.setProperties(properties);
		return defaultConfiguration;
	}
	
	@Override
	public void setConfiguration(PluginConfiguration configuration) {
		this.port = Integer.valueOf((String)configuration.getProperty("PORT").getValue());
	}
	
	@Override
	public void setPluginInfo(PluginInfo pluginInfo) {
		this.pluginLocation = pluginInfo.getPluginFolder().getAbsolutePath();		
		this.pubResourceDir = StringUtils.getString(this.pluginLocation, "/pub/");
		this.templateResourceDir = StringUtils.getString(this.pluginLocation, "/templates/");
	}
	
	@Override
	public void validateConfiguration(PluginConfiguration configuration) throws InvalidPluginConfigurationException {
		if (configuration.getProperty("PORT") == null) {
			throw new InvalidPluginConfigurationException("Listening port not defined");
		}
		Object value = configuration.getProperty("PORT").getValue();
		int port = 0;
		try {
			port = Integer.parseInt((String)value);
		} catch (NumberFormatException e) {
			throw new InvalidPluginConfigurationException(StringUtils.getString(value, " is not a valid number"));
		}
		if (port <= 1024) {
			throw new InvalidPluginConfigurationException("Listening port must be a number greater than 1024");
		}		
	}
}
