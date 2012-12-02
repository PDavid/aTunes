/*
 * aTunes 3.0.0
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

package net.sourceforge.atunes.kernel.modules.state;

import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingConstants;

import net.sourceforge.atunes.gui.frame.FramePosition;
import net.sourceforge.atunes.gui.frame.FrameSize;
import net.sourceforge.atunes.gui.frame.FrameState;
import net.sourceforge.atunes.model.ColumnBean;
import net.sourceforge.atunes.model.FontSettings;
import net.sourceforge.atunes.model.IColorBean;
import net.sourceforge.atunes.model.IColorBeanFactory;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IFramePosition;
import net.sourceforge.atunes.model.IFrameSize;
import net.sourceforge.atunes.model.IFrameState;
import net.sourceforge.atunes.model.IStateUI;
import net.sourceforge.atunes.model.LookAndFeelBean;

/**
 * This class represents the application settings that are stored at application
 * shutdown and loaded at application startup.
 * <p>
 * <b>NOTE: All classes that are used as properties must be Java Beans!</b>
 * </p>
 */
public class ApplicationStateUI implements IStateUI {

	private static final String UNCHECKED = "unchecked";
	
	/**
     * Component responsible of store state
     */
    private IStateStore stateStore;
    
    private IColorBeanFactory colorBeanFactory;
    
    /**
     * @param colorBeanFactory
     */
    public void setColorBeanFactory(IColorBeanFactory colorBeanFactory) {
		this.colorBeanFactory = colorBeanFactory;
	}
    
    /**
     * Sets state store
     * @param store
     */
    public void setStateStore(IStateStore store) {
		this.stateStore = store;
	}

    @Override
	public boolean isShowStatusBar() {
        return (Boolean) this.stateStore.retrievePreference(Preferences.SHOW_STATUS_BAR, true);
    }

    @Override
	public void setShowStatusBar(boolean showStatusBar) {
        this.stateStore.storePreference(Preferences.SHOW_STATUS_BAR, showStatusBar);
    }
    
    @Override
	public boolean isShowOSD() {
        return (Boolean) this.stateStore.retrievePreference(Preferences.SHOW_OSD, true);
    }

    @Override
	public void setShowOSD(boolean showOSD) {
    	this.stateStore.storePreference(Preferences.SHOW_OSD, showOSD);
    }
    
    @Override
	public boolean isShowSystemTray() {
        return (Boolean) this.stateStore.retrievePreference(Preferences.SHOW_SYSTEM_TRAY, false);
    }

    @Override
	public void setShowSystemTray(boolean showSystemTray) {
    	this.stateStore.storePreference(Preferences.SHOW_SYSTEM_TRAY, showSystemTray);
    }
    
    @Override
	public boolean isShowTrayPlayer() {
        return (Boolean) this.stateStore.retrievePreference(Preferences.SHOW_TRAY_PLAYER, false);
    }

    @Override
	public void setShowTrayPlayer(boolean showTrayPlayer) {
    	this.stateStore.storePreference(Preferences.SHOW_TRAY_PLAYER, showTrayPlayer);
    }
    
    @Override
	@SuppressWarnings(UNCHECKED)
	public Class<? extends IFrame> getFrameClass() {
        return (Class<? extends IFrame>) this.stateStore.retrievePreference(Preferences.FRAME_CLASS, null);
    }

    @Override
	public void setFrameClass(Class<? extends IFrame> frameClass) {
    	this.stateStore.storePreference(Preferences.FRAME_CLASS, frameClass);
    }
    
    @Override
	public LookAndFeelBean getLookAndFeel() {
        return (LookAndFeelBean) this.stateStore.retrievePreference(Preferences.LOOK_AND_FEEL, null);
    }

    @Override
	public void setLookAndFeel(LookAndFeelBean lookAndFeel) {
    	this.stateStore.storePreference(Preferences.LOOK_AND_FEEL, lookAndFeel);
    }

    @Override
	public FontSettings getFontSettings() {
        return (FontSettings) this.stateStore.retrievePreference(Preferences.FONT_SETTINGS, null);
    }

    @Override
	public void setFontSettings(FontSettings fontSettings) {
    	this.stateStore.storePreference(Preferences.FONT_SETTINGS, fontSettings);
    }
    
