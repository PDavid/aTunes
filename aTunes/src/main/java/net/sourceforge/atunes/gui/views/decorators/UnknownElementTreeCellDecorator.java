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

package net.sourceforge.atunes.gui.views.decorators;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;

import net.sourceforge.atunes.gui.AbstractTreeCellDecorator;
import net.sourceforge.atunes.model.IUnknownObjectChecker;

/**
 * Tree cell decorator for unknown elements
 * 
 * @author alex
 * 
 */
public class UnknownElementTreeCellDecorator extends
	AbstractTreeCellDecorator<JLabel, Object> {

    private IUnknownObjectChecker unknownObjectChecker;

    private Color unknownElementForegroundColor;

    /**
     * @param unknownElementForegroundColor
     */
    public void setUnknownElementForegroundColor(
	    final Color unknownElementForegroundColor) {
	this.unknownElementForegroundColor = unknownElementForegroundColor;
    }

    /**
     * @param unknownObjectChecker
     */
    public void setUnknownObjectChecker(
	    final IUnknownObjectChecker unknownObjectChecker) {
	this.unknownObjectChecker = unknownObjectChecker;
    }

    @Override
    public Component decorateTreeCellComponent(final JLabel component,
	    final Object userObject, final boolean isSelected) {
	if (checkUnknown(userObject.toString())) {
	    component.setForeground(unknownElementForegroundColor);
	}
	return component;
    }

    private boolean checkUnknown(final String string) {
	return unknownObjectChecker.isUnknownAlbum(string)
		|| unknownObjectChecker.isUnknownArtist(string)
		|| unknownObjectChecker.isUnknownGenre(string)
		|| unknownObjectChecker.isUnknownYear(string);
    }

}
