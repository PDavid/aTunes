/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2011 Alex Aranda, Sylvain Gaudard and contributors
 *
 * See http://www.atunes.org/wiki/index.php?title=Contributing for information about contributors
 *
 * http://www.atunes.org
 * http://sourceforge.net/projects/atunes
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package net.sourceforge.atunes.gui.lookandfeel;

import java.awt.Font;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import net.sourceforge.atunes.gui.views.dialogs.FontChooserDialog.FontSettings;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.OsManager;
import net.sourceforge.atunes.kernel.modules.plugins.PluginsHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.state.beans.FontBean;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;

import org.commonjukebox.plugins.exceptions.PluginSystemException;
import org.commonjukebox.plugins.model.Plugin;
import org.commonjukebox.plugins.model.PluginInfo;
import org.commonjukebox.plugins.model.PluginListener;

public final class LookAndFeelSelector implements PluginListener {

    private static final boolean USE_FONT_SMOOTHING_SETTINGS_FROM_OS_DEFAULT_VALUE = false;
    private static final boolean USE_FONT_SMOOTHING_DEFAULT_VALUE = true;

    /**
     * Singleton instance
     */
    private static LookAndFeelSelector instance;

    /**
     * Current look and feel
     */
    private AbstractLookAndFeel currentLookAndFeel;

    /**
     * Map containing look and feels
     */
    private Map<String, Class<? extends AbstractLookAndFeel>> lookAndFeels;

    /**
     * Default look and feel
     */
    private static Class<? extends AbstractLookAndFeel> defaultLookAndFeelClass;
    
    /**
     * Look and Feel change listeners
     */
    private static List<LookAndFeelChangeListener> changeListeners;

    /**
     * Default constructor
     */
    private LookAndFeelSelector() {
        lookAndFeels = OsManager.getLookAndFeels();
        defaultLookAndFeelClass = OsManager.getDefaultLookAndFeel();
    }

    /**
     * Returns singleton instance
     * 
     * @return
     */
    public static LookAndFeelSelector getInstance() {
        if (instance == null) {
            instance = new LookAndFeelSelector();
        }
        return instance;
    }

    @Override
    public void pluginActivated(PluginInfo plugin) {
        try {
        	AbstractLookAndFeel laf = (AbstractLookAndFeel) PluginsHandler.getInstance().getNewInstance(plugin);
            lookAndFeels.put(laf.getName(), laf.getClass());
        } catch (PluginSystemException e) {
            Logger.error(LogCategories.PLUGINS, e);
        }
    }

    @Override
    public void pluginDeactivated(PluginInfo arg0, Collection<Plugin> instances) {
        for (Plugin instance : instances) {
            lookAndFeels.remove(((AbstractLookAndFeel) instance).getName());
        }
    }

    /**
     * Sets the look and feel.
     * 
     * @param theme
     *            the new look and feel
     */
    public void setLookAndFeel(LookAndFeelBean lookAndFeelBean) {
        if (Kernel.isIgnoreLookAndFeel()) {
            return;
        }

        if (lookAndFeelBean == null || lookAndFeelBean.getName() == null) {
            lookAndFeelBean = new LookAndFeelBean();
            AbstractLookAndFeel defaultLookAndFeel = null;
			try {
				defaultLookAndFeel = defaultLookAndFeelClass.newInstance();
			} catch (InstantiationException e) {
				Logger.error(LogCategories.DESKTOP, e);
			} catch (IllegalAccessException e) {
				Logger.error(LogCategories.DESKTOP, e);
			}
            lookAndFeelBean.setName(defaultLookAndFeel.getName());
            lookAndFeelBean.setSkin(defaultLookAndFeel.getDefaultSkin());
            if (ApplicationState.getInstance().getLookAndFeel() == null) {
                ApplicationState.getInstance().setLookAndFeel(lookAndFeelBean);
            }
        }

        Class<? extends AbstractLookAndFeel> currentLookAndFeelClass = lookAndFeels.get(lookAndFeelBean.getName());
        if (currentLookAndFeelClass == null) {
            currentLookAndFeelClass = defaultLookAndFeelClass;
        }

        try {
			currentLookAndFeel = currentLookAndFeelClass.newInstance();
		} catch (InstantiationException e) {
			Logger.error(LogCategories.DESKTOP, e);
		} catch (IllegalAccessException e) {
			Logger.error(LogCategories.DESKTOP, e);
		}
		
        currentLookAndFeel.initializeLookAndFeel();
        currentLookAndFeel.setLookAndFeel(lookAndFeelBean.getSkin());        
        initializeFonts(currentLookAndFeel);
    }
    
