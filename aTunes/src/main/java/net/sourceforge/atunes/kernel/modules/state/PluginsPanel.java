/*
 * aTunes 3.1.0
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

package net.sourceforge.atunes.kernel.modules.state;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableModel;

import net.sourceforge.atunes.gui.views.controls.UrlLabel;
import net.sourceforge.atunes.gui.views.dialogs.PluginEditorDialog;
import net.sourceforge.atunes.model.IDesktop;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IErrorDialog;
import net.sourceforge.atunes.model.IExceptionDialog;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IPluginsHandler;
import net.sourceforge.atunes.model.IStateCore;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import org.commonjukebox.plugins.exceptions.InvalidPluginConfigurationException;
import org.commonjukebox.plugins.exceptions.PluginSystemException;
import org.commonjukebox.plugins.model.PluginConfiguration;
import org.commonjukebox.plugins.model.PluginFolder;
import org.commonjukebox.plugins.model.PluginInfo;

/**
 * Panel to manage plugins
 * @author alex
 *
 */
public final class PluginsPanel extends AbstractPreferencesPanel {

	private static final String HTML = "</html>";

	private static final String B = ":</b> ";

	private static final String HTML_B = "<html><b>";

	private static final long serialVersionUID = 8611795969151425262L;

	private JCheckBox enabledPluginBox;

	private JTable pluginsTable;

	private JButton pluginPreferencesButton;

	private JButton uninstallPluginButton;

	static final int CELL_HEIGHT = 30;

	/**
	 * Plugins modified with their modified configuration
	 */
	private Map<PluginInfo, PluginConfiguration> pluginsModified;

	/**
	 * Plugins activated or deactivated
	 */
	private Map<PluginInfo, Boolean> pluginsActivation;

	private IPluginsHandler pluginsHandler;

	private ILookAndFeelManager lookAndFeelManager;

	private IDesktop desktop;

	private IStateCore stateCore;

	private IDialogFactory dialogFactory;

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param stateCore
	 */
	public void setStateCore(final IStateCore stateCore) {
		this.stateCore = stateCore;
	}

	/**
	 * @param pluginsHandler
	 */
	public void setPluginsHandler(final IPluginsHandler pluginsHandler) {
		this.pluginsHandler = pluginsHandler;
	}

