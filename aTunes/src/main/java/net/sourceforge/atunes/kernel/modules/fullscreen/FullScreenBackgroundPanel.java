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

package net.sourceforge.atunes.kernel.modules.fullscreen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JPanel;
import javax.swing.OverlayLayout;

import net.sourceforge.atunes.utils.ImageUtils;

final class FullScreenBackgroundPanel extends JPanel {
	
	private static final long serialVersionUID = 109708757849271173L;

    /** The background. */
    private transient Image backgroundImage;

	public FullScreenBackgroundPanel() {
		super();
        OverlayLayout overlay = new OverlayLayout(this);
        setLayout(overlay);
        setBackground(Color.black);
	}
	
	/**
	 * @param backgroundImage
	 */
	public void setBackgroundImage(Image backgroundImage) {
		this.backgroundImage = backgroundImage;
	}
	
	@Override
	public void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    if (backgroundImage == null && g instanceof Graphics2D) {
	        Graphics2D g2d = (Graphics2D) g;
	        g2d.setPaint(Color.BLACK);
	        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
	    } else {
	        Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
	        g.drawImage(ImageUtils.scaleBufferedImageBicubic(backgroundImage, scrSize.width, scrSize.height), 0, 0, this);
	    }
	}
}