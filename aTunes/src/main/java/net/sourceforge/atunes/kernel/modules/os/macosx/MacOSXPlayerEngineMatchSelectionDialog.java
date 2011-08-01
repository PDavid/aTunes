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
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;
import net.sourceforge.atunes.gui.views.controls.SimpleTextPane;
import net.sourceforge.atunes.kernel.OsManager;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Dialog to select player engine in Mac OS
 * @author alex
 *
 */
class MacOSXPlayerEngineMatchSelectionDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5594530223379760626L;

	private JDialog previousDialog;
	
	private JList matchesList;
	
	private JButton finishButton;
	
	private List<String> matches;
	
	public MacOSXPlayerEngineMatchSelectionDialog(JDialog previousDialog, List<String> matches) {
		super((JFrame)previousDialog.getParent(), true);
		this.previousDialog = previousDialog;
		this.matches = matches;
		setSize(GuiUtils.getComponentWidthForResolution(0.4f),
				GuiUtils.getComponentHeightForResolution(0.3f));
		setResizable(false);
		setLocationRelativeTo(previousDialog.getParent());
		setTitle(I18nUtils.getString("PLAYER_ENGINE_SELECTION"));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addContent();
	}

	private void addContent() {
		SimpleTextPane instructions = new SimpleTextPane(I18nUtils.getString("MAC_PLAYER_ENGINE_SELECTION"));
		
		matchesList = LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getList();
		matchesList.setListData(matches.toArray());
		matchesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		matchesList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				finishButton.setEnabled(true);
			}
		});
		
		JButton previousButton = new JButton(I18nUtils.getString("PREVIOUS"));
		previousButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				MacOSXPlayerEngineMatchSelectionDialog.this.setVisible(false);
				previousDialog.setVisible(true);
			}
		});
		finishButton = new JButton(I18nUtils.getString("FINISH"));
		finishButton.setEnabled(false);
		finishButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				OsManager.setOSProperty(MacOSXOperatingSystem.MPLAYER_COMMAND, (String)matchesList.getSelectedValue());
				previousDialog.dispose();
				MacOSXPlayerEngineMatchSelectionDialog.this.setVisible(false);
				OsManager.playerEngineFound();
			}
		});
		JButton cancelButton = new JButton(I18nUtils.getString("CANCEL"));
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				MacOSXPlayerEngineMatchSelectionDialog.this.setVisible(false);
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
		c.weighty = 0.2;
		c.fill = GridBagConstraints.BOTH;
		panel.add(instructions, c);
		c.gridy = 1;
		c.weighty = 0.8;
		panel.add(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getListScrollPane(matchesList), c);
		c.gridy = 2;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(buttons, c);
		
		
		add(panel);
	}
	
}
