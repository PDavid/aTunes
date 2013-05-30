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

package net.sourceforge.atunes.gui.lookandfeel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.model.FontSettings;
import net.sourceforge.atunes.model.IApplicationArguments;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IFontBeanFactory;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.ILookAndFeelChangeListener;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IStateCore;
import net.sourceforge.atunes.model.IStateUI;
import net.sourceforge.atunes.model.LookAndFeelBean;
import net.sourceforge.atunes.utils.Logger;

/**
 * Responsible of change and manage look and feel
 * 
 * @author alex
 * 
 */
public final class LookAndFeelManager implements 
		ILookAndFeelManager {

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

	private IApplicationArguments applicationArguments;

	private IBeanFactory beanFactory;

	private IOSManager osManager;

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Initialize manager
	 */
	public void initialize() {
		this.lookAndFeels = this.osManager.getLookAndFeels();
		this.defaultLookAndFeelClass = this.osManager.getDefaultLookAndFeel();
	}

	/**
	 * @param applicationArguments
	 */
	public void setApplicationArguments(
			final IApplicationArguments applicationArguments) {
		this.applicationArguments = applicationArguments;
	}

	@Override
	public void setLookAndFeel(final LookAndFeelBean bean,
			final IStateCore stateCore, final IStateUI stateUI,
			final IOSManager osManager) {
		if (this.applicationArguments.isIgnoreLookAndFeel()) {
			return;
		}

		LookAndFeelBean lookAndFeelBean = bean;
		if (lookAndFeelBean == null || lookAndFeelBean.getName() == null) {
			lookAndFeelBean = new LookAndFeelBean();
			ILookAndFeel defaultLookAndFeel = null;
			try {
				defaultLookAndFeel = this.defaultLookAndFeelClass.newInstance();
			} catch (InstantiationException e) {
				Logger.error(e);
			} catch (IllegalAccessException e) {
				Logger.error(e);
			}
			lookAndFeelBean.setName(defaultLookAndFeel.getName());
			lookAndFeelBean.setSkin(defaultLookAndFeel.getDefaultSkin());
			if (stateUI.getLookAndFeel() == null) {
				stateUI.setLookAndFeel(lookAndFeelBean);
			}
		}

		Class<? extends ILookAndFeel> currentLookAndFeelClass = this.lookAndFeels
				.get(lookAndFeelBean.getName());
		if (currentLookAndFeelClass == null) {
			currentLookAndFeelClass = this.defaultLookAndFeelClass;
		}

		try {
			this.currentLookAndFeel = currentLookAndFeelClass.newInstance();
			this.currentLookAndFeel.setOsManager(osManager);
			this.currentLookAndFeel.setBeanFactory(this.beanFactory);
		} catch (InstantiationException e) {
			Logger.error(e);
		} catch (IllegalAccessException e) {
			Logger.error(e);
		}

		this.currentLookAndFeel.initializeLookAndFeel(this.beanFactory);
		this.currentLookAndFeel.setLookAndFeel(lookAndFeelBean.getSkin());
		initializeFonts(this.currentLookAndFeel, stateCore, stateUI);
		initializeColors();
	}

	/**
	 * Color initialization for some ui components
	 */
	private void initializeColors() {
		UIManager.put("ToolTip.border",
				BorderFactory.createLineBorder(GuiUtils.getBorderColor()));
		UIManager.put("ToolTip.background", new ColorUIResource(Color.WHITE));
		UIManager.put("ToolTip.foreground", new ColorUIResource(Color.BLACK));
	}

	/**
	 * Initializes fonts for look and feel
	 * 
	 * @param lookAndFeel
	 * @param state
	 * @param stateUI
	 */
	private void initializeFonts(final ILookAndFeel lookAndFeel,
			final IStateCore stateCore, final IStateUI stateUI) {
		FontSettings fontSettings = stateUI.getFontSettings();

		setAntialiasingProperties(lookAndFeel, fontSettings);

		Font font = UIManager.getFont("Label.font");
		if (lookAndFeel.supportsCustomFontSettings()) {
			if (fontSettings != null) {
				font = fontSettings.getFont().toFont();
			} else {
				font = getFontToUse(lookAndFeel, stateCore.getLocale()
						.getLanguage());
				stateUI.setFontSettings(new FontSettings(this.beanFactory
						.getBean(IFontBeanFactory.class).getFontBean(font),
						USE_FONT_SMOOTHING_DEFAULT_VALUE,
						USE_FONT_SMOOTHING_SETTINGS_FROM_OS_DEFAULT_VALUE));
			}
		}
		lookAndFeel.setBaseFont(font);
		lookAndFeel.initializeFonts(font);
	}

	private Font getFontToUse(final ILookAndFeel lookAndFeel, String language) {
		/*
		 * Get appropriate font for the currently selected language. For Chinese
		 * or Japanese we should use default font.
		 */
		if ("zh".equals(language) || "ja".equals(language)) {
			return new Font(Font.SANS_SERIF, Font.PLAIN, 12);
		} else {
			return lookAndFeel.getSuggestedFont();
		}
	}

	private void setAntialiasingProperties(final ILookAndFeel lookAndFeel,
			FontSettings fontSettings) {
		if (lookAndFeel.supportsCustomFontSettings() && fontSettings != null
				&& !fontSettings.isUseFontSmoothingSettingsFromOs()) {
			if (fontSettings.isUseFontSmoothing()) {
				System.setProperty("awt.useSystemAAFontSettings", "lcd");
			} else {
				System.setProperty("awt.useSystemAAFontSettings", "false");
			}
		} else {
			System.setProperty("awt.useSystemAAFontSettings", "lcd");
		}
	}

	@Override
	public List<String> getAvailableLookAndFeels() {
		return new ArrayList<String>(this.lookAndFeels.keySet());
	}

	@Override
	public List<String> getAvailableSkins(final String lookAndFeelName) {
		Class<? extends ILookAndFeel> clazz = this.lookAndFeels
				.get(lookAndFeelName);
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
				return lookAndFeel.getSkins() != null ? lookAndFeel.getSkins()
						: new ArrayList<String>();
			}
		}
		return new ArrayList<String>();
	}

	@Override
	public String getCurrentLookAndFeelName() {
		return this.currentLookAndFeel.getName();
	}

	@Override
	public void applySkin(final String selectedSkin,
			final IStateCore stateCore, final IStateUI stateUI,
			final IOSManager osManager) {
		LookAndFeelBean bean = new LookAndFeelBean();
		bean.setName(this.currentLookAndFeel.getName());
		bean.setSkin(selectedSkin);
		setLookAndFeel(bean, stateCore, stateUI, osManager);
		for (Window window : Window.getWindows()) {
			// Only update displayable windows
			// References to disposed windows (not displayable) are kept so
			// calling to update those windows can throw exceptions or errors
			if (window.isDisplayable()) {
				SwingUtilities.updateComponentTreeUI(window);
			}
		}
		// Notify listeners
		for (ILookAndFeelChangeListener listener : getChangeListeners()) {
			listener.lookAndFeelChanged();
		}
	}

	@Override
	public ILookAndFeel getCurrentLookAndFeel() {
		return this.currentLookAndFeel;
	}

	@Override
	public String getDefaultSkin(final String lookAndFeelName) {
		Class<? extends ILookAndFeel> clazz = this.lookAndFeels
				.get(lookAndFeelName);
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

	@Override
	public ILookAndFeel getDefaultLookAndFeel() {
		try {
			return this.defaultLookAndFeelClass.newInstance();
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
		if (this.changeListeners == null) {
			this.changeListeners = new ArrayList<ILookAndFeelChangeListener>();
		}
		return this.changeListeners;
	}

	@Override
	public void addLookAndFeelChangeListener(
			final ILookAndFeelChangeListener listener) {
		getChangeListeners().add(listener);
	}

	@Override
	public void removeLookAndFeelChangeListener(
			final ILookAndFeelChangeListener listener) {
		getChangeListeners().remove(listener);
	}
}