	/**
	 * @param lookAndFeelManager
	 */
	public void setLookAndFeelManager(final ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

	/**
	 * @param desktop
	 */
	public void setDesktop(final IDesktop desktop) {
		this.desktop = desktop;
	}

	/**
	 * Instantiates a new plugins panel.
	 */
	public PluginsPanel() {
		super(I18nUtils.getString("PLUGINS"));
	}

	/**
	 * Initializes panel
	 */
	public void initialize() {
		enabledPluginBox = new JCheckBox(I18nUtils.getString("ENABLED_PLUGINS"));
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setVisible(stateCore.isPluginsEnabled());

		pluginsTable = lookAndFeelManager.getCurrentLookAndFeel().getTable();
		pluginsTable.setRowHeight(CELL_HEIGHT);
		pluginsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		pluginsTable.setColumnModel(new PluginsTableDefaultTableColumnModel());
		JScrollPane scrollPane = lookAndFeelManager.getCurrentLookAndFeel().getTableScrollPane(pluginsTable);

		JPanel pluginDetailPanel = new JPanel(new GridBagLayout());
		final JLabel pluginNameLabel = new JLabel();
		final JLabel pluginVersionLabel = new JLabel();
		final JLabel pluginClassNameLabel = new JLabel();
		final JLabel pluginLocationLabel = new JLabel();
		final JLabel pluginAuthorLabel = new JLabel();
		final UrlLabel pluginUrlLabel = new UrlLabel(desktop);

		pluginsTable.setDefaultRenderer(PluginInfo.class, lookAndFeelManager.getCurrentLookAndFeel().getTableCellRenderer(new PluginsTableCellRendererCode()));

		pluginsTable.getSelectionModel().addListSelectionListener(new PluginsTableListSelectionListener(pluginLocationLabel,
				pluginUrlLabel, pluginClassNameLabel, pluginAuthorLabel,
				pluginVersionLabel, pluginNameLabel));

		pluginPreferencesButton = new JButton(StringUtils.getString(I18nUtils.getString("PREFERENCES"), "..."));
		pluginPreferencesButton.setEnabled(false);

		pluginPreferencesButton.addActionListener(new PluginPreferencesActionListener());

		JButton installNewPluginButton = new JButton(StringUtils.getString(I18nUtils.getString("INSTALL"), "..."));
		installNewPluginButton.addActionListener(new InstallNewPluginActionListener());

		uninstallPluginButton = new JButton(I18nUtils.getString("UNINSTALL"));
		uninstallPluginButton.setEnabled(false);

		uninstallPluginButton.addActionListener(new UninstallPluginActionListener());


		arrangePanel(mainPanel, scrollPane, pluginDetailPanel, pluginNameLabel,
				pluginVersionLabel, pluginClassNameLabel, pluginLocationLabel,
				pluginAuthorLabel, pluginUrlLabel, installNewPluginButton);

	}

	/**
	 * @param mainPanel
	 * @param scrollPane
	 * @param pluginDetailPanel
	 * @param pluginNameLabel
	 * @param pluginVersionLabel
	 * @param pluginClassNameLabel
	 * @param pluginLocationLabel
	 * @param pluginAuthorLabel
	 * @param pluginUrlLabel
	 * @param installNewPluginButton
	 */
	private void arrangePanel(final JPanel mainPanel, final JScrollPane scrollPane,
			final JPanel pluginDetailPanel, final JLabel pluginNameLabel,
			final JLabel pluginVersionLabel, final JLabel pluginClassNameLabel,
			final JLabel pluginLocationLabel, final JLabel pluginAuthorLabel,
			final UrlLabel pluginUrlLabel, final JButton installNewPluginButton) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = stateCore.isPluginsEnabled() ? 0 : 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		add(enabledPluginBox,c);

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		add(mainPanel,c);

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 3;

		mainPanel.add(scrollPane, c);

		arrangePluginDetailPanel(pluginDetailPanel, pluginNameLabel,
				pluginVersionLabel, pluginClassNameLabel, pluginLocationLabel,
				pluginAuthorLabel, pluginUrlLabel);

		c.gridy = 3;
		c.insets = new Insets(10, 5, 10, 5);
		c.gridheight = 1;
		c.gridwidth = 2;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(pluginDetailPanel, c);

		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(10, 10, 0, 0);
		c.gridwidth = 1;
		mainPanel.add(pluginPreferencesButton, c);

		c.gridy = 1;
		mainPanel.add(installNewPluginButton, c);

		c.gridy = 2;
		mainPanel.add(uninstallPluginButton, c);
	}

	/**
	 * @param pluginDetailPanel
	 * @param pluginNameLabel
	 * @param pluginVersionLabel
	 * @param pluginClassNameLabel
	 * @param pluginLocationLabel
	 * @param pluginAuthorLabel
	 * @param pluginUrlLabel
	 */
	private void arrangePluginDetailPanel(final JPanel pluginDetailPanel,
			final JLabel pluginNameLabel, final JLabel pluginVersionLabel,
			final JLabel pluginClassNameLabel,
			final JLabel pluginLocationLabel, final JLabel pluginAuthorLabel,
			final UrlLabel pluginUrlLabel) {
		GridBagConstraints c2 = new GridBagConstraints();
		c2.gridx = 0;
		c2.weightx = 1;
		c2.fill = GridBagConstraints.HORIZONTAL;
		c2.anchor = GridBagConstraints.WEST;
		c2.gridy = 0;
		pluginDetailPanel.add(pluginNameLabel, c2);
		c2.gridy = 1;
		pluginDetailPanel.add(pluginVersionLabel, c2);
		c2.gridy = 2;
		pluginDetailPanel.add(pluginClassNameLabel, c2);
		c2.gridy = 3;
		pluginDetailPanel.add(pluginLocationLabel, c2);
		c2.gridy = 4;
		pluginDetailPanel.add(pluginAuthorLabel, c2);
		c2.gridy = 5;
		pluginDetailPanel.add(pluginUrlLabel, c2);
	}

