/*
 * aTunes 2.0.0-SNAPSHOT
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
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

import javax.swing.ImageIcon;
import javax.swing.JButton;
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

import net.sourceforge.atunes.gui.Fonts;
import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.gui.views.controls.CustomModalDialog;
import net.sourceforge.atunes.gui.views.controls.UrlLabel;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.plugins.PluginsHandler;
import net.sourceforge.atunes.kernel.modules.state.ApplicationState;
import net.sourceforge.atunes.misc.log.LogCategories;
import net.sourceforge.atunes.misc.log.Logger;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.ImageUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.commonjukebox.plugins.PluginConfiguration;
import org.commonjukebox.plugins.PluginInfo;
import org.commonjukebox.plugins.PluginProperty;
import org.commonjukebox.plugins.PluginSystemException;
import org.jvnet.substance.api.renderers.SubstanceDefaultTableCellRenderer;

public final class PluginsPanel extends PreferencesPanel {

    private static final long serialVersionUID = 8611795969151425262L;

    JTable pluginsTable;

    JButton pluginPreferencesButton;

    JButton uninstallPluginButton;

    private static final int CELL_HEIGHT = 30;

    Logger logger = new Logger();

    /**
     * Plugins modified with their modified configuration
     */
    Map<PluginInfo, PluginConfiguration> pluginsModified;

    /**
     * Plugins activated or deactivated
     */
    Map<PluginInfo, Boolean> pluginsActivation;

    /**
     * Instantiates a new plugins panel.
     */
    public PluginsPanel() {
        super(I18nUtils.getString("PLUGINS"));
        pluginsTable = new JTable();
        pluginsTable.setRowHeight(CELL_HEIGHT);
        pluginsTable.setShowGrid(false);
        pluginsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pluginsTable.setColumnModel(new DefaultTableColumnModel() {
            private static final long serialVersionUID = -4128210690358582389L;

            @Override
            public void addColumn(TableColumn column) {
                super.addColumn(column);
                if (column.getHeaderValue().equals(I18nUtils.getString("ACTIVE"))) {
                    column.setMinWidth(80);
                    column.setMaxWidth(100);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(pluginsTable);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 0.6;
        c.fill = GridBagConstraints.BOTH;
        c.gridheight = 3;

        add(scrollPane, c);

        JPanel pluginDetailPanel = new JPanel(new GridLayout(6, 1));
        final JLabel pluginNameLabel = new JLabel();
        pluginNameLabel.setFont(Fonts.SMALL_FONT);
        final JLabel pluginVersionLabel = new JLabel();
        pluginVersionLabel.setFont(Fonts.SMALL_FONT);
        final JLabel pluginClassNameLabel = new JLabel();
        pluginClassNameLabel.setFont(Fonts.SMALL_FONT);
        final JLabel pluginLocationLabel = new JLabel();
        pluginLocationLabel.setFont(Fonts.SMALL_FONT);
        final JLabel pluginAuthorLabel = new JLabel();
        pluginAuthorLabel.setFont(Fonts.SMALL_FONT);
        final UrlLabel pluginUrlLabel = new UrlLabel();
        pluginUrlLabel.setFont(Fonts.SMALL_FONT);
        pluginDetailPanel.add(pluginNameLabel);
        pluginDetailPanel.add(pluginVersionLabel);
        pluginDetailPanel.add(pluginClassNameLabel);
        pluginDetailPanel.add(pluginLocationLabel);
        pluginDetailPanel.add(pluginAuthorLabel);
        pluginDetailPanel.add(pluginUrlLabel);

        c.gridy = 3;
        c.insets = new Insets(10, 5, 10, 5);
        c.gridheight = 1;
        c.weighty = 0.4;
        c.gridwidth = 2;
        add(pluginDetailPanel, c);

        pluginsTable.setDefaultRenderer(PluginInfo.class, new SubstanceDefaultTableCellRenderer() {

            private static final long serialVersionUID = -9147687375378503942L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, ((PluginInfo) value).getName(), isSelected, hasFocus, row, column);
                if (((PluginInfo) value).getIcon() != null) {
                    ((JLabel) c).setIcon(ImageUtils.scaleImageBicubic(((PluginInfo) value).getIcon(), CELL_HEIGHT - 5, CELL_HEIGHT - 5));
                } else {
                    ((JLabel) c).setIcon(ImageUtils.scaleImageBicubic(ImageLoader.getImage(ImageLoader.EMPTY).getImage(), CELL_HEIGHT - 5, CELL_HEIGHT - 5));
                }
                return c;
            }

        });

        pluginsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // Enable preferences button if plugin has any configuration and update detail panel
                if (pluginsTable.getSelectedRow() != -1) {
                    PluginInfo plugin = ((PluginsTableModel) pluginsTable.getModel()).getPluginAt(pluginsTable.getSelectedRow());
                    pluginNameLabel.setText(StringUtils.getString("<html><b>", I18nUtils.getString("NAME"), ":</b> ", plugin.getName(), "</html>"));
                    pluginVersionLabel.setText(StringUtils.getString("<html><b>", I18nUtils.getString("VERSION"), ":</b> ", plugin.getVersion(), "</html>"));
                    pluginClassNameLabel.setText(StringUtils.getString("<html><b>", I18nUtils.getString("CLASS_NAME"), ":</b> ", plugin.getClassName(), "</html>"));
                    pluginLocationLabel.setText(StringUtils.getString("<html><b>", I18nUtils.getString("LOCATION"), ":</b> ", plugin.getPluginLocation(), "</html>"));
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
        add(pluginPreferencesButton, c);

        pluginPreferencesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = pluginsTable.getSelectedRow();
                PluginInfo plugin = ((PluginsTableModel) pluginsTable.getModel()).getPluginAt(row);
                PluginConfiguration configuration = ((PluginsTableModel) pluginsTable.getModel()).getPluginConfigurationAt(row);
                PluginEditorDialog dialog = new PluginEditorDialog(GuiHandler.getInstance().getEditPreferencesDialog(), plugin, configuration);
                dialog.setVisible(true);
                configuration = dialog.getConfiguration();
                if (configuration != null) {
                    pluginsModified.put(plugin, configuration);
                }
            }
        });

        JButton installNewPluginButton = new JButton(StringUtils.getString(I18nUtils.getString("INSTALL"), "..."));
        c.gridy = 1;
        add(installNewPluginButton, c);

        installNewPluginButton.addActionListener(new ActionListener() {
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
                if (fileChooser.showOpenDialog(GuiHandler.getInstance().getFrame().getFrame()) == JFileChooser.APPROVE_OPTION) {
                    File zipFile = fileChooser.getSelectedFile();
                    try {
                        PluginsHandler.getInstance().installPlugin(zipFile);
                        // Update panel after installing a new plugin
                        updatePanel(null);
                    } catch (Exception e1) {
                        GuiHandler.getInstance().showErrorDialog(e1.getMessage());
                        logger.error(LogCategories.PLUGINS, e1);
                    }
                }
            }
        });

        uninstallPluginButton = new JButton(I18nUtils.getString("UNINSTALL"));
        uninstallPluginButton.setEnabled(false);
        c.gridy = 2;
        add(uninstallPluginButton, c);

        uninstallPluginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = pluginsTable.getSelectedRow();
                PluginInfo plugin = ((PluginsTableModel) pluginsTable.getModel()).getPluginAt(row);
                try {
                    PluginsHandler.getInstance().uninstallPlugin(plugin);
                    // Update panel after uninstalling a plugin
                    updatePanel(null);
                } catch (Exception e1) {
                    GuiHandler.getInstance().showErrorDialog(e1.getMessage());
                    logger.error(LogCategories.PLUGINS, e1);
                }
            }
        });
    }

    @Override
    public boolean applyPreferences(ApplicationState state) {
        boolean restartNeeded = false;
        try {
            // if any plugin has been modified then write configuration
            for (PluginInfo plugin : pluginsModified.keySet()) {
                logger.debug(LogCategories.PLUGINS, "Writting configuration of plugin: ", plugin.getName());
                PluginConfiguration.setConfiguration(plugin, pluginsModified.get(plugin));

                restartNeeded = restartNeeded || PluginsHandler.getInstance().pluginNeedsRestart(plugin);
            }
            // If any plugin has been activated or deactivated then apply changes
            for (PluginInfo plugin : pluginsActivation.keySet()) {
                if (pluginsActivation.get(plugin)) {
                    plugin.activate();
                } else {
                    plugin.deactivate();
                }

                restartNeeded = restartNeeded || PluginsHandler.getInstance().pluginNeedsRestart(plugin);
            }
        } catch (PluginSystemException e) {
            logger.error(LogCategories.PLUGINS, e);
            if (e.getCause() != null) {
                logger.error(LogCategories.PLUGINS, e.getCause());
            }
        }
        return restartNeeded;
    }

    @Override
    public void updatePanel(ApplicationState state) {
        List<PluginInfo> plugins = PluginsHandler.getInstance().getAvailablePlugins();
        pluginsTable.setModel(new PluginsTableModel(plugins));
        // Select first plugin
        if (!plugins.isEmpty()) {
            pluginsTable.getSelectionModel().setSelectionInterval(0, 0);
        }
    }

    @Override
    public void resetImmediateChanges(ApplicationState state) {
        // Do nothing
    }

    @Override
    public boolean validatePanel() {
        return true;
    }

    @Override
    public void dialogVisibilityChanged(boolean visible) {
        if (visible) {
            // Initialize plugins modified 
            pluginsModified = new HashMap<PluginInfo, PluginConfiguration>();
            pluginsActivation = new HashMap<PluginInfo, Boolean>();
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
                return PluginConfiguration.getConfiguration(this.plugins.get(row));
            } catch (PluginSystemException e) {
                logger.error(LogCategories.PLUGINS, e);
                return null;
            }
        }

        public PluginInfo getPluginAt(int row) {
            return this.plugins.get(row);
        }

    }

    private class PluginConfigurationPanel extends JPanel {

        private static final long serialVersionUID = 8063088904049173181L;

        private PluginConfiguration configuration;

        public PluginConfigurationPanel(PluginConfiguration configuration) {
            super(new GridBagLayout());
            this.configuration = configuration;
            addContent();
        }

        private void addContent() {
            JTable table = new JTable();
            table.setColumnModel(new DefaultTableColumnModel() {

                private static final long serialVersionUID = -2883977670543989394L;

                @Override
                public void addColumn(TableColumn column) {
                    super.addColumn(column);
                    if (column.getModelIndex() == 0) {
                        column.setMaxWidth(200);
                    }
                }
            });
            table.setModel(new PluginConfigurationTableModel(configuration));

            table.setDefaultRenderer(PluginProperty.class, new SubstanceDefaultTableCellRenderer() {
                private static final long serialVersionUID = -3718138141911631700L;

                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(table, ((PluginProperty<?>) value).getName(), isSelected, hasFocus, row, column);
                    ((JLabel) c).setToolTipText(((PluginProperty<?>) value).getDescription());
                    return c;
                }

            });

            JScrollPane scrollPane = new JScrollPane(table);
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.weightx = 1;
            c.weighty = 1;
            c.fill = GridBagConstraints.BOTH;
            c.insets = new Insets(5, 5, 5, 5);
            add(scrollPane, c);
        }

    }

    private class PluginConfigurationTableModel implements TableModel {

        private PluginConfiguration configuration;

        private List<String> propertiesNames;

        private List<TableModelListener> listeners = new ArrayList<TableModelListener>();

        PluginConfigurationTableModel(PluginConfiguration configuration) {
            this.configuration = configuration;
            this.propertiesNames = configuration.getPropertiesNames();
        }

        @Override
        public void addTableModelListener(TableModelListener l) {
            listeners.add(l);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) {
                return PluginProperty.class;
            }
            return String.class;
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public String getColumnName(int columnIndex) {
            if (columnIndex == 0) {
                return I18nUtils.getString("NAME");
            }
            return I18nUtils.getString("VALUE");
        }

        @Override
        public int getRowCount() {
            return this.configuration.getPropertiesCount();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return this.configuration.getProperty(this.propertiesNames.get(rowIndex));
            }
            return this.configuration.getProperty(this.propertiesNames.get(rowIndex)).getValue();
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex != 0;
        }

        @Override
        public void removeTableModelListener(TableModelListener l) {
            listeners.remove(l);
        }

        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            this.configuration.getProperty(this.propertiesNames.get(rowIndex)).setValue(value);
        }

    }

    private class PluginEditorDialog extends CustomModalDialog {

        /**
         * 
         */
        private static final long serialVersionUID = -2629422819919724654L;

        PluginConfiguration configuration;

        PluginEditorDialog(EditPreferencesDialog owner, PluginInfo plugin, PluginConfiguration configuration) {
            super(owner, GuiUtils.getComponentWidthForResolution(1280, 500), GuiUtils.getComponentHeightForResolution(1024, 300), true);
            this.configuration = configuration;
            setResizable(true);
            setTitle(StringUtils.getString(I18nUtils.getString("PLUGIN_PROPERTIES_EDITOR"), ": ", plugin.getName()));
            add(getContent());
            GuiUtils.applyComponentOrientation(this);
            enableCloseActionWithEscapeKey();
        }

        private JPanel getContent() {
            JPanel panel = new JPanel(new BorderLayout());
            PluginConfigurationPanel configPanel = new PluginConfigurationPanel(configuration);
            panel.add(configPanel, BorderLayout.CENTER);
            JButton okButton = new JButton(I18nUtils.getString("OK"));
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    PluginEditorDialog.this.setVisible(false);
                }
            });
            JButton cancelButton = new JButton(I18nUtils.getString("CANCEL"));
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // User canceled edition so set configuration to null
                    PluginEditorDialog.this.configuration = null;
                    PluginEditorDialog.this.setVisible(false);
                }
            });
            JPanel buttonsPanel = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.weightx = 1;
            c.anchor = GridBagConstraints.EAST;
            c.insets = new Insets(10, 5, 10, 5);
            buttonsPanel.add(okButton, c);
            c.gridx = 1;
            c.weightx = 0;
            c.anchor = GridBagConstraints.CENTER;
            buttonsPanel.add(cancelButton, c);
            panel.add(buttonsPanel, BorderLayout.SOUTH);

            return panel;
        }

        /**
         * @return the configuration
         */
        public PluginConfiguration getConfiguration() {
            return configuration;
        }
    }

    @Override
    public ImageIcon getIcon() {
        return ImageLoader.getImage(ImageLoader.PLUGIN);
    }

}
