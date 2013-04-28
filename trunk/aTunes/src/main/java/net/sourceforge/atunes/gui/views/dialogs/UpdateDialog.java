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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.gui.views.controls.UrlLabel;
import net.sourceforge.atunes.model.ApplicationVersion;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IUpdateDialog;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Dialog to show if a new version is available
 * 
 * @author alex
 * 
 */
public final class UpdateDialog extends AbstractCustomDialog implements
		IUpdateDialog {

	private static final long serialVersionUID = -778226654176158965L;

	/**
	 * @param frame
	 * @param controlsBuilder
	 */
	public UpdateDialog(final IFrame frame,
			final IControlsBuilder controlsBuilder) {
		super(frame, 550, 600, controlsBuilder);
	}

	/**
	 * Initializes dialog
	 * 
	 * @param version
	 */
	@Override
	public void initialize(final ApplicationVersion version) {
		setTitle(I18nUtils.getString("NEW_VERSION_AVAILABLE"));
		setResizable(false);

		JPanel panel = new JPanel(new GridBagLayout());
		String text = I18nUtils.getString("NEW_VERSION_AVAILABLE_TEXT");
		text = text.replace("(%VERSION%)", Constants.VERSION.toShortString());
		text = text.replace("(%NEW_VERSION%)", version.toShortString());

		JLabel img = new JLabel(Images.getImage(Images.APP_LOGO_90));

		JTextArea text1 = getControlsBuilder().createTextArea();
		text1.setText(text);
		text1.setBorder(BorderFactory.createEmptyBorder());
		text1.setEditable(false);
		text1.setOpaque(false);
		text1.setLineWrap(true);
		text1.setWrapStyleWord(true);

		JTextPane changes = getControlsBuilder().createReadOnlyTextPane(
				version.getChanges());

		UrlLabel url = (UrlLabel) getControlsBuilder().getUrlLabel(
				I18nUtils.getString("GO_TO_DOWNLOAD_PAGE"),
				version.getDownloadURL());

		UrlLabel downloadNow = (UrlLabel) getControlsBuilder().getUrlLabel(
				I18nUtils.getString("DOWNLOAD_NOW"),
				version.getDirectDownloadURL());

		downloadNow.setHorizontalAlignment(SwingConstants.CENTER);
		url.setHorizontalAlignment(SwingConstants.CENTER);

		JButton ok = new JButton(I18nUtils.getString("OK"));
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				dispose();
			}
		});

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10, 10, 10, 10);
		panel.add(img, c);
		c.gridy = 1;
		panel.add(text1, c);
		c.gridy = 2;
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0.5;
		panel.add(getControlsBuilder().createScrollPane(changes), c);
		c.gridy = 3;
		c.insets = new Insets(10, 0, 10, 0);
		c.weighty = 0;
		panel.add(url, c);
		c.gridy = 4;
		panel.add(downloadNow, c);
		c.gridy = 5;
		c.fill = GridBagConstraints.NONE;
		panel.add(ok, c);

		add(panel);
	}
}
