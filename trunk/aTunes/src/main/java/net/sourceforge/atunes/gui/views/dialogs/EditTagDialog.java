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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.TextTagAttribute;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The dialog for tag editing
 */
public final class EditTagDialog extends AbstractCustomDialog {

	private static final long serialVersionUID = 3395292301087643037L;

	private JCheckBox coverCheckBox;
	private JLabel cover;
	private JButton coverButton;
	private JButton removeCoverButton;
	private JButton okButton;
	private JButton cancelButton;
	private final JTabbedPane tabbedPane = new JTabbedPane();

	private final Map<TextTagAttribute, TagAttributeControls> controls = new HashMap<TextTagAttribute, TagAttributeControls>();

	/**
	 * Instantiates a new edits the tag dialog.
	 * 
	 * @param frame
	 */
	/**
	 * @param frame
	 * @param controlsBuilder
	 */
	public EditTagDialog(final IFrame frame,
			final IControlsBuilder controlsBuilder) {
		super(frame, 500, 600, controlsBuilder);
	}

	@Override
	public void initialize() {
		setTitle(I18nUtils.getString("EDIT_TAG"));
		setResizable(true);

		setLayout(new BorderLayout());

		add(this.tabbedPane, BorderLayout.CENTER);
		this.tabbedPane.addTab(I18nUtils.getString("TAGS"), getTagEditTab());
		this.tabbedPane.addTab(I18nUtils.getString("COVER"), getCoverTab());
		add(getOKAndCancelButtonPanel(), BorderLayout.SOUTH);
	}

