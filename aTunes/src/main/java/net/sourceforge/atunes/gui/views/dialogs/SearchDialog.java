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

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.kernel.modules.internetsearch.SearchFactory;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.ISearch;
import net.sourceforge.atunes.model.ISearchDialog;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class SearchDialog.
 */
public final class SearchDialog extends AbstractCustomDialog implements
		ISearchDialog {

	private static final long serialVersionUID = 89888215541058798L;

	/** The result. */
	private ISearch result;

	/** The set as default. */
	private boolean setAsDefault;

	/** The set as default check box. */
	private JCheckBox setAsDefaultCheckBox;

	/**
	 * Instantiates a new search dialog.
	 * 
	 * @param frame
	 * @param controlsBuilder
	 */
	public SearchDialog(final IFrame frame,
			final IControlsBuilder controlsBuilder) {
		super(frame, 300, 300, controlsBuilder);
	}

	@Override
	public void initialize() {
		setResizable(false);
		setContent(getLookAndFeel());
	}

	@Override
	public ISearch getResult() {
		return this.result;
	}

	@Override
	public boolean isSetAsDefault() {
		return this.setAsDefault;
	}

	/**
	 * Sets the content.
	 * 
	 * @param lookAndFeel
	 */
	private void setContent(final ILookAndFeel lookAndFeel) {
		JPanel panel = new JPanel(new GridBagLayout());
		JLabel text = new JLabel(StringUtils.getString(
				I18nUtils.getString("SEARCH_AT"), "..."));
		text.setFont(text.getFont().deriveFont(Font.PLAIN));
		final JList list = lookAndFeel.getList();
		list.setListData(SearchFactory.getSearches().toArray());
		list.setSelectedIndex(0);
		list.setOpaque(false);

		this.setAsDefaultCheckBox = new JCheckBox(
				I18nUtils.getString("SET_AS_DEFAULT"));
		this.setAsDefaultCheckBox.setOpaque(false);
		this.setAsDefaultCheckBox.setFont(this.setAsDefaultCheckBox.getFont()
				.deriveFont(Font.PLAIN));
		this.setAsDefaultCheckBox.setHorizontalAlignment(SwingConstants.CENTER);

		JScrollPane scrollPane = getControlsBuilder().createScrollPane(list);
		JButton okButton = new JButton(I18nUtils.getString("OK"));
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				SearchDialog.this.result = (ISearch) list.getSelectedValue();
				SearchDialog.this.setAsDefault = SearchDialog.this.setAsDefaultCheckBox
						.isSelected();
				hideDialog();
			}
		});
		JButton cancelButton = new JButton(I18nUtils.getString("CANCEL"));
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				SearchDialog.this.result = null;
				hideDialog();
			}
		});

		JPanel auxPanel = new JPanel();
		auxPanel.setOpaque(false);
		auxPanel.add(okButton);
		auxPanel.add(cancelButton);

		arrangePanel(panel, text, scrollPane, auxPanel);

		add(panel);
	}

	/**
	 * @param panel
	 * @param text
	 * @param scrollPane
	 * @param auxPanel
	 */
	private void arrangePanel(final JPanel panel, final JLabel text,
			final JScrollPane scrollPane, final JPanel auxPanel) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(15, 15, 0, 0);
		panel.add(text, c);
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(10, 15, 0, 15);
		panel.add(scrollPane, c);
		c.gridy = 2;
		c.weightx = 0;
		c.insets = new Insets(0, 0, 0, 0);
		panel.add(this.setAsDefaultCheckBox, c);
		c.gridy = 3;
		c.weightx = 1;
		panel.add(auxPanel, c);
	}

	@Override
	public void setSetAsDefaultVisible(final boolean setAsDefaultVisible) {
		this.setAsDefaultCheckBox.setVisible(setAsDefaultVisible);
	}

	@Override
	public void setVisible(final boolean b) {
		this.setAsDefaultCheckBox.setSelected(false);
		super.setVisible(b);
	}

	@Override
	public void showDialog() {
		setVisible(true);
	}

	@Override
	public void hideDialog() {
		setVisible(false);
		dispose();
	}
}
