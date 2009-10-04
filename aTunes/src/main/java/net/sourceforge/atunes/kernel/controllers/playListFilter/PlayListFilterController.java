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
package net.sourceforge.atunes.kernel.controllers.playListFilter;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import net.sourceforge.atunes.gui.views.panels.PlayListFilterPanel;
import net.sourceforge.atunes.kernel.controllers.model.SimpleController;
import net.sourceforge.atunes.kernel.modules.playlist.PlayListHandler;
import net.sourceforge.atunes.misc.log.LogCategories;

/**
 * The Class PlayListFilterController.
 */
public class PlayListFilterController extends SimpleController<PlayListFilterPanel> {

    /**
     * Instantiates a new play list filter controller.
     * 
     * @param panel
     *            the panel
     */
    public PlayListFilterController(PlayListFilterPanel panel) {
        super(panel);
        addBindings();
        addStateBindings();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sourceforge.atunes.kernel.controllers.model.Controller#addBindings()
     */
    @Override
    protected void addBindings() {
        getComponentControlled().getFilterTextField().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                String text = getComponentControlled().getFilterTextField().getText();
                if (text.equals("")) {
                    text = null;
                }
                PlayListHandler.getInstance().setFilter(text);
            }
        });
        getComponentControlled().getClearFilterButton().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                getComponentControlled().getFilterTextField().setText("");
                PlayListHandler.getInstance().setFilter(null);
            }
        });
    }

    @Override
    protected void addStateBindings() {
        // Nothing to do
    }

    /**
     * Empty filter.
     */
    public void emptyFilter() {
        getLogger().debug(LogCategories.CONTROLLER);

        getComponentControlled().getFilterTextField().setText("");
    }

    @Override
    protected void notifyReload() {
        // Nothing to do
    }

    /**
     * Reapply filter.
     */
    public void reapplyFilter() {
        getLogger().debug(LogCategories.CONTROLLER);

        if (PlayListHandler.getInstance().isFiltered()) {
            PlayListHandler.getInstance().setFilter(getComponentControlled().getFilterTextField().getText());
        }
    }
}
