/*
 * aTunes 2.0.0-SNAPSHOT
 * Copyright (C) 2006-2010 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
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

import net.sourceforge.atunes.gui.views.dialogs.PatternInputDialog;
import net.sourceforge.atunes.kernel.modules.gui.GuiHandler;
import net.sourceforge.atunes.kernel.modules.pattern.AbstractPattern;
import net.sourceforge.atunes.kernel.modules.repository.data.AudioFile;
import net.sourceforge.atunes.kernel.modules.repository.tags.writer.EditTagFromFileNamePatternProcess;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Calls process to set tag from a pattern of file name entered by user
 * 
 * @author fleax
 * 
 */
public class AutoSetTagFromFileNamePatternAction extends ActionOverSelectedObjects<AudioFile> {

    private static final long serialVersionUID = -8458591967408812850L;

    public AutoSetTagFromFileNamePatternAction() {
        super(StringUtils.getString(I18nUtils.getString("AUTO_SET_TAG_FROM_FILE_NAME_PATTERN"), "..."), AudioFile.class);
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("AUTO_SET_TAG_FROM_FILE_NAME_PATTERN"));
    }

    @Override
    protected void performAction(List<AudioFile> objects) {
        // Show pattern input dialog
        PatternInputDialog inputDialog = new PatternInputDialog(GuiHandler.getInstance().getFrame().getFrame(), false);
        inputDialog.show(AbstractPattern.getRecognitionPatterns(), objects.get(0).getNameWithoutExtension());
        String pattern = inputDialog.getResult();

        // If user entered a pattern apply to files
        if (pattern != null) {
            new EditTagFromFileNamePatternProcess(objects, pattern).execute();
        }
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
