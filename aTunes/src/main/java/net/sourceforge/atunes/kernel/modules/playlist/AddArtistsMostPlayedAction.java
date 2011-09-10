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

package net.sourceforge.atunes.kernel.modules.playlist;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Properties;

import net.sourceforge.atunes.kernel.actions.CustomAbstractAction;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Adds artists most played to play list
 * 
 * @author fleax
 * 
 */
public class AddArtistsMostPlayedAction extends CustomAbstractAction {

    private static final long serialVersionUID = -5006402582163507581L;

    public static final String INSTANCE_1 = "ADD_ARTIST_MOST_PLAYED";
    public static final String INSTANCE_5 = "ADD_5_ARTISTS_MOST_PLAYED";
    public static final String INSTANCE_10 = "ADD_10_ARTISTS_MOST_PLAYED";

    public static final String PARAMETER = "PARAMETER";

    @Override
    protected void initialize() {
        putValue(NAME, I18nUtils.getString(getActionId()));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString(getActionId()));
    }

    @Override
    protected Properties getProperties(String instanceid) {
        Properties instanceProperties = new Properties();
        if (INSTANCE_1.equals(instanceid)) {
            instanceProperties.put(PARAMETER, 1);
        } else if (INSTANCE_5.equals(instanceid)) {
            instanceProperties.put(PARAMETER, 5);
        } else {
            instanceProperties.put(PARAMETER, 10);
        }
        return instanceProperties;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SmartPlayListHandler.getInstance().addArtistsMostPlayed((Integer) this.getProperties().get(PARAMETER));
    }

    @Override
    public boolean isEnabledForPlayListSelection(List<IAudioObject> selection) {
        return true;
    }

}
