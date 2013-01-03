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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IInputDialog;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The Class InputDialog.
 */
public final class InputDialog extends AbstractCustomDialog implements
		IInputDialog {

	private static final long serialVersionUID = -5789081662254435503L;

	/** The text field. */
	private JTextField textField;

	/** The result. */
	private String result = null;

	private String text;

	/**
	 * Instantiates a new input dialog.
	 * 
	 * @param frame
	 * @param controlsBuilder
	 */
	public InputDialog(final IFrame frame,
			final IControlsBuilder controlsBuilder) {
		super(frame, 400, 130, controlsBuilder);
	}

	@Override
	public void initialize() {
		setResizable(false);
		JPanel panel = new JPanel(new GridBagLayout());
		this.textField = getControlsBuilder().createTextField();
		JButton okButton = new JButton(I18nUtils.getString("OK"));
		ActionListener okListener = new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				InputDialog.this.result = InputDialog.this.textField.getText();
				dispose();
			}
		};
		okButton.addActionListener(okListener);
		this.textField.addActionListener(okListener);
		JButton cancelButton = new JButton(I18nUtils.getString("CANCEL"));
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				InputDialog.this.result = null;
				dispose();
			}
		});
		arrangePanel(panel, okButton, cancelButton);
	}

	/**
	 * @param panel
	 * @param okButton
	 * @param cancelButton
	 */
	private void arrangePanel(final JPanel panel, final JButton okButton,
			final JButton cancelButton) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10, 20, 10, 20);
		GridLayout gl = new GridLayout(1, 2, 40, 0);
		JPanel auxPanel = new JPanel(gl);
		auxPanel.add(okButton);
		auxPanel.add(cancelButton);
		panel.add(this.textField, c);
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(10, 50, 0, 50);
		panel.add(auxPanel, c);
		add(panel);
		getRootPane().setDefaultButton(okButton);
	}

	@Override
	public String getResult() {
		return this.result;
	}

	@Override
	public void setText(final String text) {
		this.text = text;
	}

	@Override
	public void showDialog() {
		this.textField.setText(this.text);
		this.textField.setSelectionStart(0);
		this.textField.setSelectionEnd(this.text != null ? this.text.length()
				: 0);
		setVisible(true);
	}

	@Override
	public void hideDialog() {
		setVisible(false);
	}
}
