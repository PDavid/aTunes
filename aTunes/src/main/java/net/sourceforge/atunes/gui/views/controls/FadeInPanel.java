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

package net.sourceforge.atunes.gui.views.controls;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * A panel with a fade in effect when becomes visible
 * @author alex
 *
 */
public class FadeInPanel extends JPanel {

	private static final long serialVersionUID = -20972054569161501L;

	BufferedImage image;
	
	private Timer timer;
	
	private float alpha;
	
	@Override
	public void setVisible(boolean aFlag) {
		boolean alreadyVisible = isVisible();
		super.setVisible(aFlag);
		if (aFlag && !alreadyVisible) {
			alpha = 0.0f;
			if (timer != null) {
				timer.stop();
			} else {
				timer = new Timer(50, new FadeInTimerActionListener(this));
			}
			timer.start();
		}
	}
	
	@Override
	public void paint(Graphics g) {
		if (alpha >= 1.0f || alpha < 0.0f) {
			super.paint(g);
		} else {
			if (image == null || image.getWidth() != getWidth() || image.getHeight() != getHeight()) {
				image = (BufferedImage) createImage(getWidth(), getHeight());
			}

			Graphics2D g2 = image.createGraphics();
			g2.setClip(g.getClip());
			super.paint(g2);
			g2.dispose();

			g2 = (Graphics2D) g.create();
			g2.setComposite(AlphaComposite.SrcOver.derive(alpha));
			g2.drawImage(image, 0, 0, null);
			g2.dispose();
		}
	}

	/**
	 * @return the alpha
	 */
	protected float getAlpha() {
		return alpha;
	}

	/**
	 * @param alpha the alpha to set
	 */
	protected void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	/**
	 * @return the timer
	 */
	protected Timer getTimer() {
		return timer;
	}
	
	private static final class FadeInTimerActionListener implements ActionListener {
		
		private FadeInPanel panel;
		
		public FadeInTimerActionListener(FadeInPanel panel) {
			this.panel = panel;
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			panel.setAlpha(panel.getAlpha() + 0.15f);
			if (panel.getAlpha() > 1.0f) {
				panel.getTimer().stop();
			}
			panel.repaint();
		}
	}
}
