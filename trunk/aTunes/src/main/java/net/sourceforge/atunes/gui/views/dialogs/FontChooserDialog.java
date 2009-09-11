/*
 * aTunes 1.14.0
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
package net.sourceforge.atunes.gui.views.dialogs;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.ConstructorProperties;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.atunes.gui.views.controls.CustomModalDialog;
import net.sourceforge.atunes.kernel.modules.state.beans.FontBean;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.LanguageTool;

public class FontChooserDialog extends CustomModalDialog {

    public static class FontSettings {

        private FontBean font;
        private boolean useFontSmoothing;
        private boolean useFontSmoothingSettingsFromOs;

        @ConstructorProperties( { "font", "useFontSmoothing", "useFontSmoothingSettingsFromOs" })
        public FontSettings(FontBean font, boolean useFontSmoothing, boolean useFontSmoothingSettingsFromOs) {
            super();
            this.font = font;
            this.useFontSmoothing = useFontSmoothing;
            this.useFontSmoothingSettingsFromOs = useFontSmoothingSettingsFromOs;
        }

        public FontSettings() {
        }

        void setFont(FontBean font) {
            this.font = font;
        }

        public FontBean getFont() {
            return font;
        }

        void setUseFontSmoothing(boolean useFontSmoothing) {
            this.useFontSmoothing = useFontSmoothing;
        }

        public boolean isUseFontSmoothing() {
            return useFontSmoothing;
        }

        void setUseFontSmoothingSettingsFromOs(boolean useFontSmoothingSettingsFromOs) {
            this.useFontSmoothingSettingsFromOs = useFontSmoothingSettingsFromOs;
        }

        public boolean isUseFontSmoothingSettingsFromOs() {
            return useFontSmoothingSettingsFromOs;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((font == null) ? 0 : font.hashCode());
            result = prime * result + (useFontSmoothing ? 1231 : 1237);
            result = prime * result + (useFontSmoothingSettingsFromOs ? 1231 : 1237);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            FontSettings other = (FontSettings) obj;
            if (font == null) {
                if (other.font != null)
                    return false;
            } else if (!font.equals(other.font))
                return false;
            if (useFontSmoothing != other.useFontSmoothing)
                return false;
            if (useFontSmoothingSettingsFromOs != other.useFontSmoothingSettingsFromOs)
                return false;
            return true;
        }

    }

    private static final long serialVersionUID = 2941323406891892062L;

    private static final Integer[] FONT_SIZES = new Integer[] { 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };

    private JList fontList;
    private JList fontSizeList;
    private JCheckBox useFontSmoothingCheckbox;
    private JCheckBox useFontSmoothingSettingsFromOsCheckbox;
    private JLabel fontPreviewLabel;
    private JButton okButton;
    private JButton cancelButton;

    private Locale locale;

    private FontSettings fontSettings = new FontSettings();

    public FontChooserDialog(Window owner, int width, int height, Font font, boolean useFontSmoothing, boolean useFontSmoothingSettingsFromOs, Locale locale) {
        super(owner, width, height, true);
        this.locale = locale;
        this.fontSettings.setFont(new FontBean(font));
        this.fontSettings.setUseFontSmoothing(useFontSmoothing);
        this.fontSettings.setUseFontSmoothingSettingsFromOs(useFontSmoothingSettingsFromOs);
        setResizable(false);
        setTitle(LanguageTool.getString("FONT_SETTINGS"));
        setContent(getContent());
        GuiUtils.applyComponentOrientation(this);
        enableDisposeActionWithEscapeKey();
    }

    private JPanel getContent() {
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames(locale);

        fontPreviewLabel = new JLabel();
        fontPreviewLabel.setMinimumSize(new Dimension(50, 20));
        fontPreviewLabel.setMaximumSize(new Dimension(50, 20));
        fontPreviewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        fontPreviewLabel.setText("Test Test");
        fontList = new JList(fonts);
        fontList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fontList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                fontSettings.setFont(new FontBean(new Font(fontList.getSelectedValue().toString(), fontSettings.getFont().getStyle(), fontSettings.getFont().getSize())));
                updatePreview();
            }
        });
        fontList.setSelectedValue(fontSettings.getFont().getName(), true);
        fontSizeList = new JList(FONT_SIZES);
        fontSizeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fontSizeList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                fontSettings.setFont(new FontBean(fontSettings.getFont().toFont().deriveFont(Float.valueOf(fontSizeList.getSelectedValue().toString()))));
                updatePreview();
            }
        });
        fontSizeList.setSelectedValue(fontSettings.font.getSize(), true);
        useFontSmoothingCheckbox = new JCheckBox(LanguageTool.getString("USE_FONT_SMOOTHING"));
        useFontSmoothingCheckbox.setSelected(fontSettings.isUseFontSmoothing());
        useFontSmoothingCheckbox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                fontSettings.setUseFontSmoothing(useFontSmoothingCheckbox.isSelected());
            }
        });
        useFontSmoothingCheckbox.setSelected(fontSettings.isUseFontSmoothing());
        useFontSmoothingSettingsFromOsCheckbox = new JCheckBox(LanguageTool.getString("USE_OS_SETTINGS_FOR_FONT_SMOOTHING"));
        useFontSmoothingSettingsFromOsCheckbox.setSelected(fontSettings.isUseFontSmoothingSettingsFromOs());
        useFontSmoothingSettingsFromOsCheckbox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                fontSettings.setUseFontSmoothingSettingsFromOs(useFontSmoothingSettingsFromOsCheckbox.isSelected());
                useFontSmoothingCheckbox.setEnabled(!useFontSmoothingSettingsFromOsCheckbox.isSelected());
            }
        });
        useFontSmoothingSettingsFromOsCheckbox.setSelected(fontSettings.isUseFontSmoothingSettingsFromOs());
        okButton = new JButton();
        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                FontChooserDialog.this.dispose();
            }
        });
        okButton.setText(LanguageTool.getString("OK"));
        cancelButton = new JButton();
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                fontSettings = null;
                FontChooserDialog.this.dispose();
            }
        });
        cancelButton.setText(LanguageTool.getString("CANCEL"));

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.weightx = 1;
        c.weighty = 0.7;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(new JScrollPane(fontList), c);
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        c.weightx = 0.3;
        c.weighty = 0.7;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        panel.add(new JScrollPane(fontSizeList), c);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.weightx = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        panel.add(fontPreviewLabel, c);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        c.weightx = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.LINE_START;
        panel.add(useFontSmoothingCheckbox, c);
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        c.weightx = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.LINE_END;
        panel.add(useFontSmoothingSettingsFromOsCheckbox, c);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 2;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, c);
        return panel;
    }

    private void updatePreview() {
        fontPreviewLabel.setFont(fontSettings.getFont().toFont());
    }

    public FontSettings getSelectedFontSettings() {
        return fontSettings;
    }

}
