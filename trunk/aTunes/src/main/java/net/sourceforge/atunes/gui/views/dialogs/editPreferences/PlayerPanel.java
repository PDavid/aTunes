/*
 * aTunes 1.14.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

package net.sourceforge.atunes.gui.views.dialogs.editPreferences;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.atunes.gui.ColorDefinitions;
import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.kernel.modules.hotkeys.Hotkey;
import net.sourceforge.atunes.kernel.modules.hotkeys.HotkeyHandler;
import net.sourceforge.atunes.kernel.modules.hotkeys.HotkeysConfig;
import net.sourceforge.atunes.kernel.modules.player.PlayerHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.SystemProperties;
import net.sourceforge.atunes.misc.SystemProperties.OperatingSystem;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.LanguageTool;

import org.jvnet.substance.api.renderers.SubstanceDefaultTableCellRenderer;

public class PlayerPanel extends PreferencesPanel {

    class HotkeyTableModel extends AbstractTableModel {

        private static final long serialVersionUID = -5726677745418003289L;

        /** The data. */
        private HotkeysConfig hotkeysConfig;

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public String getColumnName(int column) {
            return column == 0 ? LanguageTool.getString("ACTION") : LanguageTool.getString("HOTKEY");
        }

        @Override
        public int getRowCount() {
            return hotkeysConfig != null ? hotkeysConfig.size() : 0;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (hotkeysConfig != null) {
                if (columnIndex == 0) {
                    return hotkeysConfig.getHotkeyByOrder(rowIndex).getDescription();
                } else if (columnIndex == 1) {
                    return hotkeysConfig.getHotkeyByOrder(rowIndex).getKeyDescription();
                }
            }
            return "";
        }

        public void setHotkeysConfig(HotkeysConfig hotkeysConfig) {
            this.hotkeysConfig = hotkeysConfig;
            conflicts = hotkeysConfig.conflicts();
            notRecommendedKeys = hotkeysConfig.notRecommendedKeys();
            fireTableDataChanged();
        }

        public HotkeysConfig getHotkeysConfig() {
            return hotkeysConfig;
        }

    }

    private static final long serialVersionUID = 4489293347321979288L;

    /** The play at startup. */
    private JCheckBox playAtStartup;

    /** The show ticks. */
    private JCheckBox showTicks;

    /** The use fade away. */
    private JCheckBox useFadeAway;

    /** The use short path names. */
    private JCheckBox useShortPathNames;

    /** The enable global hotkeys. */
    private JCheckBox enableGlobalHotkeys;

    /** The hotkey table. */
    JTable hotkeyTable;

    /** The table model. */
    HotkeyTableModel tableModel = new HotkeyTableModel();

    Set<Integer> conflicts = new HashSet<Integer>();
    Set<Integer> notRecommendedKeys = new HashSet<Integer>();

    private JButton resetHotkeys;

    /** The cache files before playing. */
    private JCheckBox cacheFilesBeforePlaying;

    private JComboBox engineCombo;

    /**
     * Instantiates a new player panel.
     */
    public PlayerPanel() {
        super(LanguageTool.getString("PLAYER"));
        Box engineBox = Box.createHorizontalBox();
        engineBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        engineBox.add(new JLabel(LanguageTool.getString("PLAYER_ENGINE")));
        engineBox.add(Box.createHorizontalStrut(6));
        engineCombo = new JComboBox(PlayerHandler.getInstance().getEngineNames());
        engineBox.add(engineCombo);
        engineBox.add(Box.createHorizontalGlue());
        playAtStartup = new JCheckBox(LanguageTool.getString("PLAY_AT_STARTUP"));
        useFadeAway = new JCheckBox(LanguageTool.getString("USE_FADE_AWAY"));
        showTicks = new JCheckBox(LanguageTool.getString("SHOW_TICKS"));
        useShortPathNames = new JCheckBox(LanguageTool.getString("USE_SHORT_PATH_NAMES_FOR_MPLAYER"));
        enableGlobalHotkeys = new JCheckBox(LanguageTool.getString("ENABLE_GLOBAL_HOTKEYS"));

        hotkeyTable = new JTable();
        hotkeyTable.setModel(tableModel);
        hotkeyTable.getTableHeader().setReorderingAllowed(false);
        hotkeyTable.getTableHeader().setResizingAllowed(false);
        hotkeyTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        hotkeyTable.setEnabled(HotkeyHandler.getInstance().areHotkeysSupported());
        hotkeyTable.setDefaultRenderer(Object.class, new SubstanceDefaultTableCellRenderer() {
            private static final long serialVersionUID = 1111298953883261220L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean arg2, boolean arg3, int row, int arg5) {
                Component c = super.getTableCellRendererComponent(table, value, arg2, arg3, row, arg5);
                GuiUtils.applyComponentOrientation((JLabel) c);
                if (conflicts.contains(row) || notRecommendedKeys.contains(row)) {
                    ((JLabel) c).setForeground(ColorDefinitions.WARNING_COLOR);
                }
                String keyWarnings = "";
                if (conflicts.contains(row)) {
                    keyWarnings += LanguageTool.getString("DUPLICATE_HOTKEYS");
                }
                if (notRecommendedKeys.contains(row)) {
                    if (conflicts.contains(row)) {
                        keyWarnings += " ";
                    }
                    keyWarnings += LanguageTool.getString("NOT_RECOMMENDED_HOTKEYS");
                }
                ((JLabel) c).setToolTipText(keyWarnings.isEmpty() ? null : keyWarnings);
                return c;
            }
        });
        hotkeyTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int selectedRow = hotkeyTable.getSelectedRow();
                int modifiersEx = e.getModifiersEx();
                int keyCode = e.getKeyCode();
                if (selectedRow != -1 && keyCode != KeyEvent.VK_UNDEFINED && (modifiersEx & InputEvent.BUTTON1_DOWN_MASK) == 0 && (modifiersEx & InputEvent.BUTTON2_DOWN_MASK) == 0
                        && (modifiersEx & InputEvent.BUTTON3_DOWN_MASK) == 0) {
                    Hotkey hotkey = tableModel.getHotkeysConfig().getHotkeyByOrder(selectedRow);
                    hotkey.setMod(modifiersEx);
                    hotkey.setKey(keyCode);

                    conflicts = tableModel.getHotkeysConfig().conflicts();
                    notRecommendedKeys = tableModel.getHotkeysConfig().notRecommendedKeys();

                    tableModel.fireTableRowsUpdated(0, tableModel.getRowCount());
                }
            }

        });

        InputMap inputMap = hotkeyTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        for (KeyStroke keyStroke : inputMap.allKeys()) {
            inputMap.put(keyStroke, "none");
        }

        resetHotkeys = new JButton(LanguageTool.getString("RESET"));
        resetHotkeys.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableModel.setHotkeysConfig(HotkeyHandler.getInstance().getDefaultHotkeysConfiguration());
                tableModel.fireTableDataChanged();
            }
        });

        JScrollPane scrollPane = new JScrollPane(hotkeyTable);
        scrollPane.setMinimumSize(new Dimension(400, 200));
        cacheFilesBeforePlaying = new JCheckBox(LanguageTool.getString("CACHE_FILES_BEFORE_PLAYING"));

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
        add(playAtStartup, c);
        c.gridy = 2;
        add(useFadeAway, c);
        c.gridy = 3;
        add(showTicks, c);
        c.gridy = 4;
        add(useShortPathNames, c);
        c.gridy = 5;
        add(cacheFilesBeforePlaying, c);
        c.gridy = 6;
        add(enableGlobalHotkeys, c);
        c.gridy = 7;
        c.weightx = 0;
        c.insets = new Insets(10, 20, 5, 10);
        add(scrollPane, c);
        c.gridy = 10;
        c.weighty = 1;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(0, 20, 0, 0);
        add(resetHotkeys, c);
    }

    /**
     * Gets the enable global hotkeys.
     * 
     * @return the enable global hotkeys
     */
    public JCheckBox getEnableGlobalHotkeys() {
        return enableGlobalHotkeys;
    }

    @Override
    public boolean applyPreferences(ApplicationState state) {
        boolean needRestart = false;
        state.setPlayAtStartup(playAtStartup.isSelected());
        state.setUseFadeAway(useFadeAway.isSelected());
        state.setShowTicks(showTicks.isSelected());
        state.setUseShortPathNames(useShortPathNames.isSelected());
        state.setEnableHotkeys(enableGlobalHotkeys.isSelected());
        state.setHotkeysConfig(tableModel.getHotkeysConfig());
        state.setCacheFilesBeforePlaying(cacheFilesBeforePlaying.isSelected());
        String engine = engineCombo.getSelectedItem().toString();
        if (!state.getPlayerEngine().equals(engine)) {
            state.setPlayerEngine(engine);
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
        return useShortPathNames;
    }

    /**
     * Sets the enable hotkeys.
     * 
     * @param enable
     *            the new enable hotkeys
     */
    private void setEnableHotkeys(boolean enable) {
        enableGlobalHotkeys.setSelected(enable);
    }

    /**
     * Sets the play at startup.
     * 
     * @param play
     *            the new play at startup
     */
    private void setPlayAtStartup(boolean play) {
        playAtStartup.setSelected(play);
    }

    /**
     * Sets the use short path names.
     * 
     * @param use
     *            the new use short path names
     */
    private void setUseShortPathNames(boolean use) {
        useShortPathNames.setSelected(use);
    }

    /**
     * Sets the use fade away.
     * 
     * @param useFadeAway
     *            the new use fade away
     */
    private void setUseFadeAway(boolean useFadeAway) {
        this.useFadeAway.setSelected(useFadeAway);
    }

    /**
     * Sets the show ticks.
     * 
     * @param showTicks
     *            the new show ticks
     */
    private void setShowTicks(boolean showTicks) {
        this.showTicks.setSelected(showTicks);
    }

    /**
     * Sets the cache files before playing.
     * 
     * @param cacheFiles
     *            the new cache files before playing
     */
    private void setCacheFilesBeforePlaying(boolean cacheFiles) {
        this.cacheFilesBeforePlaying.setSelected(cacheFiles);
    }

    private void setHotkeysConfig(HotkeysConfig hotkeysConfig) {
        this.tableModel.setHotkeysConfig(hotkeysConfig);
    }

    private void setPlayerEngine(String playerEngine) {
        engineCombo.setSelectedItem(playerEngine);
    }

    @Override
    public void updatePanel(ApplicationState state) {
        setPlayerEngine(state.getPlayerEngine());
        setPlayAtStartup(state.isPlayAtStartup());
        setUseFadeAway(state.isUseFadeAway());
        setShowTicks(state.isShowTicks());
        setCacheFilesBeforePlaying(state.isCacheFilesBeforePlaying());
        setEnableHotkeys(state.isEnableHotkeys());
        HotkeysConfig hotkeysConfig = state.getHotkeysConfig();
        setHotkeysConfig(hotkeysConfig != null ? hotkeysConfig : HotkeyHandler.getInstance().getHotkeysConfig());
        setUseShortPathNames(state.isUseShortPathNames());
        getUseShortPathNames().setEnabled(SystemProperties.OS == OperatingSystem.WINDOWS);
        getEnableGlobalHotkeys().setEnabled(HotkeyHandler.getInstance().areHotkeysSupported());
    }

    @Override
    public boolean validatePanel() {
        return true;
    }

    @Override
    public void dialogVisibilityChanged(boolean visible) {
        // Use this method from PreferencesPanel instead of override setVisible
        // As Dialog uses CardLayout to show panels, that layout invokes setVisible(false) when creating dialog just before showing it
        // so it can execute both code blocks before showing dialog
        if (visible) {
            HotkeyHandler.getInstance().disableHotkeys();
        } else {
            // Enable hotkeys again only if user didn't disable them
            if (ApplicationState.getInstance().isEnableHotkeys()) {
                HotkeyHandler.getInstance().enableHotkeys(ApplicationState.getInstance().getHotkeysConfig());
            }
        }
    }

    @Override
    public ImageIcon getIcon() {
        return ImageLoader.PLAY_TINY;
    }

}
