/*
 * aTunes 2.2.0-SNAPSHOT
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

package net.sourceforge.atunes.gui.views.dialogs.editPreferences;

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
import javax.swing.JComponent;
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
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.AbstractTableCellRendererCode;
import net.sourceforge.atunes.gui.views.controls.UrlLabel;
import net.sourceforge.atunes.gui.views.dialogs.PluginEditorDialog;
import net.sourceforge.atunes.model.IDesktop;
import net.sourceforge.atunes.model.IErrorDialogFactory;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.IPluginsHandler;
import net.sourceforge.atunes.model.IState;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.Logger;
import net.sourceforge.atunes.utils.StringUtils;

import org.commonjukebox.plugins.exceptions.InvalidPluginConfigurationException;
import org.commonjukebox.plugins.exceptions.PluginSystemException;
import org.commonjukebox.plugins.model.PluginConfiguration;
import org.commonjukebox.plugins.model.PluginFolder;
import org.commonjukebox.plugins.model.PluginInfo;

public final class PluginsPanel extends AbstractPreferencesPanel {

	private static final long serialVersionUID = 8611795969151425262L;

	private JPanel mainPanel;

	private JCheckBox enabledPluginBox;

	private JTable pluginsTable;

	private JButton pluginPreferencesButton;

	private JButton uninstallPluginButton;

	private static final int CELL_HEIGHT = 30;

	/**
	 * Plugins modified with their modified configuration
	 */
	private Map<PluginInfo, PluginConfiguration> pluginsModified;

	/**
	 * Plugins activated or deactivated
	 */
	private Map<PluginInfo, Boolean> pluginsActivation;

	/**
	 * Preferences dialog
	 */
	private EditPreferencesDialog dialog;
	
	private IState state;
	
	private IPluginsHandler pluginsHandler;

	/**
	 * Instantiates a new plugins panel.
	 * @param dialog
	 * @param frame
	 * @param lookAndFeel
	 * @param state
	 * @param pluginsHandler
	 * @param desktop 
	 */
	public PluginsPanel(EditPreferencesDialog dialog, final IFrame frame, final ILookAndFeel lookAndFeel, IState state, IPluginsHandler pluginsHandler, IDesktop desktop) {
		super(I18nUtils.getString("PLUGINS"));
		this.state = state;
		this.dialog = dialog;
		this.pluginsHandler = pluginsHandler;

		enabledPluginBox = new JCheckBox(I18nUtils.getString("ENABLED_PLUGINS"));

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = state.isPluginsEnabled() ? 0 : 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;

		add(enabledPluginBox,c);

		mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setVisible(state.isPluginsEnabled());
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		add(mainPanel,c);

		pluginsTable = lookAndFeel.getTable();
		pluginsTable.setRowHeight(CELL_HEIGHT);
		pluginsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		pluginsTable.setColumnModel(new PluginsTableDefaultTableColumnModel());

		JScrollPane scrollPane = lookAndFeel.getTableScrollPane(pluginsTable);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 3;

		mainPanel.add(scrollPane, c);

		JPanel pluginDetailPanel = new JPanel(new GridBagLayout());
		final JLabel pluginNameLabel = new JLabel();
		final JLabel pluginVersionLabel = new JLabel();
		final JLabel pluginClassNameLabel = new JLabel();
		final JLabel pluginLocationLabel = new JLabel();
		final JLabel pluginAuthorLabel = new JLabel();
		final UrlLabel pluginUrlLabel = new UrlLabel(desktop);

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

		c.gridy = 3;
		c.insets = new Insets(10, 5, 10, 5);
		c.gridheight = 1;
		c.gridwidth = 2;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(pluginDetailPanel, c);

		pluginsTable.setDefaultRenderer(PluginInfo.class, lookAndFeel.getTableCellRenderer(new PluginsTableCellRendererCode(lookAndFeel)));

		pluginsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// Enable preferences button if plugin has any configuration and update detail panel
				if (pluginsTable.getSelectedRow() != -1) {
					PluginInfo plugin = ((PluginsTableModel) pluginsTable.getModel()).getPluginAt(pluginsTable.getSelectedRow());
					pluginNameLabel.setText(StringUtils.getString("<html><b>", I18nUtils.getString("NAME"), ":</b> ", plugin.getName(), "</html>"));
					pluginVersionLabel.setText(StringUtils.getString("<html><b>", I18nUtils.getString("VERSION"), ":</b> ", plugin.getVersion(), "</html>"));
					pluginClassNameLabel.setText(StringUtils.getString("<html><b>", I18nUtils.getString("CLASS_NAME"), ":</b> ", plugin.getClassName(), "</html>"));
					pluginLocationLabel.setText(StringUtils.getString("<html><b>", I18nUtils.getString("LOCATION"), ":</b> ", plugin.getPluginFolder().getName(), "</html>"));
					pluginAuthorLabel.setText(StringUtils.getString("<html><b>", I18nUtils.getString("AUTHOR"), ":</b> ", plugin.getAuthor(), "</html>"));
					pluginUrlLabel.setText(plugin.getUrl(), plugin.getUrl());
					pluginPreferencesButton.setEnabled(((PluginsTableModel) pluginsTable.getModel()).getPluginConfigurationAt(pluginsTable.getSelectedRow()) != null);
					uninstallPluginButton.setEnabled(pluginsTable.getSelectedRow() != -1);
				}
			}
		});

		pluginPreferencesButton = new JButton(StringUtils.getString(I18nUtils.getString("PREFERENCES"), "..."));
		pluginPreferencesButton.setEnabled(false);
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(10, 10, 0, 0);
		c.gridwidth = 1;
		mainPanel.add(pluginPreferencesButton, c);

		pluginPreferencesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int row = pluginsTable.getSelectedRow();
				PluginInfo plugin = ((PluginsTableModel) pluginsTable.getModel()).getPluginAt(row);
				PluginConfiguration configuration = ((PluginsTableModel) pluginsTable.getModel()).getPluginConfigurationAt(row);
				PluginEditorDialog dialog = new PluginEditorDialog(PluginsPanel.this.dialog, plugin, configuration, lookAndFeel);
				dialog.setVisible(true);
				configuration = dialog.getConfiguration();
				if (configuration != null) {
					// Validate plugin configuration
					try {
						PluginsPanel.this.pluginsHandler.validateConfiguration(plugin, configuration);
						pluginsModified.put(plugin, configuration);
					} catch (InvalidPluginConfigurationException ex) {
						Context.getBean(IErrorDialogFactory.class).getDialog().showErrorDialog(frame, StringUtils.getString(I18nUtils.getString("PLUGIN_CONFIGURATION_INVALID"), ex.getMessage()));
					}
				}
			}
		});

		JButton installNewPluginButton = new JButton(StringUtils.getString(I18nUtils.getString("INSTALL"), "..."));
		c.gridy = 1;
		mainPanel.add(installNewPluginButton, c);

		installNewPluginButton.addActionListener(new InstallNewPluginActionListener());

		uninstallPluginButton = new JButton(I18nUtils.getString("UNINSTALL"));
		uninstallPluginButton.setEnabled(false);
		c.gridy = 2;
		mainPanel.add(uninstallPluginButton, c);

		uninstallPluginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int row = pluginsTable.getSelectedRow();
				PluginInfo plugin = ((PluginsTableModel) pluginsTable.getModel()).getPluginAt(row);
				try {
					Map<PluginFolder, PluginSystemException> problemsFound = PluginsPanel.this.pluginsHandler.uninstallPlugin(plugin);
					if (problemsFound != null) {
						for (Map.Entry<PluginFolder, PluginSystemException> pluginFolderEntry : problemsFound.entrySet()) {
							Context.getBean(IErrorDialogFactory.class).getDialog().showExceptionDialog(I18nUtils.getString("PLUGIN_UNINSTALLATION_ERROR"), pluginFolderEntry.getValue());
						}
					}

					// Update panel after uninstalling a plugin
					updatePanel(null);
				} catch (Exception e1) {
					Context.getBean(IErrorDialogFactory.class).getDialog().showErrorDialog(frame, e1.getMessage());
					Logger.error(e1);
				}
			}
		});
	}

	@Override
	public boolean applyPreferences(IState state) {
		boolean restartNeeded = false;
		if (state.isPluginsEnabled() != enabledPluginBox.isSelected()){
			state.setPluginsEnabled(enabledPluginBox.isSelected());
			restartNeeded = true;
		}

		if (state.isPluginsEnabled()) {
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
						Context.getBean(IErrorDialogFactory.class).getDialog().showExceptionDialog(sb.toString(), t);
					}

					restartNeeded = restartNeeded || pluginsHandler.pluginNeedsRestart(plugin);
				}
				// If any plugin has been activated or deactivated then apply changes
				for (PluginInfo plugin : pluginsActivation.keySet()) {
					if (pluginsActivation.get(plugin)) {
						pluginsHandler.activatePlugin(plugin);
					} else {
						pluginsHandler.deactivatePlugin(plugin);
					}

					restartNeeded = restartNeeded || pluginsHandler.pluginNeedsRestart(plugin);
				}
			} catch (PluginSystemException e) {
				Logger.error(e);
				if (e.getCause() != null) {
					Logger.error(e.getCause());
				}
			}
		}
		return restartNeeded;
	}

	/**
	 * Sets the plugins availlability
	 * 
	 * @param enabled
	 */
	private void setPluginsEnabled(boolean enabled) {
		enabledPluginBox.setSelected(enabled);
	}

	@Override
	public void updatePanel(IState state) {
		if (state != null) {
			setPluginsEnabled(state.isPluginsEnabled());
			if (state.isPluginsEnabled()){
				List<PluginInfo> plugins = pluginsHandler.getAvailablePlugins();
				pluginsTable.setModel(new PluginsTableModel(plugins));
			}
		}
	}

	@Override
	public void resetImmediateChanges(IState state) {
		// Do nothing
	}

	@Override
	public void validatePanel() throws PreferencesValidationException {
	}

	@Override
	public void dialogVisibilityChanged(boolean visible) {
		if (visible) {
			// Initialize plugins modified 
			pluginsModified = new HashMap<PluginInfo, PluginConfiguration>();
			pluginsActivation = new HashMap<PluginInfo, Boolean>();
			// Select first plugin
			if (state.isPluginsEnabled()) {
				if (!pluginsHandler.getAvailablePlugins().isEmpty()) {
					pluginsTable.getSelectionModel().setSelectionInterval(0, 0);
				}
			}
		}
	}

	private final class InstallNewPluginActionListener implements
	ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			FileFilter filter = new FileFilter() {
				@Override
				public boolean accept(File f) {
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
							Context.getBean(IErrorDialogFactory.class).getDialog().showExceptionDialog(I18nUtils.getString("PLUGIN_INSTALLATION_ERROR"), pluginFolderEntry.getValue());
						}
					}

					// Update panel after installing a new plugin
					updatePanel(null);
				} catch (Exception e1) {
					Context.getBean(IErrorDialogFactory.class).getDialog().showExceptionDialog(I18nUtils.getString("PLUGIN_INSTALLATION_ERROR"), e1);
				}
			}
		}
	}

	private static class PluginsTableCellRendererCode extends AbstractTableCellRendererCode<JLabel, PluginInfo> {
		public PluginsTableCellRendererCode(ILookAndFeel lookAndFeel) {
			super(lookAndFeel);
		}

		@Override
		public JComponent getComponent(JLabel c, JTable table, PluginInfo value, boolean isSelected, boolean hasFocus, int row, int column) {
			c.setText(value.getName());
			if (value.getIcon() != null) {
				c.setIcon(ImageUtils.scaleImageBicubic(value.getIcon(), CELL_HEIGHT - 5, CELL_HEIGHT - 5));
			} else {
				c.setIcon(null);
			}
			return c;
		}
	}

	private static class PluginsTableDefaultTableColumnModel extends DefaultTableColumnModel {
		private static final long serialVersionUID = -4128210690358582389L;

		@Override
		public void addColumn(TableColumn column) {
			super.addColumn(column);
			if (column.getHeaderValue().equals(I18nUtils.getString("ACTIVE"))) {
				column.setMinWidth(80);
				column.setMaxWidth(100);
			}
		}
	}

	private class PluginsTableModel implements TableModel {

		private List<PluginInfo> plugins;

		private List<TableModelListener> listeners = new ArrayList<TableModelListener>();

		public PluginsTableModel(List<PluginInfo> plugins) {
			super();
			this.plugins = plugins;
			// Sort plugins by name
			Collections.sort(this.plugins, new Comparator<PluginInfo>() {
				@Override
				public int compare(PluginInfo o1, PluginInfo o2) {
					return o1.getName().compareToIgnoreCase(o2.getName());
				}
			});
		}

		@Override
		public void addTableModelListener(TableModelListener l) {
			listeners.add(l);
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
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
		public String getColumnName(int columnIndex) {
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
		public Object getValueAt(int rowIndex, int columnIndex) {
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
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == 0;
		}

		@Override
		public void removeTableModelListener(TableModelListener l) {
			listeners.remove(l);
		}

		@Override
		public void setValueAt(Object value, int rowIndex, int columnIndex) {
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

		public PluginConfiguration getPluginConfigurationAt(int row) {
			try {
				return pluginsHandler.getConfiguration(this.plugins.get(row));
			} catch (PluginSystemException e) {
				Logger.error(e);
				return null;
			}
		}

		public PluginInfo getPluginAt(int row) {
			return this.plugins.get(row);
		}

	}


}
