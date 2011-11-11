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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.gui.model.NavigationTableModel.Property;
import net.sourceforge.atunes.kernel.modules.columns.AbstractColumn;
import net.sourceforge.atunes.kernel.modules.columns.DateColumn;
import net.sourceforge.atunes.model.IColumn;

public class PodcastNavigationColumnSet extends AbstractCustomNavigatorColumnSet {

    public PodcastNavigationColumnSet(String columnSetName) {
        super(columnSetName);
    }

    @Override
    protected List<IColumn> getAllowedColumns() {
        List<IColumn> columns = new ArrayList<IColumn>();

        AbstractColumn property1 = new PodcastNotListenedPropertyColumn("", Property.class);
        property1.setVisible(true);
        property1.setWidth(20);
        property1.setResizable(false);
        columns.add(property1);

        AbstractColumn property2 = new PodcastDownloadedPropertyColumn("", Property.class);
        property2.setVisible(true);
        property2.setWidth(20);
        property2.setResizable(false);
        columns.add(property2);

        AbstractColumn property3 = new PodcastOldEntryPropertyColumn("", Property.class);
        property3.setVisible(true);
        property3.setWidth(20);
        property3.setResizable(false);
        columns.add(property3);

        AbstractColumn entries = new PodcastEntriesColumn("PODCAST_ENTRIES", String.class);
        entries.setVisible(true);
        entries.setWidth(300);
        entries.setUsedForFilter(true);
        columns.add(entries);

        AbstractColumn duration = new PodcastDurationColumn("DURATION", String.class);
        duration.setVisible(true);
        duration.setWidth(60);
        duration.setUsedForFilter(true);
        columns.add(duration);
        
        AbstractColumn date = new DateColumn();
        date.setVisible(true);
        date.setUsedForFilter(true);
        columns.add(date);

        return columns;
    }

}