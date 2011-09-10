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
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.model.IAudioObject;

public final class RadioNavigationColumnSet extends AbstractCustomNavigatorColumnSet {

    private static final class UrlColumn extends AbstractColumn {
        /**
		 * 
		 */
        private static final long serialVersionUID = -1615880013918017198L;

        private UrlColumn(String name, Class<?> columnClass) {
            super(name, columnClass);
        }

        @Override
        protected int ascendingCompare(IAudioObject o1, IAudioObject o2) {
            return o1.getUrl().compareTo(o2.getUrl());
        }

        @Override
        public Object getValueFor(IAudioObject audioObject) {
            return audioObject.getUrl();
        }
    }

    private static final class NameColumn extends AbstractColumn {
        /**
		 * 
		 */
        private static final long serialVersionUID = 3613237620716484881L;

        private NameColumn(String name, Class<?> columnClass) {
            super(name, columnClass);
        }

        @Override
        public Object getValueFor(IAudioObject audioObject) {
            return ((Radio) audioObject).getName();
        }

        @Override
        protected int ascendingCompare(IAudioObject o1, IAudioObject o2) {
            return ((Radio) o1).getName().compareTo(((Radio) o2).getName());
        }
    }

    private static final class EmptyColumn extends AbstractColumn {
        /**
		 * 
		 */
        private static final long serialVersionUID = 3613237620716484881L;

        private EmptyColumn(String name, Class<?> columnClass) {
            super(name, columnClass);
        }

        @Override
        protected int ascendingCompare(IAudioObject o1, IAudioObject o2) {
            return 0;
        }

        @Override
        public Object getValueFor(IAudioObject audioObject) {
            return Property.NO_PROPERTIES;
        }
    }

    public RadioNavigationColumnSet(String columnSetName) {
        super(columnSetName);
    }

    @Override
    protected List<AbstractColumn> getAllowedColumns() {
        List<AbstractColumn> columns = new ArrayList<AbstractColumn>();

        AbstractColumn property = new EmptyColumn("", Property.class);
        property.setVisible(true);
        property.setWidth(20);
        property.setResizable(false);
        columns.add(property);

        AbstractColumn name = new NameColumn("NAME", String.class);
        name.setVisible(true);
        name.setWidth(150);
        name.setUsedForFilter(true);
        columns.add(name);

        AbstractColumn url = new UrlColumn("URL", String.class);
        url.setVisible(true);
        url.setWidth(400);
        url.setUsedForFilter(true);
        columns.add(url);

        return columns;
    }

}