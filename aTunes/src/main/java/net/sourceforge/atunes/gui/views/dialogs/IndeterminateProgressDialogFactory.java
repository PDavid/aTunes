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

package net.sourceforge.atunes.gui.views.dialogs;

import java.awt.Window;

import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.model.IIndeterminateProgressDialog;
import net.sourceforge.atunes.model.IIndeterminateProgressDialogFactory;
import net.sourceforge.atunes.model.ILookAndFeelManager;

public class IndeterminateProgressDialogFactory implements IIndeterminateProgressDialogFactory {

	@Override
	public IIndeterminateProgressDialog newDialog(Window parent, ILookAndFeelManager lookAndFeelManager) {
		return new IndeterminateProgressDialog(parent, lookAndFeelManager);
	}

	@Override
	public IIndeterminateProgressDialog newDialog(IFrame parent, ILookAndFeelManager lookAndFeelManager) {
		return new IndeterminateProgressDialog(parent, lookAndFeelManager);
	}

	
}
