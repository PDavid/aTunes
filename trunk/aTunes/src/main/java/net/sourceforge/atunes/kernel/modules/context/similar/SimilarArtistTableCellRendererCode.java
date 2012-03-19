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

package net.sourceforge.atunes.kernel.modules.context.similar;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTable;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.modules.context.ContextTable;
import net.sourceforge.atunes.kernel.modules.context.ContextTableAction;
import net.sourceforge.atunes.kernel.modules.context.ContextTableRowPanel;
import net.sourceforge.atunes.model.IArtistInfo;
import net.sourceforge.atunes.model.IDesktop;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

class SimilarArtistTableCellRendererCode extends ContextTableRowPanel<IArtistInfo> {

	private IDesktop desktop;
	
	/**
	 * @param lookAndFeel
	 * @param desktop
	 */
	public SimilarArtistTableCellRendererCode(ILookAndFeel lookAndFeel, IDesktop desktop) {
		super(lookAndFeel);
		this.desktop = desktop;
	}

	@Override
    public JComponent getComponent(JComponent superComponent, JTable t, IArtistInfo value, boolean isSelected, boolean hasFocus, int row, int column) {
        return getPanelForTableRenderer(value.getImage(), 
        							    StringUtils.getString("<html><br>", value.getName(), "<br>", value.getMatch(), "%<br>", value.isAvailable() ? I18nUtils.getString("AVAILABLE_IN_REPOSITORY") : "", "</html>"), 
        							    superComponent.getBackground(),
        							    superComponent.getForeground(),
        							    Constants.THUMB_IMAGE_WIDTH, 
        							    Constants.THUMB_IMAGE_HEIGHT,
        							    hasFocus);
    }

	@Override
	public List<ContextTableAction<IArtistInfo>> getActions() {
		List<ContextTableAction<IArtistInfo>> actions = new ArrayList<ContextTableAction<IArtistInfo>>();
		actions.add(new ReadMoreContextTableAction(I18nUtils.getString("READ_MORE"), (ContextTable) getTable(), desktop));
		actions.add(new AddAlbumArtistToPlayListContextTableAction(I18nUtils.getString("ADD_ALBUM_ARTIST_TO_PLAYLIST"),
				(ContextTable) getTable(), desktop));
		
		return actions;
	}
}    
