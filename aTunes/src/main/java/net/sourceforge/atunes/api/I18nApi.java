package net.sourceforge.atunes.api;

import net.sourceforge.atunes.utils.I18nUtils;

import org.commonjukebox.plugins.PluginApi;

@PluginApi
public class I18nApi {
	
	/**
	 * Returns a text for the given key in the currently selected language
	 * @param key
	 * @return
	 */
	public static String getString(String key) {
		return I18nUtils.getString(key);
	}

}