	@Override
	public boolean applyPreferences() {
		boolean restartNeeded = false;
		if (stateCore.isPluginsEnabled() != enabledPluginBox.isSelected()){
			stateCore.setPluginsEnabled(enabledPluginBox.isSelected());
			restartNeeded = true;
		}

		if (stateCore.isPluginsEnabled()) {
			restartNeeded = writePluginsConfiguration(restartNeeded);
		}
		return restartNeeded;
	}

	/**
	 * @param restartNeeded
	 * @return
	 */
	private boolean writePluginsConfiguration(final boolean restartNeeded) {
		boolean restart = restartNeeded;
		try {
			// if any plugin has been modified then write configuration
			for (PluginInfo plugin : pluginsModified.keySet()) {
				Logger.debug("Writting configuration of plugin: ", plugin.getName());

				// Avoid plugins throw exceptions when setting configuration
				try {
					pluginsHandler.setConfiguration(plugin, pluginsModified.get(plugin));
				} catch (PluginSystemException t) {
					StringBuilder sb = new StringBuilder();
					sb.append(I18nUtils.getString("PLUGIN_CONFIGURATION_ERROR"));
					sb.append(" ");
					sb.append(plugin.getName());
					dialogFactory.newDialog(IExceptionDialog.class).showExceptionDialog(sb.toString(), t);
				}

				restart = restart || pluginsHandler.pluginNeedsRestart(plugin);
			}
			// If any plugin has been activated or deactivated then apply changes
			for (PluginInfo plugin : pluginsActivation.keySet()) {
				if (pluginsActivation.get(plugin)) {
					pluginsHandler.activatePlugin(plugin);
				} else {
					pluginsHandler.deactivatePlugin(plugin);
				}

				restart = restart || pluginsHandler.pluginNeedsRestart(plugin);
			}
		} catch (PluginSystemException e) {
			Logger.error(e);
			if (e.getCause() != null) {
				Logger.error(e.getCause());
			}
		}
		return restart;
	}

	/**
	 * Sets the plugins availlability
	 * 
	 * @param enabled
	 */
	private void setPluginsEnabled(final boolean enabled) {
		enabledPluginBox.setSelected(enabled);
	}

	@Override
	public void updatePanel() {
		setPluginsEnabled(stateCore.isPluginsEnabled());
		if (stateCore.isPluginsEnabled()){
			List<PluginInfo> plugins = pluginsHandler.getAvailablePlugins();
			pluginsTable.setModel(new PluginsTableModel(plugins));
		}
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
		if (visible) {
			// Initialize plugins modified
			pluginsModified = new HashMap<PluginInfo, PluginConfiguration>();
			pluginsActivation = new HashMap<PluginInfo, Boolean>();
			// Select first plugin
			if (stateCore.isPluginsEnabled() && !pluginsHandler.getAvailablePlugins().isEmpty()) {
				pluginsTable.getSelectionModel().setSelectionInterval(0, 0);
			}
		}
	}

