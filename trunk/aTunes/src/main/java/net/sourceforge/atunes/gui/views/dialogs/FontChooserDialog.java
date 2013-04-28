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

package net.sourceforge.atunes.gui.views.dialogs;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import net.sourceforge.atunes.model.FontSettings;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IFontBeanFactory;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * @author alex
 * 
 */
public final class FontChooserDialog extends AbstractCustomDialog {

	private static final long serialVersionUID = 2941323406891892062L;

	private static final Integer[] FONT_SIZES = new Integer[] { 6, 7, 8, 9, 10,
			11, 12, 13, 14, 15, 16 };

	@SuppressWarnings("rawtypes")
	private JList fontList;
	@SuppressWarnings("rawtypes")
	private JList fontSizeList;
	private JCheckBox useFontSmoothingCheckbox;
	private JCheckBox useFontSmoothingSettingsFromOsCheckbox;
	private JLabel fontPreviewLabel;

	private Locale locale;

	private FontSettings fontSettings = new FontSettings();

	private IFontBeanFactory fontBeanFactory;

	/**
	 * @param fontBeanFactory
	 */
	public void setFontBeanFactory(final IFontBeanFactory fontBeanFactory) {
		this.fontBeanFactory = fontBeanFactory;
	}

	/**
	 * @param owner
	 * @param controlsBuilder
	 */
	public FontChooserDialog(final IFrame owner,
			final IControlsBuilder controlsBuilder) {
		super(owner, 300, 300, controlsBuilder);
	}

	/**
	 * Initializes fonts
	 * 
	 * @param font
	 * @param useFontSmoothing
	 * @param useFontSmoothingSettingsFromOs
	 * @param locale
	 */
	public void initializeFont(final Font font, final boolean useFontSmoothing,
			final boolean useFontSmoothingSettingsFromOs, final Locale locale) {
		this.locale = locale;
		this.fontSettings.setFont(this.fontBeanFactory.getFontBean(font));
		this.fontSettings.setUseFontSmoothing(useFontSmoothing);
		this.fontSettings
				.setUseFontSmoothingSettingsFromOs(useFontSmoothingSettingsFromOs);
		setResizable(false);
		setTitle(I18nUtils.getString("FONT_SETTINGS"));
		add(getContent(getLookAndFeel()));
	}

	@SuppressWarnings("unchecked")
	private JPanel getContent(final ILookAndFeel iLookAndFeel) {
		String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getAvailableFontFamilyNames(this.locale);

		this.fontPreviewLabel = new JLabel();
		this.fontPreviewLabel.setMinimumSize(new Dimension(50, 20));
		this.fontPreviewLabel.setMaximumSize(new Dimension(50, 20));
		this.fontPreviewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		this.fontPreviewLabel.setText("Test Test");
		this.fontList = iLookAndFeel.getList();
		this.fontList.setListData(fonts);
		this.fontList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.fontList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(final ListSelectionEvent e) {
				FontChooserDialog.this.fontSettings
						.setFont(FontChooserDialog.this.fontBeanFactory
								.getFontBean(new Font(
										FontChooserDialog.this.fontList
												.getSelectedValue().toString(),
										FontChooserDialog.this.fontSettings
												.getFont().getStyle(),
										FontChooserDialog.this.fontSettings
												.getFont().getSize())));
				updatePreview();
			}
		});
		this.fontList.setSelectedValue(this.fontSettings.getFont().getName(),
				true);
		this.fontSizeList = iLookAndFeel.getList();
		this.fontSizeList.setListData(FONT_SIZES);
		this.fontSizeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.fontSizeList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(final ListSelectionEvent e) {
				FontChooserDialog.this.fontSettings.setFont(FontChooserDialog.this.fontBeanFactory.getFontBean(FontChooserDialog.this.fontSettings
						.getFont()
						.toFont()
						.deriveFont(
								Float.valueOf(FontChooserDialog.this.fontSizeList
										.getSelectedValue().toString()))));
				updatePreview();
			}
		});
		this.fontSizeList.setSelectedValue(this.fontSettings.getFont()
				.getSize(), true);
		this.useFontSmoothingCheckbox = new JCheckBox(
				I18nUtils.getString("USE_FONT_SMOOTHING"));
		this.useFontSmoothingCheckbox.setSelected(this.fontSettings
				.isUseFontSmoothing());
		this.useFontSmoothingCheckbox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(final ItemEvent e) {
				FontChooserDialog.this.fontSettings
						.setUseFontSmoothing(FontChooserDialog.this.useFontSmoothingCheckbox
								.isSelected());
			}
		});
		this.useFontSmoothingCheckbox.setSelected(this.fontSettings
				.isUseFontSmoothing());
		this.useFontSmoothingSettingsFromOsCheckbox = new JCheckBox(
				I18nUtils.getString("USE_OS_SETTINGS_FOR_FONT_SMOOTHING"));
		this.useFontSmoothingSettingsFromOsCheckbox
				.setSelected(this.fontSettings
						.isUseFontSmoothingSettingsFromOs());
		this.useFontSmoothingSettingsFromOsCheckbox
				.addItemListener(new ItemListener() {

					@Override
					public void itemStateChanged(final ItemEvent e) {
						FontChooserDialog.this.fontSettings
								.setUseFontSmoothingSettingsFromOs(FontChooserDialog.this.useFontSmoothingSettingsFromOsCheckbox
										.isSelected());
						FontChooserDialog.this.useFontSmoothingCheckbox
								.setEnabled(!FontChooserDialog.this.useFontSmoothingSettingsFromOsCheckbox
										.isSelected());
					}
				});
		this.useFontSmoothingSettingsFromOsCheckbox
				.setSelected(this.fontSettings
						.isUseFontSmoothingSettingsFromOs());
		JButton okButton = new JButton();
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				FontChooserDialog.this.dispose();
			}
		});
		okButton.setText(I18nUtils.getString("OK"));
		JButton cancelButton = new JButton();
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				FontChooserDialog.this.fontSettings = null;
				FontChooserDialog.this.dispose();
			}
		});
		cancelButton.setText(I18nUtils.getString("CANCEL"));

		return createPanel(okButton, cancelButton);
	}

	/**
	 * @param okButton
	 * @param cancelButton
	 * @return
	 */
	private JPanel createPanel(final JButton okButton,
			final JButton cancelButton) {
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
		panel.add(getControlsBuilder().createScrollPane(this.fontList), c);
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 1;
		c.weightx = 0.3;
		c.weighty = 0.7;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		panel.add(getControlsBuilder().createScrollPane(this.fontSizeList), c);
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.CENTER;
		panel.add(this.fontPreviewLabel, c);
		c.gridy = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_START;
		panel.add(this.useFontSmoothingCheckbox, c);
		c.gridy = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_END;
		panel.add(this.useFontSmoothingSettingsFromOsCheckbox, c);
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
		this.fontPreviewLabel.setFont(this.fontSettings.getFont().toFont());
	}

	/**
	 * @return font settings selected by user
	 */
	public FontSettings getSelectedFontSettings() {
		return this.fontSettings;
	}
}
