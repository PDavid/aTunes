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

package net.sourceforge.atunes.gui.views.controls;

import javax.swing.JLabel;

import net.sourceforge.atunes.model.IColorMutableImageIcon;
import net.sourceforge.atunes.model.ILookAndFeelChangeListener;
import net.sourceforge.atunes.model.ILookAndFeelManager;

/**
 * A JLabel that is repaint when look and feel changes
 * @author fleax
 *
 */
public class LookAndFeelAwareLabel extends JLabel implements ILookAndFeelChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5017109922596290169L;

	private ILookAndFeelManager lookAndFeelManager;
	
	private IColorMutableImageIcon icon;
	
	/**
	 * @param lookAndFeelManager
	 */
	public LookAndFeelAwareLabel(ILookAndFeelManager lookAndFeelManager, IColorMutableImageIcon icon) {
		super();
		this.lookAndFeelManager = lookAndFeelManager;
		this.icon = icon;
		lookAndFeelManager.addLookAndFeelChangeListener(this);
		paintIcon();
	}

	private void paintIcon() {
		setIcon(icon.getIcon(lookAndFeelManager.getCurrentLookAndFeel().getPaintForSpecialControls()));
	}
	
	@Override
	public void lookAndFeelChanged() {
		paintIcon();
	}
}
