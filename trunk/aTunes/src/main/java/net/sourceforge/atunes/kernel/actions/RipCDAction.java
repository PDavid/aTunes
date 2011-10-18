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

import java.awt.event.ActionEvent;

import net.sourceforge.atunes.model.ICommandHandler;
import net.sourceforge.atunes.model.IRipperHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This action start rip dialog
 * 
 * @author alex
 * 
 */
public class RipCDAction extends CustomAbstractAction {

    private static final long serialVersionUID = -362457188090138933L;

    RipCDAction() {
        super(StringUtils.getString(I18nUtils.getString("RIP_CD"), "..."));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("RIP_CD"));
    	setCommandHandler(getBean(ICommandHandler.class));
    }

    @Override
    public void setEnabled(boolean newValue) {    	
    	super.setEnabled(getBean(IRipperHandler.class).isRipSupported() && newValue);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
    	getBean(IRipperHandler.class).startCdRipper();
    }

    @Override
    public String getCommandName() {
        return "cdripper";
    }

}
