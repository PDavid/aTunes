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

package net.sourceforge.atunes.gui.lookandfeel.system;

import java.awt.Font;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.sourceforge.atunes.gui.lookandfeel.AbstractLookAndFeel;
import net.sourceforge.atunes.model.IBeanFactory;
import net.sourceforge.atunes.utils.Logger;

/**
 * Native look and feel for any OS
 * 
 * @author alex
 * 
 */
public class SystemLookAndFeel extends AbstractLookAndFeel {

	/**
	 * Name of look and feel
	 */
	public static final String SYSTEM = "System";

	@Override
	public String getName() {
		return SYSTEM;
	}

	@Override
	public String getDescription() {
		return "System Look And Feel";
	}

	@Override
	public List<String> getSkins() {
		return null;
	}

	@Override
	public void initializeLookAndFeel(final IBeanFactory beanFactory) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);

		// There is a problem with GTKLookAndFeel which shows slider values so
		// we disable it
		UIManager.put("Slider.paintValue", false);
	}

	@Override
	public String getDefaultSkin() {
		return null;
	}

	@Override
	public boolean customComboBoxRenderersSupported() {
		return false;
	}

	@Override
	public void setLookAndFeel(final String skin) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			Logger.error(e);
		} catch (InstantiationException e) {
			Logger.error(e);
		} catch (IllegalAccessException e) {
			Logger.error(e);
		} catch (UnsupportedLookAndFeelException e) {
			Logger.error(e);
		}
	}

	@Override
	public boolean supportsCustomFontSettings() {
		return false;
	}

	@Override
	public Font getSuggestedFont() {
		return UIManager.getFont("Label.font");
	}
}
