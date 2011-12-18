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

import net.sourceforge.atunes.kernel.modules.columns.AbstractColumn;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IRadio;

final class RadioNameColumn extends AbstractColumn<String> {
    /**
	 * 
	 */
    private static final long serialVersionUID = 3613237620716484881L;

    RadioNameColumn(String name) {
        super(name);
    }

    @Override
    public String getValueFor(IAudioObject audioObject) {
    	if (audioObject instanceof IRadio) {
    		return ((IRadio) audioObject).getName();
    	}
        return null;
    }

    @Override
    protected int ascendingCompare(IAudioObject o1, IAudioObject o2) {
    	if (o1 instanceof IRadio && o2 instanceof IRadio) {
    		return ((IRadio) o1).getName().compareTo(((IRadio) o2).getName());
    	}
    	return o1.getTitleOrFileName().compareTo(o2.getTitleOrFileName());
    }
}