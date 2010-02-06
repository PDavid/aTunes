package net.sourceforge.atunes.plugins.webinterface;

import java.util.ArrayList;
import java.util.Collection;

import net.sourceforge.atunes.api.LoggerService;
import net.sourceforge.atunes.kernel.modules.plugins.GeneralPurposePlugin;
import net.sourceforge.atunes.utils.StringUtils;

import org.commonjukebox.plugins.Plugin;
import org.commonjukebox.plugins.PluginConfiguration;
import org.commonjukebox.plugins.PluginInfo;
import org.commonjukebox.plugins.PluginProperty;

public class WebInterfacePlugin extends GeneralPurposePlugin implements Plugin {
	
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
			WebServer.start(this.port, this.pubResourceDir, this.templateResourceDir);
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
		PluginConfiguration defaultConfiguration = new PluginConfiguration(properties);
		return defaultConfiguration;
	}
	
	@Override
	public void setConfiguration(PluginConfiguration configuration) {
		this.port = Integer.valueOf((String)configuration.getProperty("PORT").getValue());
	}
	
	@Override
	public void setPluginInfo(PluginInfo pluginInfo) {
		String pluginLocation = pluginInfo.getPluginLocation();
		this.pubResourceDir = StringUtils.getString(pluginLocation, "/pub/");
		this.templateResourceDir = StringUtils.getString(pluginLocation, "/templates/");
	}
	
	

}
