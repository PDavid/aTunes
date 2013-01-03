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

package net.sourceforge.atunes.gui.views.dialogs.properties;

import java.awt.Dimension;

import net.sourceforge.atunes.gui.views.controls.AbstractCustomDialog;
import net.sourceforge.atunes.model.IAudioObjectPropertiesDialog;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IFrame;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * A dialog to show information about audio objects
 * 
 * @author alex
 * 
 */
public abstract class AudioObjectPropertiesDialog extends AbstractCustomDialog
		implements IAudioObjectPropertiesDialog {

	private static final long serialVersionUID = 6097305595858691246L;

	/**
	 * Instantiates a new properties dialog.
	 * 
	 * @param frame
	 * @param controlsBuilder
	 */
	AudioObjectPropertiesDialog(final IFrame frame,
			final IControlsBuilder controlsBuilder) {
		super(frame, 560, 480, controlsBuilder);
		setMinimumSize(new Dimension(560, 480));
		setResizable(true);
	}

	/**
	 * Gets the html formatted.
	 * 
	 * @param desc
	 *            the desc
	 * @param text
	 *            the text
	 * 
	 * @return the html formatted
	 */
	protected String getHtmlFormatted(final String desc, final String text) {
		return StringUtils.getString("<html><b>", desc, ": </b>", text,
				"</html>");
	}

	@Override
	public void showDialog() {
		setVisible(true);
	}

	@Override
	public void hideDialog() {
		setVisible(false);
	}
}
