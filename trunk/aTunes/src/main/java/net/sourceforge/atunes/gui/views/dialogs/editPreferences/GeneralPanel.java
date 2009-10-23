/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.gui.views.dialogs.editPreferences;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.Fonts;
import net.sourceforge.atunes.gui.LookAndFeelSelector;
import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.images.ThemePreviewLoader;
import net.sourceforge.atunes.gui.views.dialogs.FontChooserDialog;
import net.sourceforge.atunes.gui.views.dialogs.FontChooserDialog.FontSettings;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.kernel.modules.state.beans.LocaleBean;
import net.sourceforge.atunes.kernel.modules.visual.VisualHandler;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.api.renderers.SubstanceDefaultListCellRenderer;

/**
 * The preferences panel for general settings.
 */
public class GeneralPanel extends PreferencesPanel {

    private static final long serialVersionUID = -9216216930198145476L;

    private JCheckBox showTitle;
    private JComboBox windowType;
    private JComboBox language;
    private JCheckBox showIconTray;
    private JCheckBox showTrayPlayer;
    private JButton fontSettings;
    JComboBox theme;
    JLabel themePreview;

    FontSettings currentFontSettings;

    /**
     * Instantiates a new general panel.
     */
    public GeneralPanel() {
        // Titles are in bold. Label order has been changed, so number don't match any more
        super(I18nUtils.getString("GENERAL"));
        showTitle = new JCheckBox(I18nUtils.getString("SHOW_TITLE"));
        JLabel label = new JLabel(I18nUtils.getString("WINDOW_TYPE"));
        label.setFont(Fonts.GENERAL_FONT_BOLD);
        windowType = new JComboBox(new String[] { I18nUtils.getString("STANDARD_WINDOW"), I18nUtils.getString("MULTIPLE_WINDOW") });
        JLabel label2 = new JLabel(I18nUtils.getString("LANGUAGE"));
        label2.setFont(Fonts.GENERAL_FONT_BOLD);

        List<Locale> langs = I18nUtils.getLanguages();
        Locale[] array = langs.toArray(new Locale[langs.size()]);
        final Locale currentLocale = ApplicationState.getInstance().getLocale().getLocale();
        Arrays.sort(array, new Comparator<Locale>() {
            @Override
            public int compare(Locale l1, Locale l2) {
                return Collator.getInstance().compare(l1.getDisplayName(currentLocale), l2.getDisplayName(currentLocale));
            }
        });
        language = new JComboBox(array);
        language.setRenderer(new SubstanceDefaultListCellRenderer() {
            private static final long serialVersionUID = 4124370361802581951L;

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (!(value instanceof Locale)) {
                    throw new IllegalArgumentException("Argument value must be instance of Locale");
                }

                Locale displayingLocale = (Locale) value;
                Locale currentLocale = ApplicationState.getInstance().getLocale().getLocale();

                String name = displayingLocale.getDisplayName(currentLocale);
                name = String.valueOf(name.charAt(0)).toUpperCase(currentLocale) + name.substring(1);
                Component c = super.getListCellRendererComponent(list, name, index, isSelected, cellHasFocus);

                // The name of flag file should be flag_<locale>.png
                // if the name of bundle is MainBundle_<locale>.properties
                String flag = StringUtils.getString("flag_", displayingLocale.toString(), ".png");
                ((JLabel) c).setIcon(new ImageIcon(GeneralPanel.class.getResource(StringUtils.getString("/", Constants.TRANSLATIONS_DIR, "/", flag))));
                return c;
            }
        });

        showIconTray = new JCheckBox(I18nUtils.getString("SHOW_TRAY_ICON"));
        showTrayPlayer = new JCheckBox(I18nUtils.getString("SHOW_TRAY_PLAYER"));
        JLabel label3 = new JLabel(I18nUtils.getString("THEME"));
        label3.setFont(Fonts.GENERAL_FONT_BOLD);

