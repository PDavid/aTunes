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

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.kernel.modules.tags.ClearTagsProcess;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IPlayListHandler;
import net.sourceforge.atunes.utils.I18nUtils;

public class ClearTagAction extends AbstractActionOverSelectedObjects<ILocalAudioObject> {

    private static final long serialVersionUID = 4476719536754930347L;

    public static final String PLAYLIST = "PLAYLIST";
    public static final String NAVIGATOR = "NAVIGATOR";

    ClearTagAction() {
        super(I18nUtils.getString("CLEAR_TAG"), ILocalAudioObject.class);
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("CLEAR_TAG"));
    }

    @Override
    protected void performAction(List<ILocalAudioObject> objects) {
        new ClearTagsProcess(objects, getState(), Context.getBean(IPlayListHandler.class)).execute();
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        if (NAVIGATOR.equals(getActionId())) {
            return !rootSelected && !selection.isEmpty();
        }
        return super.isEnabledForNavigationTreeSelection(rootSelected, selection);
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(List<IAudioObject> selection) {
        if (NAVIGATOR.equals(getActionId())) {
            return !selection.isEmpty();
        }
        return super.isEnabledForNavigationTableSelection(selection);
    }
}
