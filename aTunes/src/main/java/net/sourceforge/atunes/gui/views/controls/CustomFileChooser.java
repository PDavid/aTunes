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

package net.sourceforge.atunes.gui.views.controls;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentListener;

import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IFileSelectorDialog;
import net.sourceforge.atunes.model.IFolderSelectorDialog;
import net.sourceforge.atunes.model.IOSManager;

/**
 * A custom file chooser with text field and button that opens the chooser.
 */
public final class CustomFileChooser extends JPanel {

	private static final long serialVersionUID = 4713483251093570020L;

	private final JTextField textField;
	private String result;

  	public CustomFileChooser(final String title, final Component parent,
			final int length, final int type, final IOSManager osManager,
			final IBeanFactory beanFactory, IControlsBuilder controlsBuilder) {

      this(title, parent, length, type, osManager, beanFactory, controlsBuilder, null);
    }

  /**
	 * Instantiates a new custom file chooser.
	 *
	 * @param title
	 * @param parent
	 * @param length
	 * @param type
	 * @param osManager
	 * @param beanFactory
	 * @param controlsBuilder
   * @param folder path to a default(initial) folder, user's HOME is used when null
	 */
	public CustomFileChooser(final String title, final Component parent,
			final int length, final int type, final IOSManager osManager,
			final IBeanFactory beanFactory, IControlsBuilder controlsBuilder,
      final String folder) {
		super(new GridBagLayout());
		// Use user home by default
		final File defaultFolder = new File( folder != null ? folder : osManager.getUserHome() );
		textField = controlsBuilder.createTextField();
		textField.setColumns(length);
		textField.setText(net.sourceforge.atunes.utils.FileUtils.getPath(defaultFolder));
		JButton button = new JButton("...");

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				File selected = null;
				if (type == JFileChooser.DIRECTORIES_ONLY) {
					IFolderSelectorDialog dialog = beanFactory.getBean(
							IDialogFactory.class).newDialog(
							IFolderSelectorDialog.class);
					dialog.setTitle(title);
					selected = dialog.selectFolder(defaultFolder);
				} else {
					IFileSelectorDialog dialog = beanFactory.getBean(
							IDialogFactory.class).newDialog(
							IFileSelectorDialog.class);
					dialog.setTitle(title);
					selected = dialog.loadFile(defaultFolder);
				}
				if (selected != null) {
					result = net.sourceforge.atunes.utils.FileUtils
							.getPath(selected);
				} else {
					result = null;
				}
				textField.setText(result);
			}
		});

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(textField, c);
		c.gridx = 1;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(0, 2, 0, 0);
		add(button, c);
	}

	/**
	 * Gets the result.
	 *
	 * @return the result
	 */
	public String getResult() {
		result = textField.getText();
		return result;
	}

	/**
	 * Sets the text.
	 *
	 * @param text
	 *            the new text
	 */
	public void setText(final String text) {
		textField.setText(text);
		result = text;
	}

	/**
	 * Adds a document listener to text field
	 *
	 * @param listener
	 */
	public void addDocumentListener(final DocumentListener listener) {
		this.textField.getDocument().addDocumentListener(listener);
	}

}
