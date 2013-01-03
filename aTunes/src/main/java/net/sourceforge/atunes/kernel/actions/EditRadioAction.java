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

import java.util.List;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IRadioHandler;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Edits radio properties
 * 
 * @author alex
 * 
 */
public class EditRadioAction extends AbstractActionOverSelectedObjects<IRadio> {

    private static final long serialVersionUID = -922076985505834816L;

    private IRadioHandler radioHandler;

    /**
     * @param radioHandler
     */
    public void setRadioHandler(final IRadioHandler radioHandler) {
	this.radioHandler = radioHandler;
    }

    /**
     * Default constructor
     */
    public EditRadioAction() {
	super(I18nUtils.getString("EDIT_RADIO"));
    }

    @Override
    protected void executeAction(final List<IRadio> objects) {
	IRadio radio = objects.get(0); // Guaranteed only one radio
	IRadio radioEdited = radioHandler.editRadio(radio);
	if (radioEdited != null) {
	    radioHandler.replace(radio, radioEdited);
	}
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(
	    final boolean rootSelected, final List<ITreeNode> selection) {
	if (rootSelected || selection.size() != 1
		|| !(selection.get(0) instanceof IRadio)) {
	    return false;
	}
	return true;
    }

    @Override
    public boolean isEnabledForNavigationTableSelection(
	    final List<IAudioObject> selection) {
	return selection.size() == 1;
    }
}
