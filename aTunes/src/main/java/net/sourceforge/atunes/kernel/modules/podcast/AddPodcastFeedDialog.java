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

package net.sourceforge.atunes.kernel.modules.podcast;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.model.IAddPodcastFeedDialog;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IIconFactory;
import net.sourceforge.atunes.model.IPodcastFeed;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * The Class AddPodcastFeedDialog.
 */
public final class AddPodcastFeedDialog extends AbstractCustomDialog implements
		IAddPodcastFeedDialog {

	private static final long serialVersionUID = 7295438534550341824L;

	/** The podcast feed. */
	private IPodcastFeed podcastFeed;

	/** The name text field. */
	private JTextField nameTextField;

	/** The url text field. */
	private JTextField urlTextField;

	private IIconFactory rssMediumIcon;

	private IControlsBuilder controlsBuilder;

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

		JLabel nameLabel = new JLabel(I18nUtils.getString("NAME"));
		this.nameTextField = this.controlsBuilder.createTextField();
		JLabel urlLabel = new JLabel(I18nUtils.getString("URL"));
		this.urlTextField = this.controlsBuilder.createTextField();

		JButton okButton = getOkButton();
		JButton cancelButton = getCancelButton();

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.insets = new Insets(5, 10, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(nameLabel, c);
		c.gridx = 2;
		c.weightx = 1;
		panel.add(this.nameTextField, c);
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 0;
		panel.add(urlLabel, c);
		c.gridx = 2;
		c.weightx = 1;
		panel.add(this.urlTextField, c);
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 2;
		c.fill = GridBagConstraints.NONE;
		c.weightx = -1;
		panel.add(
				new JLabel(this.rssMediumIcon.getIcon(getLookAndFeel()
						.getPaintForSpecialControls())), c);

		JPanel auxPanel = new JPanel();
		auxPanel.add(okButton);
		auxPanel.add(cancelButton);

		c.gridx = 0;
		c.gridy = 2;
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
				if (AddPodcastFeedDialog.this.nameTextField.getText() == null
						|| AddPodcastFeedDialog.this.nameTextField.getText()
								.trim().isEmpty()) {
					AddPodcastFeedDialog.this.podcastFeed = new PodcastFeed("",
							AddPodcastFeedDialog.this.urlTextField.getText());
					AddPodcastFeedDialog.this.podcastFeed
							.setRetrieveNameFromFeed(true);
				} else {
					AddPodcastFeedDialog.this.podcastFeed = new PodcastFeed(
							AddPodcastFeedDialog.this.nameTextField.getText(),
							AddPodcastFeedDialog.this.urlTextField.getText());
				}
				AddPodcastFeedDialog.this.dispose();
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
