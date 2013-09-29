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

package net.sourceforge.atunes.kernel.modules.podcast;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.gui.views.controls.CustomFileChooser;
import net.sourceforge.atunes.model.IAddPodcastFeedDialog;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPodcastFeed;
import net.sourceforge.atunes.model.IStatePodcast;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class AddPodcastFeedDialog.
 */
public final class AddPodcastFeedDialog extends AbstractCustomDialog implements
		IAddPodcastFeedDialog {

	private static final long serialVersionUID = 7295438534550341824L;

	/** The podcast feed. */
	private IPodcastFeed podcastFeed;

	/** Text input field for name of the podcast feed. */
	private JTextField nameTextField;

	/** Text input field for podcast feed URL. */
	private JTextField urlTextField;

	private IIconFactory rssMediumIcon;

	/** File chooser for changing of the podcast feed directory. */
	private CustomFileChooser folderChooser;

	private IBeanFactory beanFactory;

	private IOSManager osManager;

	private IStatePodcast statePodcast;

	private IControlsBuilder controlsBuilder;

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param statePodcast
	 */
	public void setStatePodcast(final IStatePodcast statePodcast) {
		this.statePodcast = statePodcast;
	}

	/**
	 * @param controlsBuilder
	 */
	public void setControlsBuilder(final IControlsBuilder controlsBuilder) {
		this.controlsBuilder = controlsBuilder;
	}

	/**
	 * Instantiates a new adds the podcast feed dialog.
	 * 
	 * @param frame
	 * @param controlsBuilder
	 */
	public AddPodcastFeedDialog(final IFrame frame,
			final IControlsBuilder controlsBuilder) {
		super(frame, 500, 170, controlsBuilder);
	}

	/**
	 * Initializes dialog
	 */
	@Override
	public void initialize() {
		setTitle(I18nUtils.getString("ADD_PODCAST_FEED"));
		setResizable(false);
		add(getContent());
	}

	@Override
	public void hideDialog() {
		setVisible(false);
	}

	/**
	 * @param rssMediumIcon
	 */
	public void setRssMediumIcon(final IIconFactory rssMediumIcon) {
		this.rssMediumIcon = rssMediumIcon;
	}

	/**
	 * Gets the content.
	 * 
	 * @return the content
	 */
	private JPanel getContent() {
		JPanel panel = new JPanel(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		JLabel nameLabel = new JLabel(I18nUtils.getString("NAME"));
		nameTextField = controlsBuilder.createTextField();
		c.gridx = 1;
		c.gridy = 0;
		c.insets = new Insets(5, 10, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(nameLabel, c);
		c.gridx = 2;
		c.weightx = 1;
		panel.add(nameTextField, c);
		nameTextField.getDocument().addDocumentListener(new DocumentListener() {
			void updateFolderName(DocumentEvent e) {
				String podcastDownloadPath = statePodcast
						.getPodcastFeedEntryDownloadPath();

				if (!StringUtils.isEmpty(podcastDownloadPath)) {
					String folder = folderChooser.getResult();
					if (!StringUtils.isEmpty(folder)
							&& folder.startsWith(podcastDownloadPath)) {
						try {
							String content = e.getDocument().getText(0,
									e.getDocument().getLength());
							folderChooser.setText(podcastDownloadPath
									+ File.separator + content);
						} catch (BadLocationException e1) {
							e1.printStackTrace(); // FIXME
						}
					}
				}
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				updateFolderName(e);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				System.out.println("*** remove ***");
				updateFolderName(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				System.out.println("*** insert ***");
				updateFolderName(e);
			}
		});

		JLabel urlLabel = new JLabel(I18nUtils.getString("URL"));
		urlTextField = controlsBuilder.createTextField();
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 0;
		panel.add(urlLabel, c);
		c.gridx = 2;
		c.weightx = 1;
		panel.add(urlTextField, c);

		JLabel directoryLabel = new JLabel(I18nUtils.getString("FOLDER"));
		folderChooser = new CustomFileChooser(I18nUtils.getString("FOLDER"),
				this, 20, JFileChooser.DIRECTORIES_ONLY, osManager,
				beanFactory, controlsBuilder,
				statePodcast.getPodcastFeedEntryDownloadPath());
		c.gridx = 1;
		c.gridy = 2;
		c.weightx = 0;
		panel.add(directoryLabel, c);
		c.gridx = 2;
		c.weightx = 1;
		panel.add(folderChooser, c);

		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 3;
		c.fill = GridBagConstraints.NONE;
		c.weightx = -1;
		panel.add(
				new JLabel(rssMediumIcon.getIcon(getLookAndFeel()
						.getPaintForSpecialControls())), c);

		JPanel auxPanel = new JPanel();
		JButton okButton = getOkButton();
		JButton cancelButton = getCancelButton();
		auxPanel.add(okButton);
		auxPanel.add(cancelButton);
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 3;
		c.insets = new Insets(0, 0, 0, 0);
		panel.add(auxPanel, c);

		return panel;
	}

	/**
	 * @return
	 */
	private JButton getCancelButton() {
		JButton cancelButton = new JButton(I18nUtils.getString("CANCEL"));
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				AddPodcastFeedDialog.this.dispose();
			}
		});
		return cancelButton;
	}

	/**
	 * @return
	 */
	private JButton getOkButton() {
		JButton okButton = new JButton(I18nUtils.getString("OK"));
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				String downloadPodcastPath = statePodcast
						.getPodcastFeedEntryDownloadPath();
				String folder = folderChooser.getResult();
				if (!StringUtils.isEmpty(downloadPodcastPath)) {
					// remove top-level podcast folder from the folder name
					folder = folder.replaceFirst(
							Pattern.quote(downloadPodcastPath), "");
					if (folder.startsWith(File.separator)) {
						folder = folder.substring(1);
					}
				}
				podcastFeed = new PodcastFeed(nameTextField.getText(),
						urlTextField.getText(), folder);
				dispose();
			}
		});
		return okButton;
	}

	@Override
	public IPodcastFeed getPodcastFeed() {
		return this.podcastFeed;
	}

	@Override
	public void showDialog() {
		setVisible(true);
	}
}
