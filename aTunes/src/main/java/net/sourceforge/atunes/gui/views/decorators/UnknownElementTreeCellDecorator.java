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

package net.sourceforge.atunes.gui.views.decorators;

import java.awt.Component;

import javax.swing.JLabel;

import net.sourceforge.atunes.gui.AbstractTreeCellDecorator;
import net.sourceforge.atunes.gui.ColorDefinitions;
import net.sourceforge.atunes.model.IUnknownObjectChecker;

public class UnknownElementTreeCellDecorator extends AbstractTreeCellDecorator<JLabel, Object> {

	private IUnknownObjectChecker unknownObjectChecker;
	
	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}
	
    @Override
    public Component decorateTreeCellComponent(JLabel component, Object userObject, boolean isSelected) {
    	if (checkUnknown(userObject.toString())) {
        	component.setForeground(ColorDefinitions.GENERAL_UNKNOWN_ELEMENT_FOREGROUND_COLOR);
        }
        return component;
    }
    
    private boolean checkUnknown(String string) {
    	return unknownObjectChecker.isUnknownAlbum(string) || 
    		   unknownObjectChecker.isUnknownArtist(string) ||
    		   unknownObjectChecker.isUnknownGenre(string) ||
    		   unknownObjectChecker.isUnknownYear(string);
    }

}
