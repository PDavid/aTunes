/*
 * aTunes 2.1.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.sourceforge.atunes.gui.views.controls.AbstractCustomModalDialog;
import net.sourceforge.atunes.gui.views.controls.CustomTextField;
import net.sourceforge.atunes.utils.GuiUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

import org.commonjukebox.plugins.PluginConfiguration;
import org.commonjukebox.plugins.PluginInfo;
import org.commonjukebox.plugins.PluginProperty;

public class PluginEditorDialog extends AbstractCustomModalDialog {

    /**
     * 
     */
    private static final long serialVersionUID = -2629422819919724654L;

    private PluginConfiguration configuration;

    public PluginEditorDialog(Window owner, PluginInfo plugin, PluginConfiguration configuration) {
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
        panel.add(new JScrollPane(configPanel), BorderLayout.CENTER);
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

    private static class PluginConfigurationPanel extends JPanel {

        private static final class TextFieldKeyAdapter extends KeyAdapter {
            private final JTextField textField;
            private final PluginProperty<?> property;

            private TextFieldKeyAdapter(JTextField textField, PluginProperty<?> property) {
                this.textField = textField;
                this.property = property;
            }

            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        property.setValue(textField.getText());
                    }
                });
            }
        }

        private static final long serialVersionUID = 8063088904049173181L;

        private PluginConfiguration configuration;

        public PluginConfigurationPanel(PluginConfiguration configuration) {
            super(new GridBagLayout());
            this.configuration = configuration;
            addContent();
        }

        private void addContent() {
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.weightx = 0.5;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.WEST;
            c.insets = new Insets(10, 10, 0, 10);
            for (String property : this.configuration.getPropertiesNames()) {
                PluginProperty<?> pluginProperty = this.configuration.getProperty(property);
                JLabel propertyLabel = getPropertyLabel(pluginProperty);
                JComponent propertyValue = getPropertyEditor(pluginProperty);

                // When last component is added set weighty = 1
                if (c.gridy == this.configuration.getPropertiesCount() - 1) {
                    c.weighty = 1;
                    c.anchor = GridBagConstraints.NORTHWEST;
                }

                c.gridx = 0;
                this.add(propertyLabel, c);
                c.gridx = 1;
                this.add(propertyValue, c);

                c.gridy++;
            }
        }

        private JLabel getPropertyLabel(PluginProperty<?> property) {
            return new JLabel(property.getDescription());
        }

        private JComponent getPropertyEditor(final PluginProperty<?> property) {
            final JTextField textField = new CustomTextField(property.getValue().toString());
            textField.addKeyListener(new TextFieldKeyAdapter(textField, property));
            return textField;
        }
    }

}
