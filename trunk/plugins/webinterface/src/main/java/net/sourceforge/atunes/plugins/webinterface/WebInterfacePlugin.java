package net.sourceforge.atunes.plugins.webinterface;

import net.sourceforge.atunes.api.LoggerService;
import net.sourceforge.atunes.kernel.modules.plugins.GeneralPurposePlugin;
import net.sourceforge.atunes.utils.StringUtils;

import org.commonjukebox.plugins.Plugin;
import org.commonjukebox.plugins.PluginConfiguration;
import org.commonjukebox.plugins.PluginInfo;

public class WebInterfacePlugin extends GeneralPurposePlugin implements Plugin {
	
	/**
	 * "pub" directory: static contents
	 */
	private String pubResourceDir;
	
	/**
	 * "template" directory: dynamic contents
	 */
	private String templateResourceDir;
	
	@Override
	public void activate() {
		try {
			WebServer.start(8000, this.pubResourceDir, this.templateResourceDir);
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
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public PluginConfiguration getDefaultConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setConfiguration(PluginConfiguration configuration) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setPluginInfo(PluginInfo pluginInfo) {
		String pluginLocation = pluginInfo.getPluginLocation();
		this.pubResourceDir = StringUtils.getString(pluginLocation, "/pub/");
		this.templateResourceDir = StringUtils.getString(pluginLocation, "/templates/");
	}
	
	

}