	private final class UninstallPluginActionListener implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			int row = pluginsTable.getSelectedRow();
			PluginInfo plugin = ((PluginsTableModel) pluginsTable.getModel()).getPluginAt(row);
			try {
				Map<PluginFolder, PluginSystemException> problemsFound = PluginsPanel.this.pluginsHandler.uninstallPlugin(plugin);
				if (problemsFound != null) {
					for (Map.Entry<PluginFolder, PluginSystemException> pluginFolderEntry : problemsFound.entrySet()) {
						dialogFactory.newDialog(IExceptionDialog.class).showExceptionDialog(I18nUtils.getString("PLUGIN_UNINSTALLATION_ERROR"), pluginFolderEntry.getValue());
					}
				}
			} catch (Exception e1) {
				dialogFactory.newDialog(IErrorDialog.class).showErrorDialog(e1.getMessage());
				Logger.error(e1);
			}
		}
	}

	private final class PluginPreferencesActionListener implements
	ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			int row = pluginsTable.getSelectedRow();
			PluginInfo plugin = ((PluginsTableModel) pluginsTable.getModel()).getPluginAt(row);
			PluginConfiguration configuration = ((PluginsTableModel) pluginsTable.getModel()).getPluginConfigurationAt(row);
			PluginEditorDialog editorDialog = dialogFactory.newDialog(PluginEditorDialog.class);
			editorDialog.initializeDialog(plugin, configuration);
			editorDialog.showDialog();
			configuration = editorDialog.getConfiguration();
			if (configuration != null) {
				// Validate plugin configuration
				try {
					PluginsPanel.this.pluginsHandler.validateConfiguration(plugin, configuration);
					pluginsModified.put(plugin, configuration);
				} catch (InvalidPluginConfigurationException ex) {
					dialogFactory.newDialog(IErrorDialog.class).showErrorDialog(StringUtils.getString(I18nUtils.getString("PLUGIN_CONFIGURATION_INVALID"), ex.getMessage()));
				}
			}
		}
	}

	private final class PluginsTableListSelectionListener implements
	ListSelectionListener {
		private final JLabel pluginLocationLabel;
		private final UrlLabel pluginUrlLabel;
		private final JLabel pluginClassNameLabel;
		private final JLabel pluginAuthorLabel;
		private final JLabel pluginVersionLabel;
		private final JLabel pluginNameLabel;

		private PluginsTableListSelectionListener(final JLabel pluginLocationLabel,
				final UrlLabel pluginUrlLabel, final JLabel pluginClassNameLabel,
				final JLabel pluginAuthorLabel, final JLabel pluginVersionLabel,
				final JLabel pluginNameLabel) {
			this.pluginLocationLabel = pluginLocationLabel;
			this.pluginUrlLabel = pluginUrlLabel;
			this.pluginClassNameLabel = pluginClassNameLabel;
			this.pluginAuthorLabel = pluginAuthorLabel;
			this.pluginVersionLabel = pluginVersionLabel;
			this.pluginNameLabel = pluginNameLabel;
		}

		@Override
		public void valueChanged(final ListSelectionEvent e) {
			// Enable preferences button if plugin has any configuration and update detail panel
			if (pluginsTable.getSelectedRow() != -1) {
				PluginInfo plugin = ((PluginsTableModel) pluginsTable.getModel()).getPluginAt(pluginsTable.getSelectedRow());
				pluginNameLabel.setText(StringUtils.getString(HTML_B, I18nUtils.getString("NAME"), B, plugin.getName(), HTML));
				pluginVersionLabel.setText(StringUtils.getString(HTML_B, I18nUtils.getString("VERSION"), B, plugin.getVersion(), HTML));
				pluginClassNameLabel.setText(StringUtils.getString(HTML_B, I18nUtils.getString("CLASS_NAME"), B, plugin.getClassName(), HTML));
				pluginLocationLabel.setText(StringUtils.getString(HTML_B, I18nUtils.getString("LOCATION"), B, plugin.getPluginFolder().getName(), HTML));
				pluginAuthorLabel.setText(StringUtils.getString(HTML_B, I18nUtils.getString("AUTHOR"), B, plugin.getAuthor(), HTML));
				pluginUrlLabel.setText(plugin.getUrl(), plugin.getUrl());
				pluginPreferencesButton.setEnabled(((PluginsTableModel) pluginsTable.getModel()).getPluginConfigurationAt(pluginsTable.getSelectedRow()) != null);
				uninstallPluginButton.setEnabled(pluginsTable.getSelectedRow() != -1);
			}
		}
	}

	private final class InstallNewPluginActionListener implements
	ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			FileFilter filter = new FileFilter() {
				@Override
				public boolean accept(final File f) {
					return f.isDirectory() || f.getName().toUpperCase().endsWith("ZIP");
				}

				@Override
				public String getDescription() {
					return I18nUtils.getString("ZIP_FILES");
				}
			};
			fileChooser.setFileFilter(filter);
			if (fileChooser.showOpenDialog(getPreferenceDialog()) == JFileChooser.APPROVE_OPTION) {
				File zipFile = fileChooser.getSelectedFile();
				try {
					Map<PluginFolder, PluginSystemException> problemsFound = pluginsHandler.installPlugin(zipFile);
					if (problemsFound != null) {
						for (Entry<PluginFolder, PluginSystemException> pluginFolderEntry : problemsFound.entrySet()) {
							dialogFactory.newDialog(IExceptionDialog.class).showExceptionDialog(I18nUtils.getString("PLUGIN_INSTALLATION_ERROR"), pluginFolderEntry.getValue());
						}
					}

				} catch (Exception e1) {
					dialogFactory.newDialog(IExceptionDialog.class).showExceptionDialog(I18nUtils.getString("PLUGIN_INSTALLATION_ERROR"), e1);
				}
			}
		}
	}

	private class PluginsTableModel implements TableModel {

		private List<PluginInfo> plugins;

		private final List<TableModelListener> listeners = new ArrayList<TableModelListener>();

		public PluginsTableModel(final List<PluginInfo> plugins) {
			super();
			this.plugins = plugins;
			// Sort plugins by name
			Collections.sort(this.plugins, new Comparator<PluginInfo>() {
				@Override
				public int compare(final PluginInfo o1, final PluginInfo o2) {
					return o1.getName().compareToIgnoreCase(o2.getName());
				}
			});
		}

		@Override
		public void addTableModelListener(final TableModelListener l) {
			listeners.add(l);
		}

		@Override
		public Class<?> getColumnClass(final int columnIndex) {
			switch (columnIndex) {
			case 0:
				return Boolean.class;
			case 1:
				return PluginInfo.class;
			default:
				break;
			}
			return null;
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public String getColumnName(final int columnIndex) {
			switch (columnIndex) {
			case 0:
				return I18nUtils.getString("ACTIVE");
			case 1:
				return I18nUtils.getString("NAME");
			default:
				break;
			}
			return null;
		}

		@Override
		public int getRowCount() {
			return this.plugins.size();
		}

		@Override
		public Object getValueAt(final int rowIndex, final int columnIndex) {
			switch (columnIndex) {
			case 0:
				return (pluginsActivation.containsKey(this.plugins.get(rowIndex)) ? pluginsActivation.get(this.plugins.get(rowIndex)) : this.plugins.get(rowIndex).isActive());
			case 1:
				return this.plugins.get(rowIndex);
			default:
				break;
			}
			return null;
		}

		@Override
		public boolean isCellEditable(final int rowIndex, final int columnIndex) {
			return columnIndex == 0;
		}

		@Override
		public void removeTableModelListener(final TableModelListener l) {
			listeners.remove(l);
		}

		@Override
		public void setValueAt(final Object value, final int rowIndex, final int columnIndex) {
			if (columnIndex == 0) {
				// If value changed then add to map
				if (((Boolean) value) != this.plugins.get(rowIndex).isActive()) {
					pluginsActivation.put(this.plugins.get(rowIndex), (Boolean) value);
				} else {
					// If value is the same then remove from map to avoid set the same status as it was previously
					pluginsActivation.remove(this.plugins.get(rowIndex));
				}
			}
		}

		public PluginConfiguration getPluginConfigurationAt(final int row) {
			try {
				return pluginsHandler.getConfiguration(this.plugins.get(row));
			} catch (PluginSystemException e) {
				Logger.error(e);
				return null;
			}
		}

		public PluginInfo getPluginAt(final int row) {
			return this.plugins.get(row);
		}

	}
}
