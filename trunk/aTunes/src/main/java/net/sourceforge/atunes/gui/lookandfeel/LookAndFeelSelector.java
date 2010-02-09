/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import java.awt.Window;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import net.sourceforge.atunes.gui.lookandfeel.nimbus.NimbusLookAndFeel;
import net.sourceforge.atunes.gui.lookandfeel.substance.SubstanceLookAndFeel;
import net.sourceforge.atunes.gui.lookandfeel.system.SystemLookAndFeel;
import net.sourceforge.atunes.kernel.Kernel;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;

import org.commonjukebox.plugins.Plugin;
import org.commonjukebox.plugins.PluginInfo;
import org.commonjukebox.plugins.PluginListener;
import org.commonjukebox.plugins.PluginSystemException;

public final class LookAndFeelSelector implements PluginListener {

    /**
     * Singleton instance
     */
    private static LookAndFeelSelector instance;

    /**
     * Logger
     */
    private Logger logger;

    /**
     * Current look and feel
     */
    private LookAndFeel currentLookAndFeel;

    /**
     * Map containing look and feels
     */
    private Map<String, LookAndFeel> lookAndFeels;

    /**
     * Default look and feel
     */
    private static LookAndFeel defaultLookAndFeel = new SubstanceLookAndFeel();

    /**
     * Default constructor
     */
    private LookAndFeelSelector() {
        lookAndFeels = new HashMap<String, LookAndFeel>();

        // Default look and feel
        lookAndFeels.put(defaultLookAndFeel.getName(), defaultLookAndFeel);

        // System look and feel
        SystemLookAndFeel system = new SystemLookAndFeel();
        lookAndFeels.put(system.getName(), system);

        // Nimbus look and feel (only 1.6.10 or later)
        if (SystemProperties.IS_JAVA_6_UPDATE_10_OR_LATER) {
            NimbusLookAndFeel nimbus = new NimbusLookAndFeel();
            lookAndFeels.put(nimbus.getName(), nimbus);
        }
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
            LookAndFeel laf = (LookAndFeel) plugin.getInstance();
            lookAndFeels.put(laf.getName(), laf);
        } catch (PluginSystemException e) {
            getLogger().error(LogCategories.PLUGINS, e);
        }
    }

    @Override
    public void pluginDeactivated(PluginInfo arg0, Collection<Plugin> instances) {
        for (Plugin instance : instances) {
            lookAndFeels.remove(((LookAndFeel) instance).getName());
        }
    }

    /**
     * Sets the look and feel.
     * 
     * @param theme
     *            the new look and feel
     */
    public void setLookAndFeel(LookAndFeelBean lookAndFeelBean) {
        if (Kernel.IGNORE_LOOK_AND_FEEL) {
            return;
        }

        if (lookAndFeelBean == null || lookAndFeelBean.getName() == null) {
            lookAndFeelBean = new LookAndFeelBean();
            lookAndFeelBean.setName(defaultLookAndFeel.getName());
            lookAndFeelBean.setSkin(defaultLookAndFeel.getDefaultSkin());
            if (ApplicationState.getInstance().getLookAndFeel() == null) {
                ApplicationState.getInstance().setLookAndFeel(lookAndFeelBean);
            }
        }

        currentLookAndFeel = lookAndFeels.get(lookAndFeelBean.getName());
        if (currentLookAndFeel == null) {
            currentLookAndFeel = defaultLookAndFeel;
        }

        currentLookAndFeel.initializeLookAndFeel();
        currentLookAndFeel.setLookAndFeel(lookAndFeelBean.getSkin());
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
        LookAndFeel lookAndFeel = lookAndFeels.get(lookAndFeelName);
        if (lookAndFeel != null) {
            return lookAndFeel.getSkins() != null ? lookAndFeel.getSkins() : new ArrayList<String>();
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
    }

    /**
     * @return the currentLookAndFeel
     */
    public LookAndFeel getCurrentLookAndFeel() {
        return currentLookAndFeel;
    }

    /**
     * Returns default skin for a given look and feel
     * 
     * @param lookAndFeelName
     * @return
     */
    public String getDefaultSkin(String lookAndFeelName) {
        LookAndFeel lookAndFeel = lookAndFeels.get(lookAndFeelName);
        if (lookAndFeel != null) {
            return lookAndFeel.getDefaultSkin();
        }
        return null;
    }

    /**
     * Returns logger
     * 
     * @return
     */
    private Logger getLogger() {
        if (logger == null) {
            logger = new Logger();
        }
        return logger;
    }

    /**
     * @return the defaultLookAndFeel
     */
    public static LookAndFeel getDefaultLookAndFeel() {
        return defaultLookAndFeel;
    }
}
