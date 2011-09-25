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

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.gui.views.controls.CustomJFileChooser;
import net.sourceforge.atunes.gui.views.controls.SimpleTextPane;
import net.sourceforge.atunes.gui.views.controls.UrlLabel;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;

class MacOSXPlayerSelectionDialog extends AbstractCustomDialog {
	
	private static final String SEARCH_PANEL = "searchPanel";

	private static final String FIRST_PANEL = "firstPanel";
	
	private static final String SEARCH_RESULTS_PANEL = "searchResultsPanel";

	private static final String ENTER_PATH_PANEL = "enterPathPanel";
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -1119645857786634652L;
	
	/**
	 * URL to MPlayerX in Mac App Store
	 */
	static final String MPLAYER_APP_STORE_URL = "http://itunes.apple.com/en/app/mplayerx/id421131143?mt=12";

	private IOSManager osManager;
	
	private JPanel panel;
	
	private JList matchesList;
	
	private ILookAndFeelManager lookAndFeelManager;
	
	/**
	 * @param parent
	 * @param osManager
	 * @param lookAndFeelManager
	 */
	public MacOSXPlayerSelectionDialog(IFrame parent, IOSManager osManager, ILookAndFeelManager lookAndFeelManager) {
		super(parent, 450, 250, true, CloseAction.NOTHING, lookAndFeelManager.getCurrentLookAndFeel());
		this.osManager = osManager;
		this.lookAndFeelManager = lookAndFeelManager;
		setResizable(false);
		setTitle(I18nUtils.getString("PLAYER_ENGINE_SELECTION"));
		
        panel = new JPanel(new CardLayout());
        panel.add(FIRST_PANEL, getFirstOptionsPanel());
        panel.add(SEARCH_PANEL, getSearchPanel());
        panel.add(SEARCH_RESULTS_PANEL, getSearchResultsPanel());
        panel.add(ENTER_PATH_PANEL, getEnterPlayerEnginePanel());
        add(panel);
        
		((CardLayout)panel.getLayout()).show(panel, FIRST_PANEL);

	}
	
