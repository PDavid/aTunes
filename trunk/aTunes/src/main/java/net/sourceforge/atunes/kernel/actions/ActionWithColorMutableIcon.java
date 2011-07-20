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

package net.sourceforge.atunes.kernel.actions;

import net.sourceforge.atunes.gui.images.ColorMutableImageIcon;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelChangeListener;
import net.sourceforge.atunes.gui.lookandfeel.LookAndFeelSelector;

public abstract class ActionWithColorMutableIcon extends CustomAbstractAction  implements LookAndFeelChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6248901947210263210L;

	public ActionWithColorMutableIcon(String text) {
		super(text);
		LookAndFeelSelector.getInstance().addLookAndFeelChangeListener(this);
		lookAndFeelChanged(); // Initial icon set
	}

	@Override
	public final void lookAndFeelChanged() {
		putValue(SMALL_ICON, getIcon().getIcon(LookAndFeelSelector.getInstance().getCurrentLookAndFeel().getPaintForSpecialControls()));
	}
	
	/**
	 * Returns color mutable icon
	 * @return
	 */
	public abstract ColorMutableImageIcon getIcon();
}
