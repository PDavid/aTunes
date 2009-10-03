/*
 * aTunes 2.0.0-SNAPSHOT
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

import java.awt.event.ActionEvent;

import net.sourceforge.atunes.kernel.modules.repository.RepositoryHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This action refreshes repository
 * 
 * @author fleax
 * 
 */
public class ImportToRepositoryAction extends Action {

    private static final long serialVersionUID = -5708270585764283210L;

    ImportToRepositoryAction() {
        super(StringUtils.getString(I18nUtils.getString("IMPORT"), "..."));
        putValue(SHORT_DESCRIPTION, StringUtils.getString(I18nUtils.getString("IMPORT"), "..."));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        RepositoryHandler.getInstance().importFoldersToRepository();
    }

}
