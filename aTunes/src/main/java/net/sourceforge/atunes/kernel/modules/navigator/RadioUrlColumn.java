/*
 * aTunes
 * Copyright (C) Alex Aranda, Sylvain Gaudard and contributors
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

import net.sourceforge.atunes.kernel.modules.columns.AbstractColumn;
import net.sourceforge.atunes.model.IAudioObject;

/**
 * Radio URL
 * 
 * @author alex
 * 
 */
public final class RadioUrlColumn extends AbstractColumn<String> {
    private static final long serialVersionUID = -1615880013918017198L;

    RadioUrlColumn() {
	super("URL");
	setVisible(true);
	setWidth(400);
	setUsedForFilter(true);
    }

    @Override
    protected int ascendingCompare(final IAudioObject o1, final IAudioObject o2) {
	return compare(o1.getUrl(), o2.getUrl());
    }

    @Override
    protected int descendingCompare(final IAudioObject ao1,
	    final IAudioObject ao2) {
	return -ascendingCompare(ao1, ao2);
    }

    @Override
    public String getValueFor(final IAudioObject audioObject, final int row) {
	return audioObject.getUrl();
    }
}