    @Override
	public boolean isShowAdvancedPlayerControls() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.SHOW_ADVANCED_PLAYER_CONTROLS, false);
    }
    
    @Override
	public void setShowAdvancedPlayerControls(boolean show) {
    	this.stateStore.storePreference(Preferences.SHOW_ADVANCED_PLAYER_CONTROLS, show);
    }

    @Override
	public int getOsdDuration() {
    	return (Integer) this.stateStore.retrievePreference(Preferences.OSD_DURATION, 2);
    }

    @Override
	public void setOsdDuration(int osdDuration) {
    	this.stateStore.storePreference(Preferences.OSD_DURATION, osdDuration);
    }

    @Override
	public String getFullScreenBackground() {
    	return (String) this.stateStore.retrievePreference(Preferences.FULL_SCREEN_BACKGROUND, null);
    }

    @Override
	public void setFullScreenBackground(String fullScreenBackground) {
    	this.stateStore.storePreference(Preferences.FULL_SCREEN_BACKGROUND, fullScreenBackground);
    }

    @Override
	@SuppressWarnings(UNCHECKED)
	public Map<String, ColumnBean> getSearchResultsColumns() {
    	Map<String, ColumnBean> map = (Map<String, ColumnBean>) this.stateStore.retrievePreference(Preferences.SEARCH_RESULTS_COLUMNS, null);
    	return map != null ? Collections.unmodifiableMap(map) : null;
    }

    @Override
	public void setSearchResultsColumns(Map<String, ColumnBean> searchResultsColumns) {
    	if (getSearchResultsColumns() == null || !getSearchResultsColumns().equals(searchResultsColumns)) {
    		this.stateStore.storePreference(Preferences.SEARCH_RESULTS_COLUMNS, searchResultsColumns);
    	}
    }
    
    @Override
	public FrameState getFrameState(Class<? extends IFrame> frame) {
    	// Map creation is controlled in this class to avoid modification without persistence 
    	@SuppressWarnings(UNCHECKED)
		Map<Class<? extends IFrame>, FrameState> state = (Map<Class<? extends IFrame>, FrameState>) this.stateStore.retrievePreference(Preferences.FRAME_STATES, null);
    	if (state == null) {
    		state = new HashMap<Class<? extends IFrame>, FrameState>();
    		this.stateStore.storePreference(Preferences.FRAME_STATES, state);
    	}
    	// Clone object to be sure changes made by application to frame state are not made over object in cache
    	return state.containsKey(frame) ? new FrameState(state.get(frame)) : null;
    }

    @Override
	public void setFrameState(Class<? extends IFrame> frame, IFrameState fs) {
    	// Clone object to be sure changes made by application to frame state are not made over object in cache
    	FrameState frameState = new FrameState(fs);
    	if (getFrameState(frame) == null || !getFrameState(frame).equals(frameState)) {
        	@SuppressWarnings(UNCHECKED)
    		Map<Class<? extends IFrame>, FrameState> state = (Map<Class<? extends IFrame>, FrameState>) this.stateStore.retrievePreference(Preferences.FRAME_STATES, null);
    		if (state == null) {
    			state = new HashMap<Class<? extends IFrame>, FrameState>();
    		}
    		state.put(frame, frameState);
    		this.stateStore.storePreference(Preferences.FRAME_STATES, state);
    	}
    }
    
    @Override
	public int getOsdWidth() {
    	return (Integer) this.stateStore.retrievePreference(Preferences.OSD_WIDTH, 300);
    }

    @Override
	public void setOsdWidth(int osdWidth) {
    	this.stateStore.storePreference(Preferences.OSD_WIDTH, osdWidth);
    }
    
    @Override
	public int getOsdHorizontalAlignment() {
    	return (Integer) this.stateStore.retrievePreference(Preferences.OSD_HORIZONTAL_ALINGMENT, SwingConstants.RIGHT);        
    }

    @Override
	public void setOsdHorizontalAlignment(int osdHorizontalAlignment) {
    	this.stateStore.storePreference(Preferences.OSD_HORIZONTAL_ALINGMENT, osdHorizontalAlignment);
    }
    
    @Override
	public int getOsdVerticalAlignment() {
    	return (Integer) this.stateStore.retrievePreference(Preferences.OSD_VERTICAL_ALINGMENT, SwingConstants.BOTTOM);        
    }

    @Override
	public void setOsdVerticalAlignment(int osdVerticalAlignment) {
    	this.stateStore.storePreference(Preferences.OSD_VERTICAL_ALINGMENT, osdVerticalAlignment);
    }
    
    @Override
	public IColorBean getTrayPlayerIconsColor() {
    	return (IColorBean) this.stateStore.retrievePreference(Preferences.TRAY_PLAYER_ICONS_COLOR, colorBeanFactory.getColorBean(Color.BLACK));
    }
    
    @Override
	public void setTrayPlayerIconsColor(IColorBean color) {
    	this.stateStore.storePreference(Preferences.TRAY_PLAYER_ICONS_COLOR, color);
    }
    
    @Override
	public boolean isShowPlayerControlsOnTop() {
    	return (Boolean) this.stateStore.retrievePreference(Preferences.SHOW_PLAYER_CONTROLS_ON_TOP, true);
    }
    
    @Override
	public void setShowPlayerControlsOnTop(boolean onTop) {
    	this.stateStore.storePreference(Preferences.SHOW_PLAYER_CONTROLS_ON_TOP, onTop);
    }
    
    @Override
    public IFrameSize getFrameSize() {
    	return (IFrameSize) this.stateStore.retrievePreference(Preferences.FRAME_SIZE, new FrameSize());
    }
    
    @Override
    public void setFrameSize(IFrameSize frameSize) {
    	this.stateStore.storePreference(Preferences.FRAME_SIZE, frameSize);
    }
    
    @Override
    public IFramePosition getFramePosition() {
    	return (IFramePosition) this.stateStore.retrievePreference(Preferences.FRAME_POSITION, new FramePosition());
    }
    
    @Override
    public void setFramePosition(IFramePosition framePosition) {
    	this.stateStore.storePreference(Preferences.FRAME_POSITION, framePosition);
    }
}
