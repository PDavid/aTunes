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

package net.sourceforge.atunes.kernel.actions;

import java.util.List;

import net.sourceforge.atunes.model.IAlbum;
import net.sourceforge.atunes.model.ITagHandler;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Edit titles of an album
 * 
 * @author alex
 * 
 */
public class EditTitlesAction extends
	AbstractActionOverSelectedTreeObjects<IAlbum> {

    private static final long serialVersionUID = -2883223880879440970L;

    private ITagHandler tagHandler;

    /**
     * @param tagHandler
     */
    public void setTagHandler(final ITagHandler tagHandler) {
	this.tagHandler = tagHandler;
    }

    /**
     * Default constructor
     */
    public EditTitlesAction() {
	super(I18nUtils.getString("EDIT_TITLES"));
    }

    @Override
    protected void executeAction(final List<IAlbum> albums) {
	tagHandler.editFiles(albums.get(0));
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(
	    final boolean rootSelected, final List<ITreeNode> selection) {
	if (selection.isEmpty()) {
	    return false;
	}

	for (ITreeNode node : selection) {
	    if (!(node.getUserObject() instanceof IAlbum)) {
		return false;
	    }
	}

	return selection.size() == 1;
    }
}
