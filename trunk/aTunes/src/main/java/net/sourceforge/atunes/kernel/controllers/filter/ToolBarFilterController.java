/*
 * aTunes 2.0.0-SNAPSHOT
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
package net.sourceforge.atunes.kernel.controllers.filter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sourceforge.atunes.gui.views.panels.ToolBarFilterPanel;
import net.sourceforge.atunes.kernel.controllers.model.SimpleController;
import net.sourceforge.atunes.kernel.modules.filter.Filter;
import net.sourceforge.atunes.kernel.modules.filter.FilterHandler;

public class ToolBarFilterController extends SimpleController<ToolBarFilterPanel> {

    /**
     * Group of controls (filters)
     */
    private ButtonGroup group;

    /**
     * Filters and UI controls
     */
    private Map<String, JRadioButtonMenuItem> filters;

    public ToolBarFilterController(ToolBarFilterPanel panel) {
        super(panel);
        addBindings();
        group = new ButtonGroup();
        filters = new HashMap<String, JRadioButtonMenuItem>();
    }

    @Override
    protected void addBindings() {
        // Add listeners
        getComponentControlled().getFilterTextField().getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }

            private void update() {
                // Search as user type
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        applyFilter(getFilter());
                    }
                });
            }
        });

        getComponentControlled().getClearFilterButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Disable filter
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        getComponentControlled().getFilterTextField().setText(null);
                        applyFilter(getFilter());
                    }
                });
            }
        });
    }

    @Override
    protected void addStateBindings() {
        // TODO Auto-generated method stub
    }

    @Override
    protected void notifyReload() {
        // TODO Auto-generated method stub
    }

    /**
     * Adds a new filter to controls
     * 
     * @param name
     * @param filterListener
     */
    public void addFilter(final Filter filter) {
        JRadioButtonMenuItem radioButton = new JRadioButtonMenuItem(filter.getDescription());
        radioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Remove previous filter
                applyFilter(null);

                // Set selected filter and apply
                FilterHandler.getInstance().setSelectedFilter(filter.getName());
                applyFilter(getFilter());
            }
        });
        filters.put(filter.getName(), radioButton);
        group.add(radioButton);
        getComponentControlled().getFilterButton().add(radioButton);
    }

    /**
     * Removes a filter
     * 
     * @param name
     */
    public void removeFilter(String name) {
        JRadioButtonMenuItem radioButton = filters.get(name);
        filters.remove(name);
        group.remove(radioButton);
        getComponentControlled().getFilterButton().remove(radioButton);
    }

    /**
     * Sets filter selected
     * 
     * @param filterName
     */
    public void setSelectedFilter(String filterName) {
        this.filters.get(filterName).setSelected(true);
    }

    /**
     * Applies filter by calling FilterHandler
     * 
     * @param filter
     */
    private void applyFilter(String filter) {
        FilterHandler.getInstance().applyFilter(filter);

    }

    /**
     * Returns filter
     * 
     * @return
     */
    public String getFilter() {
        String filter = getComponentControlled().getFilterTextField().getText();
        return filter.trim().equals("") ? null : filter;
    }

    /**
     * Sets filter enabled
     * 
     * @param name
     * @param enabled
     */
    public void setFilterEnabled(String name, boolean enabled) {
        JRadioButtonMenuItem filter = this.filters.get(name);
        // Filter can be null if filters have not been added yet
        if (filter != null) {
            filter.setEnabled(enabled);
        }
    }

}
