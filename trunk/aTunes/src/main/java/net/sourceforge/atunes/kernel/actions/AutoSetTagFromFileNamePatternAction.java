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

import net.sourceforge.atunes.kernel.modules.pattern.PatternInputDialog;
import net.sourceforge.atunes.kernel.modules.pattern.Patterns;
import net.sourceforge.atunes.kernel.modules.process.EditTagFromFileNamePatternProcess;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IProcessFactory;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Calls process to set tag from a pattern of file name entered by user
 * 
 * @author fleax
 * 
 */
public class AutoSetTagFromFileNamePatternAction extends
		AbstractActionOverSelectedObjects<ILocalAudioObject> {

	private static final long serialVersionUID = -8458591967408812850L;

	private IProcessFactory processFactory;

	private IDialogFactory dialogFactory;

	private Patterns patterns;

	/**
	 * @param patterns
	 */
	public void setPatterns(final Patterns patterns) {
		this.patterns = patterns;
	}

	/**
	 * @param dialogFactory
	 */
	public void setDialogFactory(final IDialogFactory dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	/**
	 * @param processFactory
	 */
	public void setProcessFactory(final IProcessFactory processFactory) {
		this.processFactory = processFactory;
	}

	/**
	 * Default constructor
	 */
	public AutoSetTagFromFileNamePatternAction() {
		super(StringUtils.getString(
				I18nUtils.getString("AUTO_SET_TAG_FROM_FILE_NAME_PATTERN"),
				"..."));
	}

	@Override
	protected void executeAction(final List<ILocalAudioObject> objects) {
		// Show pattern input dialog
		PatternInputDialog inputDialog = this.dialogFactory.newDialog(
				"nonMassivePatternInputDialog", PatternInputDialog.class);
		inputDialog.show(this.patterns.getRecognitionPatterns(), objects.get(0)
				.getNameWithoutExtension());
		String pattern = inputDialog.getResult();

		// If user entered a pattern apply to files
		if (pattern != null) {
			EditTagFromFileNamePatternProcess process = (EditTagFromFileNamePatternProcess) this.processFactory
					.getProcessByName("editTagFromFileNamePatternProcess");
			process.setFilesToChange(objects);
			process.setPattern(pattern);
			process.execute();
		}
	}

	@Override
	public boolean isEnabledForNavigationTreeSelection(
			final boolean rootSelected, final List<ITreeNode> selection) {
		return !rootSelected && !selection.isEmpty();
	}

	@Override
	public boolean isEnabledForNavigationTableSelection(
			final List<IAudioObject> selection) {
		return !selection.isEmpty();
	}
}
