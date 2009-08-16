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

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.tags.writer.TagEditionOperations;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.LanguageTool;

/**
 * Calls process to set genres automatically
 * 
 * @author fleax
 * 
 */
public class AutoSetGenresAction extends ActionOverSelectedObjects<AudioFile> {

    private static final long serialVersionUID = 2868302038954563763L;

    AutoSetGenresAction() {
        super(LanguageTool.getString("AUTO_SET_GENRE"), AudioFile.class);
        putValue(SHORT_DESCRIPTION, LanguageTool.getString("AUTO_SET_GENRE"));

    }

    @Override
    protected void performAction(List<AudioFile> objects) {
        TagEditionOperations.editGenre(objects);
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        return !rootSelected && !selection.isEmpty();
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(List<AudioObject> selection) {
        return !selection.isEmpty();
    }
}
