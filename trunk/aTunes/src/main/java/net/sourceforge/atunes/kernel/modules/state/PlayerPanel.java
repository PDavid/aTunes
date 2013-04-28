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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Box;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.atunes.gui.AbstractTableCellRendererCode;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IHotkey;
import net.sourceforge.atunes.model.IHotkeyHandler;
import net.sourceforge.atunes.model.IHotkeysConfig;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IPlayerEngine;
import net.sourceforge.atunes.model.IStateCore;
import net.sourceforge.atunes.model.IStatePlayer;
import net.sourceforge.atunes.model.IStateUI;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * @author alex
 * 
 */
public final class PlayerPanel extends AbstractPreferencesPanel {

	private static final Color WARNING_COLOR = Color.RED;

	private final class HotkeyKeyAdapter extends KeyAdapter {

		private final HotkeyTableModel tableModel;

		private final JTable table;

		private HotkeyKeyAdapter(final JTable table,
				final HotkeyTableModel tableModel) {
			this.table = table;
			this.tableModel = tableModel;
		}

		@Override
		public void keyPressed(final KeyEvent e) {
			int selectedRow = this.table.getSelectedRow();
			if (selectedRow != -1) {
				int modifiersEx = e.getModifiersEx();
				int keyCode = e.getKeyCode();
				if (validKeyPressed(modifiersEx, keyCode)) {
					IHotkey hotkey = this.tableModel.getHotkeysConfig()
							.getHotkeyByOrder(selectedRow);
					hotkey.setMod(modifiersEx);
					hotkey.setKey(keyCode);

					PlayerPanel.this.conflicts = this.tableModel
							.getHotkeysConfig().conflicts();
					PlayerPanel.this.notRecommendedKeys = this.tableModel
							.getHotkeysConfig().notRecommendedKeys();

					this.tableModel.fireTableRowsUpdated(0,
							this.tableModel.getRowCount());
				}
			}
		}

		/**
		 * @param modifiersEx
		 * @param keyCode
		 * @return
		 */
		private boolean validKeyPressed(final int modifiersEx, final int keyCode) {
			return keyCode != KeyEvent.VK_UNDEFINED
					&& isButton(modifiersEx, InputEvent.BUTTON1_DOWN_MASK)
					&& isButton(modifiersEx, InputEvent.BUTTON2_DOWN_MASK)
					&& isButton(modifiersEx, InputEvent.BUTTON3_DOWN_MASK);
		}

		private boolean isButton(final int modifiersEx, final int button) {
			return (modifiersEx & button) == 0;
		}
	}

	private final class HotkeyTableTableCellRendererCode extends
			AbstractTableCellRendererCode<JLabel, Object> {

		@Override
		public JLabel getComponent(final JLabel c, final JTable t,
				final Object value, final boolean isSelected,
				final boolean hasFocus, final int row, final int column) {
			PlayerPanel.this.controlsBuilder.applyComponentOrientation(c);
			if (PlayerPanel.this.conflicts.contains(row)
					|| PlayerPanel.this.notRecommendedKeys.contains(row)) {
				c.setForeground(WARNING_COLOR);
			}
			String keyWarnings = "";
			if (PlayerPanel.this.conflicts.contains(row)) {
				keyWarnings += I18nUtils.getString("DUPLICATE_HOTKEYS");
			}
			if (PlayerPanel.this.notRecommendedKeys.contains(row)) {
				if (PlayerPanel.this.conflicts.contains(row)) {
					keyWarnings += " ";
				}
				keyWarnings += I18nUtils.getString("NOT_RECOMMENDED_HOTKEYS");
			}
			c.setToolTipText(keyWarnings.isEmpty() ? null : keyWarnings);
			return c;
		}
	}

	class HotkeyTableModel extends AbstractTableModel {

		private static final long serialVersionUID = -5726677745418003289L;

		/** The data. */
		private IHotkeysConfig hotkeysConfig;

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public String getColumnName(final int column) {
			return column == 0 ? I18nUtils.getString("ACTION") : I18nUtils
					.getString("HOTKEY");
		}

		@Override
		public int getRowCount() {
			return this.hotkeysConfig != null ? this.hotkeysConfig.size() : 0;
		}

		@Override
		public Object getValueAt(final int rowIndex, final int columnIndex) {
			if (this.hotkeysConfig != null) {
				if (columnIndex == 0) {
					return this.hotkeysConfig.getHotkeyByOrder(rowIndex)
							.getDescription();
				} else if (columnIndex == 1) {
					return this.hotkeysConfig.getHotkeyByOrder(rowIndex)
							.getKeyDescription();
				}
			}
			return "";
		}