	private JPanel getOKAndCancelButtonPanel() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2, 10, 2, 10);
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 4;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.weighty = 0;
		panel.add(this.okButton, c);
		c.gridx = 0;
		c.gridy = 1;
		panel.add(this.cancelButton, c);
		return panel;
	}

	/**
	 * editor for attribute
	 * 
	 * @param attribute
	 * @return
	 */
	public JComponent getEditor(final TextTagAttribute attribute) {
		return this.controls.get(attribute).getEditor();
	}

	/**
	 * Text field editor for attribute
	 * 
	 * @param attribute
	 * @return
	 */
	public JTextField getTextFieldEditor(final TextTagAttribute attribute) {
		JComponent c = this.controls.get(attribute).getEditor();
		if (c instanceof JTextField) {
			return (JTextField) c;
		} else {
			throw new IllegalArgumentException(StringUtils.getString(
					attribute.toString(), " editor is not a JTextField"));
		}
	}

	/**
	 * Combo box editor for attribute
	 * 
	 * @param attribute
	 * @return
	 */
	public JComboBox getComboBoxEditor(final TextTagAttribute attribute) {
		JComponent c = this.controls.get(attribute).getEditor();
		if (c instanceof JComboBox) {
			return (JComboBox) c;
		} else {
			throw new IllegalArgumentException(StringUtils.getString(
					attribute.toString(), " editor is not a JComboBox"));
		}
	}

	/**
	 * Text area editor for attribute
	 * 
	 * @param attribute
	 * @return
	 */
	public JTextArea getTextAreaEditor(final TextTagAttribute attribute) {
		JComponent c = this.controls.get(attribute).getEditor();
		if (c instanceof JTextArea) {
			return (JTextArea) c;
		} else {
			throw new IllegalArgumentException(StringUtils.getString(
					attribute.toString(), " editor is not a JTextArea"));
		}
	}

	/**
	 * Gets the cancel button.
	 * 
	 * @return the cancel button
	 */
	public JButton getCancelButton() {
		return this.cancelButton;
	}

	/**
	 * Gets the cover tab
	 * 
	 * @return cover tab
	 */
	private JPanel getCoverTab() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		JPanel coverPanel = new JPanel();
		FlowLayout fl = new FlowLayout();
		fl.setAlignment(FlowLayout.CENTER);
		coverPanel.setLayout(fl);

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(fl);

		this.coverCheckBox = new JCheckBox();
		infoPanel.add(this.coverCheckBox);
		this.cover = new JLabel();
		this.cover.setPreferredSize(new Dimension(
				Constants.DIALOG_LARGE_IMAGE_WIDTH,
				Constants.DIALOG_LARGE_IMAGE_HEIGHT));
		this.cover.setMinimumSize(new Dimension(
				Constants.DIALOG_LARGE_IMAGE_WIDTH,
				Constants.DIALOG_LARGE_IMAGE_HEIGHT));
		this.cover.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.coverButton = new JButton(I18nUtils.getString("EDIT_COVER"));
		this.removeCoverButton = new JButton(
				I18nUtils.getString("REMOVE_COVER"));
		JPanel coverButtonsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
		coverButtonsPanel.add(this.coverButton);
		coverButtonsPanel.add(this.removeCoverButton);
		this.coverCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				setCoverSelected(EditTagDialog.this.coverCheckBox.isSelected());
			}

		});
		panel.add(infoPanel, BorderLayout.NORTH);
		coverPanel.add(this.cover);
		panel.add(coverPanel, BorderLayout.CENTER);
		panel.add(coverButtonsPanel, BorderLayout.SOUTH);

		return panel;
	}

	/**
	 * Gets the content.
	 * 
	 * @return the content
	 */
	private JPanel getTagEditTab() {
		JPanel panel = new JPanel(new GridBagLayout());

		createCustomTextFieldControl(TextTagAttribute.TITLE);
		createComboBoxControl(TextTagAttribute.ALBUM);
		createComboBoxControl(TextTagAttribute.ARTIST);
		createCustomTextFieldControl(TextTagAttribute.YEAR);
		createComboBoxControl(TextTagAttribute.GENRE);
		createTextAreaControl(TextTagAttribute.COMMENT);
		createTextAreaControl(TextTagAttribute.LYRICS);
		createCustomTextFieldControl(TextTagAttribute.TRACK);
		createCustomTextFieldControl(TextTagAttribute.DISC_NUMBER);
		createCustomTextFieldControl(TextTagAttribute.COMPOSER);
		createCustomTextFieldControl(TextTagAttribute.ALBUM_ARTIST);

		this.okButton = new JButton(I18nUtils.getString("OK"));
		this.cancelButton = new JButton(I18nUtils.getString("CANCEL"));

		arrangePanel(panel);

		return panel;
	}

	private void createControl(final TextTagAttribute attribute,
			final JComponent editor) {
		final JCheckBox checkBox = new JCheckBox();
		checkBox.setFocusable(false);
		checkBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				setTagAttributeSelected(attribute, checkBox.isSelected());
			}
		});
		checkBox.setSelected(true);
		this.controls
				.put(attribute, new TagAttributeControls(checkBox, editor));
	}

	private void createCustomTextFieldControl(final TextTagAttribute attribute) {
		createControl(attribute, getControlsBuilder().createTextField());
	}

	private void createComboBoxControl(final TextTagAttribute attribute) {
		JComboBox comboBox = new JComboBox();
		comboBox.setEditable(true);
		createControl(attribute, comboBox);
	}

	private void createTextAreaControl(final TextTagAttribute attribute) {
		JTextArea textArea = getControlsBuilder().createTextArea();
		textArea.setBorder(BorderFactory.createEmptyBorder());
		createControl(attribute, textArea);
	}

	/**
	 * @param panel
	 */
	private void arrangePanel(final JPanel panel) {
		GridBagConstraints c = new GridBagConstraints();
		addControl(TextTagAttribute.TITLE, panel, c, 0);
		addControl(TextTagAttribute.ALBUM_ARTIST, panel, c, 1);
		addControl(TextTagAttribute.ARTIST, panel, c, 2);
		addControl(TextTagAttribute.ALBUM, panel, c, 3);
		addControl(TextTagAttribute.YEAR, panel, c, 4);
		addControl(TextTagAttribute.TRACK, panel, c, 5);
		addControl(TextTagAttribute.DISC_NUMBER, panel, c, 6);
		addControl(TextTagAttribute.GENRE, panel, c, 7);
		addControl(TextTagAttribute.COMPOSER, panel, c, 8);
		addControl(TextTagAttribute.COMMENT, panel, c, 9);
		addControl(TextTagAttribute.LYRICS, panel, c, 10);
	}

	/**
	 * @param attribute
	 * @param panel
	 * @param label
	 * @param c
	 * @param row
	 */
	private void addControl(final TextTagAttribute attribute,
			final JPanel panel, final GridBagConstraints c, final int row) {
		c.insets = new Insets(2, 10, 2, 2);
		c.gridx = 0;
		c.gridy = row;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weighty = 0;
		panel.add(this.controls.get(attribute).getCheckBox(), c);

		c.insets = new Insets(2, 2, 2, 10);
		c.gridx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		panel.add(new JLabel(I18nUtils.getString(attribute.toString())), c);

		c.insets = new Insets(2, 10, 2, 10);
		c.gridx = 2;
		c.weightx = 1;
		c.gridwidth = 2;

		JComponent editor = this.controls.get(attribute).getEditor();
		if (editor instanceof JTextArea) {
			c.fill = GridBagConstraints.BOTH;
			c.weighty = 1;
			panel.add(getControlsBuilder().createScrollPane(editor), c);
		} else {
			panel.add(editor, c);
		}
	}

	/**
	 * Gets the ok button.
	 * 
	 * @return the ok button
	 */
	public JButton getOkButton() {
		return this.okButton;
	}

	/**
	 * Cover control
	 * 
	 * @return
	 */
	public JLabel getCover() {
		return this.cover;
	}

	/**
	 * Select cover button
	 * 
	 * @return
	 */
	public JButton getCoverButton() {
		return this.coverButton;
	}

	/**
	 * Remove cover button
	 * 
	 * @return
	 */
	public JButton getRemoveCoverButton() {
		return this.removeCoverButton;
	}

	/**
	 * Checkbox for given attribute
	 * 
	 * @param attribute
	 * @return
	 */
	public JCheckBox getCheckBox(final TextTagAttribute attribute) {
		return this.controls.get(attribute).getCheckBox();
	}

	/**
	 * @return the coverCheckBox
	 */
	public JCheckBox getCoverCheckBox() {
		return this.coverCheckBox;
	}

	/**
	 * Enables or disables an attribute
	 * 
	 * @param attribute
	 * @param selected
	 */
	public void setTagAttributeSelected(final TextTagAttribute attribute,
			final boolean selected) {
		this.controls.get(attribute).getCheckBox().setSelected(selected);
		this.controls.get(attribute).getEditor().setEnabled(selected);
	}

	/**
	 * Enables or disables cover
	 * 
	 * @param b
	 */
	public void setCoverSelected(final boolean b) {
		this.coverCheckBox.setSelected(b);
		this.coverButton.setEnabled(b);
		this.removeCoverButton.setEnabled(b);
	}

	private static class TagAttributeControls {

		private final JCheckBox checkBox;

		private final JComponent editor;

		TagAttributeControls(final JCheckBox checkBox, final JComponent editor) {
			this.checkBox = checkBox;
			this.editor = editor;
		}

		/**
		 * @return
		 */
		public JCheckBox getCheckBox() {
			return this.checkBox;
		}

		/**
		 * @return
		 */
		public JComponent getEditor() {
			return this.editor;
		}
	}
}
