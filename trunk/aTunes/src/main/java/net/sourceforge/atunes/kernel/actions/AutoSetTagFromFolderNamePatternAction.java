/*
 * aTunes 2.2.0-SNAPSHOT
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

import net.sourceforge.atunes.gui.views.dialogs.PatternInputDialog;
import net.sourceforge.atunes.kernel.modules.pattern.Patterns;
import net.sourceforge.atunes.kernel.modules.process.EditTagFromFolderNamePatternProcess;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.ILookAndFeelManager;
import net.sourceforge.atunes.model.IProcessFactory;
import net.sourceforge.atunes.model.IStateRepository;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Calls process to set tag from a pattern of folder name entered by user
 * 
 * @author fleax
 * 
 */
public class AutoSetTagFromFolderNamePatternAction extends AbstractActionOverSelectedObjects<ILocalAudioObject> {

    private static final long serialVersionUID = -1253820711948217089L;

    private IFrame frame;
    
    private ILookAndFeelManager lookAndFeelManager;
    
    private IProcessFactory processFactory;
    
    private IStateRepository stateRepository;
    
    /**
     * @param stateRepository
     */
    public void setStateRepository(IStateRepository stateRepository) {
		this.stateRepository = stateRepository;
	}
    
    /**
     * @param processFactory
     */
    public void setProcessFactory(IProcessFactory processFactory) {
		this.processFactory = processFactory;
	}
    
    /**
     * @param frame
     */
    public void setFrame(IFrame frame) {
		this.frame = frame;
	}
    
    /**
     * @param lookAndFeelManager
     */
    public void setLookAndFeelManager(ILookAndFeelManager lookAndFeelManager) {
		this.lookAndFeelManager = lookAndFeelManager;
	}
    
    public AutoSetTagFromFolderNamePatternAction() {
        super(StringUtils.getString(I18nUtils.getString("AUTO_SET_TAG_FROM_FOLDER_NAME_PATTERN"), "..."));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("AUTO_SET_TAG_FROM_FOLDER_NAME_PATTERN"));
    }

    @Override
    protected void executeAction(List<ILocalAudioObject> objects) {
        // Show pattern input dialog
        PatternInputDialog inputDialog = new PatternInputDialog(frame.getFrame(), false, stateRepository, lookAndFeelManager.getCurrentLookAndFeel());
        inputDialog.show(Patterns.getRecognitionPatterns(), objects.get(0).getFile().getParentFile().getAbsolutePath());
        String pattern = inputDialog.getResult();

        // If user entered a pattern apply to files
        if (pattern != null) {
        	EditTagFromFolderNamePatternProcess process = (EditTagFromFolderNamePatternProcess) processFactory.getProcessByName("editTagFromFolderNamePatternProcess");
        	process.setFilesToChange(objects);
        	process.setPattern(pattern);
        	process.execute();
        }
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(boolean rootSelected, List<DefaultMutableTreeNode> selection) {
        return !rootSelected && !selection.isEmpty();
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(List<IAudioObject> selection) {
        return !selection.isEmpty();
    }
}