		/**
		 * @param hotkeysConfig
		 */
		public void setHotkeysConfig(final IHotkeysConfig hotkeysConfig) {
			this.hotkeysConfig = hotkeysConfig;
			PlayerPanel.this.conflicts = hotkeysConfig.conflicts();
			PlayerPanel.this.notRecommendedKeys = hotkeysConfig
					.notRecommendedKeys();
			fireTableDataChanged();
		}

		/**
		 * @return hotkey configuration
		 */
		public IHotkeysConfig getHotkeysConfig() {
			return this.hotkeysConfig;
		}

	}

	private static final long serialVersionUID = 4489293347321979288L;

	private List<IPlayerEngine> engines;

	/** The play at startup. */
	private JCheckBox playAtStartup;

	/** Show advanced player controls */
	private JCheckBox showAdvancedPlayerControls;

	/**
	 * Show player controls on top
	 */
	private JCheckBox showPlayerControlsOnTop;

	/** The use fade away. */
	private JCheckBox useFadeAway;

	/** The use short path names. */
	private JCheckBox useShortPathNames;

	/** The enable global hotkeys. */
	private JCheckBox enableGlobalHotkeys;

	/**
	 * Scroll pane containing hotkeys
	 */
	private JScrollPane hotkeyScrollPane;

	/** The table model. */
	private final HotkeyTableModel tableModel = new HotkeyTableModel();

	private Set<Integer> conflicts = new HashSet<Integer>();
	private Set<Integer> notRecommendedKeys = new HashSet<Integer>();

	private JButton resetHotkeys;

	/** The cache files before playing. */
	private JCheckBox cacheFilesBeforePlaying;

	private JComboBox engineCombo;

	private IOSManager osManager;

	private IHotkeyHandler hotkeyHandler;

	private ILookAndFeelManager lookAndFeelManager;

	private IStateUI stateUI;

	private IStatePlayer statePlayer;

	private IStateCore stateCore;

	private IControlsBuilder controlsBuilder;

	/**
	 * @param controlsBuilder
	 */
	public void setControlsBuilder(final IControlsBuilder controlsBuilder) {
		this.controlsBuilder = controlsBuilder;
	}

	/**
	 * @param stateCore
	 */
	public void setStateCore(final IStateCore stateCore) {
		this.stateCore = stateCore;
	}

	/**
	 * @param statePlayer
	 */
	public void setStatePlayer(final IStatePlayer statePlayer) {
		this.statePlayer = statePlayer;
	}

	/**
	 * @param stateUI
	 */
	public void setStateUI(final IStateUI stateUI) {
		this.stateUI = stateUI;
	}

	/**
	 * @param engines
	 */
	public void setEngines(final List<IPlayerEngine> engines) {
		this.engines = engines;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(
			final ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

	/**
	 * @param hotkeyHandler
	 */
	public void setHotkeyHandler(final IHotkeyHandler hotkeyHandler) {
		this.hotkeyHandler = hotkeyHandler;
	}

	/**
	 * Instantiates a new player panel.
	 */
	public PlayerPanel() {
		super(I18nUtils.getString("PLAYER"));
	}

	/**
	 * Initializes panel
	 */
	public void initialize() {
		Box engineBox = Box.createHorizontalBox();
		engineBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		engineBox.add(new JLabel(I18nUtils.getString("PLAYER_ENGINE")));
		engineBox.add(Box.createHorizontalStrut(6));
		List<String> enginesNamesList = new ArrayList<String>();
		for (IPlayerEngine engine : this.engines) {
			enginesNamesList.add(engine.getEngineName());
		}
		String[] engineNames = enginesNamesList
				.toArray(new String[enginesNamesList.size()]);
		this.engineCombo = new JComboBox(engineNames);
		// Disable combo if no player engine available
		this.engineCombo.setEnabled(engineNames.length > 0);
		engineBox.add(this.engineCombo);
		engineBox.add(Box.createHorizontalGlue());
		this.playAtStartup = new JCheckBox(
				I18nUtils.getString("PLAY_AT_STARTUP"));
		this.useFadeAway = new JCheckBox(I18nUtils.getString("USE_FADE_AWAY"));
		this.showAdvancedPlayerControls = new JCheckBox(
				I18nUtils.getString("SHOW_ADVANCED_PLAYER_CONTROLS"));
		this.useShortPathNames = new JCheckBox(
				I18nUtils.getString("USE_SHORT_PATH_NAMES_FOR_MPLAYER"));
		this.enableGlobalHotkeys = new JCheckBox(
				I18nUtils.getString("ENABLE_GLOBAL_HOTKEYS"));
		this.showPlayerControlsOnTop = new JCheckBox(
				I18nUtils.getString("SHOW_PLAYER_CONTROLS_ON_TOP"));

		JTable hotkeyTable = getHotkeyTable();

		this.resetHotkeys = new JButton(I18nUtils.getString("RESET"));
		this.resetHotkeys.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				PlayerPanel.this.tableModel
						.setHotkeysConfig(PlayerPanel.this.hotkeyHandler
								.getDefaultHotkeysConfiguration());
				PlayerPanel.this.tableModel.fireTableDataChanged();
			}
		});

