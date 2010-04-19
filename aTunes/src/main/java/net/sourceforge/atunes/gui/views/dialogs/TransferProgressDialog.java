/*
 * aTunes 2.1.0-SNAPSHOT
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

package net.sourceforge.atunes.gui.views.dialogs;

import java.awt.Component;

import net.sourceforge.atunes.utils.StringUtils;

/**
 * The Class TransferProgressDialog.
 */
public final class TransferProgressDialog extends ProgressDialog {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1264914965691724365L;

    /**
     * Instantiates a new transfer progress dialog.
     * 
     * @param title
     *            the title
     */
    public TransferProgressDialog(String title, Component owner) {
        super(title, owner);
    }

    @Override
    public void setCurrentProgress(long value) {
        getCurrentLabel().setText(StringUtils.fromByteToMegaOrGiga(value));
    }

    @Override
    public void setTotalProgress(long value) {
        getTotalLabel().setText(StringUtils.fromByteToMegaOrGiga(value));
    }
}
