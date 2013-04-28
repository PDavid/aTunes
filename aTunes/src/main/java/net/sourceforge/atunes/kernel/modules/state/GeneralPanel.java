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
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.frame.Frames;
import net.sourceforge.atunes.gui.views.controls.ByImageChoosingPanel;
import net.sourceforge.atunes.gui.views.controls.ByImageChoosingPanel.ImageEntry;
import net.sourceforge.atunes.gui.views.dialogs.FontChooserDialog;
import net.sourceforge.atunes.model.FontSettings;
import net.sourceforge.atunes.model.IColorBeanFactory;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILocaleBean;
import net.sourceforge.atunes.model.ILocaleBeanFactory;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IStateCore;
import net.sourceforge.atunes.model.IStateUI;
import net.sourceforge.atunes.model.LookAndFeelBean;
import net.sourceforge.atunes.utils.I18nUtils;

import org.jdesktop.swingx.combobox.ListComboBoxModel;

/**
 * The preferences panel for general settings.
 */
public final class GeneralPanel extends AbstractPreferencesPanel {

	private static final long serialVersionUID = -9216216930198145476L;

	private JComboBox language;
	private JCheckBox showIconTray;
	private JCheckBox showTrayPlayer;
	private JComboBox lookAndFeel;
	private JLabel skinLabel;
	private JComboBox skin;
	private ByImageChoosingPanel<Class<? extends IFrame>> windowTypeChoosingPanel;
	private JButton trayPlayerColorSelector;

	private FontSettings currentFontSettings;

	private Color currentTrayIconColor;

	private IOSManager osManager;

	private ILookAndFeelManager lookAndFeelManager;

	private transient IColorBeanFactory colorBeanFactory;

	private transient ILocaleBeanFactory localeBeanFactory;

	private IStateUI stateUI;

	private IStateCore stateCore;

	private IDialogFactory dialogFactory;

	private Frames frameTypes;

	private String skinApplied;

	private List<Locale> availableLanguages;

	private IControlsBuilder controlsBuilder;

	/**
	 * @param controlsBuilder
	 */
	public void setControlsBuilder(final IControlsBuilder controlsBuilder) {
		this.controlsBuilder = controlsBuilder;
	}

	/**
	 * @param availableLanguages
	 */
	public void setAvailableLanguages(final List<Locale> availableLanguages) {
		this.availableLanguages = availableLanguages;
	}

