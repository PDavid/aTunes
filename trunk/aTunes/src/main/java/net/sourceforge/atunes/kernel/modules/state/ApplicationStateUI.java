/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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
import net.sourceforge.atunes.utils.ReflectionUtils;

/**
 * This class represents the application settings that are stored at application
 * shutdown and loaded at application startup.
 * <p>
 * <b>NOTE: All classes that are used as properties must be Java Beans!</b>
 * </p>
 */
public class ApplicationStateUI implements IStateUI {

	private static final String UNCHECKED = "unchecked";

	private IColorBeanFactory colorBeanFactory;

	private PreferenceHelper preferenceHelper;

	/**
	 * @param preferenceHelper
	 */
	public void setPreferenceHelper(final PreferenceHelper preferenceHelper) {
		this.preferenceHelper = preferenceHelper;
	}

	/**
	 * @param colorBeanFactory
	 */
	public void setColorBeanFactory(final IColorBeanFactory colorBeanFactory) {
		this.colorBeanFactory = colorBeanFactory;
	}

	@Override
	public boolean isShowStatusBar() {
		return this.preferenceHelper.getPreference(Preferences.SHOW_STATUS_BAR,
				Boolean.class, true);
	}

	@Override
	public void setShowStatusBar(final boolean showStatusBar) {
		this.preferenceHelper.setPreference(Preferences.SHOW_STATUS_BAR,
				showStatusBar);
	}

	@Override
	public boolean isShowOSD() {
		return this.preferenceHelper.getPreference(Preferences.SHOW_OSD,
				Boolean.class, true);
	}

	@Override
	public void setShowOSD(final boolean showOSD) {
		this.preferenceHelper.setPreference(Preferences.SHOW_OSD, showOSD);
	}

	@Override
	public boolean isShowSystemTray() {
		return this.preferenceHelper.getPreference(
				Preferences.SHOW_SYSTEM_TRAY, Boolean.class, false);
	}

	@Override
	public void setShowSystemTray(final boolean showSystemTray) {
		this.preferenceHelper.setPreference(Preferences.SHOW_SYSTEM_TRAY,
				showSystemTray);
	}

	@Override
	public boolean isShowTrayPlayer() {
		return this.preferenceHelper.getPreference(
				Preferences.SHOW_TRAY_PLAYER, Boolean.class, false);
	}

	@Override
	public void setShowTrayPlayer(final boolean showTrayPlayer) {
		this.preferenceHelper.setPreference(Preferences.SHOW_TRAY_PLAYER,
				showTrayPlayer);
	}

	@Override
	@SuppressWarnings(UNCHECKED)
	public Class<? extends IFrame> getFrameClass() {
		return this.preferenceHelper.getPreference(Preferences.FRAME_CLASS,
				Class.class, null);
	}

	@Override
	public void setFrameClass(final Class<? extends IFrame> frameClass) {
		this.preferenceHelper
				.setPreference(Preferences.FRAME_CLASS, frameClass);
	}

	@Override
	public LookAndFeelBean getLookAndFeel() {
		return this.preferenceHelper.getPreference(Preferences.LOOK_AND_FEEL,
				LookAndFeelBean.class, null);
	}

	@Override
	public void setLookAndFeel(final LookAndFeelBean lookAndFeel) {
		this.preferenceHelper.setPreference(Preferences.LOOK_AND_FEEL,
				lookAndFeel);
	}

	@Override
	public FontSettings getFontSettings() {
		return this.preferenceHelper.getPreference(Preferences.FONT_SETTINGS,
				FontSettings.class, null);
	}

	@Override
	public void setFontSettings(final FontSettings fontSettings) {
		this.preferenceHelper.setPreference(Preferences.FONT_SETTINGS,
				fontSettings);
	}

	@Override
	public boolean isShowAdvancedPlayerControls() {
		return this.preferenceHelper
				.getPreference(Preferences.SHOW_ADVANCED_PLAYER_CONTROLS,
						Boolean.class, false);
	}

	@Override
	public void setShowAdvancedPlayerControls(final boolean show) {
		this.preferenceHelper.setPreference(
				Preferences.SHOW_ADVANCED_PLAYER_CONTROLS, show);
	}

	@Override
	public int getOsdDuration() {
		return this.preferenceHelper.getPreference(Preferences.OSD_DURATION,
				Integer.class, 2);
	}

	@Override
	public void setOsdDuration(final int osdDuration) {
		this.preferenceHelper.setPreference(Preferences.OSD_DURATION,
				osdDuration);
	}

	@Override
	public String getFullScreenBackground() {
		return this.preferenceHelper.getPreference(
				Preferences.FULL_SCREEN_BACKGROUND, String.class, null);
	}

	@Override
	public void setFullScreenBackground(final String fullScreenBackground) {
		this.preferenceHelper.setPreference(Preferences.FULL_SCREEN_BACKGROUND,
				fullScreenBackground);
	}

	@Override
	@SuppressWarnings(UNCHECKED)
	public Map<String, ColumnBean> getSearchResultsColumns() {
		Map<String, ColumnBean> map = this.preferenceHelper.getPreference(
				Preferences.SEARCH_RESULTS_COLUMNS, Map.class, null);
		return map != null ? Collections.unmodifiableMap(map) : null;
	}

