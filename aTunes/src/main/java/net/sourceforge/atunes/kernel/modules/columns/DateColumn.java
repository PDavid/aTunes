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

package net.sourceforge.atunes.kernel.modules.columns;

import java.text.DateFormat;

import javax.swing.SwingConstants;

import net.sourceforge.atunes.model.AudioObject;

public class DateColumn extends AbstractColumn {

    private static final long serialVersionUID = 6832826017182272636L;
    private final DateFormat dateFormat = DateFormat.getDateInstance();

    public DateColumn() {
        super("DATE", String.class);
        setAlignment(SwingConstants.CENTER);
        setVisible(false);
        setUsedForFilter(true);
    }

    @Override
    protected int ascendingCompare(AudioObject ao1, AudioObject ao2) {
        if (ao1.getDate() == null)
            return 1;
        else if (ao2.getDate() == null)
            return -1;
        else
            return ao1.getDate().compareTo(ao2.getDate());
    }

    @Override
    public Object getValueFor(AudioObject audioObject) {
        if (audioObject.getDate() != null)
            return dateFormat.format(audioObject.getDate());
        else
            return "";
    }

    @Override
    public String getValueForFilter(AudioObject audioObject) {
        return dateFormat.format(audioObject.getDate());
    }
}
