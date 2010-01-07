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

public final class LookAndFeelSelector {

    /**
     * Current look and feel
     */
    private static LookAndFeel currentLookAndFeel;
    
    /**
     * Map containing look and feels
     */
    private static Map<String, LookAndFeel> lookAndFeels;
    
    /**
     * Default look and feel
     */
    private static LookAndFeel defaultLookAndFeel = new SubstanceLookAndFeel();
    
    static {
    	lookAndFeels = new HashMap<String, LookAndFeel>();    	
    	
    	lookAndFeels.put(defaultLookAndFeel.getName(), defaultLookAndFeel);
    	
    	SystemLookAndFeel system = new SystemLookAndFeel();
    	lookAndFeels.put(system.getName(), system);
    	
    	if (SystemProperties.IS_JAVA_6_UPDATE_10_OR_LATER) {
    		NimbusLookAndFeel nimbus = new NimbusLookAndFeel();
    		lookAndFeels.put(nimbus.getName(), nimbus);
    	}
    }

    /**
     * Sets the look and feel.
     * 
     * @param theme
     *            the new look and feel
     */
    public static void setLookAndFeel(LookAndFeelBean lookAndFeelBean) {    	
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
    public static List<String> getAvailableLookAndFeels() {
    	return new ArrayList<String>(lookAndFeels.keySet());
    }
    
    /**
     * Returns available skins for given look and feel
     * @param lookAndFeelName
     * @return
     */
    public static List<String> getAvailableSkins(String lookAndFeelName) {
    	LookAndFeel lookAndFeel = lookAndFeels.get(lookAndFeelName);
    	if (lookAndFeel != null) {
    		return lookAndFeel.getSkins() != null ? lookAndFeel.getSkins() : new ArrayList<String>();
    	}
    	return new ArrayList<String>();
    }
    
    /**
     * Returns the name of the current look and feel
     * @return
     */
    public static String getCurrentLookAndFeelName() {
    	return currentLookAndFeel.getName();
    }
    
    /**
     * Updates the user interface to use a new skin
     * 
     * @param selectedSkin
     *            The new skin
     */
    public static void applySkin(String selectedSkin) {
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
	public static LookAndFeel getCurrentLookAndFeel() {
		return currentLookAndFeel;
	}
	
	/**
	 * Returns default skin for a given look and feel
	 * @param lookAndFeelName
	 * @return
	 */
	public static String getDefaultSkin(String lookAndFeelName) {
		LookAndFeel lookAndFeel = lookAndFeels.get(lookAndFeelName);
		if (lookAndFeel != null) {
			return lookAndFeel.getDefaultSkin();
		}
		return null;
	}
}
