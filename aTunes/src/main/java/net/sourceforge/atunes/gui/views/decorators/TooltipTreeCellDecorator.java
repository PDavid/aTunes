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
import net.sourceforge.atunes.gui.views.dialogs.ExtendedToolTip;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IStateNavigation;
import net.sourceforge.atunes.model.ITreeObject;

public class TooltipTreeCellDecorator extends AbstractTreeCellDecorator<JLabel, Object> {

    private IStateNavigation stateNavigation;

    /**
     * @param stateNavigation
     */
    public void setStateNavigation(IStateNavigation stateNavigation) {
		this.stateNavigation = stateNavigation;
	}

    @SuppressWarnings("unchecked")
	@Override
    public Component decorateTreeCellComponent(JLabel component, Object userObject, boolean isSelected) {
        if (!stateNavigation.isShowExtendedTooltip() || !ExtendedToolTip.canObjectBeShownInExtendedToolTip(userObject)) {
            if (userObject instanceof ITreeObject) {
                component.setToolTipText(((ITreeObject<? extends IAudioObject>) userObject).getTooltip());
            } else {
                component.setToolTipText(userObject.toString());
            }
        } else {
            // If using extended tooltip we must set tooltip to null. If not will appear the tooltip of the parent node
            component.setToolTipText(null);
        }
        return component;
    }

}
