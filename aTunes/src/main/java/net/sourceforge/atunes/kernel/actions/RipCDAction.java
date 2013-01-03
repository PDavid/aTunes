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

import net.sourceforge.atunes.model.IApplicationArguments;
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

    private IRipperHandler ripperHandler;

    private IApplicationArguments applicationArguments;

    /**
     * @param applicationArguments
     */
    public void setApplicationArguments(
	    final IApplicationArguments applicationArguments) {
	this.applicationArguments = applicationArguments;
    }

    /**
     * @param ripperHandler
     */
    public void setRipperHandler(final IRipperHandler ripperHandler) {
	this.ripperHandler = ripperHandler;
    }

    /**
     * Defult constructor
     */
    public RipCDAction() {
	super(StringUtils.getString(I18nUtils.getString("RIP_CD"), "..."));
    }

    @Override
    public boolean isEnabled() {
	return (applicationArguments.isSimulateCD() || ripperHandler
		.isRipSupported()) && super.isEnabled();
    }

    @Override
    protected void executeAction() {
	ripperHandler.startCdRipper();
    }
}
