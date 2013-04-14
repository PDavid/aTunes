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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import net.sourceforge.atunes.model.IAudioObject;
import net.sourceforge.atunes.model.IUnknownObjectChecker;
import net.sourceforge.atunes.utils.Logger;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Component to show covers in full screen mode
 * @author alex
 *
 */
public final class CoverFlow extends JPanel implements ApplicationContextAware {

	private static final long serialVersionUID = -5982158797052430789L;

	private List<Cover> covers;

	private FullScreenImageSizeCalculator fullScreenImageSizeCalculator;

	private ApplicationContext context;

	private IUnknownObjectChecker unknownObjectChecker;

	/**
	 * @param unknownObjectChecker
	 */
	public void setUnknownObjectChecker(
			final IUnknownObjectChecker unknownObjectChecker) {
		this.unknownObjectChecker = unknownObjectChecker;
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) {
		this.context = applicationContext;
	}

	/**
	 * @param fullScreenImageSizeCalculator
	 */
	public void setFullScreenImageSizeCalculator(final FullScreenImageSizeCalculator fullScreenImageSizeCalculator) {
		this.fullScreenImageSizeCalculator = fullScreenImageSizeCalculator;
	}

	CoverFlow() {
		super(new GridBagLayout());
	}

	/**
	 * Initializes cover flow
	 */
	public void initialize() {
		covers = new ArrayList<Cover>(6);
		for (int i = 0; i < 5; i++) {
			covers.add(new Cover(fullScreenImageSizeCalculator.getImageSize(i)));
		}
		covers.add(new HiddenCover());

		setOpaque(false);
		arrangeCovers();
	}

	/**
	 * 
	 */
	private void arrangeCovers() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.2;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		add(covers.get(0), c);
		c.gridx = 1;
		add(covers.get(1), c);
		c.gridx = 2;
		c.weightx = 0.3;
		add(covers.get(2), c);
		c.gridx = 3;
		c.weightx = 0.2;
		add(covers.get(3), c);
		c.gridx = 4;
		add(covers.get(4), c);
	}

	/**
	 * Paint.
	 * 
	 * @param objects
	 * @param osManager
	 */
	void paint(final List<IAudioObject> objects) {
		int i = 0;
		for (IAudioObject ao : objects) {
			paintCover(covers.get(i), ao);
			i++;
		}
	}

	private void paintCover(final Cover cover, final IAudioObject audioObject) {
		// No object
		if (audioObject == null) {
			return;
		}

		if (coverNeedsUpdate(cover, audioObject)) {
			cover.setPreviousArtist(audioObject.getArtist(unknownObjectChecker));
			cover.setPreviousAlbum(audioObject.getAlbum(unknownObjectChecker));
			fetchCover(cover, audioObject);
		} else {
			Logger.debug("Not updating cover: ", audioObject.getArtist(unknownObjectChecker), " ", audioObject.getAlbum(unknownObjectChecker));
		}
	}

	/**
	 * @param cover
	 * @param audioObject
	 */
	private void fetchCover(final Cover cover, final IAudioObject audioObject) {
		// Fetch cover and show
		context.getBean(PaintCoversBackgroundWorker.class).getCover(cover, audioObject, cover.getImageSize());
	}

	private boolean coverNeedsUpdate(final Cover cover, final IAudioObject audioObject) {
		return cover.getPreviousArtist() == null ||
		cover.getPreviousAlbum() == null ||
		!cover.getPreviousArtist().equals(audioObject.getArtist(unknownObjectChecker)) ||
		!cover.getPreviousAlbum().equals(audioObject.getAlbum(unknownObjectChecker));
	}

}
