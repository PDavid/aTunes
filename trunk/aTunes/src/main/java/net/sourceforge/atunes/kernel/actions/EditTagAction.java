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

import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.KeyStroke;
import javax.swing.tree.DefaultMutableTreeNode;

import net.sourceforge.atunes.gui.images.ImageLoader;
import net.sourceforge.atunes.kernel.ControllerProxy;
import net.sourceforge.atunes.kernel.modules.repository.audio.AudioFile;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Opens edit tag dialog
 * 
 * @author fleax
 * 
 */
public class EditTagAction extends ActionOverSelectedObjects<AudioFile> {

    private static final long serialVersionUID = -4310895355731333072L;

    public static final String PLAYLIST = "PLAYLIST";
    public static final String NAVIGATOR = "NAVIGATOR";

    EditTagAction() {
        super(I18nUtils.getString("EDIT_TAG"), ImageLoader.getImage(ImageLoader.TAG), AudioFile.class);
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("EDIT_TAG"));
    }

    @Override
    protected void initialize() {
        if (PLAYLIST.equals(actionId)) {
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0));
        }
    }

    @Override
    protected void performAction(List<AudioFile> objects) {
        // Start edit by opening edit dialog
        ControllerProxy.getInstance().getEditTagDialogController().editFiles(objects);
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        if (NAVIGATOR.equals(actionId)) {
            return !rootSelected && !selection.isEmpty();
        }
        return super.isEnabledForNavigationTreeSelection(rootSelected, selection);
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(List<AudioObject> selection) {
        if (NAVIGATOR.equals(actionId)) {
            return !selection.isEmpty();
        }
        return super.isEnabledForNavigationTableSelection(selection);
    }
}