	@Override
	public void setSearchResultsColumns(
			final Map<String, ColumnBean> searchResultsColumns) {
		if (getSearchResultsColumns() == null
				|| !getSearchResultsColumns().equals(searchResultsColumns)) {
			this.preferenceHelper.setPreference(
					Preferences.SEARCH_RESULTS_COLUMNS, searchResultsColumns);
		}
	}

	@Override
	public FrameState getFrameState(final Class<? extends IFrame> frame) {
		// Map creation is controlled in this class to avoid modification
		// without persistence
		@SuppressWarnings(UNCHECKED)
		Map<Class<? extends IFrame>, FrameState> state = this.preferenceHelper
				.getPreference(Preferences.FRAME_STATES, Map.class, null);
		if (state == null) {
			state = new HashMap<Class<? extends IFrame>, FrameState>();
			this.preferenceHelper
					.setPreference(Preferences.FRAME_STATES, state);
		}
		// Clone object to be sure changes made by application to frame state
		// are not made over object in cache
		return state.containsKey(frame) ? new FrameState(state.get(frame))
				: null;
	}

	@Override
	public void setFrameState(final Class<? extends IFrame> frame,
			final IFrameState fs) {
		// Clone object to be sure changes made by application to frame state
		// are not made over object in cache
		FrameState frameState = new FrameState(fs);
		if (getFrameState(frame) == null
				|| !getFrameState(frame).equals(frameState)) {
			@SuppressWarnings(UNCHECKED)
			Map<Class<? extends IFrame>, FrameState> state = this.preferenceHelper
					.getPreference(Preferences.FRAME_STATES, Map.class, null);
			if (state == null) {
				state = new HashMap<Class<? extends IFrame>, FrameState>();
			}
			state.put(frame, frameState);
			this.preferenceHelper
					.setPreference(Preferences.FRAME_STATES, state);
		}
	}

	@Override
	public int getOsdWidth() {
		return this.preferenceHelper.getPreference(Preferences.OSD_WIDTH,
				Integer.class, 300);
	}

	@Override
	public void setOsdWidth(final int osdWidth) {
		this.preferenceHelper.setPreference(Preferences.OSD_WIDTH, osdWidth);
	}

	@Override
	public int getOsdHorizontalAlignment() {
		return this.preferenceHelper.getPreference(
				Preferences.OSD_HORIZONTAL_ALINGMENT, Integer.class,
				SwingConstants.RIGHT);
	}

	@Override
	public void setOsdHorizontalAlignment(final int osdHorizontalAlignment) {
		this.preferenceHelper.setPreference(
				Preferences.OSD_HORIZONTAL_ALINGMENT, osdHorizontalAlignment);
	}

	@Override
	public int getOsdVerticalAlignment() {
		return this.preferenceHelper.getPreference(
				Preferences.OSD_VERTICAL_ALINGMENT, Integer.class,
				SwingConstants.BOTTOM);
	}

	@Override
	public void setOsdVerticalAlignment(final int osdVerticalAlignment) {
		this.preferenceHelper.setPreference(Preferences.OSD_VERTICAL_ALINGMENT,
				osdVerticalAlignment);
	}

	@Override
	public IColorBean getTrayPlayerIconsColor() {
		return this.preferenceHelper.getPreference(
				Preferences.TRAY_PLAYER_ICONS_COLOR, IColorBean.class,
				this.colorBeanFactory.getColorBean(Color.BLACK));
	}

	@Override
	public void setTrayPlayerIconsColor(final IColorBean color) {
		this.preferenceHelper.setPreference(
				Preferences.TRAY_PLAYER_ICONS_COLOR, color);
	}

	@Override
	public boolean isShowPlayerControlsOnTop() {
		return this.preferenceHelper.getPreference(
				Preferences.SHOW_PLAYER_CONTROLS_ON_TOP, Boolean.class, true);
	}

	@Override
	public void setShowPlayerControlsOnTop(final boolean onTop) {
		this.preferenceHelper.setPreference(
				Preferences.SHOW_PLAYER_CONTROLS_ON_TOP, onTop);
	}

	@Override
	public IFrameSize getFrameSize() {
		return this.preferenceHelper.getPreference(Preferences.FRAME_SIZE,
				IFrameSize.class, new FrameSize());
	}

	@Override
	public void setFrameSize(final IFrameSize frameSize) {
		this.preferenceHelper.setPreference(Preferences.FRAME_SIZE, frameSize);
	}

	@Override
	public IFramePosition getFramePosition() {
		return this.preferenceHelper.getPreference(Preferences.FRAME_POSITION,
				IFramePosition.class, new FramePosition());
	}

	@Override
	public void setFramePosition(final IFramePosition framePosition) {
		this.preferenceHelper.setPreference(Preferences.FRAME_POSITION,
				framePosition);
	}

	@Override
	public Map<String, String> describeState() {
		return ReflectionUtils.describe(this);
	}

}
