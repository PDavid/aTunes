/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.modules.context.artist;

import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTable;

import net.sourceforge.atunes.Constants;
import net.sourceforge.atunes.kernel.modules.context.ContextTableAction;
import net.sourceforge.atunes.kernel.modules.context.ContextTableRowPanel;
import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.model.ILookAndFeel;
import net.sourceforge.atunes.utils.DesktopUtils;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

class AlbumsTableCellRendererCode extends ContextTableRowPanel<IAlbumInfo> {
	
	public AlbumsTableCellRendererCode(ILookAndFeel lookAndFeel) {
		super(lookAndFeel);
	}

	@Override
    public JComponent getComponent(JComponent superComponent, JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return getPanelForTableRenderer(((IAlbumInfo) value).getCover(), 
        								StringUtils.getString("<html>", ((IAlbumInfo) value).getTitle(), "</html>"), 
        								superComponent.getBackground(),
        								superComponent.getForeground(), 
        								Constants.CONTEXT_IMAGE_WIDTH, 
        								Constants.CONTEXT_IMAGE_HEIGHT,
        								hasFocus);
    }
	
	@Override
	public List<ContextTableAction<IAlbumInfo>> getActions() {
		ContextTableAction<IAlbumInfo> action = new ContextTableAction<IAlbumInfo>(I18nUtils.getString("READ_MORE"), table) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 4367597772680455920L;

			@Override
			protected void execute(IAlbumInfo object) {
				DesktopUtils.openURL(object.getUrl());
			}
			
			@Override
			protected IAlbumInfo getSelectedObject(int row) {
				return  ((ContextAlbumsTableModel) table.getModel()).getAlbum(row);
			}
			
			@Override
			protected boolean isEnabledForObject(IAlbumInfo object) {
				return true;
			}
			
		};
		return Collections.singletonList(action);		
	}
}