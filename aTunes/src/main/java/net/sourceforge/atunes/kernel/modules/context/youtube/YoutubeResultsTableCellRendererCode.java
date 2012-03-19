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

package net.sourceforge.atunes.kernel.modules.context.youtube;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTable;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.kernel.modules.context.ContextTableAction;
import net.sourceforge.atunes.kernel.modules.context.ContextTableRowPanel;
import net.sourceforge.atunes.kernel.modules.webservices.youtube.YoutubeResultEntry;
import net.sourceforge.atunes.model.IDesktop;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.model.IPlayerHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

public class YoutubeResultsTableCellRendererCode extends ContextTableRowPanel<YoutubeResultEntry> {
	
	private IDesktop desktop;
	
	public YoutubeResultsTableCellRendererCode(ILookAndFeel lookAndFeel, IDesktop desktop) {
		super(lookAndFeel);
		this.desktop = desktop;
	}

	@Override
    public JComponent getComponent(JComponent superComponent, JTable t, YoutubeResultEntry value, boolean isSelected, boolean hasFocus, int row, int column) {
        return getPanelForTableRenderer(value.getImage(), 
        								StringUtils.getString("<html>", value.getName(), "<br>(", value.getDuration(), ")</html>"), 
        								superComponent.getBackground(), 
        								superComponent.getForeground(), 
        								Constants.THUMB_IMAGE_WIDTH, 
        								Constants.THUMB_IMAGE_HEIGHT, 
        								hasFocus);
    }
	
	@Override
	public List<ContextTableAction<YoutubeResultEntry>> getActions() {
		List<ContextTableAction<YoutubeResultEntry>> actions = new ArrayList<ContextTableAction<YoutubeResultEntry>>();
		actions.add(new ContextTableAction<YoutubeResultEntry>(I18nUtils.getString("PLAY_VIDEO_AT_YOUTUBE"), getTable(), desktop) {

			/**
			 * 
			 */
			private static final long serialVersionUID = -7758596564970276630L;

			@Override
			protected void execute(YoutubeResultEntry entry) {
			     //open youtube url
				desktop.openURL(entry.getUrl());
                // When playing a video in web browser automatically pause current song
                if (Context.getBean(IPlayerHandler.class).isEnginePlaying()) {
                    Context.getBean(IPlayerHandler.class).playCurrentAudioObject(true);
                }
			}
			
			@Override
			protected YoutubeResultEntry getSelectedObject(int row) {
				return ((YoutubeResultTableModel) getTable().getModel()).getEntry(row);
			}
			
			@Override
			protected boolean isEnabledForObject(YoutubeResultEntry object) {
				return true;
			}
		});
		
		// DOWNLOAD NOT WORKING AS API HAS CHANGED AND MP4 FILES ARE NOT AVAILABLE
		// SEE BUG 3405858

		return actions;
	}
}
