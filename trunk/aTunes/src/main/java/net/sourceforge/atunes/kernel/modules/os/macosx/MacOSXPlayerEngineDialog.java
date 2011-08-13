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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingWorker;

import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.gui.views.controls.SimpleTextPane;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Dialog with options to select player engine in Mac OS
 * @author alex
 *
 */
class MacOSXPlayerEngineDialog extends AbstractCustomDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5594530223379760626L;

	public MacOSXPlayerEngineDialog(JFrame parent) {
		super(parent, 450, 250, true, true);
		setResizable(false);
		setTitle(I18nUtils.getString("PLAYER_ENGINE_SELECTION"));
        addContent();
	}

	private void addContent() {
		SimpleTextPane instructions = new SimpleTextPane(I18nUtils.getString("MAC_PLAYER_ENGINE_INSTRUCTIONS"));
		final JRadioButton search = new JRadioButton(I18nUtils.getString("SEARCH_PLAYER_ENGINE"));
		JRadioButton enterPath = new JRadioButton(I18nUtils.getString("ENTER_PLAYER_ENGINE_PATH"));
		ButtonGroup b = new ButtonGroup();
		b.add(search);
		b.add(enterPath);
		search.setSelected(true);
		JButton nextButton = new JButton(I18nUtils.getString("NEXT"));
		nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				MacOSXPlayerEngineDialog.this.setVisible(false);
				if (search.isSelected()) {
					searchPlayerEngine();
				} else {
					enterPlayerEngine();
				}
			}
		});
		JButton cancelButton = new JButton(I18nUtils.getString("CANCEL"));
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				MacOSXPlayerEngineDialog.this.setVisible(false);
			}
		});
		
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.weighty = 0.5;
		c.fill = GridBagConstraints.BOTH;
		panel.add(instructions, c);
		JPanel options = new JPanel(new GridLayout(2, 1));
		options.add(search);
		options.add(enterPath);
		c.gridy = 1;
		panel.add(options, c);
		JPanel buttons = new JPanel(new GridBagLayout());
		GridBagConstraints c2 = new GridBagConstraints();
		c2.weightx = 1;
		c2.anchor = GridBagConstraints.EAST;
		buttons.add(nextButton, c2);
		c2.gridx = 1;
		c2.weightx = 0;
		buttons.add(cancelButton, c2);
		c.gridy = 2;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(buttons, c);
		
		add(panel);
	}	
	
	/**
	 * Looks for paths where mplayer is installed and show in a dialog
	 */
	private void searchPlayerEngine() {
		GuiHandler.getInstance().showIndeterminateProgressDialog(I18nUtils.getString("SEARCHING_PLAYER_ENGINE"));
		new SwingWorker<List<String>, Void>() {
			
			@Override
			protected List<String> doInBackground() throws Exception {
				List<String> matches = new ArrayList<String>();
				ProcessBuilder pb = new ProcessBuilder("find", "/Applications/", "-name", "mplayer");
				Process process = pb.start();
				BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

				String match = null;
				try {
					while ((match = br.readLine()) != null) {
						Logger.debug(match);
						matches.add(match);
					}
				} finally {
					br.close();
				}
				Logger.debug("Process to search player engine returned code: ", process.waitFor());
				process = null;
				return matches;
			}
			
			protected void done() {
				GuiHandler.getInstance().hideIndeterminateProgressDialog();
				try {
					new MacOSXPlayerEngineMatchSelectionDialog(MacOSXPlayerEngineDialog.this, get()).setVisible(true);
				} catch (InterruptedException e) {
					Logger.error(e);
				} catch (ExecutionException e) {
					Logger.error(e);
				}
			};
			
		}.execute();
	}
	
	/**
	 * Shows dialog when user can enter player engine location manually
	 */
	private void enterPlayerEngine() {
		new MacOSXPlayerEngineEnterPathDialog(MacOSXPlayerEngineDialog.this).setVisible(true);
	}

}
