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
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IListMessageDialog;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * A dialog to show messages to user
 * 
 * @author alex
 * 
 */
public class ListMessageDialog extends AbstractCustomDialog implements
		IListMessageDialog {

	private static final long serialVersionUID = 2917633207992285783L;

	private boolean userAccepted;

	/**
	 * @param frame
	 * @param controlsBuilder
	 */
	public ListMessageDialog(final IFrame frame,
			final IControlsBuilder controlsBuilder) {
		super(frame, 700, 400, controlsBuilder);
	}

	@Override
	public void showMessage(final String message, final List<String> items) {
		JButton ok = new JButton(I18nUtils.getString("OK"));
		ok.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent arg0) {
				ListMessageDialog.this.setVisible(false);
			}
		});
		showDialog(message, items, ok);
	}

	private void showDialog(final String message, final List<String> items,
			JButton... buttons) {
		setResizable(false);
		setTitle(I18nUtils.getString("INFO"));
		setLayout(new GridBagLayout());

		JLabel messageLabel = new JLabel(message);
		JList list = getLookAndFeel().getList();
		list.setListData(items.toArray(new String[items.size()]));

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(10, 10, 10, 10);
		add(messageLabel, c);
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		add(getControlsBuilder().createScrollPane(list), c);
		c.gridy = 2;
		c.weighty = 0;
		c.fill = GridBagConstraints.NONE;
		JPanel buttonsPanel = new JPanel();
		for (JButton button : buttons) {
			buttonsPanel.add(button);
		}
		add(buttonsPanel, c);

		setVisible(true);
	}

	@Override
	public boolean showMessageConfirmation(final String message,
			final List<String> items) {
		JButton yes = new JButton(I18nUtils.getString("YES"));
		yes.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent arg0) {
				userAccepted = true;
				ListMessageDialog.this.setVisible(false);
			}
		});

		JButton no = new JButton(I18nUtils.getString("NO"));
		no.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent arg0) {
				userAccepted = false;
				ListMessageDialog.this.setVisible(false);
			}
		});

		showDialog(message, items, yes, no);

		return userAccepted;
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
	public void initialize() {
		// Do nothing
	}
}
