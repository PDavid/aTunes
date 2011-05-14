/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.kernel.modules.context;

import javax.swing.JTable;

import net.sourceforge.atunes.Constants;

/**
 * <p>
 * This class is needed for Substance look and feel 4.1+
 * </p>
 * <a href ="https://substance.dev.java.net/servlets/ProjectForumMessageView?messageID=22522&forumID=1484"
 * >https://substance.dev.java.net/servlets/ProjectForumMessageView?messageID=
 * 22522&forumID=1484</a>
 */
public final class ContextImageJTable extends JTable {

    private static final long serialVersionUID = 339974237840854168L;

    @Override
    public void setRowHeight(int heigth) {
        super.setRowHeight(Constants.CONTEXT_IMAGE_HEIGHT + 5);
    }

}
