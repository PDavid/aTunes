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

import net.sourceforge.atunes.model.IKernel;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * This action finishes application
 * 
 * @author fleax
 */
public class ExitAction extends CustomAbstractAction {

    private static final long serialVersionUID = 1900672708942690561L;

    private IKernel kernel;
    
    /**
     * @param kernel
     */
    public void setKernel(IKernel kernel) {
		this.kernel = kernel;
	}
    
    ExitAction() {
        super(I18nUtils.getString("EXIT"));
    }

    @Override
    protected void executeAction() {
    	kernel.finish();
    }
}
