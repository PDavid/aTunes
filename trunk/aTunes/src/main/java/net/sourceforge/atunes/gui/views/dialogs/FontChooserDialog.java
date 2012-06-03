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
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.gui.views.controls.CloseAction;
import net.sourceforge.atunes.model.FontSettings;
import net.sourceforge.atunes.model.IFontBeanFactory;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * @author alex
 *
 */
public final class FontChooserDialog extends AbstractCustomDialog {

    private static final long serialVersionUID = 2941323406891892062L;

    private static final Integer[] FONT_SIZES = new Integer[] { 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };

    private JList fontList;
    private JList fontSizeList;
    private JCheckBox useFontSmoothingCheckbox;
    private JCheckBox useFontSmoothingSettingsFromOsCheckbox;
    private JLabel fontPreviewLabel;

    private Locale locale;

    private FontSettings fontSettings = new FontSettings();
    
    private IFontBeanFactory fontBeanFactory;

    /**
     * @param owner
     * @param width
     * @param height
     * @param font
     * @param useFontSmoothing
     * @param useFontSmoothingSettingsFromOs
     * @param locale
     * @param fontBeanFactory
     */
    public FontChooserDialog(Window owner, int width, int height, Font font, boolean useFontSmoothing, boolean useFontSmoothingSettingsFromOs, Locale locale, IFontBeanFactory fontBeanFactory) {
        super(owner, width, height, true, CloseAction.DISPOSE);
        this.locale = locale;
        this.fontBeanFactory = fontBeanFactory;
        this.fontSettings.setFont(fontBeanFactory.getFontBean(font));
        this.fontSettings.setUseFontSmoothing(useFontSmoothing);
        this.fontSettings.setUseFontSmoothingSettingsFromOs(useFontSmoothingSettingsFromOs);
        setResizable(false);
        setTitle(I18nUtils.getString("FONT_SETTINGS"));
        add(getContent(getLookAndFeel()));
    }

    private JPanel getContent(ILookAndFeel iLookAndFeel) {
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames(locale);

        fontPreviewLabel = new JLabel();
        fontPreviewLabel.setMinimumSize(new Dimension(50, 20));
        fontPreviewLabel.setMaximumSize(new Dimension(50, 20));
        fontPreviewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        fontPreviewLabel.setText("Test Test");
        fontList = iLookAndFeel.getList();
        fontList.setListData(fonts);
        fontList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fontList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                fontSettings.setFont(fontBeanFactory.getFontBean(new Font(fontList.getSelectedValue().toString(), fontSettings.getFont().getStyle(), fontSettings.getFont().getSize())));
                updatePreview();
            }
        });
        fontList.setSelectedValue(fontSettings.getFont().getName(), true);
        fontSizeList = iLookAndFeel.getList();
        fontSizeList.setListData(FONT_SIZES);
        fontSizeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fontSizeList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                fontSettings.setFont(fontBeanFactory.getFontBean(fontSettings.getFont().toFont().deriveFont(Float.valueOf(fontSizeList.getSelectedValue().toString()))));
                updatePreview();
            }
        });
        fontSizeList.setSelectedValue(fontSettings.getFont().getSize(), true);
        useFontSmoothingCheckbox = new JCheckBox(I18nUtils.getString("USE_FONT_SMOOTHING"));
        useFontSmoothingCheckbox.setSelected(fontSettings.isUseFontSmoothing());
        useFontSmoothingCheckbox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                fontSettings.setUseFontSmoothing(useFontSmoothingCheckbox.isSelected());
            }
        });
        useFontSmoothingCheckbox.setSelected(fontSettings.isUseFontSmoothing());
        useFontSmoothingSettingsFromOsCheckbox = new JCheckBox(I18nUtils.getString("USE_OS_SETTINGS_FOR_FONT_SMOOTHING"));
        useFontSmoothingSettingsFromOsCheckbox.setSelected(fontSettings.isUseFontSmoothingSettingsFromOs());
        useFontSmoothingSettingsFromOsCheckbox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                fontSettings.setUseFontSmoothingSettingsFromOs(useFontSmoothingSettingsFromOsCheckbox.isSelected());
                useFontSmoothingCheckbox.setEnabled(!useFontSmoothingSettingsFromOsCheckbox.isSelected());
            }
        });
        useFontSmoothingSettingsFromOsCheckbox.setSelected(fontSettings.isUseFontSmoothingSettingsFromOs());
        JButton okButton = new JButton();
        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                FontChooserDialog.this.dispose();
            }
        });
        okButton.setText(I18nUtils.getString("OK"));
        JButton cancelButton = new JButton();
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                fontSettings = null;
                FontChooserDialog.this.dispose();
            }
        });
        cancelButton.setText(I18nUtils.getString("CANCEL"));

        return createPanel(iLookAndFeel, okButton, cancelButton);
    }

	/**
	 * @param iLookAndFeel
	 * @param okButton
	 * @param cancelButton
	 * @return
	 */
	private JPanel createPanel(ILookAndFeel iLookAndFeel, JButton okButton,
			JButton cancelButton) {
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
        panel.add(iLookAndFeel.getListScrollPane(fontList), c);
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        c.weightx = 0.3;
        c.weighty = 0.7;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        panel.add(iLookAndFeel.getListScrollPane(fontSizeList), c);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.weightx = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        panel.add(fontPreviewLabel, c);
        c.gridy = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.LINE_START;
        panel.add(useFontSmoothingCheckbox, c);
        c.gridy = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.LINE_END;
        panel.add(useFontSmoothingSettingsFromOsCheckbox, c);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        c.gridy = 4;
        c.weightx = 0;
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
