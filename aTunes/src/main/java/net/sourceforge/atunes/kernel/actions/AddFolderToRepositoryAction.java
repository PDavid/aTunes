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

import net.sourceforge.atunes.model.IRepositoryHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This action show a window to select folders to set as repository
 * 
 * @author fleax
 * 
 */
public class AddFolderToRepositoryAction extends CustomAbstractAction {

    private static final long serialVersionUID = 6921256152199287639L;

    private IRepositoryHandler repositoryHandler;

    /**
     * @param repositoryHandler
     */
    public void setRepositoryHandler(final IRepositoryHandler repositoryHandler) {
	this.repositoryHandler = repositoryHandler;
    }

    /**
     * Default constructor
     */
    public AddFolderToRepositoryAction() {
	super(StringUtils.getString(
		I18nUtils.getString("ADD_FOLDER_TO_REPOSITORY"), "..."));
    }

    @Override
    protected void executeAction() {
	repositoryHandler.addFolderToRepository();
    }
}
