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

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import net.sourceforge.atunes.gui.ComponentOrientationTableCellRendererCode;
import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.gui.views.controls.CloseAction;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IColumnSelectorDialog;
import net.sourceforge.atunes.model.IColumnSet;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Dialog to select column set
 */
public final class ColumnSetSelectorDialog extends AbstractCustomDialog
		implements IColumnSelectorDialog {

	private static final long serialVersionUID = -7592059207162524630L;

	private JTable columnsList;
	private ColumnsTableModel model;

	private IBeanFactory beanFactory;

	private JButton okButton;

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Instantiates a new play list column selector.
	 * 
	 * @param frame
	 * @param controlsBuilder
	 */
	public ColumnSetSelectorDialog(final IFrame frame,
			final IControlsBuilder controlsBuilder) {
		// Don't allow close dialog so user has to select columns
		super(frame, 250, 300, true, CloseAction.NOTHING, controlsBuilder);
	}

	@Override
	public void initialize() {
		add(getContent(getLookAndFeel()));
		setTitle(I18nUtils.getString("ARRANGE_COLUMNS"));
		setResizable(false);
		// TODO: Add pack to all dialogs
		pack();
	}

	/**
	 * Gets the content.
	 * 
	 * @return the content
	 */
	private JPanel getContent(final ILookAndFeel lookAndFeel) {
		JPanel panel = new JPanel(new GridBagLayout());

		this.columnsList = lookAndFeel.getTable();
		this.model = new ColumnsTableModel(this.columnsList);
		this.columnsList.setModel(this.model);
		this.columnsList.setTableHeader(null);
		this.columnsList.getColumnModel().getColumn(0).setMaxWidth(20);
		this.columnsList.getColumnModel().getColumn(0)
				.setCellEditor(new DefaultCellEditor(new JCheckBox()));
		this.columnsList.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);
		this.columnsList
				.setDefaultRenderer(
						String.class,
						lookAndFeel.getTableCellRenderer(this.beanFactory
								.getBean(ComponentOrientationTableCellRendererCode.class)));
		this.columnsList.getModel().addTableModelListener(
				new TableModelListener() {

					@Override
					public void tableChanged(final TableModelEvent event) {
						ColumnSetSelectorDialog.this.okButton
								.setEnabled(ColumnSetSelectorDialog.this.model
										.someColumnIsSelected());
					}
				});

		JScrollPane scrollPane = getControlsBuilder().createScrollPane(
				this.columnsList);
		JLabel label = new JLabel(I18nUtils.getString("SELECT_COLUMNS"));
		this.okButton = new JButton(I18nUtils.getString("OK"));
		this.okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				hideDialog();
			}
		});

		JButton upButton = new JButton(I18nUtils.getString("MOVE_UP"));
		upButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				int selectedColumn = ColumnSetSelectorDialog.this.columnsList
						.getSelectedRow();
				if (selectedColumn != -1) {
					ColumnSetSelectorDialog.this.model.moveUp(selectedColumn);
				}
			}
		});

		JButton downButton = new JButton(I18nUtils.getString("MOVE_DOWN"));
		downButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				int selectedColumn = ColumnSetSelectorDialog.this.columnsList
						.getSelectedRow();
				if (selectedColumn != -1) {
					ColumnSetSelectorDialog.this.model.moveDown(selectedColumn);
				}
			}
		});

		arrangePanel(panel, scrollPane, label, this.okButton, upButton,
				downButton);

		return panel;
	}

	/**
	 * @param panel
	 * @param scrollPane
	 * @param label
	 * @param okButton
	 * @param upButton
	 * @param downButton
	 */
	private void arrangePanel(final JPanel panel, final JScrollPane scrollPane,
			final JLabel label, final JButton okButton, final JButton upButton,
			final JButton downButton) {
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(10, 10, 10, 10);
		panel.add(label, c);

		c.gridy = 1;
		c.weighty = 1;
		c.gridheight = 2;
		panel.add(scrollPane, c);

		c.gridx = 1;
		c.gridheight = 1;
		c.weightx = 0;
		c.weighty = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.SOUTH;
		c.insets = new Insets(10, 0, 10, 10);
		panel.add(upButton, c);

		c.gridy = 2;
		c.anchor = GridBagConstraints.NORTH;
		panel.add(downButton, c);

		c.gridx = 0;
		c.gridy = 3;
		c.weighty = 0;
		c.gridwidth = 2;
		c.insets = new Insets(10, 10, 10, 10);
		panel.add(okButton, c);
	}

	@Override
	public void setColumnSetToSelect(final IColumnSet columnSet) {
		this.model.setColumns(columnSet.getColumnsForSelection());
	}

	@Override
	public void showDialog() {
		setVisible(true);
	}

	@Override
	public void hideDialog() {
		setVisible(false);
	}

}
