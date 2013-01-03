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

package net.sourceforge.atunes.kernel.modules.fullscreen;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.List;

import net.sourceforge.atunes.kernel.AbstractSimpleController;
import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IControlsBuilder;
import net.sourceforge.atunes.model.IOSManager;
import net.sourceforge.atunes.model.IStateUI;
import net.sourceforge.atunes.utils.FileUtils;

/**
 * Controller for full screen window
 * 
 * @author alex
 * 
 */
public class FullScreenController extends
		AbstractSimpleController<FullScreenWindow> {

	private FullScreenWindowFactory fullScreenWindowFactory;

	private IStateUI stateUI;

	private IOSManager osManager;

	private IControlsBuilder controlsBuilder;

	/**
	 * @param controlsBuilder
	 */
	public void setControlsBuilder(final IControlsBuilder controlsBuilder) {
		this.controlsBuilder = controlsBuilder;
	}

	/**
	 * @param osManager
	 */
	public void setOsManager(final IOSManager osManager) {
		this.osManager = osManager;
	}

	/**
	 * @param stateUI
	 */
	public void setStateUI(final IStateUI stateUI) {
		this.stateUI = stateUI;
	}

	/**
	 * @param fullScreenWindowFactory
	 */
	public void setFullScreenWindowFactory(
			final FullScreenWindowFactory fullScreenWindowFactory) {
		this.fullScreenWindowFactory = fullScreenWindowFactory;
	}

	/**
	 * Initializes controller
	 */
	public void initialize() {
		final FullScreenWindow window = this.fullScreenWindowFactory
				.getFullScreenWindow();
		setComponentControlled(window);

		KeyListener keyAdapter = new FullScreenKeyAdapter(window);

		window.addKeyListener(keyAdapter);
		window.getOptions().addKeyListener(keyAdapter);

		MouseListener clickListener = new FullScreenMouseListener(window);
		window.addMouseListener(clickListener);
		setClickListener(clickListener);

		MouseMotionListener moveListener = new FullScreenMouseMotionAdapter(
				window);
		window.addMouseMotionListener(moveListener);
		window.getCovers().addMouseMotionListener(moveListener);

		window.getSelectBackground().addActionListener(
				new SelectBackgroundActionListener(this, this.controlsBuilder));

		window.getRemoveBackground().addActionListener(
				new RemoveBackgroundActionListener(window, this.stateUI));

		FullScreenShowMenuMouseAdapter optionsAdapter = new FullScreenShowMenuMouseAdapter(
				window.getOptions(), this.osManager);
		window.getBackgroundPanel().addMouseListener(optionsAdapter);
		window.getCovers().addMouseListener(optionsAdapter);

		window.getExitFullScreen().addActionListener(
				new ExitFullScreenActionListener(window));

		setBackground();

	}

	private void setBackground() {
		File backgroundFile = null;
		if (this.stateUI.getFullScreenBackground() != null) {
			backgroundFile = new File(this.stateUI.getFullScreenBackground());
			if (!backgroundFile.exists()) {
				backgroundFile = null;
			}
		}
		getComponentControlled().setBackground(backgroundFile);
	}

	void setBackground(final File file) {
		getComponentControlled().setBackground(file);
		this.stateUI.setFullScreenBackground(FileUtils.getPath(file));
	}

	/**
	 * @param clickListener
	 */
	private void setClickListener(final MouseListener clickListener) {
		getComponentControlled().getCovers().addMouseListener(clickListener);
		getComponentControlled().getOptions().addMouseListener(clickListener);
		getComponentControlled().getPreviousButton().addMouseListener(
				clickListener);
		getComponentControlled().getPlayButton()
				.addMouseListener(clickListener);
		getComponentControlled().getNextButton()
				.addMouseListener(clickListener);
	}

	/**
	 * Shows or hides full screen
	 */
	void toggleVisibility() {
		getComponentControlled().setVisible(
				!getComponentControlled().isVisible());
	}

	/**
	 * Sets the audio object.
	 * 
	 * @param objects
	 */
	void setAudioObjects(final List<IAudioObject> objects) {
		getComponentControlled().setAudioObjects(objects);
	}

	/**
	 * Sets the playing
	 * 
	 * @param playing
	 */
	void setPlaying(final boolean playing) {
		getComponentControlled().setPlaying(playing);
	}

	/**
	 * Returns true if full screen is visible
	 * 
	 * @return
	 */
	boolean isVisible() {
		return getComponentControlled().isVisible();
	}
}