    /**
     * Initializes fonts for look and feel
     * @param lookAndFeel
     */
    private void initializeFonts(AbstractLookAndFeel lookAndFeel) {
		FontSettings fontSettings = ApplicationState.getInstance().getFontSettings();
		if (lookAndFeel.supportsCustomFontSettings() && fontSettings != null && !fontSettings.isUseFontSmoothingSettingsFromOs()) {
			if (fontSettings.isUseFontSmoothing()) {
				System.setProperty("awt.useSystemAAFontSettings", "lcd");
			} else {
				System.setProperty("awt.useSystemAAFontSettings", "false");
			}
		} else {
			System.setProperty("awt.useSystemAAFontSettings", "lcd");
		}


    	Font font = UIManager.getFont("Label.font");
    	if (lookAndFeel.supportsCustomFontSettings()) {
    		if (fontSettings != null) {
    			font = fontSettings.getFont().toFont();
    		} else {
    			/*
    			 * Get appropriate font for the currently selected language. For
    			 * Chinese or Japanese we should use default font.
    			 */
    			if ("zh".equals(ApplicationState.getInstance().getLocale().getLanguage()) || "ja".equals(ApplicationState.getInstance().getLocale().getLanguage())) {
    				font = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    			} else {
    				font = UIManager.getFont("Label.font");
    			}
    			ApplicationState.getInstance().setFontSettings(new FontSettings(new FontBean(font), USE_FONT_SMOOTHING_DEFAULT_VALUE, USE_FONT_SMOOTHING_SETTINGS_FROM_OS_DEFAULT_VALUE));
    		}
    	}
		lookAndFeel.baseFont = font;
		lookAndFeel.initializeFonts(font);
    }

    /**
     * Returns available look and feels
     */
    public List<String> getAvailableLookAndFeels() {
        return new ArrayList<String>(lookAndFeels.keySet());
    }

    /**
     * Returns available skins for given look and feel
     * 
     * @param lookAndFeelName
     * @return
     */
    public List<String> getAvailableSkins(String lookAndFeelName) {
    	Class<? extends AbstractLookAndFeel> clazz = lookAndFeels.get(lookAndFeelName);
    	if (clazz != null) {
    		AbstractLookAndFeel lookAndFeel = null;
			try {
				lookAndFeel = clazz.newInstance();
			} catch (InstantiationException e) {
				Logger.error(LogCategories.DESKTOP, e);
			} catch (IllegalAccessException e) {
				Logger.error(LogCategories.DESKTOP, e);
			}
    		if (lookAndFeel != null) {
    			return lookAndFeel.getSkins() != null ? lookAndFeel.getSkins() : new ArrayList<String>();
    		}
    	}
        return new ArrayList<String>();
    }

    /**
     * Returns the name of the current look and feel
     * 
     * @return
     */
    public String getCurrentLookAndFeelName() {
        return currentLookAndFeel.getName();
    }

    /**
     * Updates the user interface to use a new skin
     * 
     * @param selectedSkin
     *            The new skin
     */
    public void applySkin(String selectedSkin) {
        LookAndFeelBean bean = new LookAndFeelBean();
        bean.setName(currentLookAndFeel.getName());
        bean.setSkin(selectedSkin);
        setLookAndFeel(bean);
        for (Window window : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(window);
        }
        // Notify listeners
        for (LookAndFeelChangeListener listener : getChangeListeners()) {
        	listener.lookAndFeelChanged();
        }
    }

    /**
     * @return the currentLookAndFeel
     */
    public AbstractLookAndFeel getCurrentLookAndFeel() {
        return currentLookAndFeel;
    }

    /**
     * Returns default skin for a given look and feel
     * 
     * @param lookAndFeelName
     * @return
     */
    public String getDefaultSkin(String lookAndFeelName) {
    	Class<? extends AbstractLookAndFeel> clazz = lookAndFeels.get(lookAndFeelName);
    	if (clazz != null) {
    		AbstractLookAndFeel lookAndFeel = null;
    		try {
				lookAndFeel = clazz.newInstance();
			} catch (InstantiationException e) {
				Logger.error(LogCategories.DESKTOP, e);
			} catch (IllegalAccessException e) {
				Logger.error(LogCategories.DESKTOP, e);
			}
    		if (lookAndFeel != null) {
    			return lookAndFeel.getDefaultSkin();
    		}
    	}
        return null;
    }

    /**
     * @return the defaultLookAndFeel
     */
    public AbstractLookAndFeel getDefaultLookAndFeel() {
        try {
			return defaultLookAndFeelClass.newInstance();
		} catch (InstantiationException e) {
			Logger.error(LogCategories.DESKTOP, e);
		} catch (IllegalAccessException e) {
			Logger.error(LogCategories.DESKTOP, e);
		}
		return null;
    }

	/**
	 * @return the changeListeners
	 */
	protected static List<LookAndFeelChangeListener> getChangeListeners() {
		if (changeListeners == null) {
			changeListeners = new ArrayList<LookAndFeelChangeListener>();
		}
		return changeListeners;
	}
	
	/**
	 * Adds a new look and feel change listener
	 * @param listener
	 */
	public void addLookAndFeelChangeListener(LookAndFeelChangeListener listener) {
		getChangeListeners().add(listener);
	}
	
	/**
	 * Removes a look and feel change listener
	 * @param listener
	 */
	public void removeLookAndFeelChangeListener(LookAndFeelChangeListener listener) {
		getChangeListeners().remove(listener);
	}
}