	/**
	 * Set first options panel
	 */
	private JPanel getFirstOptionsPanel() {
		SimpleTextPane instructions = new SimpleTextPane(I18nUtils.getString("MAC_PLAYER_ENGINE_INSTRUCTIONS"), lookAndFeelManager);
		UrlLabel appStoreURL = new UrlLabel(I18nUtils.getString("MAC_PLAYER_ENGINE_URL"), MPLAYER_APP_STORE_URL);
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
				if (search.isSelected()) {
					searchPlayerEngine();
				} else {
					((CardLayout)panel.getLayout()).show(panel, ENTER_PATH_PANEL);
				}
			}
		});
		
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.weighty = 0.3;
		c.fill = GridBagConstraints.BOTH;
		panel.add(instructions, c);
		c.gridy = 1;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		panel.add(appStoreURL, c);
		JPanel options = new JPanel(new GridLayout(2, 1));
		options.add(search);
		options.add(enterPath);
		c.gridy = 2;
		c.fill = GridBagConstraints.BOTH;
		panel.add(options, c);
		c.gridy = 3;
		c.weighty = 0;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		panel.add(nextButton, c);
		
		return panel;
	}	
	
	/**
	 * Panel shown while searching
	 * @return
	 */
	private JPanel getSearchPanel() {
		JLabel label = new JLabel(I18nUtils.getString("SEARCHING_PLAYER_ENGINE"));
		JProgressBar bar = new JProgressBar();
		bar.setIndeterminate(true);
		
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.weighty = 0.5;
		c.anchor = GridBagConstraints.SOUTH;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10, 10, 10, 10);
		panel.add(label, c);
		c.gridy = 1;
		c.anchor = GridBagConstraints.NORTH;
		panel.add(bar, c);

		return panel;
	}
	

	/**
	 * Looks for paths where mplayer is installed and show in a dialog
	 */
	private void searchPlayerEngine() {
		((CardLayout)panel.getLayout()).show(panel, SEARCH_PANEL);

		new SwingWorker<List<String>, Void>() {
			
			@Override
			protected List<String> doInBackground() throws Exception {
				List<String> matches = new ArrayList<String>();
				// first try path where MPlayerX is installed to find faster, if not, then search all applications path
				if (executeFind(matches, "find", "/Applications/MPlayerX.app/", "-name", "mplayer") != 0) {
					executeFind(matches, "find", "/Applications/", "-name", "mplayer");
				}
				return matches;
			}
			
			protected void done() {
				try {
					showSearchResults(get());
				} catch (InterruptedException e) {
					Logger.error(e);
				} catch (ExecutionException e) {
					Logger.error(e);
				}
			};
			
		}.execute();
	}
	
	/**
	 * Executes a find command and fills list of results, returning process return code
	 * @param matches
	 * @param command
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	protected int executeFind(List<String> matches, String...command) throws InterruptedException, IOException {
		if (matches == null || command == null) {
			throw new IllegalArgumentException();
		}
		matches.clear();
		ProcessBuilder pb = new ProcessBuilder(command);
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
		int rc = process.waitFor();
		Logger.debug("Process to search player engine returned code: ", rc);
		return rc;
	}
	
	private void showSearchResults(List<String> results) {
		matchesList.setListData(results.toArray());
		((CardLayout)panel.getLayout()).show(panel, SEARCH_RESULTS_PANEL);
	}
	
	private JPanel getSearchResultsPanel() {
		SimpleTextPane instructions = new SimpleTextPane(I18nUtils.getString("MAC_PLAYER_ENGINE_SELECTION"), lookAndFeelManager);
		
		matchesList = lookAndFeel.getList();
		matchesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JButton previousButton = new JButton(I18nUtils.getString("PREVIOUS"));
		final JButton finishButton = new JButton(I18nUtils.getString("FINISH"));
		finishButton.setEnabled(false);

		finishButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				osManager.setOSProperty(MacOSXOperatingSystem.MPLAYER_COMMAND, (String)matchesList.getSelectedValue());
				MacOSXPlayerSelectionDialog.this.dispose();
				osManager.playerEngineFound();
			}
		});
		JButton cancelButton = new JButton(I18nUtils.getString("CANCEL"));
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				MacOSXPlayerSelectionDialog.this.dispose();
			}
		});
		matchesList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				finishButton.setEnabled(true);
			}
		});
		previousButton.addActionListener(new ActionListener() {
			@Override
	
			public void actionPerformed(ActionEvent arg0) {
				((CardLayout)panel.getLayout()).show(panel, FIRST_PANEL);
			}
		});

		JPanel buttons = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(10, 5, 0, 5);
		buttons.add(previousButton, c);
		c.gridx = 1;
		c.weightx = 0;
		buttons.add(finishButton, c);
		c.gridx = 2;
		c.insets = new Insets(10, 5, 0, 0);
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
		panel.add(lookAndFeel.getListScrollPane(matchesList), c);
		c.gridy = 2;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(buttons, c);
		
		return panel;
	}
	
	private JPanel getEnterPlayerEnginePanel() {
		SimpleTextPane instructions = new SimpleTextPane(I18nUtils.getString("MAC_PLAYER_ENGINE_ENTER_PATH"), lookAndFeelManager);

		final CustomJFileChooser locationFileChooser = new CustomJFileChooser(this, 0, JFileChooser.FILES_ONLY, osManager);
		JButton previousButton = new JButton(I18nUtils.getString("PREVIOUS"));
		previousButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				((CardLayout)panel.getLayout()).show(panel, FIRST_PANEL);
			}
		});
		final JButton finishButton = new JButton(I18nUtils.getString("FINISH"));
		finishButton.setEnabled(false);
		finishButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				osManager.setOSProperty(MacOSXOperatingSystem.MPLAYER_COMMAND, locationFileChooser.getResult());
				MacOSXPlayerSelectionDialog.this.dispose();
				osManager.playerEngineFound();
			}
		});
		JButton cancelButton = new JButton(I18nUtils.getString("CANCEL"));
		cancelButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				MacOSXPlayerSelectionDialog.this.dispose();
			}
		});
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

		JPanel buttons = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(10, 5, 0, 5);
		buttons.add(previousButton, c);
		c.gridx = 1;
		c.weightx = 0;
		buttons.add(finishButton, c);
		c.gridx = 2;
		c.insets = new Insets(10, 5, 0, 0);
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
		
		
		return panel;
	}

}
