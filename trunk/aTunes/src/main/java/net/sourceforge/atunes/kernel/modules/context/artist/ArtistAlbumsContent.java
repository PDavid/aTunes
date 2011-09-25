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

import java.awt.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanelContent;
import net.sourceforge.atunes.kernel.modules.context.ContextTable;
import net.sourceforge.atunes.model.IAlbumInfo;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Albums of an artist
 * 
 * @author alex
 * 
 */
public class ArtistAlbumsContent extends AbstractContextPanelContent {

    private static final long serialVersionUID = -5538266144953409867L;
    private ContextTable albumsTable;

    @Override
    protected String getContentName() {
        return I18nUtils.getString("ALBUMS");
    }

    @Override
    protected Map<String, ?> getDataSourceParameters(IAudioObject audioObject) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(ArtistInfoDataSource.INPUT_AUDIO_OBJECT, audioObject);
        parameters.put(ArtistInfoDataSource.INPUT_ALBUMS, true);
        return parameters;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void updateContentWithDataSourceResult(Map<String, ?> result) {
        if (result.containsKey(ArtistInfoDataSource.OUTPUT_ALBUMS)) {
            albumsTable.setModel(new ContextAlbumsTableModel((List<IAlbumInfo>) result.get(ArtistInfoDataSource.OUTPUT_ALBUMS)));
        }
    }

    @Override
    protected void clearContextPanelContent() {
        super.clearContextPanelContent();
        albumsTable.setModel(new ContextAlbumsTableModel(null));
    }

    @Override
    protected Component getComponent() {
        // Create components
        albumsTable = new ContextTable(getLookAndFeelManager().getCurrentLookAndFeel());
        albumsTable.addContextRowPanel(new AlbumsTableCellRendererCode(getLookAndFeelManager().getCurrentLookAndFeel()));
        return albumsTable;
    }

}
