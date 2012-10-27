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

import net.sourceforge.atunes.kernel.modules.columns.AbstractColumn;
import net.sourceforge.atunes.model.IColumn;

/**
 * Column set for radios
 * 
 * @author alex
 * 
 */
public final class RadioNavigationColumnSet extends
	AbstractCustomNavigatorColumnSet {

    /**
     * Default constructor
     */
    public RadioNavigationColumnSet() {
	super();

	List<IColumn<?>> columns = new ArrayList<IColumn<?>>();

	AbstractColumn<?> property = new RadioEmptyColumn("");
	property.setVisible(true);
	property.setWidth(20);
	property.setResizable(false);
	columns.add(property);

	AbstractColumn<?> name = new RadioNameColumn("NAME");
	name.setVisible(true);
	name.setWidth(150);
	name.setUsedForFilter(true);
	columns.add(name);

	AbstractColumn<?> url = new RadioUrlColumn("URL");
	url.setVisible(true);
	url.setWidth(400);
	url.setUsedForFilter(true);
	columns.add(url);

	setAllowedColumns(columns);
    }

}