		this.hotkeyScrollPane = this.controlsBuilder
				.createScrollPane(hotkeyTable);
		this.hotkeyScrollPane.setMinimumSize(new Dimension(400, 200));
		this.cacheFilesBeforePlaying = new JCheckBox(
				I18nUtils.getString("CACHE_FILES_BEFORE_PLAYING"));

		arrangePanel(engineBox);
	}

	/**
	 * 
	 */
	private JTable getHotkeyTable() {
		final JTable table = this.lookAndFeelManager.getCurrentLookAndFeel()
				.getTable();
		table.setModel(this.tableModel);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(false);
		table.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);
		table.setEnabled(this.hotkeyHandler.areHotkeysSupported());
		table.setDefaultRenderer(
				Object.class,
				this.lookAndFeelManager.getCurrentLookAndFeel()
						.getTableCellRenderer(
								new HotkeyTableTableCellRendererCode()));

		table.addKeyListener(new HotkeyKeyAdapter(table, this.tableModel));

		InputMap inputMap = table
				.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		for (KeyStroke keyStroke : inputMap.allKeys()) {
			inputMap.put(keyStroke, "none");
		}

		return table;
	}

	/**
	 * @param engineBox
	 */
	private void arrangePanel(final Box engineBox) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		Insets prevInsets = c.insets;
		c.insets = new Insets(0, 8, 0, 0);
		add(engineBox, c);
		c.insets = prevInsets;
		c.gridy = 1;
		add(this.playAtStartup, c);
		c.gridy = 2;
		add(this.useFadeAway, c);
		c.gridy = 3;
		add(this.showAdvancedPlayerControls, c);
		c.gridy = 4;
		add(this.showPlayerControlsOnTop, c);
		c.gridy = 5;
		add(this.useShortPathNames, c);
		c.gridy = 6;
		add(this.cacheFilesBeforePlaying, c);
		c.gridy = 7;
		add(this.enableGlobalHotkeys, c);
		c.gridy = 8;
		c.weightx = 0;
		c.insets = new Insets(10, 20, 5, 10);
		add(this.hotkeyScrollPane, c);
		c.gridy = 9;
		c.weighty = 1;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(0, 20, 0, 0);
		JPanel resetHotkeysPanel = new JPanel();
		resetHotkeysPanel.add(this.resetHotkeys);
		add(resetHotkeysPanel, c);
	}

	/**
	 * Gets the enable global hotkeys.
	 * 
	 * @return the enable global hotkeys
	 */
	private JCheckBox getEnableGlobalHotkeys() {
		return this.enableGlobalHotkeys;
	}

	@Override
	public boolean applyPreferences() {
		boolean needRestart = false;
		this.statePlayer.setPlayAtStartup(this.playAtStartup.isSelected());
		this.statePlayer.setUseFadeAway(this.useFadeAway.isSelected());
		this.stateUI
				.setShowAdvancedPlayerControls(this.showAdvancedPlayerControls
						.isSelected());
		this.statePlayer.setUseShortPathNames(this.useShortPathNames
				.isSelected());
		this.stateCore.setEnableHotkeys(this.enableGlobalHotkeys.isSelected());
		this.stateCore.setHotkeysConfig(this.tableModel.getHotkeysConfig());
		this.statePlayer
				.setCacheFilesBeforePlaying(this.cacheFilesBeforePlaying
						.isSelected());
		String engine = this.engineCombo.getSelectedItem() != null ? this.engineCombo
				.getSelectedItem().toString() : null;
		if (engine != null
				&& !this.statePlayer.getPlayerEngine().equals(engine)) {
			this.statePlayer.setPlayerEngine(engine);
			needRestart = true;
		}

		boolean onTop = this.stateUI.isShowPlayerControlsOnTop();
		this.stateUI.setShowPlayerControlsOnTop(this.showPlayerControlsOnTop
				.isSelected());
		if (onTop != this.showPlayerControlsOnTop.isSelected()) {
			needRestart = true;
		}

		return needRestart;
	}

	/**
	 * Gets the use short path names.
	 * 
	 * @return the use short path names
	 */
	private JCheckBox getUseShortPathNames() {
		return this.useShortPathNames;
	}

	/**
	 * Sets the enable hotkeys.
	 * 
	 * @param enable
	 *            the new enable hotkeys
	 */
	private void setEnableHotkeys(final boolean enable) {
		this.enableGlobalHotkeys.setSelected(enable);
	}

	/**
	 * Sets the play at startup.
	 * 
	 * @param play
	 *            the new play at startup
	 */
	private void setPlayAtStartup(final boolean play) {
		this.playAtStartup.setSelected(play);
	}

	/**
	 * Sets the use short path names.
	 * 
	 * @param use
	 *            the new use short path names
	 */
	private void setUseShortPathNames(final boolean use) {
		this.useShortPathNames.setSelected(use);
	}

	/**
	 * Sets the use fade away.
	 * 
	 * @param useFadeAway
	 *            the new use fade away
	 */
	private void setUseFadeAway(final boolean useFadeAway) {
		this.useFadeAway.setSelected(useFadeAway);
	}

	/**
	 * Sets the show advanced player controls
	 * 
	 * @param showAdvancedPlayerControls
	 */
	private void setShowAdvancedPlayerControls(
			final boolean showAdvancedPlayerControls) {
		this.showAdvancedPlayerControls.setSelected(showAdvancedPlayerControls);
	}

	/**
	 * Sets the property to show player controls on top
	 * 
	 * @param onTop
	 */
	private void setShowPlayerControlsOnTop(final boolean onTop) {
		this.showPlayerControlsOnTop.setSelected(onTop);
	}

	/**
	 * Sets the cache files before playing.
	 * 
	 * @param cacheFiles
	 *            the new cache files before playing
	 */
	private void setCacheFilesBeforePlaying(final boolean cacheFiles) {
		this.cacheFilesBeforePlaying.setSelected(cacheFiles);
	}

	private void setHotkeysConfig(final IHotkeysConfig hotkeysConfig) {
		this.tableModel.setHotkeysConfig(hotkeysConfig);
	}

	private void setPlayerEngine(final String playerEngine) {
		this.engineCombo.setSelectedItem(playerEngine);
	}

	@Override
	public void updatePanel() {
		setPlayerEngine(this.statePlayer.getPlayerEngine());
		setPlayAtStartup(this.statePlayer.isPlayAtStartup());
		setUseFadeAway(this.statePlayer.isUseFadeAway());
		setShowAdvancedPlayerControls(this.stateUI
				.isShowAdvancedPlayerControls());
		setShowPlayerControlsOnTop(this.stateUI.isShowPlayerControlsOnTop());
		setCacheFilesBeforePlaying(this.statePlayer.isCacheFilesBeforePlaying());
		setEnableHotkeys(this.stateCore.isEnableHotkeys());
		IHotkeysConfig hotkeysConfig = this.stateCore.getHotkeysConfig();
		setHotkeysConfig(hotkeysConfig != null ? hotkeysConfig
				: this.hotkeyHandler.getHotkeysConfig());
		setUseShortPathNames(this.statePlayer.isUseShortPathNames());
		getUseShortPathNames().setEnabled(this.osManager.usesShortPathNames());

		boolean hotKeysSupported = this.hotkeyHandler.areHotkeysSupported();
		getEnableGlobalHotkeys().setVisible(hotKeysSupported);
		getHotkeyScrollPane().setVisible(hotKeysSupported);
		getResetHotkeys().setVisible(hotKeysSupported);
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
		// Use this method from PreferencesPanel instead of override setVisible
		// As Dialog uses CardLayout to show panels, that layout invokes
		// setVisible(false) when creating dialog just before showing it
		// so it can execute both code blocks before showing dialog
		if (visible) {
			this.hotkeyHandler.disableHotkeys();
		} else {
			// Enable hotkeys again only if user didn't disable them
			if (this.stateCore.isEnableHotkeys()) {
				this.hotkeyHandler.enableHotkeys(this.stateCore
						.getHotkeysConfig());
			}
		}
	}

	/**
	 * @return hotkey scroll pane
	 */
	private JScrollPane getHotkeyScrollPane() {
		return this.hotkeyScrollPane;
	}

	/**
	 * @return the resetHotkeys
	 */
	private JButton getResetHotkeys() {
		return this.resetHotkeys;
	}

}
