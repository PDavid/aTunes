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

package net.sourceforge.atunes.gui.views.panels;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.gui.GuiUtils;
import net.sourceforge.atunes.gui.views.controls.PopUpButton;
import net.sourceforge.atunes.kernel.actions.ArrangePlayListColumnsAction;
import net.sourceforge.atunes.kernel.actions.CloseOtherPlaylistsAction;
import net.sourceforge.atunes.kernel.actions.ClosePlaylistAction;
import net.sourceforge.atunes.kernel.actions.CopyPlayListToDeviceAction;
import net.sourceforge.atunes.kernel.actions.NewPlayListAction;
import net.sourceforge.atunes.kernel.actions.RenamePlaylistAction;
import net.sourceforge.atunes.kernel.actions.SynchronizeDeviceWithPlayListAction;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IPlayListSelectorPanel;
import net.sourceforge.atunes.model.IPopUpButton;

public final class PlayListSelectorPanel extends JPanel implements IPlayListSelectorPanel {

	private static final long serialVersionUID = 7382098268271937439L;

	private PopUpButton options;
	
    private JComboBox playListCombo;

	private ILookAndFeelManager lookAndFeelManager;
    
    /**
     * Instantiates a new play list tab panel.
     */
    public PlayListSelectorPanel() {
        super();
		ComponentOrientation orientation = GuiUtils.getComponentOrientation();
		setLayout(new FlowLayout(orientation == ComponentOrientation.LEFT_TO_RIGHT ? FlowLayout.LEFT : FlowLayout.RIGHT, 0, 0));
    }
    
    /**
     * @param lookAndFeelManager
     */
    public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}

    /**
     * Adds the content.
     */
    public void initialize() {
    	options = new PopUpButton(PopUpButton.BOTTOM_RIGHT, lookAndFeelManager);
    	playListCombo = new JComboBox();

    	add(options);
        add(playListCombo);

        options.add(Context.getBean(NewPlayListAction.class));
        options.add(Context.getBean(RenamePlaylistAction.class));
        options.add(Context.getBean(ClosePlaylistAction.class));
        options.add(Context.getBean(CloseOtherPlaylistsAction.class));
        options.addSeparator();
        options.add(Context.getBean(ArrangePlayListColumnsAction.class));
        options.addSeparator();
        options.add(Context.getBean(CopyPlayListToDeviceAction.class));
        options.add(Context.getBean(SynchronizeDeviceWithPlayListAction.class));
    }

    /* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.panels.IPlayListSelectorPanel#getPlayListCombo()
	 */
	@Override
	public JComboBox getPlayListCombo() {
		return playListCombo;
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.atunes.gui.views.panels.IPlayListSelectorPanel#getOptions()
	 */
	@Override
	public IPopUpButton getOptions() {
		return options;
	}
	
	@Override
	public Component getSwingComponent() {
		return this;
	}
}
