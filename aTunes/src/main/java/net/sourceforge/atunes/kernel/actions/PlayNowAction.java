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

package net.sourceforge.atunes.kernel.actions;

import java.awt.event.ActionEvent;
import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.utils.I18nUtils;

public class PlayNowAction extends CustomAbstractAction {

    private static final long serialVersionUID = -2099290583376403144L;

    PlayNowAction() {
        super(I18nUtils.getString("PLAY_NOW"));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("PLAY_NOW"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Play now feature plays selected song immediately. If song is added to play list, then is automatically selected.
        // If not, it's added to play list    	
        getBean(IPlayListHandler.class).playNow(getBean(INavigationHandler.class).getSelectedAudioObjectInNavigationTable());
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(List<IAudioObject> selection) {
        return selection.size() == 1;
    }

}
