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

package net.sourceforge.atunes.kernel.modules.player;

import javax.swing.JOptionPane;

import net.sourceforge.atunes.Context;
import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IMessageDialog;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * Shows errors of playback
 * @author alex
 *
 */
final class ShowPlaybackErrorRunnable implements Runnable {
	
    private final String[] errorMessages;
    private boolean ignore;

    /**
     * @param errorMessages
     */
    ShowPlaybackErrorRunnable(String[] errorMessages) {
        this.errorMessages = errorMessages;
    }

    @Override
    public void run() {
    	StringBuilder sb = new StringBuilder();
    	for (String errorMessage : errorMessages) {
    		sb.append(errorMessage).append(" ");
    	}
        String selection = (String) Context.getBean(IDialogFactory.class).newDialog(IMessageDialog.class).showMessage(StringUtils.getString(sb.toString()), I18nUtils.getString("ERROR"),
                JOptionPane.ERROR_MESSAGE, new String[] { I18nUtils.getString("IGNORE"), I18nUtils.getString("CANCEL") });
        ignore = selection.equals(I18nUtils.getString("IGNORE"));
    }

    /**
     * @return the ignore
     */
    protected boolean isIgnore() {
        return ignore;
    }
}