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

package net.sourceforge.atunes.kernel.modules.state;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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

import net.sourceforge.atunes.gui.frame.Frames;
import net.sourceforge.atunes.gui.views.controls.ByImageChoosingPanel;
import net.sourceforge.atunes.gui.views.controls.ByImageChoosingPanel.ImageEntry;
import net.sourceforge.atunes.gui.views.dialogs.FontChooserDialog;
import net.sourceforge.atunes.model.FontSettings;
import net.sourceforge.atunes.model.IColorBeanFactory;
import net.sourceforge.atunes.model.IFontBeanFactory;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILocaleBean;
import net.sourceforge.atunes.model.ILocaleBeanFactory;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.model.LookAndFeelBean;
import net.sourceforge.atunes.utils.I18nUtils;

import org.jdesktop.swingx.combobox.ListComboBoxModel;

/**
 * The preferences panel for general settings.
 */
public final class GeneralPanel extends AbstractPreferencesPanel {

    private static final class LocaleComparator implements Comparator<Locale>, Serializable {
        /**
		 * 
		 */
		private static final long serialVersionUID = 8454923131242388725L;
		
		private final Locale currentLocale;

        private LocaleComparator(Locale currentLocale) {
            this.currentLocale = currentLocale;
        }

        @Override
        public int compare(Locale l1, Locale l2) {
            return Collator.getInstance().compare(l1.getDisplayName(currentLocale), l2.getDisplayName(currentLocale));
        }
    }

    private static final long serialVersionUID = -9216216930198145476L;

    private JComboBox language;
    private JCheckBox showIconTray;
    private JCheckBox showTrayPlayer;
    private JComboBox lookAndFeel;
    private JLabel skinLabel;
    private JComboBox skin;    
    private ByImageChoosingPanel<Class<? extends IFrame>> windowTypeChoosingPanel;
    private JButton trayPlayerColorSelector;
    
    private transient ActionListener applySkinActionListener;

    private FontSettings currentFontSettings;
    
    private Color currentTrayIconColor;
    
    private IOSManager osManager;

	private ILookAndFeelManager lookAndFeelManager;
	
	private transient IColorBeanFactory colorBeanFactory;
	
	private transient ILocaleBeanFactory localeBeanFactory;
	
	private transient IFontBeanFactory fontBeanFactory;
	
	/**
	 * @param fontBeanFactory
	 */
	public void setFontBeanFactory(IFontBeanFactory fontBeanFactory) {
		this.fontBeanFactory = fontBeanFactory;
	}
	
