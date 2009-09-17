/*
 * aTunes 1.14.0
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

package net.sourceforge.atunes.kernel.actions;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Properties;

import net.sourceforge.atunes.kernel.modules.playlist.SmartPlayListHandler;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Adds songs most played to play list
 * 
 * @author fleax
 * 
 */
public class AddSongsMostPlayedAction extends Action {

    private static final long serialVersionUID = -2036319858956733773L;

    public static final String INSTANCE_10 = "ADD_10_SONGS_MOST_PLAYED";
    public static final String INSTANCE_50 = "ADD_50_SONGS_MOST_PLAYED";
    public static final String INSTANCE_100 = "ADD_100_SONGS_MOST_PLAYED";

    public static final String PARAMETER = "PARAMETER";

    @Override
    protected void initialize() {
        putValue(NAME, I18nUtils.getString(actionId));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString(actionId));
    }

    @Override
    protected Properties getProperties(String instanceid) {
        Properties instanceProperties = new Properties();
        if (INSTANCE_10.equals(instanceid)) {
            instanceProperties.put(PARAMETER, 10);
        } else if (INSTANCE_50.equals(instanceid)) {
            instanceProperties.put(PARAMETER, 50);
        } else {
            instanceProperties.put(PARAMETER, 100);
        }
        return instanceProperties;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SmartPlayListHandler.getInstance().addSongsMostPlayed((Integer) this.properties.get(PARAMETER));
    }

    @Override
    public boolean isEnabledForPlayListSelection(List<AudioObject> selection) {
        return true;
    }

}
