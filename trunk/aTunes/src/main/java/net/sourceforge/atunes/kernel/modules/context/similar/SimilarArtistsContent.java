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

package net.sourceforge.atunes.kernel.modules.context.similar;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.atunes.kernel.modules.context.AbstractContextPanelContent;
import net.sourceforge.atunes.kernel.modules.context.ContextTable;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ISimilarArtistsInfo;
import net.sourceforge.atunes.utils.I18nUtils;

public class SimilarArtistsContent extends AbstractContextPanelContent {

    private static final long serialVersionUID = 5041098100868186051L;
    private ContextTable similarArtistsTable;

    @Override
    public String getContentName() {
        return I18nUtils.getString("SIMILAR");
    }

    @Override
    public Map<String, ?> getDataSourceParameters(IAudioObject audioObject) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(SimilarArtistsDataSource.INPUT_AUDIO_OBJECT, audioObject);
        return parameters;
    }

    @Override
    public void updateContentWithDataSourceResult(Map<String, ?> result) {
        if (result.containsKey(SimilarArtistsDataSource.OUTPUT_ARTISTS)) {
            similarArtistsTable.setModel(new SimilarArtistsTableModel(((ISimilarArtistsInfo) result.get(SimilarArtistsDataSource.OUTPUT_ARTISTS)).getArtists()));
        }
    }

    @Override
    public void clearContextPanelContent() {
        super.clearContextPanelContent();
        similarArtistsTable.setModel(new SimilarArtistsTableModel(null));
    }

    @Override
    public Component getComponent() {
        // Create components
        similarArtistsTable = new SimilarArtistsContextTable(getLookAndFeelManager().getCurrentLookAndFeel());
        similarArtistsTable.addContextRowPanel(new SimilarArtistTableCellRendererCode(getLookAndFeelManager().getCurrentLookAndFeel()));
        return similarArtistsTable;
    }
}