        fontSettings = new JButton(I18nUtils.getString("CHANGE_FONT_SETTINGS"));
        fontSettings.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                FontChooserDialog fontChooserDialog;
                if (currentFontSettings != null) {
                    fontChooserDialog = new FontChooserDialog(VisualHandler.getInstance().getFrame().getFrame(), 300, 300, currentFontSettings.getFont().toFont(),
                            currentFontSettings.isUseFontSmoothing(), currentFontSettings.isUseFontSmoothingSettingsFromOs(), ApplicationState.getInstance().getLocale()
                                    .getLocale());
                } else {
                    fontChooserDialog = new FontChooserDialog(VisualHandler.getInstance().getFrame().getFrame(), 300, 300, SubstanceLookAndFeel.getFontPolicy().getFontSet(
                            "Substance", UIManager.getDefaults()).getControlFont(), true, false, ApplicationState.getInstance().getLocale().getLocale());
                }
                fontChooserDialog.setVisible(true);
                if (fontChooserDialog.getSelectedFontSettings() != null) {
                    currentFontSettings = fontChooserDialog.getSelectedFontSettings();
                }
            }
        });

        theme = new JComboBox(new ListComboBoxModel<String>(LookAndFeelSelector.getListOfSkins()));
        theme.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedTheme = (String) theme.getSelectedItem();
                themePreview.setIcon(ThemePreviewLoader.getImage(selectedTheme));
                if (!LookAndFeelSelector.getClassNameForLookAndFeelName(selectedTheme).equals(UIManager.getLookAndFeel().getClass().getName())) {
                    GuiUtils.applyTheme(selectedTheme);
                }
            }

        });
        themePreview = new JLabel();
        themePreview.setVerticalTextPosition(SwingConstants.TOP);
        themePreview.setHorizontalTextPosition(SwingConstants.CENTER);

        GridBagConstraints c = new GridBagConstraints();
        // First display language
        c.gridy = 0;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.insets = new Insets(5, 0, 0, 0);
        add(label2, c);
        c.gridx = 1;
        add(language, c);
        c.gridx = 0;
        c.gridy = 2;
        c.insets = new Insets(25, 0, 5, 0);
        add(label, c);
        c.gridx = 1;
        c.insets = new Insets(25, 0, 0, 0);
        add(windowType, c);
        c.gridx = 0;
        c.gridy = 3;
        c.insets = new Insets(20, 0, 0, 0);
        add(label3, c);
        c.gridx = 0;
        c.gridy = 4;
        c.insets = new Insets(5, 0, 0, 0);
        add(themePreview, c);
        c.gridx = 1;
        c.insets = new Insets(40, 0, 0, 0);
        add(theme, c);
        c.gridx = 0;
        c.gridy = 5;
        c.insets = new Insets(40, 0, 40, 0);
        add(fontSettings, c);
        c.gridx = 0;
        c.gridy = 6;
        c.weightx = 1;
        c.insets = new Insets(5, 0, 0, 0);
        add(showTitle, c);
        c.gridx = 0;
        c.gridy = 7;
        c.weighty = 1;
        c.insets = new Insets(5, 0, 0, 0);
        add(showTrayPlayer, c);
        c.gridx = 1;
        c.gridy = 7;
        c.insets = new Insets(5, 0, 0, 0);
        add(showIconTray, c);
    }

    @Override
    public boolean applyPreferences(ApplicationState state) {
        boolean needRestart = false;

        state.setShowTitle(showTitle.isSelected());

        boolean oldMultipleWindow = state.isMultipleWindow();
        boolean newMultipleWindow = windowType.getSelectedItem().equals(I18nUtils.getString("MULTIPLE_WINDOW"));
        state.setMultipleWindow(newMultipleWindow);
        if (oldMultipleWindow != newMultipleWindow) {
            needRestart = true;
        }

        LocaleBean oldLocale = state.getLocale();
        LocaleBean newLocale = new LocaleBean((Locale) language.getSelectedItem());
        state.setLocale(newLocale);
        if (!oldLocale.equals(newLocale)) {
            needRestart = true;
        }

        FontSettings oldFontSettings = state.getFontSettings();
        state.setFontSettings(currentFontSettings);
        if (!oldFontSettings.equals(currentFontSettings)) {
            needRestart = true;
        }

        state.setShowSystemTray(showIconTray.isSelected());
        state.setShowTrayPlayer(showTrayPlayer.isSelected());

        String newTheme = (String) theme.getSelectedItem();
        state.setSkin(newTheme);

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
     * Sets the show title.
     * 
     * @param show
     *            the new show title
     */
    private void setShowTitle(boolean show) {
        showTitle.setSelected(show);
    }

    /**
     * Sets the show tray player.
     * 
     * @param show
     *            the new show tray player
     */
    private void setShowTrayPlayer(boolean show) {
        this.showTrayPlayer.setSelected(show);
    }

    /**
     * Sets the theme.
     * 
     * @param t
     *            the new theme
     */
    private void setTheme(String t) {
        theme.setSelectedItem(t);
        themePreview.setIcon(ThemePreviewLoader.getImage(t));
    }

    /**
     * Sets the window type.
     * 
     * @param type
     *            the new window type
     */
    private void setWindowType(String type) {
        windowType.setSelectedItem(type);
    }

    @Override
    public void updatePanel(ApplicationState state) {
        setShowTitle(state.isShowTitle());
        setWindowType(state.isMultipleWindow() ? I18nUtils.getString("MULTIPLE_WINDOW") : I18nUtils.getString("STANDARD_WINDOW"));
        setLanguage(I18nUtils.getSelectedLocale());
        setShowIconTray(state.isShowSystemTray());
        setShowTrayPlayer(state.isShowTrayPlayer());
        setTheme(state.getSkin());
        currentFontSettings = state.getFontSettings();
    }

    @Override
    public void resetImmediateChanges(ApplicationState state) {
        if (!LookAndFeelSelector.getClassNameForLookAndFeelName(state.getSkin()).equals(UIManager.getLookAndFeel().getClass().getName())) {
            GuiUtils.applyTheme(state.getSkin());
        }
    }

    @Override
    public boolean validatePanel() {
        return true;
    }

    @Override
    public void dialogVisibilityChanged(boolean visible) {
        // Do nothing
    }

    @Override
    public ImageIcon getIcon() {
        return ImageLoader.getImage(ImageLoader.PREFS);
    }

}
