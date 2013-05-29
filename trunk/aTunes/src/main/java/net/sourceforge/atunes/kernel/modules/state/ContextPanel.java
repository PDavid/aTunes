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

package net.sourceforge.atunes.kernel.modules.state;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import net.sourceforge.atunes.gui.ComponentOrientationTableCellRendererCode;
import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.kernel.actions.ClearCachesAction;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IDesktop;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.ILyricsEngineInfo;
import net.sourceforge.atunes.model.IStateContext;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Preferences of context
 * 
 * @author alex
 * 
 */
public final class ContextPanel extends AbstractPreferencesPanel {

	private static final long serialVersionUID = -9216216930198145476L;

	/** The activate context information. */
	private JCheckBox activateContext;

	/** The save pictures. */
	private JCheckBox savePictures;

	/**
	 * Checkbox to select if want to show albums in a grid
	 */
	private JCheckBox showAlbumsInGrid;

	/** The lyrics engines table. */
	private JTable enginesTable;

	private ILookAndFeelManager lookAndFeelManager;

	private IDesktop desktop;

	private IStateContext stateContext;

	private IBeanFactory beanFactory;

	private IControlsBuilder controlsBuilder;

	private JTextField similarArtistsQueryTextField;

	/**
	 * @param controlsBuilder
	 */
	public void setControlsBuilder(final IControlsBuilder controlsBuilder) {
		this.controlsBuilder = controlsBuilder;
	}

