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

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IAudioObjectExporter;
import net.sourceforge.atunes.model.ILocalAudioObjectFilter;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Adds objects to play list
 * 
 * @author alex
 * 
 */
public class ExportNavigatorSelectionAction extends
	AbstractActionOverSelectedObjects<IAudioObject> {

    private static final long serialVersionUID = 1625697867534974341L;

    private IAudioObjectExporter audioObjectExporter;

    private ILocalAudioObjectFilter localAudioObjectFilter;

    /**
     * @param localAudioObjectFilter
     */
    public void setLocalAudioObjectFilter(
	    final ILocalAudioObjectFilter localAudioObjectFilter) {
	this.localAudioObjectFilter = localAudioObjectFilter;
    }

    /**
     * @param audioObjectExporter
     */
    public void setAudioObjectExporter(
	    final IAudioObjectExporter audioObjectExporter) {
	this.audioObjectExporter = audioObjectExporter;
    }

    /**
     * Default constructor
     */
    public ExportNavigatorSelectionAction() {
	super(I18nUtils.getString("EXPORT"));
    }

    @Override
    protected void executeAction(final List<IAudioObject> objects) {
	audioObjectExporter.exportAudioObject(localAudioObjectFilter
		.getLocalAudioObjects(objects));
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(
	    final boolean rootSelected, final List<ITreeNode> selection) {
	return !selection.isEmpty();
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(
	    final List<IAudioObject> selection) {
	return !selection.isEmpty();
    }
}
