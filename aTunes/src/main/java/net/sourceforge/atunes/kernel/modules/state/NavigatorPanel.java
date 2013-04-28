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
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import net.sourceforge.atunes.gui.ComponentOrientationTableCellRendererCode;
import net.sourceforge.atunes.model.ArtistViewMode;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IStateNavigation;
import net.sourceforge.atunes.model.IStateRepository;
import net.sourceforge.atunes.model.TextTagAttribute;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Preferences of navigator
 * 
 * @author alex
 * 
 */
public final class NavigatorPanel extends AbstractPreferencesPanel {

	private static final long serialVersionUID = -4315748284461119970L;

	private JCheckBox showFavorites;
	private JCheckBox showExtendedToolTip;
	private JComboBox extendedToolTipDelay;
	private JCheckBox useSmartTagViewSorting;
	private JCheckBox useArtistNamesSorting;
	private JCheckBox useCaseSensitiveInTree;
	private JRadioButton useOnlyArtist;
	private JRadioButton useOnlyArtistOfAlbum;
	private JRadioButton useBothArtist;

	/**
	 * Check box to highlight elements with incomplete tags (selected) or not
	 * (unselected)
	 */
	private JCheckBox highlightElementsWithIncompleteBasicTags;

	/**
	 * Table to select which tag attributes are used to highlight incomplete tag
	 * folders
	 */
	private JTable highlighTagAttributesTable;

	/**
	 * Scroll pane fot tag attributes
	 */
	private JScrollPane highlightTagAttributesScrollPane;

	/**
	 * Table model to select tag attributes
	 */
	private TagAttributesTableModel tagAttributesTableModel;

	private ILookAndFeelManager lookAndFeelManager;

	private IStateRepository stateRepository;

	private IStateNavigation stateNavigation;

	private IBeanFactory beanFactory;

	private IControlsBuilder controlsBuilder;

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
	 * @param stateNavigation
	 */
	public void setStateNavigation(final IStateNavigation stateNavigation) {
		this.stateNavigation = stateNavigation;
	}

