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

package net.sourceforge.atunes.kernel.modules.navigator;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.atunes.gui.model.NavigationTableModel.Property;
import net.sourceforge.atunes.kernel.modules.columns.AbstractColumn;
import net.sourceforge.atunes.kernel.modules.columns.DateColumn;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.StringUtils;

public class PodcastNavigationColumnSet extends AbstractCustomNavigatorColumnSet {

    private static final class DurationColumn extends AbstractColumn {
        /**
		 * 
		 */
        private static final long serialVersionUID = -5577224920500040774L;

        private DurationColumn(String name, Class<?> columnClass) {
            super(name, columnClass);
        }

        @Override
        public Object getValueFor(AudioObject audioObject) {
            return StringUtils.seconds2String(audioObject.getDuration());
        }

        @Override
        protected int ascendingCompare(AudioObject o1, AudioObject o2) {
            return Integer.valueOf(o1.getDuration()).compareTo(Integer.valueOf(o2.getDuration()));
        }
    }

    private static final class PodcastEntriesColumn extends AbstractColumn {
        /**
		 * 
		 */
        private static final long serialVersionUID = -1788596965509543581L;

        private PodcastEntriesColumn(String name, Class<?> columnClass) {
            super(name, columnClass);
        }

        @Override
        public Object getValueFor(AudioObject audioObject) {
            return audioObject.getTitleOrFileName();
        }

        @Override
        protected int ascendingCompare(AudioObject o1, AudioObject o2) {
            return o1.getTitleOrFileName().compareTo(o2.getTitleOrFileName());
        }
    }

    private static final class OldEntryPropertyColumn extends AbstractColumn {
        /**
		 * 
		 */
        private static final long serialVersionUID = 1L;

        private OldEntryPropertyColumn(String name, Class<?> columnClass) {
            super(name, columnClass);
        }

        @Override
        public Object getValueFor(AudioObject audioObject) {
            return ((PodcastFeedEntry) audioObject).isOld() ? Property.OLD_ENTRY : Property.NO_PROPERTIES;
        }

        @Override
        protected int ascendingCompare(AudioObject o1, AudioObject o2) {
            return Boolean.valueOf(((PodcastFeedEntry) o1).isOld()).compareTo(Boolean.valueOf(((PodcastFeedEntry) o2).isOld()));
        }
    }

    private static final class DownloadedPropertyColumn extends AbstractColumn {
        /**
		 * 
		 */
        private static final long serialVersionUID = 1L;

        private DownloadedPropertyColumn(String name, Class<?> columnClass) {
            super(name, columnClass);
        }

        @Override
        public Object getValueFor(AudioObject audioObject) {
            return ((PodcastFeedEntry) audioObject).isDownloaded() ? Property.DOWNLOADED_ENTRY : Property.NO_PROPERTIES;
        }

        @Override
        protected int ascendingCompare(AudioObject o1, AudioObject o2) {
            return Boolean.valueOf(((PodcastFeedEntry) o1).isDownloaded()).compareTo(Boolean.valueOf(((PodcastFeedEntry) o2).isDownloaded()));
        }
    }

    private static final class NotListenedPropertyColumn extends AbstractColumn {
        /**
		 * 
		 */
        private static final long serialVersionUID = 1L;

        private NotListenedPropertyColumn(String name, Class<?> columnClass) {
            super(name, columnClass);
        }

        @Override
        public Object getValueFor(AudioObject audioObject) {
            return ((PodcastFeedEntry) audioObject).isListened() ? Property.NO_PROPERTIES : Property.NOT_LISTENED_ENTRY;
        }

        @Override
        protected int ascendingCompare(AudioObject o1, AudioObject o2) {
            return Boolean.valueOf(((PodcastFeedEntry) o1).isListened()).compareTo(Boolean.valueOf(((PodcastFeedEntry) o2).isListened()));
        }
    }

    public PodcastNavigationColumnSet(String columnSetName) {
        super(columnSetName);
    }

    @Override
    protected List<AbstractColumn> getAllowedColumns() {
        List<AbstractColumn> columns = new ArrayList<AbstractColumn>();

        AbstractColumn property1 = new NotListenedPropertyColumn("", Property.class);
        property1.setVisible(true);
        property1.setWidth(20);
        property1.setResizable(false);
        columns.add(property1);

        AbstractColumn property2 = new DownloadedPropertyColumn("", Property.class);
        property2.setVisible(true);
        property2.setWidth(20);
        property2.setResizable(false);
        columns.add(property2);

        AbstractColumn property3 = new OldEntryPropertyColumn("", Property.class);
        property3.setVisible(true);
        property3.setWidth(20);
        property3.setResizable(false);
        columns.add(property3);

        AbstractColumn entries = new PodcastEntriesColumn("PODCAST_ENTRIES", String.class);
        entries.setVisible(true);
        entries.setWidth(300);
        entries.setUsedForFilter(true);
        columns.add(entries);

        AbstractColumn duration = new DurationColumn("DURATION", String.class);
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