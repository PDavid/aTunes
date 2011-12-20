/*
 * aTunes 2.2.0-SNAPSHOT
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

import net.sourceforge.atunes.ApplicationArguments;
import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.ColorDefinitions;
import net.sourceforge.atunes.model.FontSettings;
import net.sourceforge.atunes.model.IFontBeanFactory;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.ILookAndFeelChangeListener;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPluginsHandler;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.LookAndFeelBean;
import net.sourceforge.atunes.utils.Logger;

import org.commonjukebox.plugins.exceptions.PluginSystemException;
import org.commonjukebox.plugins.model.Plugin;
import org.commonjukebox.plugins.model.PluginInfo;
import org.commonjukebox.plugins.model.PluginListener;

public final class LookAndFeelManager implements PluginListener, ILookAndFeelManager {

    private static final boolean USE_FONT_SMOOTHING_SETTINGS_FROM_OS_DEFAULT_VALUE = false;
    private static final boolean USE_FONT_SMOOTHING_DEFAULT_VALUE = true;

    /**
     * Current look and feel
     */
    private ILookAndFeel currentLookAndFeel;

    /**
     * Map containing look and feels
     */
    private Map<String, Class<? extends ILookAndFeel>> lookAndFeels;

    /**
     * Default look and feel
     */
    private Class<? extends ILookAndFeel> defaultLookAndFeelClass;
    
    /**
     * Look and Feel change listeners
     */
    private List<ILookAndFeelChangeListener> changeListeners;
    
    private IFontBeanFactory fontBeanFactory;
    
    private ApplicationArguments applicationArguments;

    public LookAndFeelManager(IOSManager osManager) {
        lookAndFeels = osManager.getLookAndFeels();
        defaultLookAndFeelClass = osManager.getDefaultLookAndFeel();
    }
    
    /**
     * @param applicationArguments
     */
    public void setApplicationArguments(ApplicationArguments applicationArguments) {
		this.applicationArguments = applicationArguments;
	}
    
    /**
     * @param fontBeanFactory
     */
    public void setFontBeanFactory(IFontBeanFactory fontBeanFactory) {
		this.fontBeanFactory = fontBeanFactory;
	}

    @Override
    public void pluginActivated(PluginInfo plugin) {
        try {
        	ILookAndFeel laf = (ILookAndFeel) Context.getBean(IPluginsHandler.class).getNewInstance(plugin);
            lookAndFeels.put(laf.getName(), laf.getClass());
        } catch (PluginSystemException e) {
            Logger.error(e);
        }
    }

    @Override
    public void pluginDeactivated(PluginInfo arg0, Collection<Plugin> instances) {
        for (Plugin instance : instances) {
            lookAndFeels.remove(((ILookAndFeel) instance).getName());
        }
    }

    @Override
	public void setLookAndFeel(LookAndFeelBean lookAndFeelBean, IState state, IOSManager osManager) {
        if (applicationArguments.isIgnoreLookAndFeel()) {
            return;
        }
        
        if (lookAndFeelBean == null || lookAndFeelBean.getName() == null) {
            lookAndFeelBean = new LookAndFeelBean();
            ILookAndFeel defaultLookAndFeel = null;
			try {
				defaultLookAndFeel = defaultLookAndFeelClass.newInstance();
			} catch (InstantiationException e) {
				Logger.error(e);
			} catch (IllegalAccessException e) {
				Logger.error(e);
			}
            lookAndFeelBean.setName(defaultLookAndFeel.getName());
            lookAndFeelBean.setSkin(defaultLookAndFeel.getDefaultSkin());
            if (state.getLookAndFeel() == null) {
                state.setLookAndFeel(lookAndFeelBean);
            }
        }

        Class<? extends ILookAndFeel> currentLookAndFeelClass = lookAndFeels.get(lookAndFeelBean.getName());
        if (currentLookAndFeelClass == null) {
            currentLookAndFeelClass = defaultLookAndFeelClass;
        }

        try {
			currentLookAndFeel = currentLookAndFeelClass.newInstance();
			currentLookAndFeel.setOsManager(osManager);
		} catch (InstantiationException e) {
			Logger.error(e);
		} catch (IllegalAccessException e) {
			Logger.error(e);
		}
		
        currentLookAndFeel.initializeLookAndFeel();
        currentLookAndFeel.setLookAndFeel(lookAndFeelBean.getSkin());        
        initializeFonts(currentLookAndFeel, state);
        ColorDefinitions.initColors();
    }
    
    /**
     * Initializes fonts for look and feel
     * @param lookAndFeel
     * @param state
     */
    private void initializeFonts(ILookAndFeel lookAndFeel, IState state) {
		FontSettings fontSettings = state.getFontSettings();
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
    			if ("zh".equals(state.getLocale().getLanguage()) || "ja".equals(state.getLocale().getLanguage())) {
    				font = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    			} else {
    				font = UIManager.getFont("Label.font");
    			}
    			state.setFontSettings(new FontSettings(fontBeanFactory.getFontBean(font), USE_FONT_SMOOTHING_DEFAULT_VALUE, USE_FONT_SMOOTHING_SETTINGS_FROM_OS_DEFAULT_VALUE));
    		}
    	}
		lookAndFeel.setBaseFont(font);
		lookAndFeel.initializeFonts(font);
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeelManager#getAvailableLookAndFeels()
	 */
    @Override
	public List<String> getAvailableLookAndFeels() {
        return new ArrayList<String>(lookAndFeels.keySet());
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeelManager#getAvailableSkins(java.lang.String)
	 */
    @Override
	public List<String> getAvailableSkins(String lookAndFeelName) {
    	Class<? extends ILookAndFeel> clazz = lookAndFeels.get(lookAndFeelName);
    	if (clazz != null) {
    		ILookAndFeel lookAndFeel = null;
			try {
				lookAndFeel = clazz.newInstance();
			} catch (InstantiationException e) {
				Logger.error(e);
			} catch (IllegalAccessException e) {
				Logger.error(e);
			}
    		if (lookAndFeel != null) {
    			return lookAndFeel.getSkins() != null ? lookAndFeel.getSkins() : new ArrayList<String>();
    		}
    	}
        return new ArrayList<String>();
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeelManager#getCurrentLookAndFeelName()
	 */
    @Override
	public String getCurrentLookAndFeelName() {
        return currentLookAndFeel.getName();
    }

    @Override
	public void applySkin(String selectedSkin, IState state, IOSManager osManager) {
        LookAndFeelBean bean = new LookAndFeelBean();
        bean.setName(currentLookAndFeel.getName());
        bean.setSkin(selectedSkin);
        setLookAndFeel(bean, state, osManager);
        for (Window window : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(window);
        }
        // Notify listeners
        for (ILookAndFeelChangeListener listener : getChangeListeners()) {
        	listener.lookAndFeelChanged();
        }
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeelManager#getCurrentLookAndFeel()
	 */
    @Override
	public ILookAndFeel getCurrentLookAndFeel() {
        return currentLookAndFeel;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeelManager#getDefaultSkin(java.lang.String)
	 */
    @Override
	public String getDefaultSkin(String lookAndFeelName) {
    	Class<? extends ILookAndFeel> clazz = lookAndFeels.get(lookAndFeelName);
    	if (clazz != null) {
    		ILookAndFeel lookAndFeel = null;
    		try {
				lookAndFeel = clazz.newInstance();
			} catch (InstantiationException e) {
				Logger.error(e);
			} catch (IllegalAccessException e) {
				Logger.error(e);
			}
    		if (lookAndFeel != null) {
    			return lookAndFeel.getDefaultSkin();
    		}
    	}
        return null;
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeelManager#getDefaultLookAndFeel()
	 */
    @Override
	public ILookAndFeel getDefaultLookAndFeel() {
        try {
			return defaultLookAndFeelClass.newInstance();
		} catch (InstantiationException e) {
			Logger.error(e);
		} catch (IllegalAccessException e) {
			Logger.error(e);
		}
		return null;
    }

	/**
	 * @return the changeListeners
	 */
	protected List<ILookAndFeelChangeListener> getChangeListeners() {
		if (changeListeners == null) {
			changeListeners = new ArrayList<ILookAndFeelChangeListener>();
		}
		return changeListeners;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeelManager#addLookAndFeelChangeListener(net.sourceforge.atunes.model.ILookAndFeelChangeListener)
	 */
	@Override
	public void addLookAndFeelChangeListener(ILookAndFeelChangeListener listener) {
		getChangeListeners().add(listener);
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.lookandfeel.ILookAndFeelManager#removeLookAndFeelChangeListener(net.sourceforge.atunes.model.ILookAndFeelChangeListener)
	 */
	@Override
	public void removeLookAndFeelChangeListener(ILookAndFeelChangeListener listener) {
		getChangeListeners().remove(listener);
	}
}
