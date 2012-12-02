/*
 * aTunes 3.0.0
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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.gui.views.controls.CloseAction;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IErrorReport;
import net.sourceforge.atunes.model.IErrorReportCreator;
import net.sourceforge.atunes.model.IErrorReporter;
import net.sourceforge.atunes.model.IExceptionDialog;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Dialog used to show an exception
 * 
 * @author alex
 * 
 */
public class ExceptionDialog extends AbstractCustomDialog implements
		IExceptionDialog {

	private static final int BORDER = 10;

	private static final int WIDTH = 600;

	private static final int HEIGHT = 500;

	private static final long serialVersionUID = -2528275301092742608L;

	private IErrorReportCreator errorReportCreator;

	private IErrorReporter errorReporter;

	/**
	 * @param errorReportCreator
	 */
	public void setErrorReportCreator(
			final IErrorReportCreator errorReportCreator) {
		this.errorReportCreator = errorReportCreator;
	}

	/**
	 * @param errorReporter
	 */
	public void setErrorReporter(final IErrorReporter errorReporter) {
		this.errorReporter = errorReporter;
	}

	/**
	 * Default constructor
	 * 
	 * @param frame
	 * @param controlsBuilder
	 */
	public ExceptionDialog(final IFrame frame,
			final IControlsBuilder controlsBuilder) {
		super(frame, WIDTH, HEIGHT, false, CloseAction.DISPOSE, controlsBuilder);
	}

	@Override
	public void showExceptionDialog(final Throwable throwable) {
		showDialog(this.errorReportCreator.createReport(null, throwable));
	}

	@Override
	public void showExceptionDialog(final String descriptionError,
			final Throwable t) {
		showDialog(this.errorReportCreator.createReport(descriptionError, t));
	}

	/**
	 * Shows a exception report dialog
	 * 
	 * @param t
	 */
	private void showDialog(final IErrorReport errorReport) {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(BORDER, BORDER, BORDER,
				BORDER));
		JLabel icon = new JLabel(Images.getImage(Images.APP_LOGO_90));
		JTextArea messageLabel = getControlsBuilder().createTextArea();
		messageLabel.setText(I18nUtils.getString("ERROR_TO_REPORT"));
		messageLabel.setEditable(false);
		messageLabel.setEnabled(false);
		messageLabel.setWrapStyleWord(true);
		messageLabel.setOpaque(false);
		messageLabel.setLineWrap(true);

		JButton sendButton = new JButton(I18nUtils.getString("SEND"));
		JButton cancelButton = new JButton(I18nUtils.getString("CANCEL"));
		final JTextArea textArea = getControlsBuilder().createTextArea();
		textArea.setEditable(false);
		JScrollPane scrollPane = getLookAndFeelManager()
				.getCurrentLookAndFeel().getScrollPane(textArea);

		JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, BORDER, BORDER));
		buttonsPanel.add(sendButton);
		buttonsPanel.add(cancelButton);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 2;
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(BORDER, BORDER, BORDER, BORDER);
		panel.add(icon, c);
		c.gridx = 1;
		c.weightx = 1;
		c.gridwidth = 3;
		c.gridheight = 1;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		panel.add(messageLabel, c);
		c.gridy = 1;
		c.weighty = 1;
		panel.add(scrollPane, c);
		c.gridx = 1;
		c.gridy = 2;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		c.weighty = 0;
		c.anchor = GridBagConstraints.EAST;
		panel.add(buttonsPanel, c);

		sendButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				ExceptionDialog.this.errorReporter.reportError(errorReport);
				ExceptionDialog.this.setVisible(false);
			}
		});

		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent arg0) {
				ExceptionDialog.this.setVisible(false);
			}
		});

		add(panel);
		super.setTitle(I18nUtils.getString("ERROR"));

		textArea.setText(errorReport.toString());
		textArea.setCaretPosition(0);

		setVisible(true);
	}

	@Override
	public void initialize() {
		// Do nothing
	}

	@Override
	public void hideDialog() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void showDialog() {
		throw new UnsupportedOperationException();
	}

	@Override
	@Deprecated
	public void setTitle(final String title) {
		// Not used
	}
}
