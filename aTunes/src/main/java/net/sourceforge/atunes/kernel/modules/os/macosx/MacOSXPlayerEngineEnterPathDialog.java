/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.modules.os.macosx;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sourceforge.atunes.gui.views.controls.CustomDialog;
import net.sourceforge.atunes.gui.views.controls.CustomJFileChooser;
import net.sourceforge.atunes.gui.views.controls.SimpleTextPane;
import net.sourceforge.atunes.kernel.OsManager;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Dialog to enter player engine path in Mac OS
 * @author alex
 *
 */
class MacOSXPlayerEngineEnterPathDialog extends CustomDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5594530223379760626L;

	private JDialog previousDialog;
		
	private JButton finishButton;
	
	public MacOSXPlayerEngineEnterPathDialog(JDialog previousDialog) {
		super((JFrame)previousDialog.getParent(), 450, 250);
		setModal(true);
		this.previousDialog = previousDialog;
		setResizable(false);
		setLocationRelativeTo(previousDialog.getParent());
		setTitle(I18nUtils.getString("PLAYER_ENGINE_SELECTION"));
		enableDisposeActionWithEscapeKey();
        addContent();
	}

	private void addContent() {
		SimpleTextPane instructions = new SimpleTextPane(I18nUtils.getString("MAC_PLAYER_ENGINE_ENTER_PATH"));

		final CustomJFileChooser locationFileChooser = new CustomJFileChooser(this, 0, JFileChooser.FILES_ONLY);
		locationFileChooser.addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				updateFinishButton();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				updateFinishButton();
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				updateFinishButton();
			}
			
			private void updateFinishButton() {
				if (!locationFileChooser.getResult().isEmpty()) {
					File file = new File(locationFileChooser.getResult());
					finishButton.setEnabled(file.exists() && !file.isDirectory());
				} else {
					finishButton.setEnabled(false);
				}
			}
			
		});

		JButton previousButton = new JButton(I18nUtils.getString("PREVIOUS"));
		previousButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				MacOSXPlayerEngineEnterPathDialog.this.setVisible(false);
				previousDialog.setVisible(true);
			}
		});
		finishButton = new JButton(I18nUtils.getString("FINISH"));
		finishButton.setEnabled(false);
		finishButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				OsManager.setOSProperty(MacOSXOperatingSystem.MPLAYER_COMMAND, locationFileChooser.getResult());
				previousDialog.dispose();
				MacOSXPlayerEngineEnterPathDialog.this.setVisible(false);
				OsManager.playerEngineFound();
			}
		});
		JButton cancelButton = new JButton(I18nUtils.getString("CANCEL"));
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				MacOSXPlayerEngineEnterPathDialog.this.setVisible(false);
			}
		});
		
		JPanel buttons = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.anchor = GridBagConstraints.EAST;
		buttons.add(previousButton, c);
		c.gridx = 1;
		c.weightx = 0;
		buttons.add(finishButton, c);
		c.gridx = 2;
		buttons.add(cancelButton, c);
		
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		c = new GridBagConstraints();
		c.weightx = 1;
		c.weighty = 0.5;
		c.fill = GridBagConstraints.BOTH;
		panel.add(instructions, c);
		c.gridy = 1;
		panel.add(locationFileChooser, c);
		c.gridy = 2;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(buttons, c);
		
		
		add(panel);
	}
	
}