	/**
	 * @param stateRepository
	 */
	public void setStateRepository(final IStateRepository stateRepository) {
		this.stateRepository = stateRepository;
	}

	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(
			final ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

	/**
	 * Instantiates a new navigator panel.
	 */
	public NavigatorPanel() {
		super(I18nUtils.getString("NAVIGATOR"));
	}

	/**
	 * Initializes panel
	 */
	public void initialize() {
		this.showFavorites = new JCheckBox(
				I18nUtils.getString("SHOW_FAVORITES"));
		this.showExtendedToolTip = new JCheckBox(
				I18nUtils.getString("SHOW_EXTENDED_TOOLTIP"));
		final JLabel label = new JLabel(
				I18nUtils.getString("EXTENDED_TOOLTIP_DELAY"));
		this.extendedToolTipDelay = new JComboBox(
				new Integer[] { 1, 2, 3, 4, 5 });
		this.showExtendedToolTip.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				label.setEnabled(NavigatorPanel.this.showExtendedToolTip
						.isSelected());
				NavigatorPanel.this.extendedToolTipDelay
						.setEnabled(NavigatorPanel.this.showExtendedToolTip
								.isSelected());
			}
		});
		this.useSmartTagViewSorting = new JCheckBox(
				I18nUtils.getString("USE_SMART_TAG_VIEW_SORTING"));
		this.useArtistNamesSorting = new JCheckBox(
				I18nUtils.getString("USE_PERSON_NAMES_ARTIST_TAG_VIEW_SORTING"));

		this.useOnlyArtist = new JRadioButton(
				I18nUtils.getString("ARTIST_VIEW_USE_ONLY_ARTIST"), true);
		this.useOnlyArtistOfAlbum = new JRadioButton(
				I18nUtils.getString("ARTIST_VIEW_USE_ONLY_ARTIST_OF_ALBUM"));
		this.useBothArtist = new JRadioButton(
				I18nUtils.getString("ARTIST_VIEW_USE_BOTH"));

		ButtonGroup artistViewOptionGroup = new ButtonGroup();
		artistViewOptionGroup.add(this.useOnlyArtist);
		artistViewOptionGroup.add(this.useOnlyArtistOfAlbum);
		artistViewOptionGroup.add(this.useBothArtist);
		JPanel artistViewPanel = getArtistViewPanel();

		this.useCaseSensitiveInTree = new JCheckBox(
				I18nUtils.getString("CASE_SENSITIVE_TREE"));
		this.highlightElementsWithIncompleteBasicTags = new JCheckBox(
				I18nUtils.getString("HIGHLIGHT_INCOMPLETE_TAG_ELEMENTS"));
		this.highlightElementsWithIncompleteBasicTags
				.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								NavigatorPanel.this.highlightTagAttributesScrollPane
										.setEnabled(NavigatorPanel.this.highlightElementsWithIncompleteBasicTags
												.isSelected());
								NavigatorPanel.this.highlighTagAttributesTable
										.setEnabled(NavigatorPanel.this.highlightElementsWithIncompleteBasicTags
												.isSelected());
							}
						});
					}
				});

		this.tagAttributesTableModel = new TagAttributesTableModel();
		this.highlighTagAttributesTable = getHighlighTagAttributesTable();

		this.highlightTagAttributesScrollPane = this.controlsBuilder
				.createScrollPane(this.highlighTagAttributesTable);
		this.highlightTagAttributesScrollPane.setMinimumSize(new Dimension(300,
				150));

		arrangePanel(label, artistViewPanel);
	}

	/**
	 * @return
	 */
	private JPanel getArtistViewPanel() {
		JPanel artistViewPanel = new JPanel(new GridLayout(3, 1));
		artistViewPanel.setBorder(new TitledBorder(I18nUtils
				.getString("ARTIST")));
		artistViewPanel.add(this.useBothArtist);
		artistViewPanel.add(this.useOnlyArtist);
		artistViewPanel.add(this.useOnlyArtistOfAlbum);
		return artistViewPanel;
	}

	/**
	 * 
	 */
	private JTable getHighlighTagAttributesTable() {
		JTable table = this.lookAndFeelManager.getCurrentLookAndFeel()
				.getTable();
		table.setModel(this.tagAttributesTableModel);
		table.setTableHeader(null);
		table.getColumnModel().getColumn(0).setMaxWidth(20);
		table.getColumnModel().getColumn(0)
				.setCellEditor(new DefaultCellEditor(new JCheckBox()));
		table.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);
		table.setDefaultRenderer(
				String.class,
				this.lookAndFeelManager
						.getCurrentLookAndFeel()
						.getTableCellRenderer(
								this.beanFactory
										.getBean(ComponentOrientationTableCellRendererCode.class)));
		return table;
	}

	/**
	 * @param label
	 * @param artistViewPanel
	 */
	private void arrangePanel(final JLabel label, final JPanel artistViewPanel) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		add(this.showFavorites, c);
		c.gridy = 1;
		add(this.showExtendedToolTip, c);
		c.gridy = 2;
		c.insets = new Insets(0, 10, 0, 0);
		add(label, c);
		c.gridx = 1;
		c.weightx = 1;
		add(this.extendedToolTipDelay, c);
		c.weightx = 0;
		c.gridx = 0;
		c.gridy = 3;
		c.insets = new Insets(0, 0, 0, 0);
		c.gridwidth = 2;
		add(this.useSmartTagViewSorting, c);
		c.gridy = 4;
		add(this.useArtistNamesSorting, c);
		c.gridy = 5;
		add(artistViewPanel, c);
		c.gridy = 6;
		add(this.useCaseSensitiveInTree, c);
		c.gridy = 7;
		add(this.highlightElementsWithIncompleteBasicTags, c);
		c.gridy = 8;
		c.weighty = 1;
		c.weightx = 0;
		c.anchor = this.controlsBuilder.getComponentOrientation()
				.isLeftToRight() ? GridBagConstraints.NORTHWEST
				: GridBagConstraints.NORTHEAST;
		c.insets = new Insets(10, 20, 10, 10);
		add(this.highlightTagAttributesScrollPane, c);
	}

	@Override
	public boolean applyPreferences() {

		this.stateNavigation.setShowFavoritesInNavigator(this.showFavorites
				.isSelected());
		this.stateNavigation.setShowExtendedTooltip(this.showExtendedToolTip
				.isSelected());
		this.stateNavigation
				.setExtendedTooltipDelay((Integer) this.extendedToolTipDelay
						.getSelectedItem());
		this.stateNavigation
				.setUseSmartTagViewSorting(this.useSmartTagViewSorting
						.isSelected());
		this.stateNavigation
				.setHighlightIncompleteTagElements(this.highlightElementsWithIncompleteBasicTags
						.isSelected());
		this.stateNavigation
				.setHighlightIncompleteTagFoldersAttributes(this.tagAttributesTableModel
						.getSelectedTagAttributes());
		this.stateNavigation
				.setUsePersonNamesArtistTagViewSorting(this.useArtistNamesSorting
						.isSelected());
		this.stateRepository
				.setKeyAlwaysCaseSensitiveInRepositoryStructure(this.useCaseSensitiveInTree
						.isSelected());

		ArtistViewMode newArtistViewState = ArtistViewMode.BOTH;
		if (this.useOnlyArtist.isSelected()) {
			newArtistViewState = ArtistViewMode.ARTIST;
		} else if (this.useOnlyArtistOfAlbum.isSelected()) {
			newArtistViewState = ArtistViewMode.ARTIST_OF_ALBUM;
		} else {
			newArtistViewState = ArtistViewMode.BOTH;
		}

		this.stateNavigation.setArtistViewMode(newArtistViewState);

		return false;
	}

	/**
	 * Sets the album tool tip delay.
	 * 
	 * @param time
	 *            the new album tool tip delay
	 */

	public void setAlbumToolTipDelay(final int time) {
		this.extendedToolTipDelay.setSelectedItem(time);
	}

	/**
	 * Sets the show album tool tip.
	 * 
	 * @param show
	 *            the new show album tool tip
	 */
	public void setShowAlbumToolTip(final boolean show) {
		this.showExtendedToolTip.setSelected(show);
	}

	/**
	 * Sets the show favorites.
	 * 
	 * @param show
	 *            the new show favorites
	 */
	private void setShowFavorites(final boolean show) {
		this.showFavorites.setSelected(show);
	}

	/**
	 * Sets the use smart tag view sorting.
	 * 
	 * @param use
	 *            the new use smart tag view sorting
	 */
	private void setUseSmartTagViewSorting(final boolean use) {
		this.useSmartTagViewSorting.setSelected(use);
	}

	/**
	 * Sets the use person names artist tag view sorting.
	 * 
	 * @param use
	 *            the new se person names artist tag view sorting
	 */
	private void setUsePersonNamesArtistTagViewSorting(final boolean use) {
		this.useArtistNamesSorting.setSelected(use);
	}

	/**
	 * Set the keys of repository structures artist and genre to case sensitive
	 * or not.
	 * 
	 * @param value
	 */
	private void setKeyAlwaysCaseSensitiveInRepositoryStructure(
			final boolean value) {
		this.useCaseSensitiveInTree.setSelected(value);
	}

	/**
	 * Sets property to highlight folder with incomplete tags
	 * 
	 * @param highlightFoldersWithIncompleteBasicTags
	 *            the highlightFoldersWithIncompleteBasicTags to set
	 */
	private void setHighlightFoldersWithIncompleteBasicTags(
			final boolean highlightFoldersWithIncompleteBasicTags) {
		this.highlightElementsWithIncompleteBasicTags
				.setSelected(highlightFoldersWithIncompleteBasicTags);
		this.highlightTagAttributesScrollPane
				.setEnabled(highlightFoldersWithIncompleteBasicTags);
		this.highlighTagAttributesTable
				.setEnabled(highlightFoldersWithIncompleteBasicTags);
	}

	@Override
	public void updatePanel() {
		this.tagAttributesTableModel
				.setTagAttributes(getAllTagAttributes(this.stateNavigation
						.getHighlightIncompleteTagFoldersAttributes()));
		setShowFavorites(this.stateNavigation.isShowFavoritesInNavigator());
		setShowAlbumToolTip(this.stateNavigation.isShowExtendedTooltip());
		setAlbumToolTipDelay(this.stateNavigation.getExtendedTooltipDelay());
		setUseSmartTagViewSorting(this.stateNavigation
				.isUseSmartTagViewSorting());
		setHighlightFoldersWithIncompleteBasicTags(this.stateNavigation
				.isHighlightIncompleteTagElements());
		setUsePersonNamesArtistTagViewSorting(this.stateNavigation
				.isUsePersonNamesArtistTagViewSorting());
		setKeyAlwaysCaseSensitiveInRepositoryStructure(this.stateRepository
				.isKeyAlwaysCaseSensitiveInRepositoryStructure());
		setArtistViewMode(this.stateNavigation.getArtistViewMode());
	}

	/**
	 * Returns a hash map with all tag attributes as key, and state (used or
	 * not) as value
	 * 
	 * @param attributes
	 * @return
	 */
	private Map<TextTagAttribute, Boolean> getAllTagAttributes(
			final List<TextTagAttribute> attributes) {
		Map<TextTagAttribute, Boolean> result = new HashMap<TextTagAttribute, Boolean>();
		for (TextTagAttribute att : TextTagAttribute.values()) {
			result.put(att, false);
		}
		for (TextTagAttribute attr : attributes) {
			result.put(attr, true);
		}
		return result;
	}

	private void setArtistViewMode(final ArtistViewMode artistViewMode) {
		if (ArtistViewMode.ARTIST.equals(artistViewMode)) {
			this.useOnlyArtist.setSelected(true);
		} else if (ArtistViewMode.ARTIST_OF_ALBUM.equals(artistViewMode)) {
			this.useOnlyArtistOfAlbum.setSelected(true);
		} else {
			this.useBothArtist.setSelected(true);
		}

	}

	@Override
	public void validatePanel() throws PreferencesValidationException {
	}

	@Override
	public void dialogVisibilityChanged(final boolean visible) {
		// Do nothing
	}

	@Override
	public void resetImmediateChanges() {
		// Do nothing
	}

}
