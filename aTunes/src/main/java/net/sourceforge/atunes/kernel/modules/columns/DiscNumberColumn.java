/*
 * aTunes 2.0.0
 * Copyright (C) 2006-2009 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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
package net.sourceforge.atunes.kernel.modules.columns;

import javax.swing.SwingConstants;

import net.sourceforge.atunes.gui.views.controls.playList.Column;
import net.sourceforge.atunes.model.AudioObject;

public class DiscNumberColumn extends Column {

    private static final long serialVersionUID = -6226391762384061708L;

    public DiscNumberColumn() {
        super("DISC_NUMBER", String.class);
        setWidth(40);
        setVisible(false);
        setAlignment(SwingConstants.CENTER);
    }

    @Override
    protected int ascendingCompare(AudioObject ao1, AudioObject ao2) {
        return Integer.valueOf(ao1.getDiscNumber()).compareTo(ao2.getDiscNumber());
    }

    @Override
    public Object getValueFor(AudioObject audioObject) {
        // Return disc number
        return audioObject.getDiscNumber() > 0 ? audioObject.getDiscNumber() : "";
    }

}