	/**
	 * @param beanFactory
	 */
	public void setBeanFactory(final IBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param stateContext
	 */
	public void setStateContext(final IStateContext stateContext) {
		this.stateContext = stateContext;
	}

	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(
			final ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

	/**
	 * @param desktop
	 */
	public void setDesktop(final IDesktop desktop) {
		this.desktop = desktop;
	}

	/**
	 * Instantiates a new context panel.
	 */
	public ContextPanel() {
		super(I18nUtils.getString("CONTEXT_INFORMATION"));
	}

	/**
	 * Initializes panel
	 */
	public void initialize() {
		this.activateContext = new JCheckBox(
				I18nUtils.getString("ACTIVATE_CONTEXT_INFORMATION"));
		this.savePictures = new JCheckBox(
				I18nUtils.getString("SAVE_PICTURES_TO_AUDIO_FOLDERS"));
		this.showAlbumsInGrid = new JCheckBox(
				I18nUtils.getString("SHOW_ALBUMS_IN_GRID"));

		this.activateContext.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				ContextPanel.this.savePictures
						.setEnabled(ContextPanel.this.activateContext
								.isSelected());
			}
		});
		JButton clearCache = new JButton(
				this.beanFactory.getBean(ClearCachesAction.class));
		JLabel info = new JLabel(Images.getImage(Images.POWERED_BY_LAST_FM),
				this.controlsBuilder.getComponentOrientationAsSwingConstant());
		info.addMouseListener(new OpenLastFmMouseAdapter(this.desktop));
		JLabel enginesTableLabel = new JLabel(
				I18nUtils.getString("LYRICS_ENGINES_SELECTION"));
		this.enginesTable = this.lookAndFeelManager.getCurrentLookAndFeel()
				.getTable();
		final LyricsEnginesTableModel model = new LyricsEnginesTableModel(
				this.enginesTable);
		this.enginesTable.setModel(model);
		this.enginesTable.setTableHeader(null);
		this.enginesTable.getColumnModel().getColumn(0).setMaxWidth(20);
		this.enginesTable.getColumnModel().getColumn(0)
				.setCellEditor(new DefaultCellEditor(new JCheckBox()));
		this.enginesTable.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);
		this.enginesTable
				.setDefaultRenderer(
						String.class,
						this.lookAndFeelManager
								.getCurrentLookAndFeel()
								.getTableCellRenderer(
										this.beanFactory
												.getBean(ComponentOrientationTableCellRendererCode.class)));
		JButton upButton = new JButton(I18nUtils.getString("MOVE_UP"));
		upButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				int selectedRow = ContextPanel.this.enginesTable
						.getSelectedRow();
				if (selectedRow > 0) {
					model.moveUp(selectedRow);
				}
			}
		});
		JButton downButton = new JButton(I18nUtils.getString("MOVE_DOWN"));
		downButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				int selectedRow = ContextPanel.this.enginesTable
						.getSelectedRow();
				if (selectedRow > 0 && selectedRow < ContextPanel.this.enginesTable.getModel()
						.getRowCount() - 1) {
					model.moveDown(selectedRow);
				}
			}
		});
		JScrollPane enginesScrollPane = this.controlsBuilder
				.createScrollPane(this.enginesTable);
		enginesScrollPane.setMinimumSize(new Dimension(200, 100));

		this.similarArtistsQueryTextField = this.controlsBuilder
				.createTextField();
		this.similarArtistsQueryTextField.setColumns(30);

		arrangePanel(clearCache, info, enginesTableLabel, upButton, downButton,
				enginesScrollPane, this.similarArtistsQueryTextField);
	}

	/**
	 * @param clearCache
	 * @param info
	 * @param enginesTableLabel
	 * @param upButton
	 * @param downButton
	 * @param enginesScrollPane
	 * @param similarArtistsQueryTextField2
	 */
	private void arrangePanel(final JButton clearCache, final JLabel info,
			final JLabel enginesTableLabel, final JButton upButton,
			final JButton downButton, final JScrollPane enginesScrollPane,
			final JTextField similarArtistsQueryTextField2) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.gridwidth = 2;
		add(this.activateContext, c);
		c.gridx = 0;
		c.gridy = 1;
		add(this.savePictures, c);
		c.gridy = 2;
		add(this.showAlbumsInGrid, c);
		c.gridy = 3;
		c.gridx = 0;
		c.weightx = 0;
		c.insets = new Insets(10, 0, 10, 0);
		c.gridwidth = 1;
		add(new JLabel(I18nUtils.getString("SIMILAR_ARTIST_SEARCH_QUERY")), c);
		c.gridx = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(similarArtistsQueryTextField2, c);
		c.gridx = 0;
		c.fill = GridBagConstraints.NONE;
		c.gridy = 4;
		add(clearCache, c);
		c.gridy = 5;
		c.insets = new Insets(0, 0, 5, 0);
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.gridwidth = 2;
		add(enginesTableLabel, c);
		c.gridy = 6;
		c.insets = new Insets(0, 10, 0, 0);
		add(enginesScrollPane, c);
		c.gridy = 7;
		c.gridheight = 1;
		c.insets = new Insets(0, 0, 0, 0);
		JPanel p = new JPanel(new FlowLayout());
		p.add(upButton);
		p.add(downButton);
		add(p, c);
		c.gridy = 8;
		c.weighty = 1;
		c.insets = new Insets(20, 0, 0, 0);
		add(info, c);
	}

	@Override
	public boolean applyPreferences() {
		this.stateContext.setUseContext(this.activateContext.isSelected());
		this.stateContext.setSaveContextPicture(this.savePictures.isSelected());
		this.stateContext
				.setLyricsEnginesInfo(((LyricsEnginesTableModel) this.enginesTable
						.getModel()).getLyricsEnginesInfo());
		boolean showAlbumsInGridPreviousValue = this.stateContext
				.isShowContextAlbumsInGrid();
		this.stateContext.setShowContextAlbumsInGrid(this.showAlbumsInGrid
				.isSelected());
		this.stateContext
				.setSimilarArtistSearchQuery(this.similarArtistsQueryTextField
						.getText());
		return showAlbumsInGridPreviousValue != this.showAlbumsInGrid
				.isSelected();
	}

	/**
	 * Sets the activate context information.
	 * 
	 * @param activate
	 *            the new activate context information
	 */
	private void setActivateContext(final boolean activate) {
		this.activateContext.setSelected(activate);
		this.savePictures.setEnabled(activate);
	}

	/**
	 * Sets the save pictures.
	 * 
	 * @param save
	 *            the new save pictures
	 */
	private void setSavePictures(final boolean save) {
		this.savePictures.setSelected(save);
	}

	/**
	 * Sets the lyrics engines info
	 * 
	 * @param list
	 *            list with lyrics engines info
	 */
	private void setLyricsEnginesInfo(final List<ILyricsEngineInfo> list) {
		List<ILyricsEngineInfo> copy = new ArrayList<ILyricsEngineInfo>();
		for (ILyricsEngineInfo lyricsEngineInfo : list) {
			copy.add(lyricsEngineInfo.copy());
		}
		((LyricsEnginesTableModel) this.enginesTable.getModel())
				.setLyricsEnginesInfo(copy);
	}

	/**
	 * Sets the show albums in grid check box
	 * 
	 * @param show
	 */
	private void setShowAlbumsInGrid(final boolean show) {
		this.showAlbumsInGrid.setSelected(show);
	}

	@Override
	public void updatePanel() {
		setActivateContext(this.stateContext.isUseContext());
		setSavePictures(this.stateContext.isSaveContextPicture());
		setLyricsEnginesInfo(this.stateContext.getLyricsEnginesInfo());
		setShowAlbumsInGrid(this.stateContext.isShowContextAlbumsInGrid());
		this.similarArtistsQueryTextField.setText(this.stateContext
				.getSimilarArtistSearchQuery());
	}

	@Override
	public void resetImmediateChanges() {
		// Do nothing
	}

	@Override
	public void validatePanel() throws PreferencesValidationException {
	}

	@Override
	public void dialogVisibilityChanged(final boolean visible) {
		// Do nothing
	}
}
