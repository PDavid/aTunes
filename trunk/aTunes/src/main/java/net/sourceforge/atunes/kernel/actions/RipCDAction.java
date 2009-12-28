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

import net.sourceforge.atunes.gui.images.Images;
import net.sourceforge.atunes.kernel.modules.cdripper.RipperHandler;
import net.sourceforge.atunes.utils.I18nUtils;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This action start rip dialog
 * 
 * @author alex
 * 
 */
public class RipCDAction extends Action {

    private static final long serialVersionUID = -362457188090138933L;

    RipCDAction() {
        super(StringUtils.getString(I18nUtils.getString("RIP_CD"), "..."), Images.getImage(Images.CD_AUDIO_TINY));
        putValue(SHORT_DESCRIPTION, I18nUtils.getString("RIP_CD"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        RipperHandler.getInstance().startCdRipper();
    }

    @Override
    public String getCommandName() {
        return "cdripper";
    }

}
