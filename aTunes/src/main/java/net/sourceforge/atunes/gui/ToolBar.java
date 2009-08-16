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

package net.sourceforge.atunes.gui;

import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import net.sourceforge.atunes.kernel.actions.Actions;
import net.sourceforge.atunes.kernel.actions.CustomSearchAction;
import net.sourceforge.atunes.kernel.actions.EditPreferencesAction;
import net.sourceforge.atunes.kernel.actions.RefreshRepositoryAction;
import net.sourceforge.atunes.kernel.actions.RipCDAction;
import net.sourceforge.atunes.kernel.actions.SelectRepositoryAction;
import net.sourceforge.atunes.kernel.actions.ShowAudioObjectPropertiesAction;
import net.sourceforge.atunes.kernel.actions.ShowContextAction;
import net.sourceforge.atunes.kernel.actions.ShowNavigatorAction;
import net.sourceforge.atunes.kernel.actions.ShowStatsAction;

/**
 * The Class ToolBar.
 */
public class ToolBar extends JToolBar {

    private static final long serialVersionUID = 3146746580753998589L;

    /** The select repository. */
    private JButton selectRepository;

    /** The refresh repository. */
    private JButton refreshRepository;

    /** The preferences. */
    private JButton preferences;

    /** The show navigator. */
    private JToggleButton showNavigator;

    /** The show file properties. */
    private JToggleButton showFileProperties;

    /** The show audio scrobbler. */
    private JToggleButton showContext;

    /** The stats. */
    private JButton stats;

    /** The rip cd. */
    private JButton ripCD;

    /** The search. */
    private JButton search;

    /**
     * Instantiates a new tool bar.
     */
    public ToolBar() {
        super();
        setFloatable(false);
        setButtons();
    }

    /**
     * Sets the buttons.
     */
    private void setButtons() {
        selectRepository = new JButton(Actions.getAction(SelectRepositoryAction.class));
        selectRepository.setText(null);
        add(selectRepository);

        refreshRepository = new JButton(Actions.getAction(RefreshRepositoryAction.class));
        refreshRepository.setText(null);
        add(refreshRepository);

        addSeparator();

        preferences = new JButton(Actions.getAction(EditPreferencesAction.class));
        preferences.setText(null);
        add(preferences);

        addSeparator();

        showNavigator = new JToggleButton(Actions.getAction(ShowNavigatorAction.class));
        showNavigator.setText(null);
        add(showNavigator);

        showFileProperties = new JToggleButton(Actions.getAction(ShowAudioObjectPropertiesAction.class));
        showFileProperties.setText(null);
        add(showFileProperties);

        showContext = new JToggleButton(Actions.getAction(ShowContextAction.class));
        showContext.setText(null);
        add(showContext);

        addSeparator();

        stats = new JButton(Actions.getAction(ShowStatsAction.class));
        stats.setText(null);
        add(stats);

        addSeparator();

        ripCD = new JButton(Actions.getAction(RipCDAction.class));
        ripCD.setText(null);
        add(ripCD);

        addSeparator();

        search = new JButton(Actions.getAction(CustomSearchAction.class));
        search.setText(null);
        add(search);

    }

    /**
     * @return the showNavigator
     */
    JToggleButton getShowNavigator() {
        return showNavigator;
    }

    /**
     * @return the showFileProperties
     */
    JToggleButton getShowFileProperties() {
        return showFileProperties;
    }

    /**
     * @return the showContext
     */
    JToggleButton getShowContext() {
        return showContext;
    }

}
