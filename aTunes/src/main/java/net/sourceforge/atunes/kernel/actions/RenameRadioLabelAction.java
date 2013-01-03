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

import net.sourceforge.atunes.model.IDialogFactory;
import net.sourceforge.atunes.model.IInputDialog;
import net.sourceforge.atunes.model.INavigationHandler;
import net.sourceforge.atunes.model.INavigationView;
import net.sourceforge.atunes.model.IRadio;
import net.sourceforge.atunes.model.IRadioHandler;
import net.sourceforge.atunes.model.ITreeNode;
import net.sourceforge.atunes.utils.I18nUtils;

/**
 * Renames label of a radio
 * 
 * @author alex
 * 
 */
public class RenameRadioLabelAction extends CustomAbstractAction {

    private static final long serialVersionUID = -606790181321223318L;

    private INavigationHandler navigationHandler;

    private IRadioHandler radioHandler;

    private INavigationView radioNavigationView;

    private IDialogFactory dialogFactory;

    /**
     * @param dialogFactory
     */
    public void setDialogFactory(final IDialogFactory dialogFactory) {
	this.dialogFactory = dialogFactory;
    }

    /**
     * @param radioNavigationView
     */
    public void setRadioNavigationView(final INavigationView radioNavigationView) {
	this.radioNavigationView = radioNavigationView;
    }

    /**
     * @param radioHandler
     */
    public void setRadioHandler(final IRadioHandler radioHandler) {
	this.radioHandler = radioHandler;
    }

    /**
     * @param navigationHandler
     */
    public void setNavigationHandler(final INavigationHandler navigationHandler) {
	this.navigationHandler = navigationHandler;
    }

    /**
     * Default constructor
     */
    public RenameRadioLabelAction() {
	super(I18nUtils.getString("RENAME_LABEL"));
    }

    @Override
    protected void executeAction() {
	Object o = radioNavigationView.getTree().getSelectedNode()
		.getUserObject();

	IInputDialog dialog = dialogFactory.newDialog(IInputDialog.class);
	dialog.setTitle(I18nUtils.getString("RENAME_LABEL"));

	if (o instanceof String) {
	    String label = (String) o;
	    dialog.setText(label);
	    dialog.showDialog();
	    String result = dialog.getResult();
	    if (result != null) {
		List<IRadio> radios = radioHandler.getRadios(label);
		radioHandler.setLabel(radios, result);
		navigationHandler.refreshView(radioNavigationView);
	    }
	} else if (o instanceof IRadio) {
	    IRadio radio = (IRadio) o;
	    dialog.setText(radio.getLabel());
	    dialog.showDialog();
	    String result = dialog.getResult();
	    if (result != null) {
		radio.setLabel(result);
		navigationHandler.refreshView(radioNavigationView);
	    }
	}
    }

    @Override
    public boolean isEnabledForNavigationTreeSelection(
	    final boolean rootSelected, final List<ITreeNode> selection) {
	return !rootSelected && selection.size() == 1;
    }

}