	/**
	 * @param osManager
	 */
	public void setOsManager(IOSManager osManager) {
		this.osManager = osManager;
	}
	
	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}
	
	/**
	 * @param colorBeanFactory
	 */
	public void setColorBeanFactory(IColorBeanFactory colorBeanFactory) {
		this.colorBeanFactory = colorBeanFactory;
	}
	
	/**
	 * @param localeBeanFactory
	 */
	public void setLocaleBeanFactory(ILocaleBeanFactory localeBeanFactory) {
		this.localeBeanFactory = localeBeanFactory;
	}
	
    /**
     * Instantiates a new general panel.
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
        language = new JComboBox();

        showIconTray = new JCheckBox(I18nUtils.getString("SHOW_TRAY_ICON"));
        showTrayPlayer = new JCheckBox(I18nUtils.getString("SHOW_TRAY_PLAYER"));
        showTrayPlayer.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				trayPlayerColorSelector.setEnabled(showTrayPlayer.isSelected());
			}
		});
        trayPlayerColorSelector = new JButton(I18nUtils.getString("SELECT_TRAY_PLAYER_COLOR"));
        trayPlayerColorSelector.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Color selectedColor = JColorChooser.showDialog(getPreferenceDialog(), 
										 I18nUtils.getString("SELECT_TRAY_PLAYER_COLOR"),
										 null);
				if (selectedColor != null) {
					currentTrayIconColor = selectedColor;
				}
			}
		});
        
        // Hide tray icons controls if not supported by operating system
        showIconTray.setVisible(osManager.areTrayIconsSupported());
        showTrayPlayer.setVisible(osManager.areTrayIconsSupported());
        trayPlayerColorSelector.setVisible(osManager.areTrayIconsSupported() && osManager.areTrayIconsColorsSupported());
        
        
        JLabel lookAndFeelLabel = new JLabel(I18nUtils.getString("LOOK_AND_FEEL"));
        skinLabel = new JLabel(I18nUtils.getString("THEME"));

        JButton fontSettings = new JButton(I18nUtils.getString("CHANGE_FONT_SETTINGS"));
        fontSettings.setEnabled(lookAndFeelManager.getCurrentLookAndFeel().supportsCustomFontSettings());
        fontSettings.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                FontChooserDialog fontChooserDialog = new FontChooserDialog(getPreferenceDialog(), 
                										 300, 
                										 300, 
                										 currentFontSettings != null ? currentFontSettings.getFont().toFont() : GeneralPanel.this.lookAndFeelManager.getCurrentLookAndFeel().getDefaultFont(),
                										 currentFontSettings != null ? currentFontSettings.isUseFontSmoothing() : true, 
                										 currentFontSettings != null ? currentFontSettings.isUseFontSmoothingSettingsFromOs() : false, 
                										 getState().getLocale().getLocale(), 
                										 GeneralPanel.this.lookAndFeelManager, 
                										 fontBeanFactory);
                fontChooserDialog.setVisible(true);
                if (fontChooserDialog.getSelectedFontSettings() != null) {
                    currentFontSettings = fontChooserDialog.getSelectedFontSettings();
                }
            }
        });

        lookAndFeel = new JComboBox();
        lookAndFeel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	// When changing look and feel set default skin in combo or current skin if selected look and feel is the current one
            	String skinName = GeneralPanel.this.lookAndFeelManager.getDefaultSkin((String)lookAndFeel.getSelectedItem());
            	if (GeneralPanel.this.lookAndFeelManager.getCurrentLookAndFeelName().equals(lookAndFeel.getSelectedItem()) &&
            			getState().getLookAndFeel().getSkin() != null) {
            		skinName = getState().getLookAndFeel().getSkin();
            	} else {
            		// Different look and feel, if skin has changed we must reset it, if not, do nothing as to change look and feel
            		// user must restart application first
            		String currentSkin = getState().getLookAndFeel().getSkin();
                    if (currentSkin == null) {
                    	currentSkin = GeneralPanel.this.lookAndFeelManager.getDefaultSkin(getState().getLookAndFeel().getName());
                    }
                    GeneralPanel.this.lookAndFeelManager.applySkin(currentSkin, getState(), osManager);
            	}
            	updateSkins((String)lookAndFeel.getSelectedItem(), skinName);
            }
        });
        skin = new JComboBox();
        List<ImageEntry<Class<? extends IFrame>>> data = new ArrayList<ImageEntry<Class<? extends IFrame>>>();
        for (Class<? extends IFrame> clazz : Frames.getClasses()) {
            data.add(new ImageEntry<Class<? extends IFrame>>(clazz, Frames.getImage(clazz)));
        }
        windowTypeChoosingPanel = new ByImageChoosingPanel<Class<? extends IFrame>>(data);

        arrangePanel(windowTypeLabel, languageLabel, lookAndFeelLabel,
				fontSettings);
    }

	/**
	 * @param windowTypeLabel
	 * @param languageLabel
	 * @param lookAndFeelLabel
	 * @param fontSettings
	 */
	private void arrangePanel(JLabel windowTypeLabel, JLabel languageLabel,
			JLabel lookAndFeelLabel, JButton fontSettings) {
		GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.insets = new Insets(5, 0, 0, 0);
        c.fill=GridBagConstraints.HORIZONTAL;
        add(languageLabel, c);
        c.gridx = 1;
        c.fill=GridBagConstraints.NONE;
        add(language, c);
        c.gridx = 0;
        c.gridy = 2;
        c.insets = new Insets(25, 0, 5, 0);
        c.fill=GridBagConstraints.HORIZONTAL;
        add(lookAndFeelLabel, c);
        c.gridx = 1;
        c.insets = new Insets(25, 0, 0, 0);
        c.fill=GridBagConstraints.NONE;
        add(lookAndFeel, c);
        c.gridx = 0;
        c.gridy = 3;
        c.insets = new Insets(10, 0, 5, 0);
        c.fill=GridBagConstraints.HORIZONTAL;
        add(skinLabel, c);
        c.gridx = 1;
        c.insets = new Insets(10, 0, 0, 0);
        c.fill=GridBagConstraints.NONE;
        add(skin, c);
        c.gridx = 0;
        c.gridy = 4;
        c.insets = new Insets(20, 0, 0, 0);
        add(windowTypeLabel, c);
        c.gridx = 0;
        c.gridy = 5;
        c.insets = new Insets(5, 0, 0, 0);
        c.gridwidth = 3;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        JScrollPane sp = lookAndFeelManager.getCurrentLookAndFeel().getScrollPane(windowTypeChoosingPanel);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.getVerticalScrollBar().setUnitIncrement(20);
        add(sp, c);
        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(20, 0, 20, 0);
        add(fontSettings, c);
        c.gridx = 0;
        c.gridy = 7;
        c.weighty = 0.2;
        c.insets = new Insets(5, 0, 0, 0);
        add(showTrayPlayer, c);
        c.gridx = 1;
        c.gridy = 7;
        c.weighty = 0.2;
        c.insets = new Insets(5, 0, 0, 0);
        add(trayPlayerColorSelector, c);
        c.gridx = 2;
        c.gridy = 7;
        c.insets = new Insets(5, 0, 0, 0);
        add(showIconTray, c);
	}

    @Override
    public boolean applyPreferences(IState state) {
        boolean needRestart = false;

        Class<? extends IFrame> oldFrameClass = state.getFrameClass();
        Class<? extends IFrame> newFrameClass = windowTypeChoosingPanel.getSelectedItem();
        state.setFrameClass(newFrameClass);
        if (!oldFrameClass.equals(newFrameClass)) {
            needRestart = true;
        }

        ILocaleBean oldLocale = state.getLocale();
        ILocaleBean newLocale = localeBeanFactory.getLocaleBean((Locale) language.getSelectedItem());
        state.setLocale(newLocale);
        if (!oldLocale.equals(newLocale)) {
            needRestart = true;
            state.setOldLocale(localeBeanFactory.getLocaleBean(Locale.getDefault()));
        }

        if (lookAndFeelManager.getCurrentLookAndFeel().supportsCustomFontSettings()) {
        	FontSettings oldFontSettings = state.getFontSettings();
        	state.setFontSettings(currentFontSettings);
        	if (!oldFontSettings.equals(currentFontSettings)) {
        		needRestart = true;
        	}
        }

        state.setShowSystemTray(showIconTray.isSelected());
        state.setShowTrayPlayer(showTrayPlayer.isSelected());
        
        if (currentTrayIconColor != null) {
        	state.setTrayPlayerIconsColor(colorBeanFactory.getColorBean(currentTrayIconColor));
        }

        LookAndFeelBean oldLookAndFeel = state.getLookAndFeel();
        LookAndFeelBean newLookAndFeel = new LookAndFeelBean();
        newLookAndFeel.setName((String) lookAndFeel.getSelectedItem());
        newLookAndFeel.setSkin((String) skin.getSelectedItem());
        state.setLookAndFeel(newLookAndFeel);
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
    private void setLanguage(Locale language) {
        this.language.setSelectedItem(language);
    }

    /**
     * Sets the show icon tray.
     * 
     * @param show
     *            the new show icon tray
     */
    private void setShowIconTray(boolean show) {
        this.showIconTray.setSelected(show);
    }

    /**
     * Sets the show tray player.
     * 
     * @param show
     *            the new show tray player
     */
    private void setShowTrayPlayer(boolean show) {
        this.showTrayPlayer.setSelected(show);
        this.trayPlayerColorSelector.setEnabled(show);
    }

    /**
     * Sets the look and feel
     * 
     * @param lookAndFeel
     */
    private void setLookAndFeel(String lookAndFeel) {
        this.lookAndFeel.setSelectedItem(lookAndFeel);
    }

    /**
     * Sets the window type.
     * 
     * @param type
     *            the new window type
     */
    private void setWindowType(Class<? extends IFrame> type) {
        windowTypeChoosingPanel.setSelectedItem(type);
    }

    @Override
    public void updatePanel(IState state) {
        List<Locale> langs = I18nUtils.getLanguages();
        Locale[] array = langs.toArray(new Locale[langs.size()]);
        final Locale currentLocale = getState().getLocale().getLocale();
        Arrays.sort(array, new LocaleComparator(currentLocale));
        language.setModel(new DefaultComboBoxModel(array));
        language.setRenderer(lookAndFeelManager.getCurrentLookAndFeel().getListCellRenderer(new LanguageListCellRendererCode(state)));

    	
        lookAndFeel.setModel(new ListComboBoxModel<String>(lookAndFeelManager.getAvailableLookAndFeels()));
        setWindowType(state.getFrameClass());
        setLanguage(I18nUtils.getSelectedLocale());
        setShowIconTray(state.isShowSystemTray());
        setShowTrayPlayer(state.isShowTrayPlayer());
        // If look and feel is not available then set default
        String lookAndFeelName = lookAndFeelManager.getAvailableLookAndFeels().contains(state.getLookAndFeel().getName()) ? state.getLookAndFeel().getName()
                : lookAndFeelManager.getDefaultLookAndFeel().getName();
        setLookAndFeel(lookAndFeelName);

        String skinName = state.getLookAndFeel().getSkin() != null ? state.getLookAndFeel().getSkin() : lookAndFeelManager.getDefaultSkin(lookAndFeelName); 
        updateSkins(lookAndFeelName, skinName);
        
        currentFontSettings = state.getFontSettings();
    }

    @Override
    public void resetImmediateChanges(IState state) {
        if (state.getLookAndFeel().getSkin() == null || !state.getLookAndFeel().getSkin().equals(skin.getSelectedItem())) {
            lookAndFeelManager.applySkin(state.getLookAndFeel().getSkin(), getState(), osManager);
        }
    }

    @Override
    public void validatePanel() throws PreferencesValidationException {
    }

    @Override
    public void dialogVisibilityChanged(boolean visible) {
        // Do nothing
    }

    /**
     * Updates skins combo for given look and feel
     * 
     * @param selectedLookAndFeel
     * @param selectedSkin
     */
    protected void updateSkins(String selectedLookAndFeel, String selectedSkin) {
        boolean hasSkins = !lookAndFeelManager.getAvailableSkins(selectedLookAndFeel).isEmpty();
        skinLabel.setEnabled(hasSkins);
        skin.setEnabled(hasSkins);
        
        // Hide skin selector if look and feel has no skins
        skinLabel.setVisible(hasSkins);
        skin.setVisible(hasSkins);
        
        // Remove all listeners when setting skin list to avoid events fired while selecting skin item
        if (applySkinActionListener != null) {
        	skin.removeActionListener(applySkinActionListener);
        }
        skin.setModel(new ListComboBoxModel<String>(lookAndFeelManager.getAvailableSkins(selectedLookAndFeel)));
        skin.setSelectedItem(selectedSkin);
        if (applySkinActionListener == null) {
        	applySkinActionListener = new ApplySkinActionListener();
        }
        skin.addActionListener(applySkinActionListener);        
    }

    private class ApplySkinActionListener implements ActionListener {
    	@Override
    	public void actionPerformed(ActionEvent e) {
    		String selectedSkin = (String) skin.getSelectedItem();
    		boolean isCurrentLookAndFeel = lookAndFeelManager.getCurrentLookAndFeelName().equals(lookAndFeel.getSelectedItem());
    		if (isCurrentLookAndFeel) {
    			lookAndFeelManager.applySkin(selectedSkin, getState(), osManager);
    		}
    	}
    }
}
