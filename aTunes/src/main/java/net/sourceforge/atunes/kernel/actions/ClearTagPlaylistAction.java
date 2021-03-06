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

import net.sourceforge.atunes.model.IChangeTagsProcess;
import net.sourceforge.atunes.model.ILocalAudioObject;
import net.sourceforge.atunes.model.IProcessFactory;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Clears tags of audio objects from play list
 * 
 * @author alex
 * 
 */
public class ClearTagPlaylistAction extends
	AbstractActionOverSelectedObjects<ILocalAudioObject> {

    private static final long serialVersionUID = 4476719536754930347L;

    private IProcessFactory processFactory;

    /**
     * @param processFactory
     */
    public void setProcessFactory(final IProcessFactory processFactory) {
	this.processFactory = processFactory;
    }

    /**
     * Default constructor
     */
    public ClearTagPlaylistAction() {
	super(I18nUtils.getString("CLEAR_TAG"));
    }

    @Override
    protected void executeAction(final List<ILocalAudioObject> objects) {
	IChangeTagsProcess process = (IChangeTagsProcess) processFactory
		.getProcessByName("clearTagsProcess");
	process.setFilesToChange(objects);
	process.execute();
    }
}