	/**
	 * @param frameTypes
	 */
	public void setFrameTypes(final Frames frameTypes) {
		this.frameTypes = frameTypes;
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param stateCore
	 */
	public void setStateCore(final IStateCore stateCore) {
		this.stateCore = stateCore;
	}

	/**
	 * @param stateUI
	 */
	public void setStateUI(final IStateUI stateUI) {
		this.stateUI = stateUI;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(
			final ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

	/**
	 * @param colorBeanFactory
	 */
	public void setColorBeanFactory(final IColorBeanFactory colorBeanFactory) {
		this.colorBeanFactory = colorBeanFactory;
	}

	/**
	 * @param localeBeanFactory
	 */
	public void setLocaleBeanFactory(final ILocaleBeanFactory localeBeanFactory) {
		this.localeBeanFactory = localeBeanFactory;
	}

	/**
	 * Instantiates a new general panel.
	 * 
	 * @param osManager
	 * @param lookAndFeelManager
	 * @param colorBeanFactory
	 * @param fontBeanFactory
	 */
	public GeneralPanel() {
		super(I18nUtils.getString("GENERAL"));
	}

	/**
	 * Initialize panel
	 */
	public void initialize() {
		JLabel windowTypeLabel = new JLabel(I18nUtils.getString("WINDOW_TYPE"));
		JLabel languageLabel = new JLabel(I18nUtils.getString("LANGUAGE"));
		this.language = new JComboBox();

		this.showIconTray = new JCheckBox(I18nUtils.getString("SHOW_TRAY_ICON"));
		this.showTrayPlayer = new JCheckBox(
				I18nUtils.getString("SHOW_TRAY_PLAYER"));
		this.showTrayPlayer.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(final ChangeEvent e) {
				GeneralPanel.this.trayPlayerColorSelector
						.setEnabled(GeneralPanel.this.showTrayPlayer
								.isSelected());
			}
		});
		this.trayPlayerColorSelector = new JButton(
				I18nUtils.getString("SELECT_TRAY_PLAYER_COLOR"));
		this.trayPlayerColorSelector.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				Color selectedColor = JColorChooser.showDialog(
						getPreferenceDialog(),
						I18nUtils.getString("SELECT_TRAY_PLAYER_COLOR"), null);
				if (selectedColor != null) {
					GeneralPanel.this.currentTrayIconColor = selectedColor;
				}
			}
		});

		// Hide tray icons controls if not supported by operating system
		this.showIconTray.setVisible(this.osManager.areTrayIconsSupported());
		this.showTrayPlayer.setVisible(this.osManager.areTrayIconsSupported());
		this.trayPlayerColorSelector.setVisible(this.osManager
				.areTrayIconsSupported()
				&& this.osManager.areTrayIconsColorsSupported());

		JLabel lookAndFeelLabel = new JLabel(
				I18nUtils.getString("LOOK_AND_FEEL"));
		this.skinLabel = new JLabel(I18nUtils.getString("THEME"));

		JButton fontSettings = getFontSettingsButton();

		this.lookAndFeel = getLookAndFeelComboBox();

		this.skin = new JComboBox(new DefaultComboBoxModel());
		this.skin.addItemListener(new ApplySkinActionListener());
		List<ImageEntry<Class<? extends IFrame>>> data = new ArrayList<ImageEntry<Class<? extends IFrame>>>();
		for (Class<? extends IFrame> clazz : this.frameTypes.getFrames()) {
			data.add(new ImageEntry<Class<? extends IFrame>>(clazz,
					this.frameTypes.getImage(clazz)));
		}
		this.windowTypeChoosingPanel = new ByImageChoosingPanel<Class<? extends IFrame>>(
				data);

		JScrollPane sp = this.controlsBuilder
				.createScrollPane(this.windowTypeChoosingPanel);
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sp.getVerticalScrollBar().setUnitIncrement(20);

		arrangePanel(windowTypeLabel, languageLabel, lookAndFeelLabel,
				fontSettings, sp);
	}

	private JComboBox getLookAndFeelComboBox() {
		final JComboBox lookAndFeel = new JComboBox();
		lookAndFeel.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(final ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					// When changing look and feel set default skin in combo or
					// current skin if selected look and feel is the current one
					if (!currentLookAndFeelIsSelected()) {
						updateSkins((String) lookAndFeel.getSelectedItem(),
								GeneralPanel.this.lookAndFeelManager
										.getDefaultSkin((String) lookAndFeel
												.getSelectedItem()));

					} else {
						if (GeneralPanel.this.skinApplied != null) {
							updateSkins((String) lookAndFeel.getSelectedItem(),
									GeneralPanel.this.skinApplied);
						} else {
							updateSkins((String) lookAndFeel.getSelectedItem(),
									GeneralPanel.this.stateUI.getLookAndFeel()
											.getSkin());
						}
					}
				}
			}

			private boolean currentLookAndFeelIsSelected() {
				return GeneralPanel.this.lookAndFeelManager
						.getCurrentLookAndFeelName().equals(
								lookAndFeel.getSelectedItem());
			}
		});

		return lookAndFeel;
	}

	private JButton getFontSettingsButton() {
		JButton fontSettings = new JButton(
				I18nUtils.getString("CHANGE_FONT_SETTINGS"));
		fontSettings.setEnabled(this.lookAndFeelManager.getCurrentLookAndFeel()
				.supportsCustomFontSettings());
		fontSettings.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				FontChooserDialog fontChooserDialog = GeneralPanel.this.dialogFactory
						.newDialog(FontChooserDialog.class);
				fontChooserDialog
						.initializeFont(
								GeneralPanel.this.currentFontSettings != null ? GeneralPanel.this.currentFontSettings
										.getFont().toFont()
										: GeneralPanel.this.lookAndFeelManager
												.getCurrentLookAndFeel()
												.getDefaultFont(),
								GeneralPanel.this.currentFontSettings != null ? GeneralPanel.this.currentFontSettings
										.isUseFontSmoothing() : true,
								GeneralPanel.this.currentFontSettings != null ? GeneralPanel.this.currentFontSettings
										.isUseFontSmoothingSettingsFromOs()
										: false, GeneralPanel.this.stateCore
										.getLocale().getLocale());
				fontChooserDialog.showDialog();
				if (fontChooserDialog.getSelectedFontSettings() != null) {
					GeneralPanel.this.currentFontSettings = fontChooserDialog
							.getSelectedFontSettings();
				}
			}
		});
		return fontSettings;
	}

	/**
	 * @param windowTypeLabel
	 * @param languageLabel
	 * @param lookAndFeelLabel
	 * @param fontSettings
	 * @param sp
	 */
	private void arrangePanel(final JLabel windowTypeLabel,
			final JLabel languageLabel, final JLabel lookAndFeelLabel,
			final JButton fontSettings, final JScrollPane sp) {
		GridBagConstraints c = new GridBagConstraints();
		addLanguage(languageLabel, c);
		addLookAndFeel(lookAndFeelLabel, c);
		addSkin(c);
		addWindowType(windowTypeLabel, sp, c);
		addFontSettings(fontSettings, c);
		addShowTrayPlayer(c);
		addTrayPlayerColorSelector(c);
		addShowIconTray(c);
	}

	/**
	 * @param c
	 */
	private void addShowIconTray(final GridBagConstraints c) {
		c.gridx = 2;
		c.gridy = 7;
		add(this.showIconTray, c);
	}

	/**
	 * @param c
	 */
	private void addTrayPlayerColorSelector(final GridBagConstraints c) {
		c.gridx = 1;
		c.gridy = 7;
		c.weighty = 0.2;
		add(this.trayPlayerColorSelector, c);
	}

	/**
	 * @param c
	 */
	private void addShowTrayPlayer(final GridBagConstraints c) {
		c.gridy = 7;
		c.weighty = 0.2;
		c.insets = new Insets(5, 0, 0, 0);
		add(this.showTrayPlayer, c);
	}

	/**
	 * @param fontSettings
	 * @param c
	 */
	private void addFontSettings(final JButton fontSettings,
			final GridBagConstraints c) {
		c.gridy = 6;
		c.gridwidth = 1;
		c.weightx = 0;
		c.weighty = 0;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(20, 0, 20, 0);
		add(fontSettings, c);
	}

	/**
	 * @param windowTypeLabel
	 * @param sp
	 * @param c
	 */
	private void addWindowType(final JLabel windowTypeLabel,
			final JScrollPane sp, final GridBagConstraints c) {
		c.gridx = 0;
		c.gridy = 4;
		c.insets = new Insets(20, 0, 0, 0);
		add(windowTypeLabel, c);
		c.gridy = 5;
		c.insets = new Insets(5, 0, 0, 0);
		c.gridwidth = 3;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		add(sp, c);
	}

	/**
	 * @param c
	 */
	private void addSkin(final GridBagConstraints c) {
		c.gridx = 0;
		c.gridy = 3;
		c.insets = new Insets(10, 0, 5, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		add(this.skinLabel, c);
		c.gridx = 1;
		c.insets = new Insets(10, 0, 0, 0);
		c.fill = GridBagConstraints.NONE;
		add(this.skin, c);
	}

	/**
	 * @param lookAndFeelLabel
	 * @param c
	 */
	private void addLookAndFeel(final JLabel lookAndFeelLabel,
			final GridBagConstraints c) {
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(25, 0, 5, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		add(lookAndFeelLabel, c);
		c.gridx = 1;
		c.insets = new Insets(25, 0, 0, 0);
		c.fill = GridBagConstraints.NONE;
		add(this.lookAndFeel, c);
	}

	/**
	 * @param languageLabel
	 * @param c
	 */
	private void addLanguage(final JLabel languageLabel,
			final GridBagConstraints c) {
		c.gridy = 0;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.insets = new Insets(5, 0, 0, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		add(languageLabel, c);
		c.gridx = 1;
		c.fill = GridBagConstraints.NONE;
		add(this.language, c);
	}

	@Override
	public boolean applyPreferences() {
		boolean needRestart = false;

		Class<? extends IFrame> oldFrameClass = this.stateUI.getFrameClass();
		Class<? extends IFrame> newFrameClass = this.windowTypeChoosingPanel
				.getSelectedItem();
		this.stateUI.setFrameClass(newFrameClass);
		if (!oldFrameClass.equals(newFrameClass)) {
			needRestart = true;
		}

		ILocaleBean oldLocale = this.stateCore.getLocale();
		ILocaleBean newLocale = this.localeBeanFactory
				.getLocaleBean((Locale) this.language.getSelectedItem());
		this.stateCore.setLocale(newLocale);
		if (!oldLocale.equals(newLocale)) {
			needRestart = true;
			this.stateCore.setOldLocale(this.localeBeanFactory
					.getLocaleBean(Locale.getDefault()));
		}

		if (this.lookAndFeelManager.getCurrentLookAndFeel()
				.supportsCustomFontSettings()) {
			FontSettings oldFontSettings = this.stateUI.getFontSettings();
			this.stateUI.setFontSettings(this.currentFontSettings);
			if (!oldFontSettings.equals(this.currentFontSettings)) {
				needRestart = true;
			}
		}

		this.stateUI.setShowSystemTray(this.showIconTray.isSelected());
		this.stateUI.setShowTrayPlayer(this.showTrayPlayer.isSelected());

		if (this.currentTrayIconColor != null) {
			this.stateUI.setTrayPlayerIconsColor(this.colorBeanFactory
					.getColorBean(this.currentTrayIconColor));
		}

		LookAndFeelBean oldLookAndFeel = this.stateUI.getLookAndFeel();
		LookAndFeelBean newLookAndFeel = new LookAndFeelBean();
		newLookAndFeel.setName((String) this.lookAndFeel.getSelectedItem());
		newLookAndFeel.setSkin((String) this.skin.getSelectedItem());
		this.stateUI.setLookAndFeel(newLookAndFeel);
		if (!oldLookAndFeel.getName().equals(newLookAndFeel.getName())) {
			needRestart = true;
		}

		return needRestart;
	}

	/**
	 * Sets the language.
	 * 
	 * @param language
	 *            the new language
	 */
	private void setLanguage(final Locale language) {
		this.language.setSelectedItem(language);
	}

	/**
	 * Sets the show icon tray.
	 * 
	 * @param show
	 *            the new show icon tray
	 */
	private void setShowIconTray(final boolean show) {
		this.showIconTray.setSelected(show);
	}

	/**
	 * Sets the show tray player.
	 * 
	 * @param show
	 *            the new show tray player
	 */
	private void setShowTrayPlayer(final boolean show) {
		this.showTrayPlayer.setSelected(show);
		this.trayPlayerColorSelector.setEnabled(show);
	}

	/**
	 * Sets the look and feel
	 * 
	 * @param lookAndFeel
	 */
	private void setLookAndFeel(final String lookAndFeel) {
		this.lookAndFeel.setSelectedItem(lookAndFeel);
	}

	/**
	 * Sets the window type.
	 * 
	 * @param type
	 *            the new window type
	 */
	private void setWindowType(final Class<? extends IFrame> type) {
		this.windowTypeChoosingPanel.setSelectedItem(type);
	}

	@Override
	public void updatePanel() {
		Locale[] array = this.availableLanguages
				.toArray(new Locale[this.availableLanguages.size()]);
		final Locale currentLocale = this.stateCore.getLocale().getLocale();
		Arrays.sort(array, new LocaleComparator(currentLocale));
		this.language.setModel(new DefaultComboBoxModel(array));
		this.language.setRenderer(this.lookAndFeelManager
				.getCurrentLookAndFeel().getListCellRenderer(
						new LanguageListCellRendererCode(this.stateCore)));

		this.lookAndFeel.setModel(new ListComboBoxModel<String>(
				this.lookAndFeelManager.getAvailableLookAndFeels()));
		setWindowType(this.stateUI.getFrameClass());
		setLanguage(I18nUtils.getSelectedLocale());
		setShowIconTray(this.stateUI.isShowSystemTray());
		setShowTrayPlayer(this.stateUI.isShowTrayPlayer());
		// If look and feel is not available then set default
		String lookAndFeelName = this.lookAndFeelManager
				.getAvailableLookAndFeels().contains(
						this.stateUI.getLookAndFeel().getName()) ? this.stateUI
				.getLookAndFeel().getName() : this.lookAndFeelManager
				.getDefaultLookAndFeel().getName();
		setLookAndFeel(lookAndFeelName);

		String skinName = this.stateUI.getLookAndFeel().getSkin() != null ? this.stateUI
				.getLookAndFeel().getSkin() : this.lookAndFeelManager
				.getDefaultSkin(lookAndFeelName);
		updateSkins(lookAndFeelName, skinName);

		this.currentFontSettings = this.stateUI.getFontSettings();
	}

	@Override
	public void resetImmediateChanges() {
		if (this.stateUI.getLookAndFeel().getSkin() == null
				|| !this.stateUI.getLookAndFeel().getSkin()
						.equals(this.skin.getSelectedItem())) {
			this.lookAndFeelManager.applySkin(this.stateUI.getLookAndFeel()
					.getSkin(), this.stateCore, this.stateUI, this.osManager);
		}
	}

	@Override
	public void validatePanel() throws PreferencesValidationException {
	}

	@Override
	public void dialogVisibilityChanged(final boolean visible) {
		// Do nothing
	}

	/**
	 * Updates skins combo for given look and feel
	 * 
	 * @param selectedLookAndFeel
	 * @param selectedSkin
	 */
	protected void updateSkins(final String selectedLookAndFeel,
			final String selectedSkin) {
		boolean hasSkins = !this.lookAndFeelManager.getAvailableSkins(
				selectedLookAndFeel).isEmpty();
		this.skinLabel.setEnabled(hasSkins);
		this.skin.setEnabled(false);

		List<String> availableSkins = this.lookAndFeelManager
				.getAvailableSkins(selectedLookAndFeel);

		((DefaultComboBoxModel) this.skin.getModel()).removeAllElements();
		if (availableSkins != null) {
			for (String availableSkin : availableSkins) {
				((DefaultComboBoxModel) this.skin.getModel())
						.addElement(availableSkin);
			}
			this.skin.getModel().setSelectedItem(selectedSkin);
		} else {
			this.skin.setSelectedIndex(-1);
		}
		this.skin.setEnabled(hasSkins);
	}

	private class ApplySkinActionListener implements ItemListener {

		@Override
		public void itemStateChanged(final ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED
					&& GeneralPanel.this.skin.isEnabled()) {
				final String selectedSkin = (String) GeneralPanel.this.skin
						.getSelectedItem();
				boolean isCurrentLookAndFeel = GeneralPanel.this.lookAndFeelManager
						.getCurrentLookAndFeelName()
						.equals(GeneralPanel.this.lookAndFeel.getSelectedItem());
				if (isCurrentLookAndFeel) {
					// Finish all pending Swing events before updating skin to
					// avoid weird Swing exceptions
					GuiUtils.callInEventDispatchThreadLater(new Runnable() {
						@Override
						public void run() {
							GeneralPanel.this.lookAndFeelManager.applySkin(
									selectedSkin, GeneralPanel.this.stateCore,
									GeneralPanel.this.stateUI,
									GeneralPanel.this.osManager);
						};
					});
					GeneralPanel.this.skinApplied = selectedSkin;
				}
			}
		}
	}
}
