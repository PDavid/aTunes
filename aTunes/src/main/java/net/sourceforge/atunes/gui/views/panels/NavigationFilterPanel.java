/*
 * aTunes 2.0.0
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
package net.sourceforge.atunes.gui.views.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Filter panel class for navigator: to use for trees and table
 */
public class NavigationFilterPanel extends JPanel {

    /**
	 * 
	 */
    private static final long serialVersionUID = 2037911468888688438L;

    /**
     * Filter text field
     */
    private JTextField filterTextField;

    /**
     * Button to clear filter
     */
    private JLabel clearFilterLabel;

    /**
     * List of listeners
     */
    List<NavigationFilterListener> listeners = new ArrayList<NavigationFilterListener>();

    /**
     * Default constructor
     */
    NavigationFilterPanel() {
        super(new GridBagLayout());

        // Filter controls
        JLabel filterLabel = new JLabel(I18nUtils.getString("FILTER"));
        filterTextField = new JTextField();
        filterTextField.setToolTipText(I18nUtils.getString("FILTER_TEXTFIELD_TOOLTIP"));
        clearFilterLabel = new JLabel(ImageLoader.getImage(ImageLoader.UNDO));
        clearFilterLabel.setToolTipText(I18nUtils.getString("CLEAR_FILTER_BUTTON_TOOLTIP"));

        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(2, 5, 0, 2);
        add(filterLabel, c);

        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 0.5;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(1, 1, 1, 2);
        add(filterTextField, c);

        c.gridx = 3;
        c.gridy = 0;
        c.weightx = 0.0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(1, 1, 1, 0);
        add(clearFilterLabel, c);

        // Add listeners
        filterTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(final KeyEvent e) {
                super.keyTyped(e);
                // Search as user type and clear filter when user pressed ESC
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        // Only clear filter if ESC pressed
                        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                            setFilter(null);
                            for (NavigationFilterListener listener : listeners) {
                                listener.filterChanged(null);
                            }
                        } else {
                            for (NavigationFilterListener listener : listeners) {
                                listener.filterChanged(getFilter());
                            }
                        }
                    }
                });
            }
        });

        clearFilterLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setFilter(null);
                for (NavigationFilterListener listener : listeners) {
                    listener.filterChanged(null);
                }
            }
        });
    }

    /**
     * Adds a listener
     * 
     * @param listener
     */
    public void addListener(NavigationFilterListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a listener
     * 
     * @param listener
     */
    public void removeListener(NavigationFilterListener listener) {
        listeners.remove(listener);
    }

    /**
     * @return the filterTextField
     */
    public JTextField getFilterTextField() {
        return filterTextField;
    }

    /**
     * Enables or disables visual components
     * 
     * @param enable
     */
    public void enableFilter(boolean enabled) {
        filterTextField.setEnabled(enabled);
        clearFilterLabel.setEnabled(enabled);
    }

    @Override
    public void requestFocus() {
        super.requestFocus();
        filterTextField.requestFocus();
    }

    /**
     * Returns filter value
     * 
     * @return
     */
    public String getFilter() {
        return filterTextField.getText() == null || filterTextField.getText().equals("") ? null : filterTextField.getText();
    }

    /**
     * Sets value of filter
     * 
     * @param filter
     */
    public void setFilter(String filter) {
        filterTextField.setText(filter);
    }

    /**
     * A listener of changes in filter
     * 
     * @author fleax
     * 
     */
    public interface NavigationFilterListener {
        public void filterChanged(String newFilter);
    }
}
